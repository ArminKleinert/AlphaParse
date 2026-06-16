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
                () -> Alpha.parse(p, text));

        System.out.println("\n----------------------------------\n--- Standard performance tests ---\n----------------------------------");
        System.out.println("Make parser without correctness check.");
        System.out.println("             " + TimeUtil.measureTimeMillis(2 * testNumMultiplierForSlowTests,
                () -> Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace().withCorrectnessCheck(false))));
        System.out.println("0_9_2:       {:lowest 33.018, :highest 45.705, :diff 12.687, :average 34.014, :mid 33.564, :median 33.600, :total 6802.700}");
        System.out.println("Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
        System.out.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

        System.out.println("---");
        var presetOpts = ParserCreationOptions.newWithStandardWhitespace();
        System.out.println("Make parser with correctness check.");
        System.out.println("             " + TimeUtil.measureTimeMillis(2 * testNumMultiplierForSlowTests,
                () -> Alpha.parser(c99GrammarText, presetOpts)));
        System.out.println("0_9_2:       {:lowest 33.076, :highest 45.583, :diff 12.507, :average 34.100, :mid 33.577, :median 33.629, :total 6819.932}");
        System.out.println("Raw types:   {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}");
        System.out.println("Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000");

        System.out.println("---");
        System.out.println("Extract the first parse.");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parse(p, text)));
        System.out.println("0_9_2:       {:lowest 1.220, :highest 5.083, :diff 3.863, :average 1.262, :mid 1.253, :median 1.253, :total 25238.140}");
        System.out.println("Raw types :  {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}");
        System.out.println("Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000");

        System.out.println("---");
        System.out.println("All parses as a lazy list. (Parse Forest)");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplier,
                () -> Alpha.parses(p, text)));
        System.out.println("0_9_2:       {:lowest 0.001, :highest 0.049, :diff 0.048, :average 0.001, :mid 0.001, :median 0.001, :total 25.709}");
        System.out.println("Raw types:   {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}");
        System.out.println("Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000");

        System.out.println("---");
        System.out.println("Get all parses (parse forest) and make an array.");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text).toArray()));
        System.out.println("0_9_2:       {:lowest 9.817, :highest 18.189, :diff 8.372, :average 10.104, :mid 10.032, :median 10.041, :total 20208.764}");
        System.out.println("Raw types:   {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}");
        System.out.println("Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000");

        System.out.println("---");
        System.out.println("Get all parses (parse forest) and iterate using a for-each loop.");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    for (var ignored : Alpha.parses(p, text)) ;
                }));
        System.out.println("0_9_2:       {:lowest 9.816, :highest 13.760, :diff 3.944, :average 10.087, :mid 10.026, :median 10.034, :total 20173.001}");
        System.out.println("Raw types:   {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}");
        System.out.println("Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000");

        System.out.println("---");
        System.out.println("Get all parses (parse forest) and turn it into an ArrayList.");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> {
                    var l = new ArrayList<>(Alpha.parses(p, text));
                }));
        System.out.println("0_9_2:       {:lowest 9.792, :highest 14.019, :diff 4.227, :average 10.085, :mid 10.032, :median 10.038, :total 20169.284}");
        System.out.println("Raw types:   {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}");
        System.out.println("Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000");

        System.out.println("---");
        System.out.println("Get all parses (parse forest) and count them.");
        System.out.println("             " + TimeUtil.measureTimeMillis(20 * testNumMultiplierForSlowTests,
                () -> Alpha.parses(p, text).size()));
        System.out.println("0_9_2:       {:lowest 9.815, :highest 15.561, :diff 5.747, :average 10.101, :mid 10.038, :median 10.045, :total 20202.445}");
        System.out.println("Raw types:   {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}");
        System.out.println("Original:    -");

        System.out.println("Count of parses: " + Alpha.parses(p, text).size());
    }
}
