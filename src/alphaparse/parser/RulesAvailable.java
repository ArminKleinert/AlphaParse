package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum RulesAvailable {
    REGEX, PLUS, CHOICE, STAR, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, SINGLY_QUOTED, CHAR_RANGE, ORDERED_CHOICE, OPTIONAL, COUNTED_REPEAT,ABNF_CORE;

    public static @NotNull Set<RulesAvailable> ebnf() {
        return Set.of(
                REGEX, PLUS, CHOICE, STAR, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, SINGLY_QUOTED, OPTIONAL);
    }

    public static @NotNull Set<RulesAvailable> abnf() {
        return Set.of(
                REGEX, PLUS, CHAR_RANGE, ORDERED_CHOICE, ABNF_CORE, COUNTED_REPEAT, OPTIONAL);
    }

    public static @NotNull Set<RulesAvailable> defaultRules() {
        return Set.of(
                REGEX, PLUS, CHOICE, STAR, EPSILON, LOOKAHEAD, NEGATIVE_LOOKAHEAD, SINGLY_QUOTED, ORDERED_CHOICE, COUNTED_REPEAT, OPTIONAL);
    }
}
