package alphaparse.tests.typical.grammars;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParsingOptions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test(s) for the PlasticLang grammar.
 * <p>
 * Grammar and tests from <a href="https://github.com/rogeralsing/PlasticLang/blob/master/Plastic/sample.pla">https://github.com/rogeralsing/PlasticLang</a>.
 */
class TestGrammarPlastic {
    private @NotNull Parser parser() {
        try {
            return Alpha.parser(
                    Files.readString(Path.of("testres/grammars/plastic.g"))
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void helloWorld() {
        var text = """
                repeat := func (times, @body) {
                    while(times >= 0) {
                        body()
                        times--
                    }
                }
                """;
        System.out.println(parser().parse(text, ParsingOptions.getDefault()));
    }

    @Test
    void helloWorld1() {
        var text = """
                "\\u8888"
                """;
        System.out.println(text);
        System.out.println(parser().parse(text));
    }
}