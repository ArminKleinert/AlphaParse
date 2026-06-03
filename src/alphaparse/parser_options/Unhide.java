package alphaparse.parser_options;

import alphaparse.Sym;
import alphaparse.grammar.Grammar;
import alphaparse.parsing.Rule;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class Unhide {
    /**
     * Applies {@link Rule#unhideContent} to all entries in the grammar.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     * @see ParsingOptions#unhide()
     */
    public static@NotNull Grammar unhideAllContent(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Rule> res = new LinkedHashMap<>();
        for (final @NotNull var symRuleEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = symRuleEntry.getKey();
            final @NotNull var value = symRuleEntry.getValue();
            final @NotNull var pUnhide = value.unhideContent();
            res.put(key, pUnhide);
        }
        return new Grammar(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     */
    public static@NotNull Grammar unhideTags(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Rule> res = new LinkedHashMap<>();
        for (final @NotNull var symRuleEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = symRuleEntry.getKey();
            final @NotNull var value = symRuleEntry.getValue();
            final @NotNull var reduction = ReductionType.nonTerminalReduction(key);
            final @NotNull var pUnhide = value.withReduction(reduction);
            res.put(key, pUnhide);
        }
        return new Grammar(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar
     * and applies {@link Rule#unhideContent()}.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     */
    public static @NotNull Grammar unhideAll(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Rule> res = new LinkedHashMap<>();
        for (final @NotNull var symRuleEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = symRuleEntry.getKey();
            final @NotNull var value = symRuleEntry.getValue();
            final @NotNull var reduction = ReductionType.nonTerminalReduction(key);
            final @NotNull var p = value.unhideContent().withReduction(reduction);
            res.put(key, p);
        }
        return new Grammar(res);
    }
}
