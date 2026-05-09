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
            CombinatorFactory.staticMakeNonTerminal("opt-whitespace").enableHideTag();

    private static @NotNull Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    private static @NotNull Combinator makeCfgRulesRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule = combinatorFactory.catCombinator(
                        List.of(optWhitespace,
                                combinatorFactory.plusCombinator(
                                        combinatorFactory.makeNonTerminal("rule"))))
                .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgCommentRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("(*"),
                                combinatorFactory.makeNonTerminal("inside-comment"),
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
                                                List.of(combinatorFactory.makeNonTerminal("comment"),
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
                                                List.of(combinatorFactory.makeNonTerminal("comment"),
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
                                List.of(combinatorFactory.makeNonTerminal("nt"),
                                        combinatorFactory.makeNonTerminal("string"),
                                        combinatorFactory.makeNonTerminal("regexp"),
                                        combinatorFactory.makeNonTerminal("opt"),
                                        combinatorFactory.makeNonTerminal("star"),
                                        combinatorFactory.makeNonTerminal("plus"),
                                        combinatorFactory.makeNonTerminal("paren"),
                                        combinatorFactory.makeNonTerminal("hide"),
                                        combinatorFactory.makeNonTerminal("epsilon"),
                                        combinatorFactory.makeNonTerminal("rep"), // ABNF feature
                                        combinatorFactory.makeNonTerminal("num-val") // ABNF feature
                                ))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgPlusRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal("factor"),
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
                                combinatorFactory.makeNonTerminal("alt-or-ord"),
                                optWhitespace,
                                combinatorFactory.stringTerminal(")").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgHideRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("alt-or-ord"),
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
                                List.of(combinatorFactory.makeNonTerminal("rules"),
                                        combinatorFactory.makeNonTerminal("alt-or-ord")))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgNtRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(List.of(
                        combinatorFactory.negateRule(
                                combinatorFactory.makeNonTerminal("epsilon")),
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
                        combinatorFactory.makeNonTerminal("factor")));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgLookRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("&").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("factor")));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgNegRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("!").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("factor")));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgOneOrMoreRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRuleCurlies =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("{").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("alt-or-ord"),
                                optWhitespace,
                                combinatorFactory.stringTerminal("}").enableHideTag()));
        final @NotNull Combinator rulesRuleStar =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal("factor"),
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
                                combinatorFactory.makeNonTerminal("alt-or-ord"),
                                optWhitespace,
                                combinatorFactory.stringTerminal("]").enableHideTag()));
        final @NotNull Combinator rulesQuestionMark =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.makeNonTerminal("factor"),
                                optWhitespace,
                                combinatorFactory.stringTerminal("?").enableHideTag()));
        final @NotNull Combinator rule =
                combinatorFactory.choiceCombinator(List.of(rulesBrackets, rulesQuestionMark));
        return rule;
    }

    private static @NotNull Combinator makeCfgAltOrOrdRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.choiceCombinator(
                                List.of(combinatorFactory.makeNonTerminal("alt"),
                                        combinatorFactory.makeNonTerminal("ord")))
                        .hideTag();
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgHideNtRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("nt"),
                                optWhitespace,
                                combinatorFactory.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgRuleRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator optWs = combinatorFactory.makeNonTerminal("opt-whitespace");
        final @NotNull Combinator rulesRule =
                combinatorFactory.catCombinator(
                        List.of(combinatorFactory.choiceCombinator(
                                        List.of(combinatorFactory.makeNonTerminal("nt"),
                                                combinatorFactory.makeNonTerminal("hide-nt"))),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("rule-separator").enableHideTag(),
                                optWhitespace,
                                combinatorFactory.makeNonTerminal("alt-or-ord"),
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
                        List.of(combinatorFactory.makeNonTerminal("cat"),
                                combinatorFactory.plusCombinator(
                                        combinatorFactory.catCombinator(
                                                List.of(optWhitespace,
                                                        combinatorFactory.stringTerminal("/").enableHideTag(),
                                                        optWhitespace,
                                                        combinatorFactory.makeNonTerminal("cat"))))));
        return rulesRule;
    }

    private static @NotNull Combinator makeCfgAltRhs(final @NotNull CombinatorFactory combinatorFactory) {
        final @NotNull Combinator catNt = combinatorFactory.makeNonTerminal("cat");
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
                combinatorFactory.makeNonTerminal("factor"),
                combinatorFactory.makeNonTerminal("look"),
                combinatorFactory.makeNonTerminal("neg")
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
        final @NotNull SequencedMap<String, Combinator> grammarMap = new LinkedHashMap<>();
        final @NotNull CombinatorFactory cs = new CombinatorFactory(false);
        grammarMap.put(("rules"), makeCfgRulesRhs(cs));
        grammarMap.put(("comment"), makeCfgCommentRhs(cs));
        grammarMap.put(("inside-comment"), makeCfgInsideCommentRhs(cs));
        grammarMap.put(("opt-whitespace"), makeCfgOptWhitespaceRhs(cs));
        grammarMap.put(("rule-separator"), makeCfgRuleSeparatorRhs(cs));
        grammarMap.put(("rule"), makeCfgRuleRhs(cs));
        grammarMap.put(("nt"), makeCfgNtRhs(cs));
        grammarMap.put(("hide-nt"), makeCfgHideNtRhs(cs));
        grammarMap.put(("alt-or-ord"), makeCfgAltOrOrdRhs(cs));
        grammarMap.put(("alt"), makeCfgAltRhs(cs));
        grammarMap.put(("ord"), makeCfgOrdRhs(cs)); // Technically ABNF, but should be included without it as a PAKRAT extension.
        grammarMap.put(("paren"), makeCfgParenRhs(cs));
        grammarMap.put(("hide"), makeCfgHideRhs(cs));
        grammarMap.put(("cat"), makeCfgCatRhs(cs));
        grammarMap.put(("rep"), makeCfgRepRhs(cs)); // ABNF
        grammarMap.put(("string"), makeCfgStringRhs(cs));
        grammarMap.put(("regexp"), makeCfgRegexRhs(cs));
        grammarMap.put(("opt"), makeCfgOptRhs(cs));
        grammarMap.put(("star"), makeCfgOneOrMoreRhs(cs));
        grammarMap.put(("plus"), makeCfgPlusRhs(cs));
        grammarMap.put(("look"), makeCfgLookRhs(cs));
        grammarMap.put(("neg"), makeCfgNegRhs(cs));
        grammarMap.put(("epsilon"), makeCfgEpsilonRhs(cs));
        grammarMap.put(("factor"), makeCfgFactorRhs(cs));
        grammarMap.put(("num-val"), makeABNFNumVal(cs)); // ABNF
        grammarMap.put(("rules-or-parser"), makeCfgRulesOrParserRhs(cs));
        return new Grammar(grammarMap).applyStandardReductions();
    }
}
