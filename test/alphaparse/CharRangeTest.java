package alphaparse;

import alphaparse.parser.Combinator;
import alphaparse.parser.Grammar;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class CharRangeTest {
    @Test
    void outputForTemps() {
        var opts = Alpha.ParserCreationOptions.getDefault().withRedefinitionOption(Grammar.RedefinitionOption.CHOICE);
        var parser = Alpha.parser("S : 'A'\nS : 'A'", opts);
        System.out.println(parser);
        System.out.println(parser.parse("A"));
    }
}