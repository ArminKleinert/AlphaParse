package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.util.Transform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

class ParserAsFunctionTest {
    @Test
    void asMapper() {
        var p = Alpha.parser("S = #'[A-Z]'");
        var strings = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(it -> String.valueOf((char) it)).toList();
        Assertions.assertEquals(
                strings.stream().map(it -> Alpha.parse(p, it, ParsingOptions.getDefault())).toList(),
                strings.stream().map(p::parse).toList()
        );
    }

    @Test
    void asMapper2() {
        Map<Sym, Function<List<Object>, Object>> transformMap = Map.of(
                Sym.sym("S"), o -> o.stream()
                        .map(it -> (String) it)
                        .map(it -> it.charAt(0))
                        .map(Integer::valueOf)
                        .findFirst().orElseThrow()
        );
        var p = Alpha.parser("S = #'[A-Z]'");
        var strings = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(it -> String.valueOf((char) it)).toList();
        var ints = strings.stream().map(s -> (int) s.charAt(0)).toList();

        Assertions.assertEquals(
                ints,
                strings.stream().map(p::parse).map(it -> Transform.transform(it, transformMap)).toList()
        );
    }
}
