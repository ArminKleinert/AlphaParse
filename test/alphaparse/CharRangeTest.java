package alphaparse;

import alphaparse.result.ParseTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class CharRangeTest {
    @Test
    void unicodeCodepointSingleParse() {
        var parser = Alpha.parser("S : %x1F381");
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF82").isFailure());
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointSingleParse2() {
        var parser = Alpha.parser("S : %x1F381-1F381");
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF82").isFailure());
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointShortRangeParse() {
        var parser = Alpha.parser("S : %x1F381-1F382");
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertEquals(ParseTree.create("S", "\uD83C\uDF82"), parser.parse("\uD83C\uDF82"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointLongRangeParse() {
        var parser = Alpha.parser("S : %x41-1F382");
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A"), parser.parse("A"));
        Assertions.assertEquals(ParseTree.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertEquals(ParseTree.create("S", "\uD83C\uDF82"), parser.parse("\uD83C\uDF82"));
    }

    @Test
    void unicodeCodepointSmallCharParse() {
        var parser = Alpha.parser("S : %x41-5A");
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(ParseTree.create("S", "A"), parser.parse("A"));
        Assertions.assertEquals(ParseTree.create("S", "Z"), parser.parse("Z"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF81").isFailure());
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceRange() {
        var parserBin = Alpha.parser("S : %b1000001-1011010").grammar().getProduction("S");
        var parserDec = Alpha.parser("S : %d65-90").grammar().getProduction("S");
        var parserHex = Alpha.parser("S : %x41-5A").grammar().getProduction("S");
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceSingle() {
        var parserBin = Alpha.parser("S : %b1000001").grammar().getProduction("S");
        var parserDec = Alpha.parser("S : %d65").grammar().getProduction("S");
        var parserHex = Alpha.parser("S : %x41").grammar().getProduction("S");
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceRangeSingle() {
        var parserBin = Alpha.parser("S : %b1000001-1000001").grammar().getProduction("S");
        var parserDec = Alpha.parser("S : %d65-65").grammar().getProduction("S");
        var parserHex = Alpha.parser("S : %x41-41").grammar().getProduction("S");
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointCombinatorPrecedence() {
        var text = "🎁🎁🎁";
        var tree = ParseTree.create("S", "🎁", "🎁", "🎁");
        Assertions.assertEquals(
                tree,
                Alpha.parser("S : %x1F381-1F381 %x1F381-1F381 %x1F381-1F381").parse(text));
        Assertions.assertEquals(
                tree,
                Alpha.parser("S : %x1F381 %x1F381 %x1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : %x1F381+").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : %x1F381-1F381+").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : %x1F381*").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : %x1F381-1F381*").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : 1*3 %x1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : 1*3 %x1F381-1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : *3 %x1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : *3 %x1F381-1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : 1* %x1F381").parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S : 1* %x1F381-1F381").parse(text));
    }
}