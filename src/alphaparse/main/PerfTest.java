package alphaparse.main;

import alphaparse.Alpha;
import alphaparse.IO2;
import alphaparse.list.UnmodList;
import alphaparse.result.ParseTree;
import alphaparse.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

class PerfTest {
    public static void fullTest(
            final boolean doRun,
            final @NotNull String c99GrammarText,
            final int testNumMultiplierForSlowTests,
            final int testNumMultiplier) // 10 for very few, 100 for normal, 1000 for really many, 10000 if you want to wait a day
    {

        /**/
        if (!doRun)
            return;

            /* Old statistics:

Make parser:     {:min 105.916, :max 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000
First parse:     {:min 3.238, :max 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000
All parses:      {:min 3.235, :max 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000
To Array:        {:min 37.369, :max 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000
Iteration:       {:min 37.264, :max 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000
ArrayList:       {:min 37.336, :max 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000
Vector:          {:min 37.259, :max 50.422, :diff 13.162, :average 38.018, :mid 37.843, :median 37.843, :sum 760355.614} // n=20000

----------------------------------
---   Memory optimized tests   ---
----------------------------------
First parse:     {:min 3.243, :max 6.779, :diff 3.537, :average 3.300, :mid 3.284, :median 3.284, :sum 65992.776} // n=20000
All parses:      {:min 3.238, :max 6.909, :diff 3.671, :average 3.308, :mid 3.291, :median 3.291, :sum 66157.123} // n=20000
To Array:        {:min 37.350, :max 50.681, :diff 13.331, :average 38.118, :mid 37.937, :median 37.938, :sum 762350.525} // n=20000
Iteration:       {:min 37.219, :max 46.091, :diff 8.872, :average 38.005, :mid 37.828, :median 37.831, :sum 760101.547} // n=20000
ArrayList:       {:min 37.305, :max 47.066, :diff 9.761, :average 38.076, :mid 37.894, :median 37.898, :sum 761521.718} // n=20000
Vector:          {:min 37.282, :max 45.358, :diff 8.076, :average 38.025, :mid 37.864, :median 37.862, :sum 760506.577} // n=20000
Count of parses: {:min 37.236, :max 47.796, :diff 10.560, :average 38.009, :mid 37.836, :median 37.835, :sum 760184.764} // n=20000



Previous:
----------------------------------
--- Standard performance tests ---
----------------------------------
Make parser: {:lowest 49.696, :highest 72.810, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}
Lazy + Cons: {:lowest 52.767, :highest 61.580, :average 55.096, :mid 54.384, :median 54.748, :total 1101.919}
Wrapped:     {:lowest 52.239, :highest 64.242, :average 55.384, :mid 53.760, :median 56.033, :total 1107.688}
Last Clj:    {:lowest 59.582, :highest 74.739, :average 65.015, :mid 64.846, :median 63.972, :total 1300.317}
Old II:      {:lowest 108.780, :highest 221.089, :average 145.901, :mid 126.475, :median 157.120, :total 2918.036}
First parse: {:lowest 1.274, :highest 3.420, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Lazy + Cons: {:lowest 1.298, :highest 3.204, :average 1.340, :mid 1.328, :median 1.329, :total 2680.798}
Wrapped:     {:lowest 1.304, :highest 3.341, :average 1.351, :mid 1.336, :median 1.336, :total 2701.281}
Last Clj:    {:lowest 1.330, :highest 4.323, :average 1.499, :mid 1.443, :median 1.473, :total 2998.924}
Old II:      {:lowest 2.749, :highest 12.445, :average 3.083, :mid 2.942, :median 2.993, :total 6167.700}
All parses:  {:lowest 1.246, :highest 3.653, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Lazy + Cons: {:lowest 1.298, :highest 3.672, :average 1.349, :mid 1.332, :median 1.334, :total 2698.603}
Wrapped:     {:lowest 1.309, :highest 3.676, :average 1.349, :mid 1.336, :median 1.337, :total 2697.247}
Last Clj:    {:lowest 1.324, :highest 3.914, :average 1.494, :mid 1.416, :median 1.437, :total 2989.904}
Old II:      {:lowest 2.745, :highest 5.329, :average 2.985, :mid 2.888, :median 2.926, :total 5971.108}
List parses: {:lowest 13.913, :highest 19.723, :average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Lazy + Cons: {:lowest 64.445, :highest 86.821, :average 69.479, :mid 69.419, :median 69.344, :total 34739.406}
Wrapped:     {:lowest 60.033, :highest 87.687, :average 65.503, :mid 65.453, :median 65.479, :total 32751.615}
Last Clj:    {:lowest 66.228, :highest 97.553, :average 70.562, :mid 70.422, :median 70.480, :total 35281.444}
Old II:      {:lowest 20.236, :highest 32.395, :average 22.033, :mid 21.793, :median 21.905, :total 22033.985} // bugged
Iteration:   {:lowest 13.906, :highest 23.016, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}
Lazy + Cons: {:lowest 65.107, :highest 75.482, :average 70.275, :mid 70.649, :median 70.445, :total 7027.492}
ArrayList:   {:lowest 13.909, :highest 23.171, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}
Lazy + Cons: {:lowest 91.598, :highest 103.884, :average 96.257, :mid 95.753, :median 96.109, :total 9625.654}
Vector:      {:lowest 13.935, :highest 20.012, :average 14.226, :mid 14.152, :median 14.151, :total 284512.227}
Lazy + Cons: {:lowest 92.687, :highest 101.089, :average 96.402, :mid 96.667, :median 96.378, :total 9640.242}
Count of parses: {:lowest 13.960, :highest 23.369, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}
Count of parses: 4096

----------------------------------
---   Memory optimized tests   ---
----------------------------------
First parse: {:lowest 1.259, :highest 3.534, :average 1.300, :mid 1.294, :median 1.294, :total 26009.072}
Lazy + Cons: {:lowest 1.298, :highest 3.500, :average 1.347, :mid 1.333, :median 1.338, :total 2693.509}
Wrapped:     {:lowest 1.310, :highest 3.397, :average 1.351, :mid 1.339, :median 1.340, :total 2702.179}
Last Clj:    {:lowest 1.330, :highest 4.323, :average 1.499, :mid 1.443, :median 1.473, :total 2998.924}
Old II:      {:lowest 2.749, :highest 12.445, :average 3.083, :mid 2.942, :median 2.993, :total 6167.700}
All parses:  {:lowest 1.245, :highest 3.663, :average 1.281, :mid 1.273, :median 1.274, :total 25610.369}
Lazy + Cons: {:lowest 1.295, :highest 2.911, :average 1.340, :mid 1.327, :median 1.328, :total 2680.103}
Wrapped:     {:lowest 1.311, :highest 3.268, :average 1.351, :mid 1.339, :median 1.339, :total 2702.521}
Last Clj:    {:lowest 1.324, :highest 3.914, :average 1.494, :mid 1.416, :median 1.437, :total 2989.904}
Old II:      {:lowest 2.745, :highest 5.329, :average 2.985, :mid 2.888, :median 2.926, :total 5971.108}
List parses: {:lowest 13.908, :highest 19.640, :average 14.217, :mid 14.144, :median 14.146, :total 284349.087}
Lazy + Cons: {:lowest 63.622, :highest 75.022, :average 70.172, :mid 69.789, :median 70.320, :total 35085.863}
Wrapped:     {:lowest 59.235, :highest 69.048, :average 65.446, :mid 65.441, :median 65.435, :total 32722.896}
Last Clj:    {:lowest 66.228, :highest 97.553, :average 70.562, :mid 70.422, :median 70.480, :total 35281.444}
Old II:      {:lowest 20.236, :highest 32.395, :average 22.033, :mid 21.793, :median 21.905, :total 22033.985} // bugged
Iteration:   {:lowest 13.905, :highest 24.831, :average 14.226, :mid 14.148, :median 14.146, :total 284518.360}
Lazy + Cons: {:lowest 65.322, :highest 75.068, :average 70.626, :mid 71.000, :median 70.731, :total 7062.617}
ArrayList:   {:lowest 13.908, :highest 20.130, :average 14.241, :mid 14.146, :median 14.151, :total 284829.510}
Lazy + Cons: {:lowest 92.084, :highest 103.503, :average 96.438, :mid 96.138, :median 96.124, :total 9643.777}
Vector:      {:lowest 13.945, :highest 18.788, :average 14.258, :mid 14.192, :median 14.191, :total 285156.758}
Lazy + Cons: {:lowest 68.185, :highest 79.929, :average 77.652, :mid 77.740, :median 77.877, :total 7765.175}
Count of parses: {:lowest 13.937, :highest 18.469, :average 14.255, :mid 14.182, :median 14.183, :total 285105.507}
             */

        var text = "int a(int r){return r;}\n            int a(int r, int a){return r;}";
        var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());

