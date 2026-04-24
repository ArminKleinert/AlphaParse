package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonRegex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 */
public final class TerminalRegexpCombinator extends CombinatorTerminal {
    private final @NotNull Pattern regexp;

    private TerminalRegexpCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Pattern regexp) {
        super(hide, red);
        this.regexp = regexp;
    }

    /**
     * TODO
     *
     * @param regexp TODO
     */
    public TerminalRegexpCombinator(final @NotNull Pattern regexp) {
        super();
        this.regexp = regexp;
    }

    private static @Nullable String reMatchAtFront(final @NotNull Pattern regexp, final @NotNull CharSequence text) {
        final @NotNull Matcher matcher = regexp.matcher(text);
        if (matcher.lookingAt()) return matcher.group();
        return null;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Pattern regexp = getRegexp();
        final @NotNull String text = runner.tramp().getText();
        final @NotNull String subString = text.substring(index);
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        final @Nullable String match = reMatchAtFront(regexp, subString);
        if (match != null) {
            runner.success(nodeKey, match, index + match.length());
        } else {
            runner.fail(nodeKey, index, new ParseFailureReasonRegex(regexp));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Pattern regexp = this.getRegexp();
        final @NotNull String text = runner.tramp().getSegment();
        final @NotNull String substring = text.substring(index);
        final @Nullable String match = reMatchAtFront(regexp, substring);
        final int desiredLength = text.length() - index;
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (match != null && match.length() == desiredLength) {
            runner.success(nodeKey, match, text.length());
        } else {
            runner.fail(nodeKey, index, new ParseFailureReasonRegex(regexp, true));
        }
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull Pattern getRegexp() {
        return regexp;
    }

    @Override
    public @NotNull TerminalRegexpCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new TerminalRegexpCombinator(hide, red, regexp);
    }

    @Override
    public @NotNull TerminalRegexpCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new TerminalRegexpCombinator(hide, red, regexp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TerminalRegexpCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && Objects.equals(regexp.pattern(), that.getRegexp().pattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hide, red, regexp.pattern());
    }
}
