package alphaparse;

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
        Function<List<Object>, Object> transformFn =
                o -> o.stream().mapToInt(it->Integer.parseInt((String) it)).sum();
        Assertions.assertEquals(
                Integer.valueOf(6),
                Transform.transform(tree, Map.of(Sym.sym("S"), transformFn), (o)->(Integer)o));
    }
    @Test
    void testAdd2() {
        var p = Alpha.parser("""
                S : NUM (<'+'> NUM)*
                <NUM> : #'\\d+'
                """);
        var tree = p.parse("1+2+3").castToParseSuccess();
        Function<List<Object>, Object> transformFn =
                o -> o.stream().mapToInt(it->Integer.parseInt((String) it)).sum();
        Assertions.assertEquals(
                Integer.valueOf(6),
                Transform.transform(tree, Map.of(Sym.sym("S"), transformFn), (o)->(Integer)o));
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
                Integer.valueOf(6),
                Transform.transform(tree, transformMap, (o)->(Integer)o));
    }
}