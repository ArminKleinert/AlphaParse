package alphaparse;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.FormatUtils;
import alphaparse.result.ParseTree;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.result.failure.failureReason.ParseFailureReasonRegex;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AlphaTest {

    @Test
    void parse() {
        var p = Alpha.parser("S : 'A'|'B'|'C'");
        var res = Alpha.parse(p, "A");
        Assertions.assertEquals(
                new ParseTree(Keyword.intern("S"), List.of("A")),
                res
        );
    }
    @Test
    void parsePartial() {
    }
    @Test
    void parseFailure() {
    }
    @Test
    void parseWithOptions() {
    }
    @Test
    void parseTotal() {
    }

    @Test
    void parses() {
    }

    @Test
    void parsesWithOptions() {
    }

    @Test
    void parsesTotalSuccess() {
    }
    @Test
    void parsesTotalFailure() {
    }


    @Test
    void parsesOrFailureSuccess() {
    }
    @Test
    void parsesOrFailureFailure() {
    }

    @Test
    void parserFrom() {
    }

    @Test
    void parserFromString() {
    }

    @Test
    void parserFromFile() {
    }

    @Test
    void parserFromStringWithOptions() {
    }


    @Test
    void parserFromFileWithOptions() {
    }

    @Test
    void parserFromGrammarWithOptions() {
    }
}