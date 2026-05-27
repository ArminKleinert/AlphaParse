package alphaparse.result.failure;

import alphaparse.Print;
import alphaparse.parsing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A failure lists reasons for the failure. This class represents the possible reasons.
 *
 * @param combinator      The combinator was parsed when the failure appeared.
 * @param reasonString    A string describing the failure. Please prefer {@link ParseFailureReason#failureReasonString}.
 * @param untilEndOfInput Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
 * @param tag             A symbol or string indicating the type of reason. E.g. lookahead, string terminal, regex terminal, etc.
 */
public record ParseFailureReason(
        @NotNull Combinator combinator,
        @Nullable String reasonString,
        boolean untilEndOfInput,
        @NotNull String tag) {
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
    public @NotNull String tag() {
        return tag;
    }

    /**
     * Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     *
     * @return Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     */
    public boolean untilEndOfInput() {
        return untilEndOfInput;
    }

    /**
     * Builds an instance based on a {@link TerminalUnicodeCharCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofUnicodeChar(final TerminalUnicodeCharCombinator combinator, final boolean untilEndOfInput) {

        return new ParseFailureReason(
                combinator,
                Arrays.toString(IntStream.range(combinator.getLo(), combinator.getHi() + 1).mapToObj(it -> (char) it).toArray()),
                untilEndOfInput, "char");
    }

    /**
     * Builds an instance based on a {@link EpsilonCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofEpsilon(final EpsilonCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, "end-of-string", untilEndOfInput, "epsilon");
    }

    /**
     * Builds an instance based on a {@link RepetitionCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofRepetition(final RepetitionCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, null, untilEndOfInput, "rep");
    }

    /**
     * Builds an instance based on a {@link LookaheadCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofLookahead(final LookaheadCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, untilEndOfInput ? "end-of-string" : null, untilEndOfInput, "look");
    }

    /**
     * Builds an instance based on a {@link NegativeLookaheadCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofNegated(final NegativeLookaheadCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, "NOT " + Print.combinatorToString(combinator.getParser()), untilEndOfInput, "neg");
    }

    /**
     * Builds an instance based on a {@link ExclusionCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofExclusion(final ExclusionCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator,
                Print.combinatorToString(combinator.getParserExpected())+" but NOT " + Print.combinatorToString(combinator.getParserExcluded()),
                untilEndOfInput, "exclude");
    }

    /**
     * Builds an instance based on a {@link OptionalCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofOptional(final OptionalCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, null, untilEndOfInput, "optional");
    }

    /**
     * Builds an instance based on a {@link TerminalRegexpCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofRegexTerminal(final TerminalRegexpCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, null, untilEndOfInput, "regex");
    }

    /**
     * Builds an instance based on a {@link TerminalStringCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofStringTerminal(final TerminalStringCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, null, untilEndOfInput, "string");
    }

    /**
     * Builds an instance based on a {@link TerminalSpecialSequenceCombinator}.
     *
     * @param combinator      The combinator.
     * @param untilEndOfInput Whether the combinator is followed by end-of-string.
     * @return A failure-reason based on the parameters.
     */
    public static @NotNull ParseFailureReason ofSpecialSequence(final TerminalSpecialSequenceCombinator combinator, final boolean untilEndOfInput) {
        return new ParseFailureReason(combinator, null, untilEndOfInput, "function");
    }
}
