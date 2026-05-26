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
 * Test(s) for the otc (Obfuscated Tiny C) grammar.
 * <p>
 * Grammar and tests from <a href="https://esolangs.org/wiki/Obfuscated_Tiny_C">esolangs.org/wiki/Obfuscated_Tiny_C</a>.
 */
class TestGrammarOTC {
    private Parser parser() {
        try {
            return Alpha.parser(
                    Files.readString(Path.of("grammars/otc.g")),
                    ParserCreationOptions.newWithStandardWhitespace()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verifySimpleProgram() {
        var text = "int i; int main (int argc, char** argv) { i = 11; while (--i) { printf(\"%d\", i); } }";
        Assertions.assertTrue(parser().parse(text).isSuccess());
    }
}