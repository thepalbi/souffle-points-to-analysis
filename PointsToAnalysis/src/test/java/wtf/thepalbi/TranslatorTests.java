package wtf.thepalbi;

import org.junit.Test;
import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;
import wtf.thepalbi.utils.UUIDHeapLocationFactory;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.parallelSort;

/**
 * Unit test for simple App.
 */
public class TranslatorTests {

    public Body getBodyForClassAndMethod(String innerClassName, String methodName) {
        String fullyQualifiedName = this.getClass().getCanonicalName() + "$" + innerClassName;

        Options.v().setPhaseOption("jb", "use-original-names: true");

        // This should be the path to the test-classes directory
        String pathToRunningTestJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToTargetClasses = pathToRunningTestJar.replace("test-classes", "test-classes");

        // Set as classpath both test and src classes
        Options.v().set_process_dir(asList(pathToRunningTestJar, pathToTargetClasses));
        // Avoid errors from resolving transitive dependencies
        Options.v().set_allow_phantom_refs(true);
        // Add rt.jar in classpath
        Options.v().set_prepend_classpath(true);
        // Output format is Jimple, so JBP wil be used
        Options.v().set_output_format(Options.output_format_jimple);
        // Both of these are needed for loading target classes
        Scene.v().loadNecessaryClasses();
        // Keep line numbers to craft invocation sites
        Options.v().set_keep_line_number(true);

        PackManager.v().runBodyPacks();

        // Get class first, then get desired method
        return Scene.v().getSootClass(fullyQualifiedName).getMethodByName(methodName).getActiveBody();
    }

    // TODO: Start adding test cases just counting the expected fact count of some type to the Jimple translation

    @Test
    public void simpleTranslation() throws Exception {
        Body methodBody = getBodyForClassAndMethod("SomeStringGetsCreated", "main");
        PointToAnalysis analysis = new PointToAnalysis();

        String targetPackage = "wtf.thepalbi";

        List<Body> targetBodies = Scene.v().getClasses().stream()
                .filter(sootClass -> sootClass.getPackageName().startsWith(targetPackage))
                .map(sootClass -> sootClass.getMethods())
                .flatMap(sootMethods -> sootMethods.stream().map(sootMethod -> sootMethod.getActiveBody()))
                .collect(Collectors.toList());

        analysis.main(targetBodies, methodBody, Scene.v());
    }

    // Class under test located in the same @Test class, to make things more clear
    public static class SomeStringGetsCreated {
        public String someString;

        public static void main(String[] args) {
            SomeStringGetsCreated someStringGetsCreated = new SomeStringGetsCreated();
            String b = new String();
            someStringGetsCreated.someString = new String();
            String c = someStringGetsCreated.method2(b);
            System.out.println(c);
        }

        public String method2(String gola) {
            return gola + " gola";
        }
    }
}
