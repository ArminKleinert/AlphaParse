package alphaparse;

import alphaparse.parser.*;
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
            final @NotNull CombinatorsSource combinatorsSource,
            final @NotNull Alpha.ParserCreationOptions options) {
        return switch (options.stringCaseInsensitive()) {
            case TRUE -> combinatorsSource.stringOrStringCiTerminal(s, true);
            case FALSE -> combinatorsSource.stringOrStringCiTerminal(s, false);
            default -> combinatorsSource.stringOrStringCiTerminal(s, caseInsensitiveByDefault);
        };
    }

    private static @NotNull Combinator buildRepRule(final @NotNull ParseTree tree,
                                                    final @NotNull CombinatorsSource combinatorsSource,
                                                    final @NotNull Alpha.ParserCreationOptions options) {
        final @NotNull var partsUncut = (String) tree.getContent().getFirst().content();
        @NotNull var parts = partsUncut.split("\\*");
        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("Invalid format for repetition rule: " + partsUncut);
        }
        if (parts.length == 1) {
            final @NotNull var temp = new String[]{"", ""};
            if (partsUncut.charAt(0) == '*') { // Only maximum provided
                temp[1] = parts[0];
            } else if (partsUncut.charAt(partsUncut.length() - 1) == '*') {// Only minimum provided
                temp[0] = parts[0];
            } else { // Only an exact number is given: Both minimum and maximum.
                temp[0] = parts[0];
                temp[1] = temp[0];
            }
            parts = temp;
        }
        final int min = parts[0].isBlank() ? 0 : Integer.parseInt(parts[0]);
        final int max = parts[1].isBlank() ? Integer.MAX_VALUE : Integer.parseInt(parts[1]);
        final @NotNull var repeatedRule = (Combinator) buildRule((ParseTree)
                        tree.getContent().get(1).content(),
                combinatorsSource,
                options);
        return combinatorsSource.repetitionCombinator(min, max, repeatedRule);
    }

    private static @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> buildRuleRule(
            final @NotNull ParseTree tree,
            final @NotNull CombinatorsSource combinatorsSource,
            final @NotNull Alpha.ParserCreationOptions options) {
        final @NotNull var allContents = tree.getContent();
        final @NotNull var nt = (ParseTree) allContents.getFirst().content();
        final @NotNull var altOrOrd = (ParseTree) allContents.get(1).content();
        @NotNull var content = nt.getContent().getFirst();

        final @NotNull Keyword key;
        final @NotNull Combinator rule;

        if (Objects.equals(Keyword.intern("hide-nt"), nt.getTag().content())) {
            content = ((ParseTree) content.content()).getContent().getFirst();
            key = Keyword.intern(content.toString());
            rule = combinatorsSource.hideTag((Combinator) buildRule(altOrOrd, combinatorsSource, options));
        } else {
            key = Keyword.intern((String) content.content());
            rule = (Combinator) buildRule(altOrOrd, combinatorsSource, options);
        }

        return Grammar.entry(key, rule);
    }

    private static @NotNull Object buildRule(@NotNull ParseTree tree,
                                             final @NotNull CombinatorsSource combinatorsSource,
                                             final @NotNull Alpha.ParserCreationOptions options) {
        for (; ; ) {
            if (tree.getTag().content().equals(ParseTree.NULL_TAG)) {
                tree = (ParseTree) tree.getContent().getFirst().content();
                continue;
            }

            final @NotNull var tag = tree.getTag().content().getName();
            switch (tag) {
                case "rule" -> {
                    return buildRuleRule(tree, combinatorsSource, options);
                }
                case "nt" -> {
                    return combinatorsSource.makeNonTerminal(
                            Keyword.intern((String) tree.getContent().getFirst().content()));
                }
                case "alt" -> {
                    return combinatorsSource.alternationCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorsSource, options))
                            .toList());
                }
                case "ord" -> {
                    return combinatorsSource.orderedChoiceCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorsSource, options))
                            .toList());
                }
                case "hide" -> {
                    return ((Combinator) buildRule(
                            ((Node.NodeParseTree) tree.getContent().getFirst()).content(),
                            combinatorsSource, options)).enableHideTag();
                }
                case "cat" -> {
                    return combinatorsSource.catCombinator(tree.getContent()
                            .stream().map((c) -> (Combinator) buildRule(
                                    (ParseTree) c.content(), combinatorsSource, options))
                            .toList());
                }
                case "string" -> {
                    return stringOrStringCaseInsensitiveCombinator(
                            StrParser.processString((String) tree.getContent().getFirst().content()),
                            false, combinatorsSource, options);
                }
                case "string-ci" -> {
                    return stringOrStringCaseInsensitiveCombinator(
                            StrParser.processString((String) tree.getContent().getFirst().content()),
                            true, combinatorsSource, options);
                }
                case "regexp" -> {
                    return combinatorsSource.createRegexTerminal(
                            StrParser.processRegexp((String) tree.getContent().getFirst().content()));
                }
                case "neg" -> {
                    return combinatorsSource.negateRule((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
                }
                case "opt" -> {
                    return combinatorsSource.optionalCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
                }
                case "star" -> {
                    return combinatorsSource.starCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
                }
                case "plus" -> {
                    return combinatorsSource.plusCombinator((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
                }
                case "look" -> {
                    return combinatorsSource.makeLookahead((Combinator) buildRule(
                            (ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
                }
                case "rep" -> {
                    return buildRepRule(tree, combinatorsSource, options);
                }
                case "epsilon" -> {
                    return EpsilonCombinator.getDefault();
                }
                case "paren" -> {
                    tree = (ParseTree) tree.getContent().getFirst().content();
                    continue; // Open up the grouping and take it to the top.
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

    static @NotNull Parser buildParserFromCombinators(final @NotNull Grammar grammarMap,
                                                      final @NotNull Alpha.ParserCreationOptions options) {
        if (options.startProduction() == null)
            throw new IllegalArgumentException("No start production provided.");
        return new Parser(
                Cfg.checkGrammarValidity(Reduction.applyStandardReductions(grammarMap)),
                options.startProduction(),
                options.outputFormat());
    }

    static @NotNull Parser buildParser(final @NotNull String spec,
                                       final @NotNull Alpha.ParserCreationOptions options) {
        final @NotNull var rules = Gll.parse(
                EbnfG.makeCfg(),
                Keyword.intern("rules"),
                spec, false);
        if (rules instanceof AlphaParseFailure) {
            throw new IllegalStateException("Error parsing grammar specification:\n" + rules + "\n");
        }

        final @NotNull var productions = new ArrayList<Map.Entry<Keyword, Combinator>>();
        final @NotNull CombinatorsSource combinatorsSource = new CombinatorsSource();

        for (final Node rule : rules.castToParseSuccess().getContent()) {
            productions.add(buildRuleRule((ParseTree) rule.content(), combinatorsSource, options));
        }

        final @NotNull var startProduction = options.startProduction() != null
                ? options.startProduction()
                : productions.getFirst().getKey();

        @NotNull var grammar =
                checkGrammarValidity(Reduction.applyStandardReductions(Grammar.fromProductions(productions)));

        if (options.whitespaceParser() != null) {
            grammar = combinatorsSource.autoWhitespace(
                    grammar,
                    startProduction,
                    options.whitespaceParser().grammar(),
                    options.whitespaceParser().startProduction()
            );
        }

        return new Parser(grammar, startProduction, options.outputFormat());
    }
}