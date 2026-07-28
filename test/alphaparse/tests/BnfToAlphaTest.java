package alphaparse.tests;

import alphaparse.Alpha;
import alphaparse.grammar.Grammar;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class BnfToAlphaTest {
private @NotNull Parser parser() {
    try {
        var opts = ParserCreationOptions.getDefault()
                .addAvailableRule(RulesAvailable.EXPLICIT_EOF)
                .addAvailableRule(RulesAvailable.ABNF_IDENTIFIERS)
                .withRuleDefinitionOps(Set.of("::="));
        return Alpha.parser(
                Files.readString(Path.of("testres/grammars/bnf.g")),
                opts
        );
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

@Test
void test() {
    System.out.println(parser().show());
    System.out.println(parser().parse("<digit> ::= <a>\n"));
}

public @NotNull Grammar bnfToParser(ParseTree pt) {
    throw new UnsupportedOperationException();
}
}
