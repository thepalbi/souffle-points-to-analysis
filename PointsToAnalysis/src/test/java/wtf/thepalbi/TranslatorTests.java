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

    @Test
    public void simpleTranslation() {
        Body methodBody = getBodyForClassAndMethod("SomeStringGetsCreated", "method");
        PointsToFactGenerator pointsToFactGenerator = new PointsToFactGenerator();
        Collection<SouffleFact> factCollection = pointsToFactGenerator.fromMethodBody(methodBody);
    }

    // Class under test located in the same @Test class, to make things more clear
    public static class SomeStringGetsCreated {
        public static String method() {
            String b = new String();
            Integer someInteger = new Integer(1);
            String a = someInteger.toString();
            return a;
        }
    }
}
