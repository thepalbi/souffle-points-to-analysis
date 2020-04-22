package wtf.thepalbi;

import org.apache.commons.io.FileUtils;
import soot.*;
import wtf.thepalbi.relations.*;
import wtf.thepalbi.utils.HeapLocationFactory;
import wtf.thepalbi.utils.UUIDHeapLocationFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static wtf.thepalbi.relations.FactWriter.writeMethod;
import static wtf.thepalbi.relations.FactWriter.writeSignature;

/**
 * Main class for running a points to analysis.
 */
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

    private final Scene scene;

    /**
     * Builds a new {@link PointToAnalysis} using an already existing {@link Scene}. Note that the supplied scene must
     * already have ran though the Body Packs. This ensures that the classes are loaded in its Jimple form.
     *
     * @param scene a Soot scene from which to load classes in the form of A{@link SootClass}.
     */
    public PointToAnalysis(Scene scene) {
        this.scene = scene;
    }

    /**
     * Runs the analysis for the classes below the given package name, using as the first <it>reachable</it> class the
     * starting method.
     *
     * @param packageName    A package name from which to load classes the analysis will look into.
     * @param startingMethod The main method from which to run the analysis. This will be the method marked as
     *                       <it>reachable</it>.
     * @return The result of the analysis.
     * @throws Exception is raised if something fails during the analysis.  // TODO: Improve error handling.
     */
    public PointsToResult forClassesUnderPackage(String packageName, Body startingMethod) throws Exception {
        List<Body> targetBodies = Scene.v().getClasses().stream()
                .filter(sootClass -> sootClass.getPackageName().startsWith(packageName))
                // Filter interface, they do not have method bodies
                .filter(sootClass -> !sootClass.isInterface())
                .map(sootClass -> sootClass.getMethods())
                // This might fail because some class has no active body
                .flatMap(sootMethods -> sootMethods.stream().map(sootMethod -> sootMethod.getActiveBody()))
                .collect(Collectors.toList());
        return run(targetBodies, startingMethod);
    }

    private PointsToResult run(Iterable<Body> bodies, Body startingMethod) throws Exception {
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
                .filter(vCallFact -> {
                    SootMethodRef methodRef = ((VCallFact) vCallFact).getCalledMethodRef();
                    // Filter for non-interface methods TODO: How should I handle them?
                    // and with no active body. Also, omit Void returning methods
                    return !methodRef.getDeclaringClass().isInterface() &&
                            !methodRef.resolve().hasActiveBody() &&
                            !(methodRef.getReturnType() instanceof VoidType);
                })
                .forEach(vCallWithoutBody -> {
                    SootMethodRef methodRef = ((VCallFact) vCallWithoutBody).getCalledMethodRef();
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

        // Write datalog script to temp file
        InputStream pointsToScriptAsStream = this.getClass().getClassLoader().getResourceAsStream("vanilla-andersen.dl");
        String tempWrittenScript = workingDirectory + "/script.dl";
        FileUtils.copyInputStreamToFile(pointsToScriptAsStream, new File(tempWrittenScript));

        // Run Souffle script
        String[] souffleCommand = {
                "souffle",
                "-F" + inputDirectory,
                "-D" + outputDirectory,
                tempWrittenScript
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
        // Global heap representation
        Map<String, HeapObject> heap = new HashMap<>();
        accumulatedFacts.stream()
                .filter(fact -> fact instanceof HeapTypeFact)
                .map(fact -> (HeapTypeFact) fact)
                .forEach(heapTypeFact -> heap.put(
                        heapTypeFact.getHeapLocation(),
                        new HeapObject(
                                heapTypeFact.getHeapLocation(),
                                heapTypeFact.getType().toString())));

        // Compute a local to HeapObject map. Note there
        Map<String, List<HeapObject>> localToHeapObject = new HashMap<>();
        for (String[] csvRow : parsedOutputFacts.get("VarPointsTo")) {
            String localName = csvRow[0];
            String heapLocation = csvRow[1];
            localToHeapObject
                    .computeIfAbsent(localName, someLocalName -> new LinkedList<>())
                    .add(heap.get(heapLocation));
        }

        // Compute heapobject.field -> heapobject map
        for (String[] csvRow : parsedOutputFacts.get("FieldPointsTo")) {
            String baseHeapLoc = csvRow[0];
            String fieldSignature = csvRow[1];
            String pointedHeapLoc = csvRow[2];
            // Gets the object from the heap, or `allocates` it according to its type
            heap.get(baseHeapLoc).setFieldPointsTo(fieldSignature, heap.get(pointedHeapLoc));
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
                .map(fact -> ((TypeFact) fact).getType())
                .forEach(seenTypeName -> {
                    SootClass seenClass = scene.getSootClass(seenTypeName.toString());
                    for (SootMethod method : seenClass.getMethods()) {
                        // Generate a Lookup fact for seenType -> method -> method signature
                        lookupFacts.add(new LookupFact(
                                seenTypeName.toString(),
                                writeSignature(method),
                                writeMethod(method)));
                    }
                });

        collectedFacts.addAll(lookupFacts);
        return collectedFacts;
    }
}
