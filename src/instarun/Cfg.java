package instarun;

import instarun.parser.Grammar;
import instarun.parsetree.Node;
import instarun.reduction.ReductionType;
import instarun.result.ParseTree;
import instarun.parser.Parser;
import instarun.parser.Reduction;
import instarun.parser.combinator.*;
import instarun.result.InstaFailure;
import instarun.util.StrParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Cfg {

    public enum GlobalCaseInsensitivity {
        TRUE, FALSE, DEFAULT
    }

    private static Combinator stringOrStringCaseInsensitiveCombinator(
            final @NotNull String s, final boolean caseInsensitiveByDefault,
            final @NotNull CombinatorsSource combinatorsSource,
            final @NotNull Insta.ParserCreationOptions options) {
        return switch (options.isStringCaseInsensitive()) {
            case TRUE -> combinatorsSource.stringOrStringCiTerminal(s, true);
            case FALSE -> combinatorsSource.stringOrStringCiTerminal(s, false);
            default -> combinatorsSource.stringOrStringCiTerminal(s, caseInsensitiveByDefault);
        };
    }

    public static @NotNull Combinator buildRepRule(final @NotNull ParseTree tree,
                                                   final @NotNull CombinatorsSource combinatorsSource,
                                                   final @NotNull Insta.ParserCreationOptions options) {
        final var partsUncut = (String) tree.getContent().getFirst().content();
        var parts = partsUncut.split("\\*");
        if (parts.length == 0 || parts.length > 2) {
            throw new IllegalArgumentException("Invalid format for repetition rule: " + partsUncut);
        }
        if (parts.length == 1) {
            var temp = new String[]{"", ""};
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
        return combinatorsSource.repetitionCombinator(min, max, (Combinator) buildRule((ParseTree) tree.getContent().get(1).content(), combinatorsSource, options));
    }

    private static @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> buildRuleRule(
            final @NotNull ParseTree tree,
            final @NotNull CombinatorsSource combinatorsSource,
            final @NotNull Insta.ParserCreationOptions options) {
        final var allContents = tree.getContent();
        final var nt = (ParseTree) allContents.getFirst().content();
        final var altOrOrd = (ParseTree) allContents.get(1).content();

        var content = nt.getContent().getFirst();
        var rule = (Combinator) buildRule(altOrOrd, combinatorsSource, options);

        if (Objects.equals(Keyword.intern("hide-nt"), nt.getTag().content())) {
            content = ((ParseTree) content.content()).getContent().getFirst();
            rule = combinatorsSource.hideTag(rule);
        }

        return Grammar.entry(Keyword.intern((String) content.content()), rule);
    }

    private static @NotNull Object buildRule(@NotNull ParseTree tree,
                                             final @NotNull CombinatorsSource combinatorsSource,
                                             final @NotNull Insta.ParserCreationOptions options) {
        do {
            final var tag = tree.getTag().content();
            if (Objects.equals(tag, Keyword.intern("rule"))) {
                return buildRuleRule(tree, combinatorsSource, options);
            } else if (Objects.equals(tag, Keyword.intern("nt"))) {
                return combinatorsSource.makeNonTerminal(Keyword.intern((String) tree.getContent().getFirst().content()));
            } else if (Objects.equals(tag, Keyword.intern("alt"))) {
                return combinatorsSource.alternationCombinator(tree.getContent()
                        .stream().map((c) -> (Combinator) buildRule((ParseTree) c.content(), combinatorsSource, options))
                        .toList());
            } else if (Objects.equals(tag, Keyword.intern("ord"))) {
                return combinatorsSource.orderedChoiceCombinator(tree.getContent()
                        .stream().map((c) -> (Combinator) buildRule((ParseTree) c.content(), combinatorsSource, options))
                        .toList());
            } else if (Objects.equals(tag, Keyword.intern("hide"))) {
                return ((Combinator) tree.getContent().getFirst().content()).enableHideTag();
            } else if (Objects.equals(tag, Keyword.intern("cat"))) {
                return combinatorsSource.catCombinator(tree.getContent()
                        .stream().map((c) -> (Combinator) buildRule((ParseTree) c.content(), combinatorsSource, options))
                        .toList());
            } else if (Objects.equals(tag, Keyword.intern("string"))) {
                return stringOrStringCaseInsensitiveCombinator(StrParser.processString((String) tree.getContent().getFirst().content()), false, combinatorsSource, options);
            } else if (Objects.equals(tag, Keyword.intern("string-ci"))) {
                return stringOrStringCaseInsensitiveCombinator(StrParser.processString((String) tree.getContent().getFirst().content()), true, combinatorsSource, options);
            } else if (Objects.equals(tag, Keyword.intern("regexp"))) {
                return combinatorsSource.createRegexTerminal(StrParser.processRegexp((String) tree.getContent().getFirst().content()));
            } else if (Objects.equals(tag, Keyword.intern("neg"))) {
                return combinatorsSource.negateRule((Combinator) buildRule((ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
            } else if (Objects.equals(tag, Keyword.intern("opt"))) {
                return combinatorsSource.optionalCombinator((Combinator) buildRule((ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
            } else if (Objects.equals(tag, Keyword.intern("star"))) {
                return combinatorsSource.starCombinator((Combinator) buildRule((ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
            } else if (Objects.equals(tag, Keyword.intern("plus"))) {
                return combinatorsSource.plusCombinator((Combinator) buildRule((ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
            } else if (Objects.equals(tag, Keyword.intern("look"))) {
                return combinatorsSource.makeLookahead((Combinator) buildRule((ParseTree) tree.getContent().getFirst().content(), combinatorsSource, options));
            } else if (Objects.equals(tag, Keyword.intern("rep"))) {
                return buildRepRule(tree, combinatorsSource, options);
            } else if (Objects.equals(tag, Keyword.intern("epsilon"))) {
                return CombinatorsSource.epsilon;
            } else if (Objects.equals(tag, Keyword.intern("paren"))) {
                tree = (ParseTree) tree.getContent().getFirst().content();
                continue; // Tail recursion (somewhat)
            } else if (Objects.equals(tag, Keyword.intern("\0\0\0\0"))) {
                tree = (ParseTree) tree.getContent().getFirst().content();
                continue;
            }
            throw new UnsupportedOperationException(tag.toString());
        } while (true);
    }

    private static @NotNull Grammar checkGrammarValidity(final @NotNull Grammar g) {
        final @NotNull var analysisResult = g.analyze();
        if (!analysisResult.isValid())
            throw new IllegalStateException(
                    "The keys " + analysisResult.getUndefinedUsedNTs() + " appear on the right-hand side of the grammar, but not on the left.");

        return g;
    }

    public static @NotNull Parser buildParserFromCombinators(final @NotNull Grammar grammarMap,
                                                             final @NotNull Insta.ParserCreationOptions options) {
        return new Parser(Cfg.checkGrammarValidity(Reduction.applyStandardReductions(grammarMap)), options.getStartProduction(), options.getOutputFormat());
    }

    public static @NotNull Parser buildParser(final @NotNull String spec,
                                              final @NotNull Insta.ParserCreationOptions options) {
        var rules = Gll.parse(EbnfG.makeCfg(), Keyword.intern("rules"), spec, false);
        if (rules instanceof InstaFailure) {
            throw new IllegalStateException("Error parsing grammar specification:\n" + rules + "\n");
        }
        @NotNull var productions = new ArrayList<Map.Entry<Keyword, Combinator>>();
        // System.out.println(rules);
        final @NotNull CombinatorsSource combinatorsSource = new CombinatorsSource();
        for (final Node rule : rules.castToParseSuccess().getContent()) {
            productions.add(buildRuleRule((ParseTree) rule.content(), combinatorsSource, options));
        }
        var startProduction = productions.getFirst().getKey();
        return new Parser(
                checkGrammarValidity(Reduction.applyStandardReductions(Grammar.fromProductions(productions))),
                startProduction,
                options.getOutputFormat());
    }
}