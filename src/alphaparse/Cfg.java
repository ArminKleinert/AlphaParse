package alphaparse;

import alphaparse.error.IllegalGrammarException;
import alphaparse.error.ParserCreationFailure;
import alphaparse.grammar.Grammar;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.parser.*;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import alphaparse.parsing.*;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
import alphaparse.result.*;
import alphaparse.util.StrParser;
import org.jetbrains.annotations.NotNull;

import java.util.*;

final class Cfg {
    private final @NotNull CombinatorFactory combinatorFactory;
    private final @NotNull ParserCreationOptions options;

    private Cfg(final @NotNull ParserCreationOptions options) {
        this.combinatorFactory = new CombinatorFactory();
        this.options = options;
    }

    static @NotNull Cfg make(final @NotNull ParserCreationOptions options) {
        return new Cfg(options);
    }

    private static class GrammarBuild extends GrammarBuilder {
        String spec;
        Grammar grammarGrammar;

        public GrammarBuild(@NotNull ParserCreationOptions options, String spec, Grammar grammarGrammar) {
            super(options);
            this.spec = spec;
            this.grammarGrammar = grammarGrammar;
        }


        private @NotNull Combinator stringOrStringCaseInsensitiveCombinator(
                final @NotNull String s) {
            return switch (options.stringCaseInsensitive()) {
                case TRUE -> string(s, true);
                case FALSE, DEFAULT -> string(s, false);
            };
        }

        private @NotNull Combinator buildRepRule(final @NotNull ParseTree tree) {
            final @NotNull var partsUncut = (String) tree.getContent().getFirst().content();
            @NotNull var parts = partsUncut.split("\\*");
            if (parts.length == 1) {
            /*
            Format at this point is [0-9]+\\* or \\*[0-9]+ or [0-9]+
             */
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
            } else if (parts.length == 0) { // The input was only "*"
                parts = new String[]{"", ""};
            } else if (parts.length > 2) { // The input included more than one "*"
                throw new IllegalArgumentException("Invalid format for repetition rule: " + partsUncut);
            }
            // If none of the cases were true, the input had the usual format [0-9]+\\*[0-9]+
            final int min = parts[0].isBlank() ? 0 : Integer.parseInt(parts[0]);
            final int max = parts[1].isBlank() ? Integer.MAX_VALUE : Integer.parseInt(parts[1]);
            final @NotNull var repeatedRule =
                    (Combinator) buildRule((ParseTree) tree.getContent().get(1).content());
            return repeat(repeatedRule, min, max);
        }

        private @NotNull Map.Entry<@NotNull Sym, @NotNull Combinator> buildRuleRule(
                final @NotNull ParseTree tree) {
            final @NotNull var allContents = tree.getContent();
            final @NotNull var nt = (ParseTree) allContents.getFirst().content();
            final @NotNull var altOrOrd = (ParseTree) allContents.get(1).content();
            @NotNull var content = nt.getContent().getFirst();

            final @NotNull Sym key;
            final @NotNull Combinator rule;

            if (Objects.equals(Sym.sym("hide-nt"), nt.getTag().content())) {
                content = ((ParseTree) content.content()).getContent().getFirst();
                key = Sym.sym(content.content().toString());
                rule = new CombinatorFactory().hideTag((Combinator) buildRule(altOrOrd));
            } else {
                key = Sym.sym((String) content.content());
                rule = (Combinator) buildRule(altOrOrd);
            }

            return Map.entry(key, rule);
        }

        private final @NotNull StrParser strParser = new StrParser();

