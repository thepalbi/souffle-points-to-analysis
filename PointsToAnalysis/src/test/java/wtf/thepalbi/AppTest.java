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
    @Test
    public void shouldAnswerWithTrue() {
        Options.v().setPhaseOption("jb", "use-original-names: true");

        // This should be the path to the test-classes directory
        String pathToRunningTestJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String pathToTargetClasses = pathToRunningTestJar.replace("test-classes", "classes");

        // Set as classpath both test and src classes
        Options.v()
                .set_soot_classpath(
                        pathToRunningTestJar + ":" + pathToTargetClasses);
        Options.v().set_process_dir(asList(pathToRunningTestJar, pathToTargetClasses));
        // Add rt.jar in classpath
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        // Output format is Jimple, so JBP wil be used
        Options.v().set_output_format(Options.output_format_jimple);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runBodyPacks();

        Body methodBody =  Scene.v().getSootClass("wtf.thepalbi.TestClass").getMethodByName("main").getActiveBody();

        App app = new App();
        Collection<SouffleFact> factCollection = app.convertMethodBody(methodBody);
    }
}
