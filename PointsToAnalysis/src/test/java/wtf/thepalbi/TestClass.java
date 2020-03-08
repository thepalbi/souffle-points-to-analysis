package wtf.thepalbi;

public class TestClass {
    public static void main(String[] args) {
        System.out.println("holis");
        StringPair pair = new StringPair();

        pair.first = "holis";
        pair.second = "perro";
    }

    public static class StringPair
    {
        protected String first;
        protected String second;
    }
}
