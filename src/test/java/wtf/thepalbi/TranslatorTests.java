package wtf.thepalbi;

import org.junit.Test;
import soot.Body;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit test for simple App.
 */
public class TranslatorTests {

    public Body getBodyForClassAndMethod(String targetClassName, String methodName) {
        Options.v().setPhaseOption("jb", "use-original-names: true");

        // Setting soot class path. ITS NOT THE SAME AS PROCESS DIR
        // Adding soot classpath

        // Set as classpath both test and src classes
        // TODO: Fix this! Read ideas in notebook for using Janino + Soot for testing.
        Options.v().set_process_dir(asList("/Users/thepalbi/Facultad/aap/souffle-points-to-analysis/ModuleUnderTest/target/classes"));
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
    public void supportMethodWithArrayTypesInSignature() throws Exception {
        Body methodBody = getBodyForClassAndMethod("wtf.thepalbi.ClassUnderTest1", "main");
        new PointToAnalysis(Scene.v()).forClassesUnderPackage("wtf.thepalbi", methodBody);
    }

    @Test
    public void findTargetClassOfFactoryWithInterfaces() throws Exception {
        Body mainTestMethod = getBodyForClassAndMethod("wtf.thepalbi.ClassUnderTest2", "main");
        PointsToResult result = new PointToAnalysis(Scene.v()).forClassesUnderPackage("wtf.thepalbi", mainTestMethod);
        assertThat(result.localPointsTo(mainTestMethod.getMethod(), "r1"), not(empty()));
        assertThat(result.localPointsTo(mainTestMethod.getMethod(), "r1"), hasSize(1));
        assertThat(result.localPointsTo(mainTestMethod.getMethod(), "r1").get(0).getType(), is("wtf.thepalbi.Dog"));
    }

    @Test
    public void fieldsAssignedInSomeMethod() throws Exception {
        // TODO: Check this test
        Body methodBody = getBodyForClassAndMethod("wtf.thepalbi.ClassUnderTest1", "main");
        PointsToResult result = new PointToAnalysis(Scene.v()).forClassesUnderPackage("wtf.thepalbi", methodBody);
        List<HeapObject> pointedByPerroField = result.localFieldPointsTo(
                methodBody.getMethod(),
                "$r3",
                methodBody.getMethod().getDeclaringClass().getFieldByName("perro").getSignature());
        assertThat(pointedByPerroField, not(empty()));
        assertThat(pointedByPerroField, hasSize(1));
        assertThat(pointedByPerroField.get(0).getType(), is("java.lang.String"));
    }

    @Test
    public void fieldAssignedInEmptyConstructor() throws Exception {
        Body methodBody = getBodyForClassAndMethod("wtf.thepalbi.ClassUnderTest3", "main");
        PointsToResult result = new PointToAnalysis(Scene.v()).forClassesUnderPackage("wtf.thepalbi", methodBody);
        List<HeapObject> pointedByPerroField = result.localFieldPointsTo(
                methodBody.getMethod(),
                "$r2",
                methodBody.getMethod().getDeclaringClass().getFieldByName("perro").getSignature());
        assertThat(pointedByPerroField, not(empty()));
        assertThat(pointedByPerroField, hasSize(1));
        assertThat(pointedByPerroField.get(0).getType(), is("java.lang.String"));
    }
}
