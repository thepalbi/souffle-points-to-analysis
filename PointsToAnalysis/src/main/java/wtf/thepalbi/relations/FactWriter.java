package wtf.thepalbi.relations;

import soot.SootMethod;

import static wtf.thepalbi.PointToAnalysis.IO_SEPARATOR;

public class FactWriter {
    public static String twoParameters(String p1, String p2) {
        return p1 + IO_SEPARATOR + p2 + "\n";
    }

    public static String threeParameters(String p1, String p2, String p3) {
        return p1 + IO_SEPARATOR + p2 + IO_SEPARATOR + p3 + "\n";
    }

    public static String fourParameters(String p1, String p2, String p3, String p4) {
        return p1 + IO_SEPARATOR + p2 + IO_SEPARATOR + p3 + IO_SEPARATOR + p4 + "\n";
    }

    public static String writeMethod(SootMethod method) {
        return method.getSignature();
    }

    public static String writeSignature(SootMethod method) {
        return method.getSubSignature();
    }

    ;
}
