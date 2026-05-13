package alphaparse;

import alphaparse.grammar.Grammar;
import alphaparse.parser.*;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import alphaparse.result.AlphaParseFailure;
import alphaparse.util.StrParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

final class Cfg {
    private static @NotNull Combinator stringOrStringCaseInsensitiveCombinator(
            final @NotNull String s, final boolean caseInsensitiveByDefault,
            final @NotNull CombinatorFactory combinatorFactory,
            final @NotNull ParserCreationOptions options) {
        return switch (options.stringCaseInsensitive()) {
            case TRUE -> combinatorFactory.stringOrStringCiTerminal(s, true);
            case FALSE -> combinatorFactory.stringOrStringCiTerminal(s, false);
            default -> combinatorFactory.stringOrStringCiTerminal(s, caseInsensitiveByDefault);
        };
    }

    private static @NotNull Combinator buildRepRule(final @NotNull ParseTree tree,
                                                    final @NotNull CombinatorFactory combinatorFactory,
                                                    final @NotNull ParserCreationOptions options) {
        final @NotNull var partsUncut = (String) tree.getContent().getFirst().content();
        @NotNull var parts = partsUncut.split("\\*");
        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("Invalid format for repetition rule: " + partsUncut);
        }
        if (parts.length == 1) {
            final @NotNull var temp = new String[]{"", ""};
            if (partsUncut.charAt(0) == '*') { // Only maximum provided (e.g. `*n p`)
                temp[1] = parts[0];
            } else if (partsUncut.charAt(partsUncut.length() - 1) == '*') {// Only minimum provided (e.g. `n* p`
                temp[0] = parts[0];
            } else { // Only an exact number is given: Both minimum and maximum. (e.g. `n p`).
                temp[0] = parts[0];
                temp[1] = temp[0];
            }
            parts = temp;
        }
        final int min = parts[0].isBlank() ? 0 : Integer.parseInt(parts[0]);
        final int max = parts[1].isBlank() ? Integer.MAX_VALUE : Integer.parseInt(parts[1]);
        final @NotNull var repeatedRule = (Combinator) buildRule((ParseTree)
                        tree.getContent().get(1).content(),
                combinatorFactory,
                options);
        return combinatorFactory.repetitionCombinator(min, max, repeatedRule);
    }

    private static @NotNull Map.Entry<@NotNull Sym, @NotNull Combinator> buildRuleRule(
            final @NotNull ParseTree tree,
            final @NotNull CombinatorFactory combinatorFactory,
            final @NotNull ParserCreationOptions options) {
        final @NotNull var allContents = tree.getContent();
        final @NotNull var nt = (ParseTree) allContents.getFirst().content();
        final @NotNull var altOrOrd = (ParseTree) allContents.get(1).content();
        @NotNull var content = nt.getContent().getFirst();

        final @NotNull Sym key;
        final @NotNull Combinator rule;

        if (Objects.equals(Sym.sym("hide-nt"), nt.getTag().content())) {
            content = ((ParseTree) content.content()).getContent().getFirst();
            key = Sym.sym(content.content().toString());
            rule = combinatorFactory.hideTag((Combinator) buildRule(altOrOrd, combinatorFactory, options));
        } else {
            key = Sym.sym((String)content.content());
            rule = (Combinator) buildRule(altOrOrd, combinatorFactory, options);
        }

        return Map.entry(key, rule);
    }

    private static @NotNull Object buildRule(@NotNull ParseTree tree,
                                             final @NotNull CombinatorFactory combinatorFactory,
                                             final @NotNull ParserCreationOptions options) {
        for (; ; ) {
            if (tree.getTag().content().equals(ParseTree.NULL_TAG)) {
                tree = (ParseTree) tree.getContent().getFirst().content();
                continue;
            }

            final @NotNull var tag = tree.getTag().content().name();
            switch (tag) {
                case "rule" -> {
                    return buildRuleRule(tree, combinatorFactory, options);
                }
                case "nt" -> {
                    return combinatorFactory.makeNonTerminal(
                            Sym.sym((String) tree.getContent().getFirst().content()));
                }
                case "alt" -> {
                    return combinatorFactory.choiceCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorFactory, options))
                            .toList());
                }
                case "ord" -> {
                    return combinatorFactory.orderedChoiceCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorFactory, options))
                            .toList());
                }
                case "hide" -> {
                    return ((Combinator) buildRule(
                            ((Node.NodeParseTree) tree.getContent().getFirst()).content(),
                            combinatorFactory, options)).enableHideTag();
                }
                case "cat" -> {
                    return combinatorFactory.catCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorFactory, options))
                            .toList());
                }
                case "string" -> {
                    return stringOrStringCaseInsensitiveCombinator(
                            StrParser.processString((String) tree.getContent().getFirst().content()),
                            false, combinatorFactory, options);
                }
                case "string-ci" -> {
                    return stringOrStringCaseInsensitiveCombinator(
                            StrParser.processString((String) tree.getContent().getFirst().content()),
                            true, combinatorFactory, options);
                }
                case "regexp" -> {
                    return combinatorFactory.createRegexTerminal(
                            StrParser.processRegexp((String) tree.getContent().getFirst().content()));
                }
                case "neg" -> {
                    return combinatorFactory.negateRule((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorFactory, options));
                }
                case "opt" -> {
                    return combinatorFactory.optionalCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorFactory, options));
                }
                case "star" -> {
                    return combinatorFactory.starCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorFactory, options));
                }
                case "plus" -> {
                    return combinatorFactory.plusCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorFactory, options));
                }
                case "look" -> {
                    return combinatorFactory.makeLookahead((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorFactory, options));
                }
                case "rep" -> {
                    return buildRepRule(tree, combinatorFactory, options);
                }
                case "epsilon" -> {
                    return EpsilonCombinator.getDefault();
                }
                case "paren" -> {
                    // The parse tree is wrapped in hidden "(" ")".
                    tree = (ParseTree) tree.getContent().getFirst().content();
                    continue; // Open up the grouping and take it to the top.
                }
                case "num-val" -> {
                    var content = tree.getContent();
                    var prefix = (String) content.get(0).content(); // "b"/"d"/"x"
                    final int radix = switch (prefix.charAt(0)) {
                        case 'b' -> 2;
                        case 'd' -> 10;
                        case 'x' -> 16;
                        default -> throw new IllegalStateException();
                    };
                    var rangeFirst = Integer.parseInt(
                            (String) content.get(1).content(),
                            radix);
                    var rangeLast = content.size() > 2
                            ? Integer.parseInt((String) content.get(2).content(), radix)
                            : rangeFirst;
                    return combinatorFactory.unicodeChar(rangeFirst, rangeLast);
                }
            }
            throw new UnsupportedOperationException(tag);
        }
    }

    private static @NotNull Grammar checkGrammarValidity(final @NotNull Grammar g) {
        final @NotNull var analysisResult = g.analyze();
        if (!analysisResult.isValid())
            throw new IllegalStateException(
                    "The keys "
                            + analysisResult.getUndefinedUsedNTs()
                            + " appear on the right-hand side of the grammar, but not on the left.");

        return g;
    }

    static @NotNull Parser buildParserFromCombinators(final @NotNull Grammar grammar,
                                                      final @NotNull ParserCreationOptions options) {
        if (options.startProduction() == null)
            throw new IllegalArgumentException("No start production provided.");
        return new Parser(
                Cfg.checkGrammarValidity(grammar.applyStandardReductions(new CombinatorFactory(true))),
                options.startProduction());
    }

    static @NotNull Parser buildParser(final @NotNull String spec,
                                       final @NotNull ParserCreationOptions options,
                                       final @NotNull Grammar grammarGrammar) {
        final @NotNull var rules = Gll.parse(
                grammarGrammar,
                Sym.sym("rules"),
                spec, false);
        if (rules instanceof AlphaParseFailure) {
            throw new IllegalStateException("Error parsing grammar specification:\n" + rules + "\n");
        }

        final @NotNull var productions = new ArrayList<Map.Entry<Sym, Combinator>>();
        final @NotNull CombinatorFactory combinatorFactory = new CombinatorFactory(
                options.useParserBuffering());

        for (final Node rule : rules.castToParseSuccess().getContent()) {
            productions.add(buildRuleRule((ParseTree) rule.content(), combinatorFactory, options));
        }

        final @NotNull var startProduction = options.startProduction() != null
                ? options.startProduction()
                : productions.getFirst().getKey();

        if (options.usableRules().contains(RulesAvailable.ABNF_CORE)){
            var abnfCore = EbnfG.makeAbnfCoreRules();
            productions.addAll(0, abnfCore);
        }

        @NotNull var grammar = checkGrammarValidity(
                Grammar.fromProductions(productions, options.productionRedefinitionOption())
                        .applyStandardReductions(combinatorFactory));

        if (options.whitespaceParser() != null) {
            grammar = combinatorFactory.autoWhitespace(
                    grammar,
                    startProduction,
                    options.whitespaceParser().grammar(),
                    options.whitespaceParser().startProduction()
            );
        }

        return new Parser(grammar, startProduction);
    }
}