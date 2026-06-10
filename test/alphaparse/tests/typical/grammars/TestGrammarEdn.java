package alphaparse.tests.typical.grammars;

import alphaparse.Alpha;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class TestGrammarEdn {
    private @NotNull Parser parser() {
        try {
            return Alpha.parser(
                    Files.readString(Path.of("testres/grammars/edn.g")),
                    ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.EXPLICIT_EOF)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private List<Object> lower(AlphaParseResult pr) {
//        if (!(pr instanceof ParseTree)) throw new IllegalArgumentException("Needed parse tree, got "+pr);
//        while (((ParseTree) pr).getContent().stream().anyMatch(it->it.content() instanceof ParseTree)) {
//        }
//    }

    private @NotNull List<String> symbolList() {
        return List.of("a", "+", "*", "<a>", "a:", "+:", "*#", "<a>#");
    }

    private @NotNull List<String> symbolListWithNS() {
        return symbolList().stream().map(it -> it + '/' + it).toList();
    }

    private @NotNull List<String> keywordList() {
        return symbolList().stream().map(it -> ':' + it).toList();
    }

    private @NotNull List<String> keywordListWithNS() {
        return symbolListWithNS().stream().map(it -> ':' + it).toList();
    }

    @Test
    void verifyWithFile() throws IOException {
        var parser = parser();
        var text = Files.readString(Path.of("testres/other/edntest.edn"));
        System.out.println(parser.parse(text, ParsingOptions.getDefault()));
        //Assertions.assertTrue(parser.parse(text).isSuccess());
    }

    @Test
    void verifyLineComment() {
        var parser = parser();
        var text = "1 ;abf\n 2";
        Assertions.assertTrue(parser.parse(text).isSuccess());
    }

    @Test
    void verifyLineComment2() {
        var parser = parser();
        var text = "3 #_1  ; abf\n";
        Assertions.assertTrue(parser.parse(text).isSuccess());
    }

    @Test
    void verifyLineComment3() {
        var parser = parser();
        var text = "1#_2 #_4 ; #_abf\n3";
        Assertions.assertTrue(parser.parse(text).isSuccess());
    }

    @Test
    void verifySymbol() {
        var parser = parser();
        for (String s : symbolList()) {
            Assertions.assertTrue(parser.parse(s).isSuccess());
        }
    }

    @Test
    void verifySymbolWithNS() {
        var parser = parser();
        for (String s : symbolListWithNS()) {
            Assertions.assertTrue(parser.parse(s).isSuccess());
        }
    }

    @Test
    void verifyKeyword() {
        var parser = parser();
        for (String s : keywordList()) {
            Assertions.assertTrue(parser.parse(s).isSuccess());
        }
    }

    @Test
    void verifyKeywordWithNS() {
        var parser = parser();
        for (String s : keywordListWithNS()) {
            Assertions.assertTrue(parser.parse(s).isSuccess());
        }
    }

    @Test
    void verifyList() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("()").isSuccess());
        Assertions.assertTrue(parser.parse("(12)").isSuccess());
        Assertions.assertTrue(parser.parse("(1 2)").isSuccess());
        Assertions.assertTrue(parser.parse("( 1 2 )").isSuccess());
        Assertions.assertTrue(parser.parse("( (1) () )").isSuccess());
    }

    @Test
    void verifyVector() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("[]").isSuccess());
        Assertions.assertTrue(parser.parse("[12]").isSuccess());
        Assertions.assertTrue(parser.parse("[1 2]").isSuccess());
        Assertions.assertTrue(parser.parse("[ 1 2 ]").isSuccess());
        Assertions.assertTrue(parser.parse("[ (1) [] ]").isSuccess());
    }

    @Test
    void verifyMap() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("{}").isSuccess());
        Assertions.assertTrue(parser.parse("{12}").isFailure());
        Assertions.assertTrue(parser.parse("{1 2}").isSuccess());
        Assertions.assertTrue(parser.parse("{ (1) []}").isSuccess());
    }

    @Test
    void verifyTypeDispatch() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("#m/n {}").isSuccess());
        Assertions.assertTrue(parser.parse("#m/n {:name \"abc\"}").isSuccess());
    }

    @Test
    void verifyInts() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("0").isSuccess());
        Assertions.assertTrue(parser.parse("+0").isSuccess());
        Assertions.assertTrue(parser.parse("-0").isSuccess());
        Assertions.assertTrue(parser.parse("1").isSuccess());
        Assertions.assertTrue(parser.parse("+1").isSuccess());
        Assertions.assertTrue(parser.parse("-1").isSuccess());
    }

    @Test
    void verifyFloat() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("1.0").isSuccess());
        Assertions.assertTrue(parser.parse("+1.0").isSuccess());
        Assertions.assertTrue(parser.parse("-1.0").isSuccess());
    }

    @Test
    void verifyFloatExpFormat() {
        var parser = parser();
        Assertions.assertTrue(parser.parse("1e+1").isSuccess());
        Assertions.assertTrue(parser.parse("1e-1").isSuccess());
        Assertions.assertTrue(parser.parse("1E+1").isSuccess());
        Assertions.assertTrue(parser.parse("1E-1").isSuccess());
        Assertions.assertTrue(parser.parse("1.0e+1").isSuccess());
        Assertions.assertTrue(parser.parse("1.0e-1").isSuccess());
        Assertions.assertTrue(parser.parse("1.0E+1").isSuccess());
        Assertions.assertTrue(parser.parse("1.0E-1").isSuccess());

        Assertions.assertTrue(parser.parse("+1e+1").isSuccess());
        Assertions.assertTrue(parser.parse("+1e-1").isSuccess());
        Assertions.assertTrue(parser.parse("+1E+1").isSuccess());
        Assertions.assertTrue(parser.parse("+1E-1").isSuccess());
        Assertions.assertTrue(parser.parse("+1.0e+1").isSuccess());
        Assertions.assertTrue(parser.parse("+1.0e-1").isSuccess());
        Assertions.assertTrue(parser.parse("+1.0E+1").isSuccess());
        Assertions.assertTrue(parser.parse("+1.0E-1").isSuccess());

        Assertions.assertTrue(parser.parse("-1e+1").isSuccess());
        Assertions.assertTrue(parser.parse("-1e-1").isSuccess());
        Assertions.assertTrue(parser.parse("-1E+1").isSuccess());
        Assertions.assertTrue(parser.parse("-1E-1").isSuccess());
        Assertions.assertTrue(parser.parse("-1.0e+1").isSuccess());
        Assertions.assertTrue(parser.parse("-1.0e-1").isSuccess());
        Assertions.assertTrue(parser.parse("-1.0E+1").isSuccess());
        Assertions.assertTrue(parser.parse("-1.0E-1").isSuccess());
    }
}