        IO2.println("Preparing performance tests...");
        TimeUtil.measureTimeMillis(20,
                () -> Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace()));
        TimeUtil.measureTimeMillis(2000,
                () -> Alpha.parse(p, text, Alpha.ParsingOptions.optMemory()));

        IO2.println("\n----------------------------------\n--- Standard performance tests ---\n----------------------------------");
        IO2.println("Make parser: " + TimeUtil.measureTimeMillis(2 * testNumMultiplierForSlowTests,
                () -> Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace())));
        IO2.println("Previous:    {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
        IO2.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

        IO2.println("---");
        IO2.println("First parse: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parse(p, text)));
        IO2.println("Previous:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}");
        IO2.println("Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000");

        IO2.println("---");
        IO2.println("All parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parses(p, text)));
        IO2.println("Previous:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}");
        IO2.println("Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000");

        IO2.println("---");
        IO2.println("To array:    " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> new UnmodList<>(Alpha.parses(p, text))));
        IO2.println("Previous:    {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}");
        IO2.println("Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000");

        IO2.println("---");
        IO2.println("Iteration:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    for (var ignored : Alpha.parses(p, text)) ;
                }));
        IO2.println("Previous:    {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}");
        IO2.println("Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000");

        IO2.println("---");
        IO2.println("ArrayList:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    var l = new ArrayList<>(Alpha.parses(p, text));
                }));
        IO2.println("Previous:    {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}");
        IO2.println("Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000");

