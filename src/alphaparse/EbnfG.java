package alphaparse;

import alphaparse.parser.combinator.Combinator;
import alphaparse.parser.Grammar;
import alphaparse.reduction.Reduction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public final class EbnfG {
    private static final @NotNull Combinator optWhitespace =
            new CombinatorsSource().makeNonTerminal(Keyword.intern("opt-whitespace")).enableHideTag();

    @NotNull
    private static Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    @NotNull
    private static Combinator makeCfgRulesRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule = combinatorsSource.catCombinator(
                        List.of(optWhitespace,
                                combinatorsSource.plusCombinator(
                                        combinatorsSource.makeNonTerminal(Keyword.intern("rule")))))
                .hideTag();
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgCommentRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("(*"),
                                combinatorsSource.makeNonTerminal(Keyword.intern("inside-comment")),
                                combinatorsSource.stringTerminal("*)"))).hideTag();
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgInsideCommentRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Pattern insideComment = regexDoc("(?s)(?:(?!(?:\\(\\*|\\*\\))).)*", "Comment text");
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.createRegexTerminal(insideComment),
                                combinatorsSource.starCombinator(
                                        combinatorsSource.catCombinator(
                                                List.of(combinatorsSource.makeNonTerminal(Keyword.intern("comment")),
                                                        combinatorsSource.createRegexTerminal(insideComment))))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgOptWhitespaceRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Pattern ws = regexDoc("[,\\s]*", "optional whitespace");
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.createRegexTerminal(ws),
                                combinatorsSource.starCombinator(
                                        combinatorsSource.catCombinator(
                                                List.of(combinatorsSource.makeNonTerminal(Keyword.intern("comment")),
                                                        combinatorsSource.createRegexTerminal(ws))))));
        return rulesRule;
    }


    private static Combinator makeCfgEpsilonRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                        List.of(combinatorsSource.stringTerminal("Epsilon"),
                                combinatorsSource.stringTerminal("epsilon"),
                                combinatorsSource.stringTerminal("EPSILON"),
                                combinatorsSource.stringTerminal("eps"),
                                combinatorsSource.stringTerminal("ε")));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgFactorRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                                List.of(combinatorsSource.makeNonTerminal(Keyword.intern("nt")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("string")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("regexp")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("opt")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("star")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("plus")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("paren")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("hide")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("epsilon")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("rep"))))
                        .hideTag();
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgPlusRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorsSource.stringTerminal("+").enableHideTag()));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgRuleSeparatorRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                        List.of(combinatorsSource.stringTerminal(":"),
                                combinatorsSource.stringTerminal(":="),
                                combinatorsSource.stringTerminal("::="),
                                combinatorsSource.stringTerminal("=")));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgParenRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("(").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorsSource.stringTerminal(")").enableHideTag()));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgHideRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorsSource.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgStringRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Pattern singleQuotedString =
                regexDoc("'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted string");
        final @NotNull Pattern doubleQuotedString =
                regexDoc("\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted string");
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                        List.of(combinatorsSource.createRegexTerminal(singleQuotedString),
                                combinatorsSource.createRegexTerminal(doubleQuotedString)));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgRegexRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Pattern singleQuotedRegex =
                regexDoc("#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted regexp");
        final @NotNull Pattern doubleQuotedRegex =
                regexDoc("#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted regexp");
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                        List.of(combinatorsSource.createRegexTerminal(singleQuotedRegex),
                                combinatorsSource.createRegexTerminal(doubleQuotedRegex)));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgRulesOrParserRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                                List.of(combinatorsSource.makeNonTerminal(Keyword.intern("rules")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord"))))
                        .hideTag();
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgNtRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(List.of(
                        combinatorsSource.negateRule(
                                combinatorsSource.makeNonTerminal(Keyword.intern("epsilon"))),
                        combinatorsSource.createRegexTerminal(
                                regexDoc("[^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./]+", "Non-terminal"))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgRepRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(List.of(
                        combinatorsSource.alternationCombinator(
                                List.of(combinatorsSource.createRegexTerminal(regexDoc("[0-9]+", "NUM")),
                                        combinatorsSource.createRegexTerminal(regexDoc("[0-9]*\\*[0-9]+", "NUM")),
                                        combinatorsSource.createRegexTerminal(regexDoc("[0-9]+\\*[0-9]*", "NUM")))),
                        optWhitespace,
                        combinatorsSource.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgLookRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("&").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgNegRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("!").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("factor"))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgOneOrMoreRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRuleCurlies =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("{").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorsSource.stringTerminal("}").enableHideTag()));
        final @NotNull Combinator rulesRuleStar =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorsSource.stringTerminal("*").enableHideTag()));
        final @NotNull Combinator rule = combinatorsSource.alternationCombinator(
                List.of(rulesRuleCurlies, rulesRuleStar));
        return rule;
    }

    @NotNull
    private static Combinator makeCfgOptRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesBrackets =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("[").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                optWhitespace,
                                combinatorsSource.stringTerminal("]").enableHideTag()));
        final @NotNull Combinator rulesQuestionMark =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.makeNonTerminal(Keyword.intern("factor")),
                                optWhitespace,
                                combinatorsSource.stringTerminal("?").enableHideTag()));
        final @NotNull Combinator rule = combinatorsSource.alternationCombinator(List.of(rulesBrackets, rulesQuestionMark));
        return rule;
    }

    @NotNull
    private static Combinator makeCfgAltOrOrdRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.alternationCombinator(
                                List.of(combinatorsSource.makeNonTerminal(Keyword.intern("alt")),
                                        combinatorsSource.makeNonTerminal(Keyword.intern("ord"))))
                        .hideTag();
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgHideNtRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("nt")),
                                optWhitespace,
                                combinatorsSource.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgRuleRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator optWs = combinatorsSource.makeNonTerminal(Keyword.intern("opt-whitespace"));
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.alternationCombinator(
                                        List.of(combinatorsSource.makeNonTerminal(Keyword.intern("nt")),
                                                combinatorsSource.makeNonTerminal(Keyword.intern("hide-nt")))),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("rule-separator")).enableHideTag(),
                                optWhitespace,
                                combinatorsSource.makeNonTerminal(Keyword.intern("alt-or-ord")),
                                combinatorsSource.alternationCombinator(
                                                List.of(optWs,
                                                        combinatorsSource.catCombinator(
                                                                List.of(optWs,
                                                                        combinatorsSource.alternationCombinator(
                                                                                List.of(combinatorsSource.stringTerminal(";"),
                                                                                        combinatorsSource.stringTerminal("."))),
                                                                        optWs))))
                                        .enableHideTag()));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgOrdRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(combinatorsSource.makeNonTerminal(Keyword.intern("cat")),
                                combinatorsSource.plusCombinator(
                                        combinatorsSource.catCombinator(
                                                List.of(optWhitespace,
                                                        combinatorsSource.stringTerminal("/").enableHideTag(),
                                                        optWhitespace,
                                                        combinatorsSource.makeNonTerminal(Keyword.intern("cat")))))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgAltRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator catNt = combinatorsSource.makeNonTerminal(Keyword.intern("cat"));
        final @NotNull Combinator rulesRule =
                combinatorsSource.catCombinator(
                        List.of(catNt,
                                combinatorsSource.starCombinator(combinatorsSource.catCombinator(
                                        List.of(
                                                optWhitespace,
                                                combinatorsSource.stringTerminal("|").enableHideTag(),
                                                optWhitespace,
                                                catNt)))));
        return rulesRule;
    }

    @NotNull
    private static Combinator makeCfgCatRhs(final @NotNull CombinatorsSource combinatorsSource) {
        final @NotNull Combinator factorLookNeg = combinatorsSource.alternationCombinator(List.of(
                combinatorsSource.makeNonTerminal(Keyword.intern("factor")),
                combinatorsSource.makeNonTerminal(Keyword.intern("look")),
                combinatorsSource.makeNonTerminal(Keyword.intern("neg"))));
        final @NotNull Combinator rulesRule =
                combinatorsSource.plusCombinator(
                        combinatorsSource.catCombinator(
                                List.of(optWhitespace,
                                        factorLookNeg,
                                        optWhitespace)));
        return rulesRule;
    }

    static @NotNull Grammar makeCfg() {
        final @NotNull HashMap<Keyword, Combinator> grammarMap = new HashMap<>();
        final @NotNull CombinatorsSource cs = new CombinatorsSource();
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
        grammarMap.put(Keyword.intern("ord"), makeCfgOrdRhs(cs));
        grammarMap.put(Keyword.intern("paren"), makeCfgParenRhs(cs));
        grammarMap.put(Keyword.intern("hide"), makeCfgHideRhs(cs));
        grammarMap.put(Keyword.intern("cat"), makeCfgCatRhs(cs));
        grammarMap.put(Keyword.intern("rep"), makeCfgRepRhs(cs));
        grammarMap.put(Keyword.intern("string"), makeCfgStringRhs(cs));
        grammarMap.put(Keyword.intern("regexp"), makeCfgRegexRhs(cs));
        grammarMap.put(Keyword.intern("opt"), makeCfgOptRhs(cs));
        grammarMap.put(Keyword.intern("star"), makeCfgOneOrMoreRhs(cs));
        grammarMap.put(Keyword.intern("plus"), makeCfgPlusRhs(cs));
        grammarMap.put(Keyword.intern("look"), makeCfgLookRhs(cs));
        grammarMap.put(Keyword.intern("neg"), makeCfgNegRhs(cs));
        grammarMap.put(Keyword.intern("epsilon"), makeCfgEpsilonRhs(cs));
        grammarMap.put(Keyword.intern("factor"), makeCfgFactorRhs(cs));
        grammarMap.put(Keyword.intern("rules-or-parser"), makeCfgRulesOrParserRhs(cs));
        return Reduction.applyStandardReductions(new Grammar(grammarMap));
    }
}
