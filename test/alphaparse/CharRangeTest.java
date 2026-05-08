package alphaparse;

import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CharRangeTest {
    @Test
    void outputForTemps() {
        var parser = Alpha.parser("S : %d65-66");
        System.out.println(parser);
        System.out.println(parser.parse("A"));
    }
}