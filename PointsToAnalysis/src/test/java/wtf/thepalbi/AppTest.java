package wtf.thepalbi;

import org.junit.Test;
import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Unit test for simple App.
 */
public class AppTest {

    public Body getBodyForClassAndMethod(String fullyQualifiedClassName, String methodName) {
        Options.v().setPhaseOption("jb", "use-original-names: true");

        // This should be the path to the test-classes directory
        String pathToRunningTestJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToTargetClasses = pathToRunningTestJar.replace("test-classes", "classes");

        // Set as classpath both test and src classes
        Options.v()
                .set_soot_classpath(
                        pathToRunningTestJar + ":" + pathToTargetClasses);
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
        return Scene.v().getSootClass(fullyQualifiedClassName).getMethodByName(methodName).getActiveBody();
    }

    @Test
    public void shouldAnswerWithTrue() {
        Body methodBody = getBodyForClassAndMethod("wtf.thepalbi.AppTest$ClassUnderTest", "main")
        PointsToFactGenerator pointsToFactGenerator = new PointsToFactGenerator();
        Collection<SouffleFact> factCollection = pointsToFactGenerator.fromMethodBody(methodBody);
    }

    // Class under test located in the same @Test class, to make things more clear
    public static class ClassUnderTest {
        public static void main(String[] args) {
            TestClass.StringPair pair = new TestClass.StringPair();
            pair.first = "holis";
            pair.second = "perro";
        }
    }

    public static class StringPair {
        protected String first;
        protected String second;
    }
}
