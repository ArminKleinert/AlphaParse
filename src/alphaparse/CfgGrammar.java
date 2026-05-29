package alphaparse;

import alphaparse.grammar.Grammar;
import alphaparse.parsing.*;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CfgGrammar {
    final @NotNull CombinatorFactory cf;
    final @NotNull Set<RulesAvailable> rulesAvailable;
    final @NotNull ParserCreationOptions options;

    private CfgGrammar(final @NotNull ParserCreationOptions options) {
        this.options = options;
        this.rulesAvailable = options.usableRules();
        this.cf = options.useParserBuffering()
                ? new CombinatorFactory(true)
                : new CombinatorFactory(false);
    }

    private final @NotNull Combinator optWhitespace =
            CombinatorFactory.staticMakeNonTerminal(Sym.sym("opt-whitespace")).enableHideTag();

    private @NotNull List<@NotNull Combinator> cListOf(Combinator... elements) {
        return Arrays.stream(elements).filter(Objects::nonNull).toList();
    }

    private @Nullable NonTerminalCombinator makeNT(
            final @NotNull String symString, final @NotNull RulesAvailable ra) {
        return rulesAvailable.contains(ra) ? cf.makeNonTerminal(Sym.sym(symString)) : null;
    }

    /*
     * These rules are added later if {@link RulesAvailable.ABNF_CORE} is in the Set of available rules when creating a parser.
     */
    static @NotNull List<Map.Entry<Sym, Combinator>> makeAbnfCoreRules() {
        final @NotNull CombinatorFactory cf = new CombinatorFactory(false);
        var CRLF = cf.stringTerminal("\r\n");
        var WSP = cf.createRegexTerminal(Pattern.compile("[\\u0020\\u0009]"));

        final @NotNull List<Map.Entry<Sym, Combinator>> m = List.of(
                Map.entry(Sym.sym("ALPHA"), cf.createRegexTerminal(Pattern.compile("[a-zA-Z]"))),
                Map.entry(Sym.sym("BIT"), cf.createRegexTerminal(Pattern.compile("[01]"))),
                Map.entry(Sym.sym("CHAR"), cf.createRegexTerminal(Pattern.compile("[\\u0001-\\u007F]"))),
                Map.entry(Sym.sym("CR"), cf.stringTerminal("\r")),
                Map.entry(Sym.sym("CRLF"), CRLF),
                Map.entry(Sym.sym("CTL"), cf.createRegexTerminal(Pattern.compile("[\\u0000-\\u001F|\\u007F]"))),
                Map.entry(Sym.sym("DIGIT"), cf.createRegexTerminal(Pattern.compile("[0-9]"))),
                Map.entry(Sym.sym("DQUOTE"), cf.stringTerminal("\"")),
                Map.entry(Sym.sym("HEXDIG"), cf.createRegexTerminal(Pattern.compile("[0-9a-fA-F]"))),
                Map.entry(Sym.sym("HTAB"), cf.createRegexTerminal(Pattern.compile("\t"))),
                Map.entry(Sym.sym("LF"), cf.createRegexTerminal(Pattern.compile("\n"))),
                Map.entry(Sym.sym("LWSP"), cf.starCombinator(cf.choiceCombinatorDistinct(List.of(WSP, cf.catCombinator(List.of(CRLF, WSP)))))),
                Map.entry(Sym.sym("OCTET"), cf.createRegexTerminal(Pattern.compile("[\\u0000-\\u00FF]"))),
                Map.entry(Sym.sym("SP"), cf.stringTerminal(" ")),
                Map.entry(Sym.sym("VCHAR"), cf.createRegexTerminal(Pattern.compile("[\\u0021-\\u007E]"))),
                Map.entry(Sym.sym("WSP"), WSP)
        );
        return m;
    }

    private @NotNull Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    private @NotNull Combinator makeCfgRulesRhs() {
        final @NotNull Combinator rulesRule = cf.catCombinator(
                        List.of(optWhitespace,
                                cf.plusCombinator(
                                        cf.makeNonTerminal(Sym.sym("rule")) /// {@link #makeCfgRuleRhs}
                                )))
                .hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgCommentRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.stringTerminal("(*"),
                                makeCfgInsideCommentRhs(),
                                cf.stringTerminal("*)"))).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgInsideCommentRhs() {
        final @NotNull Pattern insideComment = Pattern.compile("(?s)(?:(?!\\(\\*|\\*\\)).)* (?x) # Comment text");
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.createRegexTerminal(insideComment),
                                cf.starCombinator(
                                        cf.catCombinator(
                                                List.of(cf.makeNonTerminal(Sym.sym("comment")), /// {@link #makeCfgCommentRhs}
                                                        cf.createRegexTerminal(insideComment))))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgOptWhitespaceRhs() {
        final @NotNull Pattern ws = regexDoc("[,\\s]*", "optional whitespace");
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.createRegexTerminal(ws),
                                cf.starCombinator(
                                        cf.catCombinator(
                                                List.of(cf.makeNonTerminal(Sym.sym("comment")), /// {@link #makeCfgCommentRhs}
                                                        cf.createRegexTerminal(ws))))));
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
            return cf.stringTerminal("\"\"");

        return cf.specialSequence(
                "One of " + epsilonNames,
                text -> epsilonNames.stream().filter(text::startsWith).max(Comparator.comparingInt(String::length))
        );
    }

    private @NotNull Combinator makeCfgFactorRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinatorDistinct(
                                cListOf(
                                        cf.makeNonTerminal(Sym.sym("string")), /// {@link #makeCfgStringRhs}
                                        makeNT("regexp", RulesAvailable.REGEX), /// {@link #makeCfgRegexRhs}
                                        makeNT("opt", RulesAvailable.OPTIONAL), /// {@link #makeCfgOptRhs}
                                        makeNT("opt_query", RulesAvailable.OPTIONAL_QUERY), /// {@link #makeCfgOptQueryRhs}
                                        makeNT("star", RulesAvailable.OPTIONAL_REPETITION_STAR), /// {@link #makeCfgZeroOrMoreStarRhs}
                                        makeNT("opt_rep", RulesAvailable.OPTIONAL_REPETITION), /// {@link #makeCfgZeroOrMoreStdRhs}
                                        makeNT("plus", RulesAvailable.PLUS), /// {@link #makeCfgPlusRhs}
                                        cf.makeNonTerminal(Sym.sym("paren")), /// {@link #makeCfgParenRhs}
                                        cf.makeNonTerminal(Sym.sym("hide")), /// {@link #makeCfgHideRhs}
                                        cf.makeNonTerminal(Sym.sym("epsilon")), /// {@link #makeCfgEpsilonRhs}
                                        makeNT("rep", RulesAvailable.VARIABLE_REPEAT), /// ABNF feature {@link #makeCfgRepRhs}
                                        makeNT("abnf-range", RulesAvailable.VALUE_RANGE), /// ABNF feature {@link #makeABNFValueRange}
                                        cf.makeNonTerminal(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
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
                cf.catCombinator(
                        List.of(cf.makeNonTerminal(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                cf.stringTerminal("+").enableHideTag()));
        return rulesRule;
    }

    /**
     * Recognition of dividers. For example, that is "=" in "S = ...".
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgRuleSeparatorRhs() {
        return cf.choiceCombinator(
                options.ruleDefinitionOps()
                        .stream()
                        .map(it -> cf.stringTerminal(it, false))
                        .toList());
    }

    private @NotNull Combinator makeCfgParenRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.stringTerminal("(").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                cf.stringTerminal(")").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                cf.stringTerminal(">").enableHideTag()));
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
        var doubleQuoteStringRegexCombinator = cf.createRegexTerminal(doubleQuotedString);

        if (!options.usableRules().contains(RulesAvailable.SINGLY_QUOTED))
            return doubleQuoteStringRegexCombinator;

        final @NotNull Pattern singleQuotedString = hasCiPrefixAvailable
                ? regexDoc(singleQuoteStringPrefixed, "Prefixed single-quoted string")
                : regexDoc(singleQuoteString, "Single-quoted string");

        return cf.choiceCombinator(List.of(
                doubleQuoteStringRegexCombinator,
                cf.createRegexTerminal(singleQuotedString)));
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
                cf.choiceCombinatorDistinct(
                        List.of(cf.createRegexTerminal(singleQuotedRegex),
                                cf.createRegexTerminal(doubleQuotedRegex)));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRulesOrParserRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinatorDistinct(
                                List.of(cf.makeNonTerminal(Sym.sym("rules")), /// {@link #makeCfgRulesRhs}
                                        cf.makeNonTerminal(Sym.sym("alt-or-ord")) /// {@link #makeCfgAltOrOrdRhs}
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

        return cf.specialSequence(
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
                    cf.createRegexTerminal(Pattern.compile("\\d*\\*?\\d*"));
        } else {
            repRegexChoice =
                    cf.createRegexTerminal(Pattern.compile("\\d+(?:\\*\\d*)?|\\*\\d+"));
        }
        final @NotNull Combinator rulesRule =
                cf.catCombinator(List.of(
                        repRegexChoice,
                        optWhitespace,
                        cf.makeNonTerminal(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
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
                cf.catCombinator(
                        List.of(cf.stringTerminal("&").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
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
                cf.catCombinator(
                        List.of(cf.stringTerminal("!").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
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
                cf.catCombinator(
                        List.of(cf.stringTerminal("{").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                cf.stringTerminal("}").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link CombinatorStar} with the pattern {@code rule*}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgZeroOrMoreStarRhs() {
        final @NotNull Combinator rule =
                cf.catCombinator(
                        List.of(cf.makeNonTerminal(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                cf.stringTerminal("*").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link OptionalCombinator} with the pattern {@code [rule]}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgOptRhs() {
        final @NotNull Combinator rule =
                cf.catCombinator(
                        List.of(cf.stringTerminal("[").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                optWhitespace,
                                cf.stringTerminal("]").enableHideTag()));
        return rule;
    }

    /**
     * Recognition of {@link OptionalCombinator} with the pattern {@code rule*}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeCfgOptQueryRhs() {
        final @NotNull Combinator rule =
                cf.catCombinator(
                        List.of(cf.makeNonTerminal(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                                optWhitespace,
                                cf.stringTerminal("?").enableHideTag()));
        return rule;
    }

    private @NotNull Combinator makeCfgAltOrOrdRhs() {
        int i = 0;
        Combinator[] l = new Combinator[2];

        if (options.usableRules().contains(RulesAvailable.ALTERNATION))
            l[i++] = (cf.makeNonTerminal(Sym.sym("alt"))); /// {@link #makeCfgAltRhs}
        if (options.usableRules().contains(RulesAvailable.ORDERED_CHOICE))
            l[i++] = (cf.makeNonTerminal(Sym.sym("ord"))); /// {@link #makeCfgOrdRhs}

        if (i == 0) return (cf.plusCombinator(cf.makeNonTerminal(Sym.sym("cat")))).hideTag(); /// {@link #makeCfgCatRhs}

        if (i == 1) return l[0].hideTag();

        final @NotNull Combinator rulesRule = cf.choiceCombinatorDistinct(List.of(l)).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideNtRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
                                optWhitespace,
                                cf.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRuleRhs() {
        final @NotNull Combinator optWs = cf.makeNonTerminal(Sym.sym("opt-whitespace")); /// {@link #makeCfgOptWhitespaceRhs}
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(cf.choiceCombinatorDistinct(
                                        List.of(cf.makeNonTerminal(Sym.sym("nt")), /// {@link #makeCfgNtRhs}
                                                cf.makeNonTerminal(Sym.sym("hide-nt")) /// {@link #makeCfgHideNtRhs}
                                        )),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("rule-separator")).enableHideTag(), /// {@link #makeCfgRuleSeparatorRhs}
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")), /// {@link #makeCfgAltOrOrdRhs}
                                cf.choiceCombinatorDistinct(
                                                List.of(optWs,
                                                        cf.catCombinator(
                                                                List.of(optWs,
                                                                        cf.choiceCombinatorDistinct(
                                                                                List.of(cf.stringTerminal(";"),
                                                                                        cf.stringTerminal("."))),
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
        final @NotNull Combinator catNt = cf.makeNonTerminal(Sym.sym("cat")); /// {@link #makeCfgCatRhs}
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(catNt,
                                cf.starCombinator(
                                        cf.catCombinator(
                                                List.of(optWhitespace,
                                                        cf.stringTerminal("/").enableHideTag(),
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
        final @NotNull Combinator catNt = cf.makeNonTerminal(Sym.sym("cat")); /// {@link #makeCfgCatRhs}
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(catNt,
                                cf.starCombinator(cf.catCombinator(
                                        List.of(optWhitespace,
                                                cf.stringTerminal("|").enableHideTag(),
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
        final @NotNull Combinator factorLookNeg = cf.choiceCombinatorDistinct(cListOf(
                cf.makeNonTerminal(Sym.sym("factor")), /// {@link #makeCfgFactorRhs}
                makeNT("look", RulesAvailable.LOOKAHEAD), /// {@link #makeCfgLookRhs}
                makeNT("neg", RulesAvailable.NEGATIVE_LOOKAHEAD), /// {@link #makeCfgNegRhs}
                makeNT("exclude", RulesAvailable.EXCLUSION) /// {@link #makeCfgExclude}
        ));
        final @NotNull Combinator rulesRule =
                cf.plusCombinator(
                        cf.catCombinator(
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
        final @NotNull Combinator factorLookNeg = cf.choiceCombinatorDistinct(cListOf(
                cf.makeNonTerminal(Sym.sym("factor")) /// {@link #makeCfgFactorRhs}
        ));
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        List.of(factorLookNeg, optWhitespace,
                                cf.stringTerminal("-").enableHideTag(),
                                optWhitespace,
                                cf.choiceCombinatorDistinct(List.of(factorLookNeg, cf.makeNonTerminal(Sym.sym("exclude"))))));
        return rulesRule;
    }

    /**
     * Recognition of {@link EOFCombinator}.
     *
     * @return A {@link Combinator}.
     */
    private @NotNull Combinator makeEofRhs() {
        return cf.stringTerminal("EOF");
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
        return cf.createRegexTerminal(regex);
    }

    @NotNull Grammar makeCfg() {
        final @NotNull SequencedMap<Sym, Combinator> grammarMap = new LinkedHashMap<>();
        final @NotNull CombinatorFactory cs = new CombinatorFactory(false);
        grammarMap.put(Sym.sym("rules"), makeCfgRulesRhs());
        grammarMap.put(Sym.sym("comment"), makeCfgCommentRhs());
        //grammarMap.put(Sym.sym("inside-comment"), g.makeCfgInsideCommentRhs());
        grammarMap.put(Sym.sym("opt-whitespace"), makeCfgOptWhitespaceRhs());
        grammarMap.put(Sym.sym("rule-separator"), makeCfgRuleSeparatorRhs());
        grammarMap.put(Sym.sym("rule"), makeCfgRuleRhs());
        grammarMap.put(Sym.sym("nt"), makeCfgNtRhs());
        grammarMap.put(Sym.sym("hide-nt"), makeCfgHideNtRhs());
        grammarMap.put(Sym.sym("paren"), makeCfgParenRhs());
        grammarMap.put(Sym.sym("hide"), makeCfgHideRhs());
        grammarMap.put(Sym.sym("cat"), makeCfgCatRhs());
        grammarMap.put(Sym.sym("string"), makeCfgStringRhs());
        grammarMap.put(Sym.sym("epsilon"), makeCfgEpsilonRhs());
        grammarMap.put(Sym.sym("factor"), makeCfgFactorRhs());
        grammarMap.put(Sym.sym("rules-or-parser"), makeCfgRulesOrParserRhs());
        grammarMap.put(Sym.sym("alt-or-ord"), makeCfgAltOrOrdRhs());

        if (rulesAvailable.contains(RulesAvailable.ALTERNATION))
            grammarMap.put(Sym.sym("alt"), makeCfgAltRhs());

        if (rulesAvailable.contains(RulesAvailable.ORDERED_CHOICE))
            grammarMap.put(Sym.sym("ord"), makeCfgOrdRhs()); // Technically ABNF, but should be included without it as a PAKRAT extension.

        if (rulesAvailable.contains(RulesAvailable.VARIABLE_REPEAT))
            grammarMap.put(Sym.sym("rep"), makeCfgRepRhs()); // ABNF

        if (rulesAvailable.contains(RulesAvailable.REGEX))
            grammarMap.put(Sym.sym("regexp"), makeCfgRegexRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL))
            grammarMap.put(Sym.sym("opt"), makeCfgOptRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_QUERY))
            grammarMap.put(Sym.sym("opt_query"), makeCfgOptQueryRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_REPETITION_STAR))
            grammarMap.put(Sym.sym("star"), makeCfgZeroOrMoreStarRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL_REPETITION))
            grammarMap.put(Sym.sym("opt_rep"), makeCfgZeroOrMoreStdRhs());

        if (rulesAvailable.contains(RulesAvailable.PLUS))
            grammarMap.put(Sym.sym("plus"), makeCfgPlusRhs());

        if (rulesAvailable.contains(RulesAvailable.LOOKAHEAD))
            grammarMap.put(Sym.sym("look"), makeCfgLookRhs());

        if (rulesAvailable.contains(RulesAvailable.NEGATIVE_LOOKAHEAD))
            grammarMap.put(Sym.sym("neg"), makeCfgNegRhs());

        if (rulesAvailable.contains(RulesAvailable.VALUE_RANGE))
            grammarMap.put(Sym.sym("abnf-range"), makeABNFValueRange()); // ABNF

        if (rulesAvailable.contains(RulesAvailable.EXCLUSION))
            grammarMap.put(Sym.sym("exclude"), makeCfgExclude());

        if (rulesAvailable.contains(RulesAvailable.EXPLICIT_EOF))
            grammarMap.put(Sym.sym("eof"), makeEofRhs());

        return new Grammar(grammarMap).applyStandardReductions(cs);
    }

    @NotNull
    static Grammar makeCfg(final @NotNull ParserCreationOptions options) {
        final @NotNull CfgGrammar g = new CfgGrammar(options);
        return g.makeCfg();
    }
}
