package wtf.thepalbi;

import org.junit.Test;
import soot.Body;
import soot.JastAddJ.Opt;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Unit test for simple App.
 */
public class TranslatorTests {

    public Body getBodyForClassAndMethod(String targetClassName, String methodName) {
        Options.v().setPhaseOption("jb", "use-original-names: true");

        // Setting soot class path. ITS NOT THE SAME AS PROCESS DIR
        // Adding soot classpath

        // Set as classpath both test and src classes
        Options.v().set_process_dir(asList("/Users/thepalbi/Facultad/aap/souffle-points-to-analysis/PointsToAnalysis/ModuleUnderTest/target/classes"));
        // Add rt.jar in classpath
        Options.v().set_prepend_classpath(true);
        // Output format is Jimple, so JBP wil be used
        Options.v().set_output_format(Options.output_format_jimple);
        // Both of these are needed for loading target classes
        // Keep line numbers to craft invocation sites
        Options.v().set_keep_line_number(true);

        Options.v().set_main_class(targetClassName);

        Scene.v().loadNecessaryClasses();

        PackManager.v().runBodyPacks();

        // Get class first, then get desired method
        return Scene.v().getSootClass(targetClassName).getMethodByName(methodName).getActiveBody();
    }

    // TODO: Start adding test cases just counting the expected fact count of some type to the Jimple translation

    @Test
    public void simpleTranslation() throws Exception {
        Body methodBody = getBodyForClassAndMethod("wtf.thepalbi.ClassUnderTest1", "main");
        PointToAnalysis analysis = new PointToAnalysis();

        String targetPackage = "wtf.thepalbi";

        List<Body> targetBodies = Scene.v().getClasses().stream()
                .filter(sootClass -> sootClass.getPackageName().startsWith(targetPackage))
                .map(sootClass -> sootClass.getMethods())
                .flatMap(sootMethods -> sootMethods.stream().map(sootMethod -> sootMethod.getActiveBody()))
                .collect(Collectors.toList());

        analysis.main(targetBodies, methodBody, Scene.v());
    }
}
