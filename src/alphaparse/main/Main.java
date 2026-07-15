package alphaparse.main;

import alphaparse.*;
import alphaparse.parser_options.*;
import alphaparse.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

final class Main {
    public static void main(String[] args) throws IOException {

        /*
        ----------------------------------
--- Standard performance tests ---
----------------------------------
Make parser without correctness check.
             {:lowest 33.126, :highest 43.380, :diff 10.254, :average 34.343, :mid 33.784, :median 33.892, :total 6868.687}
0_9_3:       {:lowest 33.107, :highest 44.635, :diff 11.528, :average 34.470, :mid 33.728, :median 33.757, :total 6893.975}
0_9_2:       {:lowest 33.408, :highest 38.596, :diff 5.188, :average 34.085, :mid 33.815, :median 33.875, :total 6817.098}
Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}
Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000
---
Make parser with correctness check.
             {:lowest 33.402, :highest 46.469, :diff 13.067, :average 34.534, :mid 33.871, :median 33.963, :total 6906.704}
0_9_3:       {:lowest 33.270, :highest 43.991, :diff 10.722, :average 34.389, :mid 33.643, :median 33.684, :total 6877.709}
0_9_2:       {:lowest 33.496, :highest 38.158, :diff 4.663, :average 34.121, :mid 33.880, :median 33.890, :total 6824.109}
Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}
Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000
---
Extract the first parse.
             {:lowest 1.251, :highest 3.771, :diff 2.520, :average 1.295, :mid 1.289, :median 1.289, :total 25908.282}
0_9_3:       {:lowest 1.231, :highest 4.927, :diff 3.696, :average 1.272, :mid 1.266, :median 1.266, :total 25443.634}
0_9_2:       {:lowest 1.233, :highest 4.808, :diff 3.575, :average 1.276, :mid 1.267, :median 1.267, :total 25524.641}
Raw types:   {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000
---
All parses as a lazy list. (Parse Forest)
             {:lowest 0.001, :highest 1.151, :diff 1.150, :average 0.002, :mid 0.001, :median 0.001, :total 30.323}
0_9_3:       {:lowest 0.001, :highest 1.030, :diff 1.029, :average 0.001, :mid 0.001, :median 0.001, :total 27.635}
0_9_2:       {:lowest 0.001, :highest 0.078, :diff 0.078, :average 0.001, :mid 0.001, :median 0.001, :total 24.908}
Raw types:   {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000
---
Get all parses (parse forest) and make an array.
             {:lowest 10.031, :highest 20.968, :diff 10.937, :average 10.364, :mid 10.283, :median 10.292, :total 20727.546}
0_9_3:       {:lowest 10.107, :highest 20.716, :diff 10.609, :average 10.394, :mid 10.328, :median 10.331, :total 20787.379}
0_9_2:       {:lowest 10.149, :highest 18.285, :diff 8.136, :average 10.384, :mid 10.336, :median 10.336, :total 20767.543}
Raw types:   {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000
---
Get all parses (parse forest) and iterate using a for-each loop.
             {:lowest 10.027, :highest 14.285, :diff 4.258, :average 10.331, :mid 10.263, :median 10.267, :total 20662.239}
0_9_3:       {:lowest 10.100, :highest 14.363, :diff 4.263, :average 10.378, :mid 10.329, :median 10.331, :total 20755.519}
0_9_2:       {:lowest 10.173, :highest 13.408, :diff 3.236, :average 10.399, :mid 10.359, :median 10.358, :total 20798.482}
Raw types:   {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}
Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000
---
Get all parses (parse forest) and turn it into an ArrayList.
             {:lowest 10.036, :highest 14.137, :diff 4.101, :average 10.362, :mid 10.290, :median 10.299, :total 20723.241}
0_9_3:       {:lowest 10.128, :highest 14.839, :diff 4.710, :average 10.375, :mid 10.306, :median 10.310, :total 20749.135}
0_9_2:       {:lowest 10.188, :highest 15.160, :diff 4.972, :average 10.404, :mid 10.368, :median 10.369, :total 20807.481}
Raw types:   {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}
Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000
---
Get all parses (parse forest) and count them.
             {:lowest 10.061, :highest 14.460, :diff 4.399, :average 10.333, :mid 10.268, :median 10.276, :total 20666.939}
0_9_3:       {:lowest 10.088, :highest 14.320, :diff 4.232, :average 10.376, :mid 10.323, :median 10.326, :total 20751.320}
0_9_2:       {:lowest 10.112, :highest 13.464, :diff 3.352, :average 10.427, :mid 10.342, :median 10.347, :total 20854.633}
Raw types:   {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}
Original:    -
Count of parses: 4096
         */
        {
            final boolean doRun = true;
            final int testNumMultiplierForSlowTests = 100;
            final int testNumMultiplier = 1000;
            final @NotNull String c99GrammarText = Files.readString(Path.of("testres/grammars/c99.g"));

            /**/
            if (!doRun) {
                return;
            }

            var text = "int a(int r){return r;}\n            int a(int r, int a){return r;}";
            var p = Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace());

            System.out.println("Preparing performance tests...");
            TimeUtil.measureTimeMillis(20,
                    () -> Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace()));
            TimeUtil.measureTimeMillis(2000,
                    () -> p.parse(text));

