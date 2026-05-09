package alphaparse;

import alphaparse.parser.Combinator;
import alphaparse.parser.CombinatorFactory;
import alphaparse.parser.Grammar;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.regex.Pattern;

final class EbnfG {
    private static final @NotNull Combinator optWhitespace =
            CombinatorFactory.staticMakeNonTerminal(Keyword.intern("opt-whitespace")).enableHideTag();

    private static @NotNull Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    private static @NotNull Combinator makeCfgRulesRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule = combinatorFactory.catCombinator(
                        List.of(optWhitespace,
                                combinatorFactory.plusCombinator(
                                        combinatorFactory.makeNonTerminal(Keyword.intern("rule")))))
                .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgCommentRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("(*"),
                                combinatorFactory.makeNonTerminal(Keyword.intern("inside-comment")),
                                combinatorFactory.stringTerminal("*)"))).hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgInsideCommentRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Pattern insideComment = regexDoc("(?s)(?:(?!(?:\\(\\*|\\*\\))).)*", "Comment text");
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.createRegexTerminal(insideComment),
                                combinatorFactory.starCombinator(
                                        combinatorFactory.catCombinator(
                                                List.of(combinatorFactory.makeNonTerminal(Keyword.intern("comment")),
                                                        combinatorFactory.createRegexTerminal(insideComment))))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgOptWhitespaceRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Pattern ws = regexDoc("[,\\s]*", "optional whitespace");
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.createRegexTerminal(ws),
                                combinatorFactory.starCombinator(
                                        combinatorFactory.catCombinator(
                                                List.of(combinatorFactory.makeNonTerminal(Keyword.intern("comment")),
                                                        combinatorFactory.createRegexTerminal(ws))))));
        return rulesRule;
    }


    private static Combinator makeCfgEpsilonRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                        List.of(combinatorFactory.stringTerminal("Epsilon"),
                                combinatorFactory.stringTerminal("epsilon"),
                                combinatorFactory.stringTerminal("EPSILON"),
                                combinatorFactory.stringTerminal("eps"),
                                combinatorFactory.stringTerminal("ε")));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgFactorRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                                List.of(combinatorFactory.makeNonTerminal(Keyword.intern("nt")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("string")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("regexp")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("opt")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("star")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("plus")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("paren")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("hide")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("epsilon")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("rep")), // ABNF feature
                                        combinatorFactory.makeNonTerminal(Keyword.intern("num-val")) // ABNF feature
                                ))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgPlusRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorFactory.stringTerminal("+").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRuleSeparatorRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                        List.of(combinatorFactory.stringTerminal(":"),
                                combinatorFactory.stringTerminal(":="),
                                combinatorFactory.stringTerminal("::="),
                                combinatorFactory.stringTerminal("=")));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgParenRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("(").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorFactory.stringTerminal(")").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgHideRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorFactory.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgStringRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Pattern singleQuotedString =
                regexDoc("'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted string");
        final @NotNull Pattern doubleQuotedString =
                regexDoc("\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted string");
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                        List.of(combinatorFactory.createRegexTerminal(singleQuotedString),
                                combinatorFactory.createRegexTerminal(doubleQuotedString)));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRegexRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Pattern singleQuotedRegex =
                regexDoc("#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted regexp");
        final @NotNull Pattern doubleQuotedRegex =
                regexDoc("#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted regexp");
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                        List.of(combinatorFactory.createRegexTerminal(singleQuotedRegex),
                                combinatorFactory.createRegexTerminal(doubleQuotedRegex)));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRulesOrParserRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                                List.of(combinatorFactory.makeNonTerminal(Keyword.intern("rules")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord"))))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgNtRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(List.of(
                        combinatorFactory.negateRule(
                                combinatorFactory.makeNonTerminal(Keyword.intern("epsilon"))),
                        combinatorFactory.createRegexTerminal(
                                regexDoc("[^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./]+", "Non-terminal"))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRepRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(List.of(
                        combinatorFactory.choiceCombinator(
                                List.of(combinatorFactory.createRegexTerminal(regexDoc("\\-?[0-9]+", "NUM")),
                                        combinatorFactory.createRegexTerminal(regexDoc("\\-?[0-9]*\\*\\-?[0-9]+", "NUM")),
                                        combinatorFactory.createRegexTerminal(regexDoc("\\-?[0-9]+\\*\\-?[0-9]*", "NUM")))),
                        optWhitespace,
                        combinatorFactory.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgLookRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("&").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgNegRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("!").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgOneOrMoreRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRuleCurlies =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("{").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorFactory.stringTerminal("}").enableHideTag()));
        final @NotNull Combinator rulesRuleStar =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorFactory.stringTerminal("*").enableHideTag()));
        final @NotNull Combinator rule = combinatorFactory.choiceCombinator(
                List.of(rulesRuleCurlies, rulesRuleStar));
        return rule;
    }

    private static @NotNull Combinator makeCfgOptRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesBrackets =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("[").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorFactory.stringTerminal("]").enableHideTag()));
        final @NotNull Combinator rulesQuestionMark =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorFactory.stringTerminal("?").enableHideTag()));
        final @NotNull Combinator rule =
                combinatorFactory.choiceCombinator(List.of(rulesBrackets, rulesQuestionMark));
        return rule;
    }

    private static @NotNull Combinator makeCfgAltOrOrdRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                                List.of(combinatorFactory.makeNonTerminal(Keyword.intern("alt")),
                                        combinatorFactory.makeNonTerminal(Keyword.intern("ord"))))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgHideNtRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("nt")),
                                optWhitespace,
                                combinatorFactory.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRuleRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator optWs = combinatorFactory.makeNonTerminal(Keyword.intern("opt-whitespace"));
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.choiceCombinator(
                                        List.of(combinatorFactory.makeNonTerminal(Keyword.intern("nt")),
                                                combinatorFactory.makeNonTerminal(Keyword.intern("hide-nt")))),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("rule-separator")).enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                combinatorFactory.choiceCombinator(
                                                List.of(optWs,
                                                        combinatorFactory.catCombinator(
                                                                List.of(optWs,
                                                                        combinatorFactory.choiceCombinator(
                                                                                List.of(combinatorFactory.stringTerminal(";"),
                                                                                        combinatorFactory.stringTerminal("."))),
                                                                        optWs))))
                                        .enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgOrdRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal(Keyword.intern("cat")),
                                combinatorFactory.plusCombinator(
                                        combinatorFactory.catCombinator(
                                                List.of(optWhitespace,
                                                        combinatorFactory.stringTerminal("/").enableHideTag(),
                                                        optWhitespace,
                                                        combinatorFactory.makeNonTerminal(Keyword.intern("cat")))))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgAltRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator catNt = combinatorFactory.makeNonTerminal(Keyword.intern("cat"));
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(catNt,
                                combinatorFactory.starCombinator(combinatorFactory.catCombinator(
                                        List.of(
                                                optWhitespace,
                                                combinatorFactory.stringTerminal("|").enableHideTag(),
                                                optWhitespace,
                                                catNt)))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgCatRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator factorLookNeg = combinatorFactory.choiceCombinator(List.of(
                combinatorFactory.makeNonTerminal(Keyword.intern("factor")),
                combinatorFactory.makeNonTerminal(Keyword.intern("look")),
                combinatorFactory.makeNonTerminal(Keyword.intern("neg"))
        ));
        final @NotNull Combinator rulesRule =
                combinatorFactory.plusCombinator(
                        combinatorFactory.catCombinator(
                                List.of(optWhitespace,
                                        factorLookNeg,
                                        optWhitespace)));
        return rulesRule;
    }

    private static @NotNull Combinator makeABNFNumVal(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator connectingMinusTerminal = combinatorFactory.stringTerminal("-").enableHideTag();

        @NotNull Combinator regexCombinatorBin = combinatorFactory.createRegexTerminal(Pattern.compile("[01]+"));
        final Combinator binaryVal = combinatorFactory.catCombinator(List.of(
                combinatorFactory.stringTerminal("b"), // binary indicator
                regexCombinatorBin,
                combinatorFactory.optionalCombinator(combinatorFactory.catCombinator(
                        List.of(connectingMinusTerminal, regexCombinatorBin)))
        ));

        @NotNull Combinator regexCombinatorDec = combinatorFactory.createRegexTerminal(Pattern.compile("[0-9]+"));
        final Combinator decimalVal = combinatorFactory.catCombinator(List.of(
                combinatorFactory.stringTerminal("d"), // decimal indicator
                regexCombinatorDec,
                combinatorFactory.optionalCombinator(combinatorFactory.catCombinator(
                        List.of(connectingMinusTerminal, regexCombinatorDec)))
        ));

        @NotNull Combinator regexCombinatorHex = combinatorFactory.createRegexTerminal(Pattern.compile("[0-9a-fA-F]+"));
        final Combinator hexadecimalVal = combinatorFactory.catCombinator(List.of(
                combinatorFactory.stringTerminal("x"), // hexadecimal indicator
                regexCombinatorHex,
                combinatorFactory.optionalCombinator(combinatorFactory.catCombinator(
                        List.of(connectingMinusTerminal, regexCombinatorHex)))
        ));
        return combinatorFactory.catCombinator(List.of(
                combinatorFactory.stringTerminal("%").enableHideTag(),
                combinatorFactory.choiceCombinator(List.of(binaryVal, decimalVal, hexadecimalVal))));
    }

    static @NotNull Grammar makeCfg() {
        final @NotNull SequencedMap<Keyword, Combinator> grammarMap = new LinkedHashMap<>();
        final @NotNull CombinatorFactory cs = new CombinatorFactory(false);
        grammarMap.put(Keyword.intern("rules"), makeCfgRulesRhs(cs));
        grammarMap.put(Keyword.intern("comment"), makeCfgCommentRhs(cs));
        grammarMap.put(Keyword.intern("inside-comment"), makeCfgInsideCommentRhs(cs));
        grammarMap.put(Keyword.intern("opt-whitespace"), makeCfgOptWhitespaceRhs(cs));
        grammarMap.put(Keyword.intern("rule-separator"), makeCfgRuleSeparatorRhs(cs));
        grammarMap.put(Keyword.intern("rule"), makeCfgRuleRhs(cs));
        grammarMap.put(Keyword.intern("nt"), makeCfgNtRhs(cs));
        grammarMap.put(Keyword.intern("hide-nt"), makeCfgHideNtRhs(cs));
        grammarMap.put(Keyword.intern("alt-or-ord"), makeCfgAltOrOrdRhs(cs));
        grammarMap.put(Keyword.intern("alt"), makeCfgAltRhs(cs));
        grammarMap.put(Keyword.intern("ord"), makeCfgOrdRhs(cs)); // Technically ABNF, but should be included without it as a PAKRAT extension.
        grammarMap.put(Keyword.intern("paren"), makeCfgParenRhs(cs));
        grammarMap.put(Keyword.intern("hide"), makeCfgHideRhs(cs));
        grammarMap.put(Keyword.intern("cat"), makeCfgCatRhs(cs));
        grammarMap.put(Keyword.intern("rep"), makeCfgRepRhs(cs)); // ABNF
        grammarMap.put(Keyword.intern("string"), makeCfgStringRhs(cs));
        grammarMap.put(Keyword.intern("regexp"), makeCfgRegexRhs(cs));
        grammarMap.put(Keyword.intern("opt"), makeCfgOptRhs(cs));
        grammarMap.put(Keyword.intern("star"), makeCfgOneOrMoreRhs(cs));
        grammarMap.put(Keyword.intern("plus"), makeCfgPlusRhs(cs));
        grammarMap.put(Keyword.intern("look"), makeCfgLookRhs(cs));
        grammarMap.put(Keyword.intern("neg"), makeCfgNegRhs(cs));
        grammarMap.put(Keyword.intern("epsilon"), makeCfgEpsilonRhs(cs));
        grammarMap.put(Keyword.intern("factor"), makeCfgFactorRhs(cs));
        grammarMap.put(Keyword.intern("num-val"), makeABNFNumVal(cs)); // ABNF
        grammarMap.put(Keyword.intern("rules-or-parser"), makeCfgRulesOrParserRhs(cs));
        return new Grammar(grammarMap).applyStandardReductions();
    }
}
