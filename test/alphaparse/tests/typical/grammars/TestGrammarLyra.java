package alphaparse.tests.typical.grammars;

import alphaparse.Alpha;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test(s) for the Lyra grammar.
 * <p>
 * Grammar and tests from <a href="https://esolangs.org/wiki/Brainfuck#Examples">esolangs.org/wiki/Brainfuck</a>.
 */
class TestGrammarLyra {
    private Parser parser() {
        try {
            return Alpha.parser(
                    Files.readString(Path.of("grammars/lyra.g")),
                            ParserCreationOptions.newWithStandardWhitespace()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void helloWorld() {
        var text = "(println! \"Hello World!\")";
        System.out.println(parser().parse(text));
    }
}