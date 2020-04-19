package wtf.thepalbi;

public class ClassUnderTest1 {
    private String someString;

    // When using String[] as a normal main method, FAILS
    public static void main(String args) {
        ClassUnderTest1 test = new ClassUnderTest1();
        String b = new String();
        b += " holis";
        test.someString = b;
        String c = test.method2(b);
        System.out.println(c);
    }

    public String method2(String gola) {
        return gola + " gola";
    }
}
