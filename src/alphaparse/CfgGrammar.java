package alphaparse;

import alphaparse.grammar.Grammar;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.parsing.*;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CfgGrammar extends GrammarBuilder {
    final @NotNull Set<RulesAvailable> rulesAvailable;
    final @NotNull ParserCreationOptions options;

    private CfgGrammar(final @NotNull ParserCreationOptions options) {
        super(options);
        this.options = options;
        this.rulesAvailable = options.usableRules();
    }

    private final @NotNull Combinator optWhitespace =
            NonTerminalCombinator.create(Sym.sym("opt-whitespace")).enableHideTag();

    private @NotNull List<@NotNull Combinator> cListOf(Combinator... elements) {
        return Arrays.stream(elements).filter(Objects::nonNull).toList();
    }

    private @Nullable NonTerminalCombinator makeNT(
            final @NotNull String symString, final @NotNull RulesAvailable ra) {
        return rulesAvailable.contains(ra) ? nt(Sym.sym(symString)) : null;
    }

    /*
     * These rules are added later if {@link RulesAvailable.ABNF_CORE} is in the Set of available rules when creating a parser.
     */
     static @NotNull List<Map.Entry<Sym, Combinator>> makeAbnfCoreRules() {
         var CRLF = new TerminalStringCombinator("\r\n", false);
        var WSP = new TerminalRegexpCombinator(Pattern.compile("[\\u0020\\u0009]"));

        final @NotNull List<Map.Entry<Sym, Combinator>> m = List.of(
                Map.entry(Sym.sym("ALPHA"), new TerminalRegexpCombinator(Pattern.compile("[a-zA-Z]"))),
                Map.entry(Sym.sym("BIT"), new TerminalRegexpCombinator(Pattern.compile("[01]"))),
                Map.entry(Sym.sym("CHAR"), new TerminalRegexpCombinator(Pattern.compile("[\\u0001-\\u007F]"))),
                Map.entry(Sym.sym("CR"), new TerminalStringCombinator("\r", false)),
                Map.entry(Sym.sym("CRLF"), CRLF),
                Map.entry(Sym.sym("CTL"), new TerminalRegexpCombinator(Pattern.compile("[\\u0000-\\u001F|\\u007F]"))),
                Map.entry(Sym.sym("DIGIT"), new TerminalRegexpCombinator(Pattern.compile("[0-9]"))),
                Map.entry(Sym.sym("DQUOTE"), new TerminalStringCombinator("\"", false)),
                Map.entry(Sym.sym("HEXDIG"), new TerminalRegexpCombinator(Pattern.compile("[0-9a-fA-F]"))),
                Map.entry(Sym.sym("HTAB"), new TerminalRegexpCombinator(Pattern.compile("\t"))),
                Map.entry(Sym.sym("LF"), new TerminalRegexpCombinator(Pattern.compile("\n"))),
                Map.entry(Sym.sym("LWSP"), new CombinatorStar(new ChoiceCombinator(List.of(WSP, new ConcatCombinator(List.of(CRLF, WSP)))))),
                Map.entry(Sym.sym("OCTET"), new TerminalRegexpCombinator(Pattern.compile("[\\u0000-\\u00FF]"))),
                Map.entry(Sym.sym("SP"), new TerminalStringCombinator(" ", false)),
                Map.entry(Sym.sym("VCHAR"), new TerminalRegexpCombinator(Pattern.compile("[\\u0021-\\u007E]"))),
                Map.entry(Sym.sym("WSP"), WSP)
        );
        return m;
    }

    private @NotNull Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    private @NotNull Combinator makeCfgRulesRhs() {
        final @NotNull Combinator rulesRule = concat(
                        List.of(optWhitespace,
                                onceOrMore(
                                        nt(Sym.sym("rule")) /// {@link #makeCfgRuleRhs}
                                )))
                .hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgCommentRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("(*"),
                                makeCfgInsideCommentRhs(),
                                string("*)"))).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgInsideCommentRhs() {
        final @NotNull Pattern insideComment = Pattern.compile("(?s)(?:(?!\\(\\*|\\*\\)).)* (?x) # Comment text");
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(regex(insideComment),
                                zeroOrMore(
                                        concat(
                                                List.of(nt(Sym.sym("comment")), /// {@link #makeCfgCommentRhs}
                                                        regex(insideComment))))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgOptWhitespaceRhs() {
        final @NotNull Pattern ws = regexDoc("[,\\s]*", "optional whitespace");
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(regex(ws),
                                zeroOrMore(
                                        concat(
                                                List.of(nt(Sym.sym("comment")), /// {@link #makeCfgCommentRhs}
                                                        regex(ws))))));
        return rulesRule;
    }

    /**
     * Recognition of {@link EpsilonCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private Combinator makeCfgEpsilonRhs() {
        var epsilonNames = options.epsilonNames();

        // If no epsilon names are provided, use string terminal which matches the empty string `""`.
        // Empty string terminals are simplified to Epsilon later.
        if (epsilonNames.isEmpty())
            return string("\"\"");

        return specialSequence(
                "One of " + epsilonNames,
                text -> epsilonNames.stream().filter(text::startsWith).max(Comparator.comparingInt(String::length))
        );
    }

    private @NotNull Combinator makeCfgFactorRhs() {
        final @NotNull Combinator rulesRule =
                alternationGuaranteeDistinct(
                                cListOf(
                                        nt(Sym.sym("string")), /// {@link #makeCfgStringRhs}
                                        makeNT("regexp", RulesAvailable.REGEX), /// {@link #makeCfgRegexRhs}
                                        makeNT("opt", RulesAvailable.OPTIONAL), /// {@link #makeCfgOptRhs}
                                        makeNT("opt_query", RulesAvailable.OPTIONAL_QUERY), /// {@link #makeCfgOptQueryRhs}
                                        makeNT("star", RulesAvailable.OPTIONAL_REPETITION_STAR), /// {@link #makeCfgZeroOrMoreStarRhs}
                                        makeNT("opt_rep", RulesAvailable.OPTIONAL_REPETITION), /// {@link #makeCfgZeroOrMoreStdRhs}
                                        makeNT("plus", RulesAvailable.PLUS), /// {@link #makeCfgPlusRhs}
                                        nt(Sym.sym("paren")), /// {@link #makeCfgParenRhs}
                                        nt(Sym.sym("hide")), /// {@link #makeCfgHideRhs}
                                        nt(Sym.sym("epsilon")), /// {@link #makeCfgEpsilonRhs}
                                        makeNT("rep", RulesAvailable.VARIABLE_REPEAT), /// ABNF feature {@link #makeCfgRepRhs}
                                        makeNT("abnf-range", RulesAvailable.VALUE_RANGE), /// ABNF feature {@link #makeABNFValueRange}
                                        nt(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
                                        null
                                ))
                        .hideTag();
        return rulesRule;
    }

    /**
     * Recognition of {@link PlusCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgPlusRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(nt(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                string("+").enableHideTag()));
        return rulesRule;
    }

    /**
     * Recognition of dividers. For example, that is "=" in "S = ...".
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgRuleSeparatorRhs() {
        return alternationC(
                options.ruleDefinitionOps()
                        .stream()
                        .map(it -> string(it, false))
                        .toList());
    }

    private @NotNull Combinator makeCfgParenRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("(").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                string(")").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("<").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                string(">").enableHideTag()));
        return rulesRule;
    }


    /**
     * Recognition of {@link TerminalStringCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgStringRhs() {
        final boolean hasCiPrefixAvailable =
                options.usableRules().contains(RulesAvailable.STRING_CASE_SENSITIVITY_PREFIX);

        final @NotNull String doubleQuoteString = "\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"";
        final @NotNull String doubleQuoteStringPrefixed = "(%[is])?" + doubleQuoteString;
        final @NotNull String singleQuoteString = "'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'";
        final @NotNull String singleQuoteStringPrefixed = "(%[is])?" + singleQuoteString;

        final @NotNull Pattern doubleQuotedString = hasCiPrefixAvailable
                ? regexDoc(doubleQuoteStringPrefixed, "Prefixed double-quoted string")
                : regexDoc(doubleQuoteString, "Double-quoted string");
        var doubleQuoteStringRegexCombinator = regex(doubleQuotedString);

        if (!options.usableRules().contains(RulesAvailable.SINGLY_QUOTED))
            return doubleQuoteStringRegexCombinator;

        final @NotNull Pattern singleQuotedString = hasCiPrefixAvailable
                ? regexDoc(singleQuoteStringPrefixed, "Prefixed single-quoted string")
                : regexDoc(singleQuoteString, "Single-quoted string");

        return alternationC(List.of(
                doubleQuoteStringRegexCombinator,
                regex(singleQuotedString)));
    }

    /**
     * Recognition of {@link TerminalRegexpCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgRegexRhs() {
        final @NotNull Pattern singleQuotedRegex =
                regexDoc("#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted regexp");
        final @NotNull Pattern doubleQuotedRegex =
                regexDoc("#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted regexp");
        final @NotNull Combinator rulesRule =
                alternationGuaranteeDistinct(
                        List.of(regex(singleQuotedRegex),
                                regex(doubleQuotedRegex)));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRulesOrParserRhs() {
        final @NotNull Combinator rulesRule =
                alternationGuaranteeDistinct(
                                List.of(nt(Sym.sym("rules")), /// {@link #makeCfgRulesRhs}
                                        nt(Sym.sym("alt-or-ord")) /// {@link #makeCfgAltOrOrdRhs}
                                ))
                        .hideTag();
        return rulesRule;
    }

    /**
     * Recognition of {@link NonTerminalCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgNtRhs() {
        final var regex = rulesAvailable.contains(RulesAvailable.EXTENDED_IDENTIFIERS)
                ? Pattern.compile("[^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./%\\-0-9][^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./%]*")
                : Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");

        return specialSequence(
                "matches " + regex + " but is not reserved for other purposes",
                text -> {
                    final @NotNull Matcher matcher = regex.matcher(text);
                    if (!matcher.lookingAt()) {
                        return Optional.empty();
                    }
                    String matched = matcher.group();
                    if (options.epsilonNames().contains(matched)) {
                        return Optional.empty();
                    }
                    return Optional.of(matched);
                });
    }

    /**
     * Recognition of {@link RepetitionCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgRepRhs() {
        final @NotNull Combinator repRegexChoice;
        if (!rulesAvailable.contains(RulesAvailable.OPTIONAL_REPETITION_STAR)) {
            repRegexChoice =
                    regex(Pattern.compile("\\d*\\*?\\d*"));
        } else {
            repRegexChoice =
                    regex(Pattern.compile("\\d+(?:\\*\\d*)?|\\*\\d+"));
        }
        final @NotNull Combinator rulesRule =
                concat(List.of(
                        repRegexChoice,
                        optWhitespace,
                        nt(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
                ));
        return rulesRule;
    }

    /**
     * Recognition of {@link LookaheadCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgLookRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("&").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
                        ));
        return rulesRule;
    }

    /**
     * Recognition of {@link NegativeLookaheadCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgNegRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("!").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
                        ));
        return rulesRule;
    }

    /**
     * Recognition of {@link CombinatorStar} with the pattern {@code {rule}}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgZeroOrMoreStdRhs() {
        final @NotNull Combinator rule =
                concat(
                        List.of(string("{").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                string("}").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link CombinatorStar} with the pattern {@code rule*}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgZeroOrMoreStarRhs() {
        final @NotNull Combinator rule =
                concat(
                        List.of(nt(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                string("*").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link OptionalCombinator} with the pattern {@code [rule]}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgOptRhs() {
        final @NotNull Combinator rule =
                concat(
                        List.of(string("[").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                string("]").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link OptionalCombinator} with the pattern {@code rule*}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgOptQueryRhs() {
        final @NotNull Combinator rule =
                concat(
                        List.of(nt(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                string("?").enableHideTag()));
        return rule;
    }

    private @NotNull Combinator makeCfgAltOrOrdRhs() {
        int i = 0;
        Combinator[] l = new Combinator[2];

        if (options.usableRules().contains(RulesAvailable.ALTERNATION))
            l[i++] = (nt(Sym.sym("alt"))); /// {@link #makeCfgAltRhs}
        if (options.usableRules().contains(RulesAvailable.ORDERED_CHOICE))
            l[i++] = (nt(Sym.sym("ord"))); /// {@link #makeCfgOrdRhs}

        if (i == 0) return onceOrMore(nt(Sym.sym("cat"))).hideTag(); /// {@link #makeCfgCatRhs}

        if (i == 1) return l[0].hideTag();

        final @NotNull Combinator rulesRule = alternationGuaranteeDistinct(List.of(l)).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideNtRhs() {
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(string("<").enableHideTag(),
                                optWhitespace,
                                nt(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
                                optWhitespace,
                                string(">").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRuleRhs() {
        final @NotNull Combinator optWs = nt(Sym.sym("opt-whitespace")); /// {@link #makeCfgOptWhitespaceRhs}
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(alternationGuaranteeDistinct(
                                        List.of(nt(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
                                                nt(Sym.sym("hide-nt")) /// {@link #makeCfgHideNtRhs}
                                        )),
                                optWhitespace,
                                nt(Sym.sym("rule-separator")).enableHideTag(), /// {@link #makeCfgRuleSeparatorRhs}
                                optWhitespace,
                                nt(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                alternationGuaranteeDistinct(
                                                List.of(optWs,
                                                        concat(
                                                                List.of(optWs,
                                                                        alternationGuaranteeDistinct(
                                                                                List.of(string(";"),
                                                                                        string("."))),
                                                                        optWs))))
                                        .enableHideTag()));
        return rulesRule;
    }

    /**
     * Recognition of {@link OrderedChoiceCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgOrdRhs() {
        final @NotNull Combinator catNt = nt(Sym.sym("cat")); /// {@link #makeCfgCatRhs}
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(catNt,
                                zeroOrMore(
                                        concat(
                                                List.of(optWhitespace,
                                                        string("/").enableHideTag(),
                                                        optWhitespace,
                                                        catNt)))));
        return rulesRule;
    }

    /**
     * Recognition of {@link ChoiceCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgAltRhs() {
        final @NotNull Combinator catNt = nt(Sym.sym("cat")); /// {@link #makeCfgCatRhs}
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(catNt,
                                zeroOrMore(concat(
                                        List.of(optWhitespace,
                                                string("|").enableHideTag(),
                                                optWhitespace,
                                                catNt)))));
        return rulesRule;
    }

    /**
     * Recognition of {@link ConcatCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgCatRhs() {
        final @NotNull Combinator factorLookNeg = alternationGuaranteeDistinct(cListOf(
                nt(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                makeNT("look", RulesAvailable.LOOKAHEAD), /// {@link #makeCfgLookRhs}
                makeNT("neg", RulesAvailable.NEGATIVE_LOOKAHEAD), /// {@link #makeCfgNegRhs}
                makeNT("exclude", RulesAvailable.EXCLUSION) /// {@link #makeCfgExclude}
        ));
        final @NotNull Combinator rulesRule =
                onceOrMore(
                        concat(
                                List.of(optWhitespace,
                                        factorLookNeg,
                                        optWhitespace)));
        return rulesRule;
    }

    /**
     * Recognition of {@link ExclusionCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgExclude() {
        final @NotNull Combinator factorLookNeg = alternationGuaranteeDistinct(cListOf(
                nt(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
        ));
        final @NotNull Combinator rulesRule =
                concat(
                        List.of(factorLookNeg, optWhitespace,
                                string("-").enableHideTag(),
                                optWhitespace,
                                alternationGuaranteeDistinct(List.of(factorLookNeg, nt(Sym.sym("exclude"))))));
        return rulesRule;
    }

    /**
     * Recognition of {@link EOFCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeEofRhs() {
        return string("EOF");
    }

    /**
     * Recognition of {@link TerminalUnicodeCharCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeABNFValueRange() {
        final Pattern regex = regexDoc(
                "%b[01]+(\\-[01]+)?|%d[0-9]+(\\-[0-9]+)?|%x[0-9a-fA-F]+(\\-[0-9a-fA-F]+)?",
                "ABNF Value Range"
        );
        return regex(regex);
    }

    @Override
    public void make() {
        addProduction(Sym.sym("rules"), makeCfgRulesRhs());
        addProduction(Sym.sym("comment"), makeCfgCommentRhs());
        //addProduction(Sym.sym("inside-comment"), g.makeCfgInsideCommentRhs());
        addProduction(Sym.sym("opt-whitespace"), makeCfgOptWhitespaceRhs());
        addProduction(Sym.sym("rule-separator"), makeCfgRuleSeparatorRhs());
        addProduction(Sym.sym("rule"), makeCfgRuleRhs());
        addProduction(Sym.sym("nt"), makeCfgNtRhs());
        addProduction(Sym.sym("hide-nt"), makeCfgHideNtRhs());
        addProduction(Sym.sym("paren"), makeCfgParenRhs());
        addProduction(Sym.sym("hide"), makeCfgHideRhs());
        addProduction(Sym.sym("cat"), makeCfgCatRhs());
        addProduction(Sym.sym("string"), makeCfgStringRhs());
        addProduction(Sym.sym("epsilon"), makeCfgEpsilonRhs());
        addProduction(Sym.sym("factor"), makeCfgFactorRhs());
        addProduction(Sym.sym("rules-or-parser"), makeCfgRulesOrParserRhs());
        addProduction(Sym.sym("alt-or-ord"), makeCfgAltOrOrdRhs());

        if (rulesAvailable.contains(RulesAvailable.ALTERNATION))
            addProduction(Sym.sym("alt"), makeCfgAltRhs());

        if (rulesAvailable.contains(RulesAvailable.ORDERED_CHOICE))
            addProduction(Sym.sym("ord"), makeCfgOrdRhs()); // Technically ABNF, but should be included without it as a PAKRAT extension.

        if (rulesAvailable.contains(RulesAvailable.VARIABLE_REPEAT))
            addProduction(Sym.sym("rep"), makeCfgRepRhs()); // ABNF

        if (rulesAvailable.contains(RulesAvailable.REGEX))
            addProduction(Sym.sym("regexp"), makeCfgRegexRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL))
            addProduction(Sym.sym("opt"), makeCfgOptRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_QUERY))
            addProduction(Sym.sym("opt_query"), makeCfgOptQueryRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_REPETITION_STAR))
            addProduction(Sym.sym("star"), makeCfgZeroOrMoreStarRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_REPETITION))
            addProduction(Sym.sym("opt_rep"), makeCfgZeroOrMoreStdRhs());

        if (rulesAvailable.contains(RulesAvailable.PLUS))
            addProduction(Sym.sym("plus"), makeCfgPlusRhs());

        if (rulesAvailable.contains(RulesAvailable.LOOKAHEAD))
            addProduction(Sym.sym("look"), makeCfgLookRhs());

        if (rulesAvailable.contains(RulesAvailable.NEGATIVE_LOOKAHEAD))
            addProduction(Sym.sym("neg"), makeCfgNegRhs());

        if (rulesAvailable.contains(RulesAvailable.VALUE_RANGE))
            addProduction(Sym.sym("abnf-range"), makeABNFValueRange()); // ABNF

        if (rulesAvailable.contains(RulesAvailable.EXCLUSION))
            addProduction(Sym.sym("exclude"), makeCfgExclude());

        if (rulesAvailable.contains(RulesAvailable.EXPLICIT_EOF))
            addProduction(Sym.sym("eof"), makeEofRhs());
    }

    @NotNull
    static Grammar makeCfg(final @NotNull ParserCreationOptions options) {
        return new CfgGrammar(options).build();
    }
}
