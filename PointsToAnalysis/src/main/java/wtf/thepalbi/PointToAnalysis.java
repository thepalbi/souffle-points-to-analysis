package wtf.thepalbi;

import soot.*;
import wtf.thepalbi.relations.*;
import wtf.thepalbi.utils.HeapLocationFactory;
import wtf.thepalbi.utils.UUIDHeapLocationFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static wtf.thepalbi.relations.FactWriter.writeMethod;
import static wtf.thepalbi.relations.FactWriter.writeSignature;

public class PointToAnalysis {

    private HeapLocationFactory heapLocationFactory = new UUIDHeapLocationFactory();

    public static final String OUTPUT_FILE_EXTENSION = ".csv";
    public static String IO_SEPARATOR = ";";

    private Map<String, FileWriter> factTypeToFile = new HashMap<>();
    public static final List<String> EXPECTED_OUTPUT_FACTS = Arrays.asList(
            "VarPointsTo",
            "FieldPointsTo",
            "CallGraph",
            "InterProcAssign",
            "Reachable");

    public PointsToResult run(Iterable<Body> bodies, Body startingMethod, Scene scene) throws Exception {
        Path workingDirectory = Files.createTempDirectory("points-to-");
        Path inputDirectory = Files.createDirectory(Paths.get(workingDirectory + "/input"));
        Path outputDirectory = Files.createDirectory(Paths.get(workingDirectory + "/output"));

        Collection<SouffleFact> accumulatedFacts = new HashSet<>();

        for (Body body : bodies) {
            accumulatedFacts.addAll(this.collectFactsForBody(body, scene));
        }

        // Add reachable facts according to starting method
        accumulatedFacts.add(new ReachableFact(startingMethod.getMethod()));
        // Fix VCall to method without Soot Bodies
        Collection<SouffleFact> fixFacts = new HashSet<>();
        accumulatedFacts.stream()
                .filter(fact -> fact instanceof VCallFact)
                .map(vCallFact -> ((VCallFact) vCallFact).getCalledMethodRef())
                // Filter for non-interface methods TODO: How should I handle them?
                // and with no active body. Also, omit Void returning methods
                .filter(methodRef -> !methodRef.getDeclaringClass().isInterface() &&
                        !methodRef.resolve().hasActiveBody() &&
                        !(methodRef.getReturnType() instanceof VoidType))
                .forEach(methodRef -> {
                    String fakeReturnLocalName = FactWriter.writeMethod(methodRef) + "fake_return_local";
                    String fakeHeapObject = this.heapLocationFactory.generate();
                    // Since method body is not parsed, a fake return should be added, and the corresponding Alloc
                    // and HeapType for that fake local allocation
                    fixFacts.add(new FormalReturnFact(methodRef, fakeReturnLocalName));
                    fixFacts.add(new AllocFact(fakeReturnLocalName, fakeHeapObject, methodRef));
                    fixFacts.add(new HeapTypeFact(fakeHeapObject, methodRef.getReturnType()));
                });
        accumulatedFacts.addAll(fixFacts);

        // Write all facts to their corresponding input files
        for (SouffleFact fact : accumulatedFacts) {
            if (!this.factTypeToFile.containsKey(fact.getRelationName())) {
                Path factsFile = Paths.get(inputDirectory + "/" + fact.getRelationName() + ".facts");
                this.factTypeToFile.put(fact.getRelationName(), new FileWriter(factsFile.toFile()));
            }
            FileWriter currentWriter = this.factTypeToFile.get(fact.getRelationName());
            currentWriter.write(fact.toIODirective());
        }

        // Close all writers
        for (FileWriter writer : this.factTypeToFile.values()) {
            writer.close();
        }

        // Run Souffle script
        String[] souffleCommand = {
                "souffle",
                "-F" + inputDirectory,
                "-D" + outputDirectory,
                this.getClass().getClassLoader().getResource("vanilla-andersen.dl").getPath()
        };

        System.out.println("Souffle command: " + String.join(" ", souffleCommand));

        Process souffleProcess = Runtime.getRuntime().exec(souffleCommand);

        int exitCode = souffleProcess.waitFor();
        if (exitCode != 0) {
            // Something failed in the Souffle process
            throw new Exception("Souffle process failed");
        }

        System.out.println("Output directory: " + outputDirectory);

        Map<String, List<String[]>> parsedOutputFacts = new HashMap<>();

        for (String expectedOutputFactsFile : EXPECTED_OUTPUT_FACTS) {
            List<String[]> csv = new LinkedList<>();
            // Using auto-closable
            try (Scanner scanner = new Scanner(new File(outputDirectory + "/" + expectedOutputFactsFile + OUTPUT_FILE_EXTENSION))) {
                while (scanner.hasNextLine()) {
                    csv.add(scanner.nextLine().split(IO_SEPARATOR));
                }
            }
            parsedOutputFacts.put(expectedOutputFactsFile, csv);
        }

        // Compute a heapLocation to type map
        Map<String, String> heapLocationToType = new HashMap<>();
        accumulatedFacts.stream()
                .filter(fact -> fact instanceof HeapTypeFact)
                .map(fact -> (HeapTypeFact) fact)
                .forEach(heapTypeFact -> {
                    heapLocationToType.put(heapTypeFact.getHeapLocation(), heapTypeFact.getType().toString());
                });

        // Compute a local to HeapObject map. Note there
        Map<String, List<HeapObject>> localToHeapObject = new HashMap<>();
        for (String[] csvRow : parsedOutputFacts.get("VarPointsTo")) {
            String localName = csvRow[0];
            String heapLocation = csvRow[1];
            HeapObject heapObject = new HeapObject(heapLocation, heapLocationToType.get(heapLocation));;
            localToHeapObject
                    .computeIfAbsent(localName, someLocalName -> new LinkedList<>())
                    .add(heapObject);
        }

        return new PointsToResult(localToHeapObject);
    }

    private Collection<SouffleFact> collectFactsForBody(Body methodBody, Scene scene) {
        Collection<SouffleFact> collectedFacts =
                new StmtToSouffleFactTranslator(heapLocationFactory).translateMethodBody(methodBody);

        // Generate Subtype facts
        Collection<SouffleFact> lookupFacts = new HashSet<>();

        collectedFacts.stream()
                .filter(fact -> fact instanceof TypeFact)
                .map(fact -> ((TypeFact) fact).getType().toString())
                .forEach(seenTypeName -> {
                    SootClass seenClass = scene.getSootClass(seenTypeName);
                    for (SootMethod method : seenClass.getMethods()) {
                        // Generate a Lookup fact for seenType -> method -> method signature
                        lookupFacts.add(new LookupFact(
                                seenTypeName,
                                writeSignature(method),
                                writeMethod(method)));
                    }
                });

        collectedFacts.addAll(lookupFacts);
        return collectedFacts;
    }
}
