package alphaparse;

import alphaparse.parser.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

final class EbnfG {
    final @NotNull CombinatorFactory cf;
    final @NotNull Set<RulesAvailable> rulesAvailable;
    final @NotNull Alpha.ParserCreationOptions options;

    private EbnfG(final @NotNull Alpha.ParserCreationOptions options) {
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

    private @Nullable Combinator make(
            final @NotNull RulesAvailable rule, final @NotNull Supplier<@NotNull Combinator> combinatorSupplier) {
        return rulesAvailable.contains(rule) ? combinatorSupplier.get() : null;
    }

    private @NotNull Pattern regexDoc(final @NotNull String patternString, final @NotNull String comment) {
        return Pattern.compile(patternString + "(?x) #" + comment);
    }

    private @NotNull Combinator makeCfgRulesRhs() {
        final @NotNull Combinator rulesRule = cf.catCombinator(
                        cListOf(optWhitespace,
                                cf.plusCombinator(
                                        cf.makeNonTerminal(Sym.sym("rule")))))
                .hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgCommentRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("(*"),
                                makeCfgInsideCommentRhs(),
                                cf.stringTerminal("*)"))).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgInsideCommentRhs() {
        final @NotNull Pattern insideComment = regexDoc("(?s)(?:(?!(?:\\(\\*|\\*\\))).)*", "Comment text");
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.createRegexTerminal(insideComment),
                                cf.starCombinator(
                                        cf.catCombinator(
                                                cListOf(cf.makeNonTerminal(Sym.sym("comment")),
                                                        cf.createRegexTerminal(insideComment))))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgOptWhitespaceRhs() {
        final @NotNull Pattern ws = regexDoc("[,\\s]*", "optional whitespace");
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.createRegexTerminal(ws),
                                cf.starCombinator(
                                        cf.catCombinator(
                                                cListOf(cf.makeNonTerminal(Sym.sym("comment")),
                                                        cf.createRegexTerminal(ws))))));
        return rulesRule;
    }


    private Combinator makeCfgEpsilonRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                        cListOf(cf.stringTerminal("Epsilon"),
                                cf.stringTerminal("epsilon"),
                                cf.stringTerminal("EPSILON"),
                                cf.stringTerminal("eps"),
                                cf.stringTerminal("ε")));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgFactorRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                                cListOf(cf.makeNonTerminal(Sym.sym("nt")),
                                        cf.makeNonTerminal(Sym.sym("string")),
                                        makeNT("regexp", RulesAvailable.REGEX),
                                        makeNT("opt", RulesAvailable.OPTIONAL),
                                        makeNT("star", RulesAvailable.STAR),
                                        makeNT("plus", RulesAvailable.PLUS),
                                        cf.makeNonTerminal(Sym.sym("paren")),
                                        cf.makeNonTerminal(Sym.sym("hide")),
                                        cf.makeNonTerminal(Sym.sym("epsilon")),
                                        makeNT("rep", RulesAvailable.COUNTED_REPEAT), // ABNF feature
                                        makeNT("num-val", RulesAvailable.CHAR_RANGE) // ABNF feature
                                ))
                        .hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgPlusRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.makeNonTerminal(Sym.sym("factor")),
                                optWhitespace,
                                cf.stringTerminal("+").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRuleSeparatorRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                        cListOf(cf.stringTerminal(":"),
                                cf.stringTerminal(":="),
                                cf.stringTerminal("::="),
                                cf.stringTerminal("=")));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgParenRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("(").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")),
                                optWhitespace,
                                cf.stringTerminal(")").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")),
                                optWhitespace,
                                cf.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgStringRhs() {
        final @NotNull Pattern singleQuotedString =
                regexDoc("'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted string");
        final @NotNull Pattern doubleQuotedString =
                regexDoc("\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted string");
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                        cListOf(cf.createRegexTerminal(singleQuotedString),
                                cf.createRegexTerminal(doubleQuotedString)));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRegexRhs() {
        final @NotNull Pattern singleQuotedRegex =
                regexDoc("#'[^'\\\\]*(?:\\\\.[^'\\\\]*)*'", "Single-quoted regexp");
        final @NotNull Pattern doubleQuotedRegex =
                regexDoc("#\\\"[^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*\\\"", "Double-quoted regexp");
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                        cListOf(cf.createRegexTerminal(singleQuotedRegex),
                                cf.createRegexTerminal(doubleQuotedRegex)));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRulesOrParserRhs() {
        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(
                                cListOf(cf.makeNonTerminal(Sym.sym("rules")),
                                        cf.makeNonTerminal(Sym.sym("alt-or-ord"))))
                        .hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgNtRhs() {
        final var pattern = "[^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./]+";
        final var regex = regexDoc("[^, \\r\\t\\n<>(){}\\[\\]+*?:=|'\"#&!;./]+", "Non-terminal");
        final @NotNull Combinator rulesRule =
                cf.catCombinator(cListOf(
                        cf.negateRule(
                                cf.makeNonTerminal(Sym.sym("epsilon"))),
                        cf.createRegexTerminal(regex)));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRepRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(cListOf(
                        cf.choiceCombinator(
                                cListOf(cf.createRegexTerminal(regexDoc("\\-?[0-9]+", "NUM")),
                                        cf.createRegexTerminal(regexDoc("\\-?[0-9]*\\*\\-?[0-9]+", "NUM")),
                                        cf.createRegexTerminal(regexDoc("\\-?[0-9]+\\*\\-?[0-9]*", "NUM")))),
                        optWhitespace,
                        cf.makeNonTerminal(Sym.sym("factor"))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgLookRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("&").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("factor"))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgNegRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("!").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("factor"))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgOneOrMoreRhs() {
        final @NotNull Combinator rulesRuleCurlies =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("{").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")),
                                optWhitespace,
                                cf.stringTerminal("}").enableHideTag()));
        final @NotNull Combinator rulesRuleStar =
                cf.catCombinator(
                        cListOf(cf.makeNonTerminal(Sym.sym("factor")),
                                optWhitespace,
                                cf.stringTerminal("*").enableHideTag()));
        final @NotNull Combinator rule = cf.choiceCombinator(
                cListOf(rulesRuleCurlies, rulesRuleStar));
        return rule;
    }

    private @NotNull Combinator makeCfgOptRhs() {
        final @NotNull Combinator rulesBrackets =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("[").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")),
                                optWhitespace,
                                cf.stringTerminal("]").enableHideTag()));
        final @NotNull Combinator rulesQuestionMark =
                cf.catCombinator(
                        cListOf(cf.makeNonTerminal(Sym.sym("factor")),
                                optWhitespace,
                                cf.stringTerminal("?").enableHideTag()));
        final @NotNull Combinator rule =
                cf.choiceCombinator(cListOf(rulesBrackets, rulesQuestionMark));
        return rule;
    }

    private @Nullable Combinator makeCfgAltOrOrdRhs() {
        final var alt = makeNT("alt", RulesAvailable.CHOICE);
        final var ord = makeNT("ord", RulesAvailable.ORDERED_CHOICE);
        final var rules = cListOf(alt, ord);

        if (rules.isEmpty())
            return null;

        final @NotNull Combinator rulesRule =
                cf.choiceCombinator(rules).hideTag();
        return rulesRule;
    }

    private @NotNull Combinator makeCfgHideNtRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.stringTerminal("<").enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("nt")),
                                optWhitespace,
                                cf.stringTerminal(">").enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgRuleRhs() {
        final @NotNull Combinator optWs = cf.makeNonTerminal(Sym.sym("opt-whitespace"));
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.choiceCombinator(
                                        cListOf(cf.makeNonTerminal(Sym.sym("nt")),
                                                cf.makeNonTerminal(Sym.sym("hide-nt")))),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("rule-separator")).enableHideTag(),
                                optWhitespace,
                                cf.makeNonTerminal(Sym.sym("alt-or-ord")),
                                cf.choiceCombinator(
                                                cListOf(optWs,
                                                        cf.catCombinator(
                                                                cListOf(optWs,
                                                                        cf.choiceCombinator(
                                                                                cListOf(cf.stringTerminal(";"),
                                                                                        cf.stringTerminal("."))),
                                                                        optWs))))
                                        .enableHideTag()));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgOrdRhs() {
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(cf.makeNonTerminal(Sym.sym("cat")),
                                cf.plusCombinator(
                                        cf.catCombinator(
                                                cListOf(optWhitespace,
                                                        cf.stringTerminal("/").enableHideTag(),
                                                        optWhitespace,
                                                        cf.makeNonTerminal(Sym.sym("cat")))))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgAltRhs() {
        final @NotNull Combinator catNt = cf.makeNonTerminal(Sym.sym("cat"));
        final @NotNull Combinator rulesRule =
                cf.catCombinator(
                        cListOf(catNt,
                                cf.starCombinator(cf.catCombinator(
                                        cListOf(
                                                optWhitespace,
                                                cf.stringTerminal("|").enableHideTag(),
                                                optWhitespace,
                                                catNt)))));
        return rulesRule;
    }

    private @NotNull Combinator makeCfgCatRhs() {
        final @NotNull Combinator factorLookNeg = cf.choiceCombinator(cListOf(
                cf.makeNonTerminal(Sym.sym("factor")),
                makeNT("look", RulesAvailable.LOOKAHEAD),
                makeNT("neg", RulesAvailable.NEGATIVE_LOOKAHEAD)
        ));
        final @NotNull Combinator rulesRule =
                cf.plusCombinator(
                        cf.catCombinator(
                                cListOf(optWhitespace,
                                        factorLookNeg,
                                        optWhitespace)));
        return rulesRule;
    }

    private @NotNull Combinator makeABNFNumVal() {
        final @NotNull Combinator connectingMinusTerminal = cf.stringTerminal("-").enableHideTag();

        @NotNull Combinator regexCombinatorBin = cf.createRegexTerminal(Pattern.compile("[01]+"));
        final Combinator binaryVal = cf.catCombinator(cListOf(
                cf.stringTerminal("b"), // binary indicator
                regexCombinatorBin,
                cf.optionalCombinator(cf.catCombinator(
                        cListOf(connectingMinusTerminal, regexCombinatorBin)))
        ));

        @NotNull Combinator regexCombinatorDec = cf.createRegexTerminal(Pattern.compile("[0-9]+"));
        final Combinator decimalVal = cf.catCombinator(cListOf(
                cf.stringTerminal("d"), // decimal indicator
                regexCombinatorDec,
                cf.optionalCombinator(cf.catCombinator(
                        cListOf(connectingMinusTerminal, regexCombinatorDec)))
        ));

        @NotNull Combinator regexCombinatorHex = cf.createRegexTerminal(Pattern.compile("[0-9a-fA-F]+"));
        final Combinator hexadecimalVal = cf.catCombinator(cListOf(
                cf.stringTerminal("x"), // hexadecimal indicator
                regexCombinatorHex,
                cf.optionalCombinator(cf.catCombinator(
                        cListOf(connectingMinusTerminal, regexCombinatorHex)))
        ));
        return cf.catCombinator(cListOf(
                cf.stringTerminal("%").enableHideTag(),
                cf.choiceCombinator(cListOf(binaryVal, decimalVal, hexadecimalVal))));
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

        if (rulesAvailable.contains(RulesAvailable.CHOICE) || rulesAvailable.contains(RulesAvailable.ORDERED_CHOICE))
            grammarMap.put(Sym.sym("alt-or-ord"), makeCfgAltOrOrdRhs());

        if (rulesAvailable.contains(RulesAvailable.CHOICE))
            grammarMap.put(Sym.sym("alt"), makeCfgAltRhs());

        if (rulesAvailable.contains(RulesAvailable.ORDERED_CHOICE))
            grammarMap.put(Sym.sym("ord"), makeCfgOrdRhs()); // Technically ABNF, but should be included without it as a PAKRAT extension.

        if (rulesAvailable.contains(RulesAvailable.COUNTED_REPEAT))
            grammarMap.put(Sym.sym("rep"), makeCfgRepRhs()); // ABNF

        if (rulesAvailable.contains(RulesAvailable.REGEX))
            grammarMap.put(Sym.sym("regexp"), makeCfgRegexRhs());

        if (rulesAvailable.contains(RulesAvailable.OPTIONAL))
            grammarMap.put(Sym.sym("opt"), makeCfgOptRhs());

        if (rulesAvailable.contains(RulesAvailable.STAR))
            grammarMap.put(Sym.sym("star"), makeCfgOneOrMoreRhs());

        if (rulesAvailable.contains(RulesAvailable.PLUS))
            grammarMap.put(Sym.sym("plus"), makeCfgPlusRhs());

        if (rulesAvailable.contains(RulesAvailable.LOOKAHEAD))
            grammarMap.put(Sym.sym("look"), makeCfgLookRhs());

        if (rulesAvailable.contains(RulesAvailable.NEGATIVE_LOOKAHEAD))
            grammarMap.put(Sym.sym("neg"), makeCfgNegRhs());

        if (rulesAvailable.contains(RulesAvailable.CHAR_RANGE))
            grammarMap.put(Sym.sym("num-val"), makeABNFNumVal()); // ABNF

        return new Grammar(grammarMap).applyStandardReductions(cs);
    }

    @NotNull
    static Grammar makeCfg(final @NotNull Alpha.ParserCreationOptions options) {
        final @NotNull EbnfG g = new EbnfG(options);
        return g.makeCfg();
    }
}
