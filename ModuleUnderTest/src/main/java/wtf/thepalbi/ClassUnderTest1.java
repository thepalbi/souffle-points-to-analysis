package wtf.thepalbi;

public class ClassUnderTest1 {

    // When using String[] as a normal main method, FAILS
    public static void main(String[] args) {
        ClassUnderTest1 test = new ClassUnderTest1();
        String[] someStrings = new String[]{"perrus"};
        String b = someStrings[0];
        b = test.method2(b);
        System.out.println(b);
    }

    public String method2(String gola) {
        return gola + " gola";
    }
}
