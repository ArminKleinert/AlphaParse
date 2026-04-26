package alphaparse;

/**
 * For Java 21 compatibility.
 */
public class IO2 {
    private IO2() {
    }

    /**
     * Equivalent to {@code System.out.println(...);}
     *
     * @param s The thing to print.
     */
    public static void println(Object s) {
        System.out.println(s);
    }

    /**
     * Equivalent to {@code System.out.println();}
     */
    public static void println() {
        System.out.println();
    }

    /**
     * Equivalent to {@code System.err.println(...);}
     *
     * @param s The thing to print.
     */
    public static void errln(Object s) {
        System.err.println(s);
    }
}
