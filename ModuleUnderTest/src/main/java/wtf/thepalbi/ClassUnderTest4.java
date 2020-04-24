package wtf.thepalbi;

public class ClassUnderTest4 {
    public static void main(String[] args) {
        fun1();
        fun2();
    }

    private static void fun1() {
        Object a1 = new A1();
        Object b1 = id(a1);
        System.out.println(b1);
    }

    private static void fun2() {
        Object a1 = new A2();
        Object b1 = id(a1);
        System.out.println(b1);
    }

    private static Object id(Object a) {
        return a;
    }
}

class A1 {
}

class A2 {
}