            System.out.println("\n----------------------------------\n--- Standard performance tests ---\n----------------------------------");
            System.out.println("Make parser without correctness check.");
            System.out.println("             " + TimeUtil.measureTimeMillis(2 * testNumMultiplierForSlowTests,
                    () -> Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace().withCorrectnessCheck(false))));
            System.out.println("0_9_3:       {:lowest 33.107, :highest 44.635, :diff 11.528, :average 34.470, :mid 33.728, :median 33.757, :total 6893.975}");
            System.out.println("0_9_2:       {:lowest 33.408, :highest 38.596, :diff 5.188, :average 34.085, :mid 33.815, :median 33.875, :total 6817.098}");
            System.out.println("Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
            System.out.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

            System.out.println("---");
            var presetOpts = ParserCreationOptions.newWithStandardWhitespace();
            System.out.println("Make parser with correctness check.");
            System.out.println("             " + TimeUtil.measureTimeMillis(2 * testNumMultiplierForSlowTests,
                    () -> Alpha.parser(c99GrammarText, presetOpts)));
            System.out.println("0_9_3:       {:lowest 33.270, :highest 43.991, :diff 10.722, :average 34.389, :mid 33.643, :median 33.684, :total 6877.709}");
            System.out.println("0_9_2:       {:lowest 33.496, :highest 38.158, :diff 4.663, :average 34.121, :mid 33.880, :median 33.890, :total 6824.109}");
            System.out.println("Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
            System.out.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

            System.out.println("---");
            System.out.println("Extract the first parse.");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                    () -> p.parse(text)));
            System.out.println("0_9_3:       {:lowest 1.231, :highest 4.927, :diff 3.696, :average 1.272, :mid 1.266, :median 1.266, :total 25443.634}");
            System.out.println("0_9_2:       {:lowest 1.233, :highest 4.808, :diff 3.575, :average 1.276, :mid 1.267, :median 1.267, :total 25524.641}");
            System.out.println("Raw types:   {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}");
            System.out.println("Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000");

            System.out.println("---");
            System.out.println("All parses as a lazy list. (Parse Forest)");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                    () -> p.parses(text)));
            System.out.println("0_9_3:       {:lowest 0.001, :highest 1.030, :diff 1.029, :average 0.001, :mid 0.001, :median 0.001, :total 27.635}");
            System.out.println("0_9_2:       {:lowest 0.001, :highest 0.078, :diff 0.078, :average 0.001, :mid 0.001, :median 0.001, :total 24.908}");
            System.out.println("Raw types:   {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}");
            System.out.println("Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000");

            System.out.println("---");
            System.out.println("Get all parses (parse forest) and make an array.");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                    () -> p.parses(text).toArray()));
            System.out.println("0_9_3:       {:lowest 10.107, :highest 20.716, :diff 10.609, :average 10.394, :mid 10.328, :median 10.331, :total 20787.379}");
            System.out.println("0_9_2:       {:lowest 10.149, :highest 18.285, :diff 8.136, :average 10.384, :mid 10.336, :median 10.336, :total 20767.543}");
            System.out.println("Raw types:   {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}");
            System.out.println("Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000");

            System.out.println("---");
            System.out.println("Get all parses (parse forest) and iterate using a for-each loop.");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                    () -> {
                        for (var ignored : p.parses(text)) ;
                    }));
            System.out.println("0_9_3:       {:lowest 10.100, :highest 14.363, :diff 4.263, :average 10.378, :mid 10.329, :median 10.331, :total 20755.519}");
            System.out.println("0_9_2:       {:lowest 10.173, :highest 13.408, :diff 3.236, :average 10.399, :mid 10.359, :median 10.358, :total 20798.482}");
            System.out.println("Raw types:   {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}");
            System.out.println("Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000");

            System.out.println("---");
            System.out.println("Get all parses (parse forest) and turn it into an ArrayList.");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                    () -> {
                        var l = new ArrayList<>(Alpha.parses(p, text, ParsingOptions.getDefault()));
                    }));
            System.out.println("0_9_3:       {:lowest 10.128, :highest 14.839, :diff 4.710, :average 10.375, :mid 10.306, :median 10.310, :total 20749.135}");
            System.out.println("0_9_2:       {:lowest 10.188, :highest 15.160, :diff 4.972, :average 10.404, :mid 10.368, :median 10.369, :total 20807.481}");
            System.out.println("Raw types:   {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}");
            System.out.println("Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000");

            System.out.println("---");
            System.out.println("Get all parses (parse forest) and count them.");
            System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                    () -> p.parses(text).size()));
            System.out.println("0_9_3:       {:lowest 10.088, :highest 14.320, :diff 4.232, :average 10.376, :mid 10.323, :median 10.326, :total 20751.320}");
            System.out.println("0_9_2:       {:lowest 10.112, :highest 13.464, :diff 3.352, :average 10.427, :mid 10.342, :median 10.347, :total 20854.633}");
            System.out.println("Raw types:   {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}");
            System.out.println("Original:    -");

            System.out.println("Count of parses: " + p.parses(text).size());
        }

        {
            final int max = 23;
            System.out.println("\n----------------------------------\n---   Number of parses tests   ---\n----------------------------------");
            System.gc();
            var grammar = "S : (A | B)+\nA : 'a' | 'b'\nB : 'b' | 'a'";
            var p = Alpha.parser(grammar);
            var sb = new StringBuilder();
            for (int n = 0; n < max; n++) {
                int num = Alpha.parses(p, sb.toString(), ParsingOptions.getDefault()).size();
                System.out.println("Parses for " + n + ": " + num + " (Correct? "
                        + (num == 0 || num == 1 << n)
                        + ")");
                sb.append("a");
            }
        }
    }
}