        private @NotNull Object buildRule(final @NotNull ParseTree tree1) {
            @NotNull ParseTree tree = tree1;
            for (; ; ) {
                if (tree.getTag().equals(ParseTree.NULL_TAG)) {
                    tree = (ParseTree) tree.getContent().getFirst().content();
                    continue;
                }

                final @NotNull var tag = tree.getTag().content().name();
                switch (tag) {
                    case "rule" -> {
                        return buildRuleRule(tree);
                    }
                    case "nt" -> {
                        var name = (String) tree.getContent().getFirst().content();
                        return nt(Sym.sym(name));
                    }
                    case "paren" -> {
                        // The parse tree is wrapped in hidden "(" ")".
                        tree = (ParseTree) tree.getContent().getFirst().content();
                        continue; // Open up the grouping and take it to the top.
                    }
                    case "alt" -> {
                        return alternationC(tree
                                .getContent()
                                .stream()
                                .map((c) -> (Combinator) buildRule(
                                        (ParseTree) c.content()))
                                .map(this::of)
                                .toList());
                    }
                    case "ord" -> {
                        return orderedChoice(tree
                                .getContent()
                                .stream()
                                .map((c) ->
                                        buildRule((ParseTree) c.content()))
                                .toList());
                    }
                    case "hide" -> {
                        return ((Combinator) buildRule(
                                ((Node.NodeParseTree) tree.getContent().getFirst()).content())).enableHideTag();
                    }
                    case "cat" -> {
                        return concat(tree
                                .getContent()
                                .stream()
                                .map((c) -> (Combinator) buildRule(
                                        (ParseTree) c.content()))
                                .toList());
                    }
                    case "string" -> {
                        String s = (String) tree.getContent().getFirst().content();
                        if (s.startsWith("%")) {
                            boolean caseInsensitive = switch (s.charAt(1)) {
                                case 'i' -> true;
                                case 's' -> false;
                                default -> throw new IllegalStateException();
                            };
                            return string(
                                    strParser.processString(s.substring(2)), caseInsensitive);
                        }
                        return stringOrStringCaseInsensitiveCombinator(
                                strParser.processString(s));
                    }
                    case "string-cs" -> {
                        return stringCS(
                                strParser.processString((String) tree.getContent().getFirst().content()));
                    }
                    case "string-ci" -> {
                        return stringCI(
                                strParser.processString((String)
                                        tree.getContent().getFirst().content()));
                    }
                    case "regexp" -> {
                        return of(
                                strParser.processRegexp((String)
                                        tree.getContent().getFirst().content()));
                    }
                    case "neg" -> {
                        return negate(buildRule(
                                (ParseTree) tree.getContent().getFirst().content()));
                    }
                    case "opt", "opt_query" -> {
                        return optional((Combinator) buildRule(
                                (ParseTree) tree.getContent().getFirst().content()));
                    }
                    case "star", "opt_rep" -> {
                        return zeroOrMore((Combinator) buildRule(
                                (ParseTree) tree.getContent().getFirst().content()));
                    }
                    case "plus" -> {
                        return onceOrMore((Combinator) buildRule(
                                (ParseTree) tree.getContent().getFirst().content()));
                    }
                    case "look" -> {
                        return lookahead(buildRule(
                                (ParseTree) tree.getContent().getFirst().content()));
                    }
                    case "rep" -> {
                        try {
                            return buildRepRule(tree);
                        } catch (IllegalArgumentException exception) {
                            throw new ParserCreationFailure(exception);
                        }
                    }
                    case "abnf-range" -> {
                        var content = tree.getContent();
                        var parts = ((String) content.get(0).content()).split("-");
                        var prefix = parts[0]; // "%b..."/"%d..."/"%x..."
                        final int radix = switch (prefix.charAt(1)) {
                            case 'b' -> 2;
                            case 'd' -> 10;
                            case 'x' -> 16;
                            default -> throw new ParserCreationFailure("Invalid format for value range.");
                        };
                        var rangeFirst = Integer.parseInt(
                                parts[0].substring(2),
                                radix);
                        var rangeLast = parts.length == 1
                                ? rangeFirst
                                : Integer.parseInt(parts[1], radix);
                        return unicodeChar(rangeFirst, rangeLast);
                    }
                    case "epsilon" -> {
                        return EpsilonCombinator.getDefault();
                    }
                    case "exclude" -> {
                        var rule1 = (Combinator) buildRule((ParseTree)
                                tree.getContent().get(0).content());
                        var rule2 = (Combinator) buildRule((ParseTree)
                                tree.getContent().get(1).content());
                        return exclude(rule1, rule2);
                    }
                    case "eof" -> {
                        return EOFCombinator.getDefault();
                    }
                }
                throw new UnsupportedOperationException(tag);
            }
        }

        @Override
        public void make() {
            final @NotNull AlphaParseResult rules = Gll.parse(
                    grammarGrammar,
                    Sym.sym("rules"),
                    spec, false, false);

            if (rules instanceof AlphaParseFailure) {
                throw new ParserCreationFailure(
                        "Error parsing grammar specification:\n" + rules + "\n");
            }

            for (final Node rule : rules.castToParseSuccess().getContent()) {
                var sc = buildRuleRule((ParseTree) rule.content());
                addProduction(sc.getKey(), sc.getValue());
            }
            if (options.usableRules().contains(RulesAvailable.ABNF_CORE)) {
                var abnfCore = CfgGrammar.makeAbnfCoreRules();
                addAllProductions(abnfCore);
            }
        }
    }

    @NotNull Parser buildParserFromCombinators(final @NotNull Grammar grammar) {
        if (options.startProduction() == null)
            throw new ParserCreationFailure("No start production provided.");
        try {
            var validatedGrammar = options.checkCorrectness()
                    ? grammar.checkGrammarValidity()
                    : grammar;
            return new Parser(
                    validatedGrammar.applyStandardReductions(combinatorFactory),
                    options.startProduction());
        } catch (IllegalGrammarException exception) {
            throw new ParserCreationFailure(exception);
        }
    }

    @NotNull Parser buildParser(final @NotNull String spec,
                                final @NotNull Grammar grammarGrammar) {
        var gb = new GrammarBuild(options, spec, grammarGrammar);

        var grammar = gb.buildWithWhitespace(options.whitespaceParser());
        final @NotNull var startProduction = options.startProduction() != null
                ? options.startProduction()
                : Objects.requireNonNull(grammar.getStartSym());

        return new Parser(grammar, startProduction);
    }
}