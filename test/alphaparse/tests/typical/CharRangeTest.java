package alphaparse.tests.typical;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.result.PT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CharRangeTest {

    private final ParserCreationOptions options =
ParserCreationOptions.getDefault().addAvailableRule(RulesAvailable.VALUE_RANGE);

    @Test
    void unicodeCodepointSingleParse() {
        var parser = Alpha.parser("S = %x1F381", options);
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(PT.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF82").isFailure());
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointSingleParse2() {
        var parser = Alpha.parser("S = %x1F381-1F381", options);
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(PT.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF82").isFailure());
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointShortRangeParse() {
        var parser = Alpha.parser("S = %x1F381-1F382", options);
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(PT.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertEquals(PT.create("S", "\uD83C\uDF82"), parser.parse("\uD83C\uDF82"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
    }

    @Test
    void unicodeCodepointLongRangeParse() {
        var parser = Alpha.parser("S = %x41-1F382", options);
        Assertions.assertTrue(parser.parse("\uD83C\uDF83").isFailure());
        Assertions.assertEquals(PT.create("S", "A"), parser.parse("A"));
        Assertions.assertEquals(PT.create("S", "🎁"), parser.parse("🎁"));
        Assertions.assertEquals(PT.create("S", "\uD83C\uDF82"), parser.parse("\uD83C\uDF82"));
    }

    @Test
    void unicodeCodepointSmallCharParse() {
        var parser = Alpha.parser("S = %x41-5A", options);
        Assertions.assertTrue(parser.parse("\uD83C\uDF80").isFailure());
        Assertions.assertEquals(PT.create("S", "A"), parser.parse("A"));
        Assertions.assertEquals(PT.create("S", "Z"), parser.parse("Z"));
        Assertions.assertTrue(parser.parse("\uD83C\uDF81").isFailure());
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceRange() {
        var parserBin = Alpha.parser("S = %b1000001-1011010", options).grammar().getProduction(Sym.sym("S"));
        var parserDec = Alpha.parser("S = %d65-90", options).grammar().getProduction(Sym.sym("S"));
        var parserHex = Alpha.parser("S = %x41-5A", options).grammar().getProduction(Sym.sym("S"));
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceSingle() {
        var parserBin = Alpha.parser("S = %b1000001", options).grammar().getProduction(Sym.sym("S"));
        var parserDec = Alpha.parser("S = %d65", options).grammar().getProduction(Sym.sym("S"));
        var parserHex = Alpha.parser("S = %x41", options).grammar().getProduction(Sym.sym("S"));
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointBinaryDecimalHexEquivalenceRangeSingle() {
        var parserBin = Alpha.parser("S = %b1000001-1000001", options).grammar().getProduction(Sym.sym("S"));
        var parserDec = Alpha.parser("S = %d65-65", options).grammar().getProduction(Sym.sym("S"));
        var parserHex = Alpha.parser("S = %x41-41", options).grammar().getProduction(Sym.sym("S"));
        Assertions.assertEquals(parserBin, parserDec);
        Assertions.assertEquals(parserBin, parserHex);
    }

    @Test
    void unicodeCodepointRulePrecedence() {
        var text = "🎁🎁🎁";
        var tree = PT.create("S", "🎁", "🎁", "🎁");
        Assertions.assertEquals(
                tree,
                Alpha.parser("S = %x1F381-1F381 %x1F381-1F381 %x1F381-1F381", options).parse(text));
        Assertions.assertEquals(
                tree,
                Alpha.parser("S = %x1F381 %x1F381 %x1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = %x1F381+", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = %x1F381-1F381+", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = %x1F381*", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = %x1F381-1F381*", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = 1*3 %x1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = 1*3 %x1F381-1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = *3 %x1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = *3 %x1F381-1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = 1* %x1F381", options).parse(text));
        Assertions.assertEquals(tree, Alpha.parser("S = 1* %x1F381-1F381", options).parse(text));
    }
}