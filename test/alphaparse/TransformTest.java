package alphaparse;

import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.ParseTree;
import alphaparse.util.Transform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

class TransformTest {
    @Test
    void testAdd1() {
        var tree = ParseTree.create("S", "1", "2", "3");
        var transformFn = new Function<List<Object>, Object>() {
            @Override
            public Object apply(List<Object> o) {
                return o.stream().mapToInt(it->Integer.parseInt((String) it)).sum();
            }
        };
        Assertions.assertEquals(
                6,
                Transform.transform(tree, Map.of(Sym.sym("S"), transformFn)));
    }
    @Test
    void testAdd2() {
        var p = Alpha.parser("""
                S : NUM (<'+'> NUM)*
                <NUM> : #'\\d+'
                """);
        var tree = p.parse("1+2+3").castToParseSuccess();
        var transformFn = new Function<List<Object>, Object>() {
            @Override
            public Object apply(List<Object> o) {
                return o.stream().mapToInt(it->Integer.parseInt((String) it)).sum();
            }
        };
        Assertions.assertEquals(
                6,
                Transform.transform(tree, Map.of(Sym.sym("S"), transformFn)));
    }
    @Test
    void testAdd3() {
        var p = Alpha.parser("""
                S : NUM ('+' NUM)*
                NUM : #'\\d+'
                """);
        var tree = p.parse("1+2+3").castToParseSuccess();
        Map<Sym,Function<List<Object>, Object>> transformMap = Map.of(
                Sym.sym("S"), o -> o.stream()
                        .filter(it -> !it.equals("+"))
                        .map(it -> (String) it)
                        .mapToInt(Integer::parseInt)
                        .sum(),
                Sym.sym("NUM"), List::getFirst
        );
        Assertions.assertEquals(
                6,
                Transform.transform(tree, transformMap));
    }
}