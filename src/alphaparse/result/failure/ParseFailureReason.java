package alphaparse.result.failure;

import alphaparse.Print;
import alphaparse.parser.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A failure lists reasons for the failure. This class represents the possible reasons.
 */
public class ParseFailureReason {
    private final @NotNull Combinator combinator;
    private final @Nullable String reasonString;
    private final boolean full;
    private final @NotNull String tag;

    /**
     * Create a new instance.
     *
     * @param full Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     */
    public ParseFailureReason(final @NotNull Combinator combinator, final @Nullable String reasonString, final boolean full, final @NotNull String tag) {
        this.full = full;
        this.combinator = combinator;
        this.reasonString = reasonString; //getExpecting().toString();
        this.tag = tag;
    }

    /**
     * Representation of the failure reasonList as a string.
     *
     * @return A representation of the expected production as a string.
     */
    public String failureReasonString() {
        if (reasonString != null) return reasonString;
        return Print.combinatorToString(combinator);
    }


    /**
     * The tag of the production.
     *
     * @return The tag of the production.
     */
    public @NotNull String getTag() {
        return tag;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ParseFailureReason that)) return false;

        return full == that.full
                && Objects.equals(tag, that.tag)
                && Objects.equals(combinator, that.combinator)
                && Objects.equals(reasonString, that.reasonString);

    }

    @Override
    public int hashCode() {
        return Objects.hash(full, combinator, reasonString, tag);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParseFailureReason.class.getSimpleName() + "[", "]")
                .add("full=" + full)
                .add("combinator=" + combinator)
                .add("reasonString='" + reasonString + "'")
                .add("tag='" + tag + "'")
                .toString();
    }

    /**
     * Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     *
     * @return Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     */
    public boolean isFull() {
        return full;
    }

    public static @NotNull ParseFailureReason ofUnicodeChar(final TerminalUnicodeCharCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, null, full, "char");
    }

    public static @NotNull ParseFailureReason ofEpsilon(final EpsilonCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, "end-of-string", full, "epsilon");
    }

    public static @NotNull ParseFailureReason ofRepetition(final RepetitionCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, null, full, "rep");
    }

    public static @NotNull ParseFailureReason ofLookahead(final LookaheadCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, full ? "end-of-string" : null, full, "look");
    }

    public static @NotNull ParseFailureReason ofNegated(final NegativeLookaheadCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, "NOT " + Print.combinatorToString(combinator.getParser()), full, "look");
    }

    public static @NotNull ParseFailureReason ofOptional(final OptionalCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, "end-of-string", full, "optional");
    }

    public static @NotNull ParseFailureReason ofRegexTerminal(final TerminalRegexpCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, null, full, "regex");
    }

    public static @NotNull ParseFailureReason ofStringTerminal(final TerminalStringCombinator combinator, final boolean full) {
        return new ParseFailureReason(combinator, null, full, "string");
    }
}
