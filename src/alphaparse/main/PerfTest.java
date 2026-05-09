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
Make parser: {:lowest 57.658, :highest 103.016, :diff 45.358, :average 63.324, :mid 59.619, :median 61.341, :total 12664.708}
Previous:    {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}
Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000
---
First parse: {:lowest 1.382, :highest 6.293, :diff 4.911, :average 1.520, :mid 1.443, :median 1.459, :total 30397.866}
Previous:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000
---
All parses:  {:lowest 0.000, :highest 0.045, :diff 0.045, :average 0.001, :mid 0.001, :median 0.001, :total 15.080}
Previous:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000
---
To array:    {:lowest 12.567, :highest 36.166, :diff 23.599, :average 13.600, :mid 12.906, :median 12.971, :total 27200.583}
Previous:    {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000
---
Iteration:   {:lowest 12.501, :highest 22.928, :diff 10.427, :average 13.509, :mid 12.809, :median 12.909, :total 27017.503}
Previous:    {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}
Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000
---
ArrayList:   {:lowest 12.480, :highest 26.343, :diff 13.862, :average 13.395, :mid 12.804, :median 12.850, :total 26790.117}
Previous:    {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}
Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000
---
Cnt parses:  {:lowest 12.539, :highest 23.903, :diff 11.364, :average 13.446, :mid 12.850, :median 12.895, :total 26892.826}
Previous:    {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}
Original:    -
Count of parses: 4096

----------------------------------
---   Memory optimized tests   ---
----------------------------------
First parse: {:lowest 1.386, :highest 5.143, :diff 3.757, :average 1.511, :mid 1.452, :median 1.456, :total 30228.963}
Std prev:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Previous:    -
Original:    {:lowest 3.243, :highest 6.779, :diff 3.537, :average 3.300, :mid 3.284, :median 3.284, :sum 65992.776} // n=20000
---
All parses:  {:lowest 0.000, :highest 0.031, :diff 0.030, :average 0.000, :mid 0.000, :median 0.000, :total 5.688}
Std prev:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Previous:    -
Original:    {:lowest 3.238, :highest 6.909, :diff 3.671, :average 3.308, :mid 3.291, :median 3.291, :sum 66157.123} // n=20000
---
List parses: {:lowest 12.530, :highest 30.854, :diff 18.324, :average 13.976, :mid 12.952, :median 13.490, :total 279516.745}
Std prev:    {:lowest 13.913, :highest 19.723, :diff 5.810, :average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Previous:    -
Original:    {:lowest 37.350, :highest 50.681, :diff 13.331, :average 38.118, :mid 37.937, :median 37.938, :sum 762350.525} // n=20000
---
Iteration:   {:lowest 12.591, :highest 21.904, :diff 9.313, :average 14.006, :mid 12.967, :median 13.321, :total 28011.304}
Std prev:    {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}
Previous:    -
Original:    {:lowest 37.219, :highest 46.091, :diff 8.872, :average 38.005, :mid 37.828, :median 37.831, :sum 760101.547} // n=20000
---
ArrayList:   {:lowest 12.588, :highest 27.211, :diff 14.623, :average 13.957, :mid 12.906, :median 13.207, :total 27913.114}
Std prev:    {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}
Previous:    -
Original:    {:lowest 37.305, :highest 47.066, :diff 9.761, :average 38.076, :mid 37.894, :median 37.898, :sum 761521.718} // n=20000
---
Cnt parses:  {:lowest 12.601, :highest 24.309, :diff 11.708, :average 14.040, :mid 12.966, :median 13.546, :total 28079.856}
Std prev:    {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}
Previous:    -
Original:    {:lowest 37.236, :highest 47.796, :diff 10.560, :average 38.009, :mid 37.836, :median 37.835, :sum 760184.764} // n=20000
Count of parses: 4096
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
        IO2.println("Previous:    {:lowest 57.658, :highest 103.016, :diff 45.358, :average 63.324, :mid 59.619, :median 61.341, :total 12664.708}");
        IO2.println("Previous 2:  {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
        IO2.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

        IO2.println("---");
        IO2.println("First parse: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parse(p, text)));
        IO2.println("Previous:    {:lowest 1.382, :highest 6.293, :diff 4.911, :average 1.520, :mid 1.443, :median 1.459, :total 30397.866}");
        IO2.println("Previous 2:  {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}");
        IO2.println("Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000");

        IO2.println("---");
        IO2.println("All parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parses(p, text)));
        IO2.println("Previous:    {:lowest 0.000, :highest 0.045, :diff 0.045, :average 0.001, :mid 0.001, :median 0.001, :total 15.080}");
        IO2.println("Previous 2:  {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}");
        IO2.println("Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000");

        IO2.println("---");
        IO2.println("To array:    " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> new UnmodList<>(Alpha.parses(p, text))));
        IO2.println("Previous:    {:lowest 12.567, :highest 36.166, :diff 23.599, :average 13.600, :mid 12.906, :median 12.971, :total 27200.583}");
        IO2.println("Previous 2:  {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}");
        IO2.println("Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000");

        IO2.println("---");
        IO2.println("Iteration:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    for (var ignored : Alpha.parses(p, text)) ;
                }));
        IO2.println("Previous:    {:lowest 12.501, :highest 22.928, :diff 10.427, :average 13.509, :mid 12.809, :median 12.909, :total 27017.503}");
        IO2.println("Previous 2:  {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}");
        IO2.println("Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000");

        IO2.println("---");
        IO2.println("ArrayList:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    var l = new ArrayList<>(Alpha.parses(p, text));
                }));
        IO2.println("Previous:    {:lowest 12.480, :highest 26.343, :diff 13.862, :average 13.395, :mid 12.804, :median 12.850, :total 26790.117}");
        IO2.println("Previous 2:  {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}");
        IO2.println("Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000");

        IO2.println("---");
        IO2.println("Cnt parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text).size()));
        IO2.println("Previous:    {:lowest 12.539, :highest 23.903, :diff 11.364, :average 13.446, :mid 12.850, :median 12.895, :total 26892.826}");
        IO2.println("Previous 2:  {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}");
        IO2.println("Original:    -");

        IO2.println("Count of parses: " + Alpha.parses(p, text).size());

        IO2.println("\n----------------------------------\n---   Memory optimized tests   ---\n----------------------------------");
        IO2.println("First parse: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parse(p, text, Alpha.ParsingOptions.optMemory())));
        IO2.println("Std prev:    {:lowest 1.382, :highest 6.293, :diff 4.911, :average 1.520, :mid 1.443, :median 1.459, :total 30397.866}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 3.243, :highest 6.779, :diff 3.537, :average 3.300, :mid 3.284, :median 3.284, :sum 65992.776} // n=20000");

        IO2.println("---");
        IO2.println("All parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parses(p, text, Alpha.ParsingOptions.optMemory())));
        IO2.println("Std prev:    {:lowest 0.000, :highest 0.045, :diff 0.045, :average 0.001, :mid 0.001, :median 0.001, :total 15.080}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 3.238, :highest 6.909, :diff 3.671, :average 3.308, :mid 3.291, :median 3.291, :sum 66157.123} // n=20000");

        IO2.println("---");
        IO2.println("List parses: " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> new UnmodList<>(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()))));
        IO2.println("Std prev:    {:lowest 12.567, :highest 36.166, :diff 23.599, :average 13.600, :mid 12.906, :median 12.971, :total 27200.583}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.350, :highest 50.681, :diff 13.331, :average 38.118, :mid 37.937, :median 37.938, :sum 762350.525} // n=20000");

        IO2.println("---");
        IO2.println("Iteration:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    for (var ignored : Alpha.parses(p, text, Alpha.ParsingOptions.optMemory())) ;
                }));
        IO2.println("Std prev:    {:lowest 12.501, :highest 22.928, :diff 10.427, :average 13.509, :mid 12.809, :median 12.909, :total 27017.503}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.219, :highest 46.091, :diff 8.872, :average 38.005, :mid 37.828, :median 37.831, :sum 760101.547} // n=20000");

        IO2.println("---");
        IO2.println("ArrayList:   " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    var l = new ArrayList<ParseTree>(8192);
                    l.addAll(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()));
                }));
        IO2.println("Std prev:    {:lowest 12.480, :highest 26.343, :diff 13.862, :average 13.395, :mid 12.804, :median 12.850, :total 26790.117}");
        IO2.println("Previous:    -");
        IO2.println("Original:    {:lowest 37.305, :highest 47.066, :diff 9.761, :average 38.076, :mid 37.894, :median 37.898, :sum 761521.718} // n=20000");

        IO2.println("---");
        IO2.println("Cnt parses:  " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()).size()));
        IO2.println("Std prev:    {:lowest 12.539, :highest 23.903, :diff 11.364, :average 13.446, :mid 12.850, :median 12.895, :total 26892.826}");
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