//        IO2.println("---");
//        IO2.println("Vector:      " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
//                () -> clojure.lang.PersistentVector.create(Alpha.parses(p, text))));
//        IO2.println("Previous:    {:lowest 13.935, :highest 20.012, :diff 6.077, :average 14.226, :mid 14.152, :median 14.151, :total 284512.227}");
//        IO2.println("Original:    {:lowest 37.259, :highest 50.422, :diff 13.162, :average 38.018, :mid 37.843, :median 37.843, :sum 760355.614} // n=20000");

        IO2.println("---");
        IO2.println("Cnt parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text).size()));
        IO2.println("Previous:    {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}");
        IO2.println("Original:    -");

        IO2.println("Count of parses: " + Alpha.parses(p, text).size());

        IO2.println("\n----------------------------------\n---   Memory optimized tests   ---\n----------------------------------");
        IO2.println("First parse: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parse(p, text, Alpha.ParsingOptions.optMemory())));
        IO2.println("Std prev:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 3.243, :highest 6.779, :diff 3.537, :average 3.300, :mid 3.284, :median 3.284, :sum 65992.776} // n=20000");

        IO2.println("---");
        IO2.println("All parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parses(p, text, Alpha.ParsingOptions.optMemory())));
        IO2.println("Std prev:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 3.238, :highest 6.909, :diff 3.671, :average 3.308, :mid 3.291, :median 3.291, :sum 66157.123} // n=20000");

        IO2.println("---");
        IO2.println("List parses: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> new UnmodList<>(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()))));
        IO2.println("Std prev:    {:lowest 13.913, :highest 19.723, :diff 5.810, :average 14.222, :mid 14.141, :median 14.144, :total 284433.845}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.350, :highest 50.681, :diff 13.331, :average 38.118, :mid 37.937, :median 37.938, :sum 762350.525} // n=20000");

        IO2.println("---");
        IO2.println("Iteration:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    for (var ignored : Alpha.parses(p, text, Alpha.ParsingOptions.optMemory())) ;
                }));
        IO2.println("Std prev:    {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.219, :highest 46.091, :diff 8.872, :average 38.005, :mid 37.828, :median 37.831, :sum 760101.547} // n=20000");

        IO2.println("---");
        IO2.println("ArrayList:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    var l = new ArrayList<ParseTree>(8192);
                    l.addAll(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()));
                }));
        IO2.println("Std prev:    {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.305, :highest 47.066, :diff 9.761, :average 38.076, :mid 37.894, :median 37.898, :sum 761521.718} // n=20000");

//        IO2.println("---");
//        IO2.println("Vector:      " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
//                () -> clojure.lang.LazilyPersistentVector.create(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()))));
//        IO2.println("Std prev:    {:lowest 13.935, :highest 20.012, :diff 6.077, :average 14.226, :mid 14.152, :median 14.151, :total 284512.227}");
//        IO2.println("Previous:    -");
//        IO2.println("Original:    {:lowest 37.282, :highest 45.358, :diff 8.076, :average 38.025, :mid 37.864, :median 37.862, :sum 760506.577} // n=20000");

        IO2.println("---");
        IO2.println("Cnt parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()).size()));
        IO2.println("Std prev:    {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.236, :highest 47.796, :diff 10.560, :average 38.009, :mid 37.836, :median 37.835, :sum 760184.764} // n=20000");

        IO2.println("Count of parses: " + Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()).size());
    }


    static void testNumberOfParses(final boolean doRun,
                                   final int max) // Currently tested with max=23, number is exclusive
    {
        if (!doRun)
            return;
        IO2.println("\n----------------------------------\n---   Number of parses tests   ---\n----------------------------------");
        System.gc();
        var grammar = "S : (A | B)+\nA : 'a' | 'b'\nB : 'b' | 'a'";
        var p = Alpha.parser(grammar);
        var sb = new StringBuilder();
        for (int n = 0; n < max; n++) {
            int num = Alpha.parses(p, sb.toString()).size();
            IO2.println("Parses for " + n + ": " + num + " (Correct? "
                    + (num == 0 || num == 1 << n)
                    + ")");
            sb.append("a");
        }
    }
}
