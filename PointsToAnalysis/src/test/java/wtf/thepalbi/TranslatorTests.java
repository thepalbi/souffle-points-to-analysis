package wtf.thepalbi;

import org.junit.Test;
import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.Collection;

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
        String pathToTargetClasses = pathToRunningTestJar.replace("test-classes", "classes");

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
        PackManager.v().runBodyPacks();

        // Get class first, then get desired method
        return Scene.v().getSootClass(fullyQualifiedName).getMethodByName(methodName).getActiveBody();
    }

    // TODO: Start adding test cases just counting the expected fact count of some type to the Jimple translation

    @Test
    public void simpleTranslation() throws Exception {
        Body methodBody = getBodyForClassAndMethod("SomeStringGetsCreated", "method");
        PointToAnalysis analysis = new PointToAnalysis();
        analysis.main(methodBody, Scene.v());

        Collection<SouffleFact> factCollection = analysis.collectFactsFromMethodBody(methodBody);
    }

    // Class under test located in the same @Test class, to make things more clear
    public static class SomeStringGetsCreated {
        public String someString;

        public String method() {
            String b = new String();
            b += "perro";
            String c = this.method2(b);
            return c;
        }

        public String method2(String gola) {
            return gola + " gola";
        }
    }
}
