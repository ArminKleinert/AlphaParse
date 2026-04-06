package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonRegex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegexpTerminal extends CombinatorTerminal {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull Pattern regexp;

    public RegexpTerminal(final @NotNull Pattern regexp) {
        super();
        this.regexp = regexp;
    }

    public RegexpTerminal(final @NotNull Pattern regexp, final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
        this.regexp = regexp;
    }

    private static @Nullable String reMatchAtFront(final @NotNull Pattern regexp, final @NotNull CharSequence text) {
        final @NotNull Matcher matcher = regexp.matcher(text);
        if (matcher.lookingAt()) return matcher.group();
        return null;
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Pattern regexp = getRegexp();
        final @NotNull String text = tramp.getText();
        final @NotNull String subString = text.substring(index);
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        final @Nullable String match = reMatchAtFront(regexp, subString);
        if (match != null) {
            Gll.success(tramp, nodeKey, match, index + match.length());
        } else {
            Gll.fail(tramp, nodeKey, index, new ParseFailureReasonRegex(regexp));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Pattern regexp = this.getRegexp();
        final @NotNull String text = tramp.getSegment();
        final @NotNull String substring = text.substring(index);
        final @Nullable String match = reMatchAtFront(regexp, substring);
        final int desiredLength = text.length() - index;
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (match != null && match.length() == desiredLength) {
            Gll.success(tramp, nodeKey, match, text.length());
        } else {
            Gll.fail(tramp, nodeKey, index, new ParseFailureReasonRegex(regexp, true));
        }
    }

    public @NotNull Pattern getRegexp() {
        return regexp;
    }

    @Override
    public @NotNull RegexpTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new RegexpTerminal(getRegexp(), hide1, this.getReduction());
    }

    @Override
    public @NotNull RegexpTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new RegexpTerminal(getRegexp(), isHidden(), red1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegexpTerminal that)) return false;
        if (hashCode() != o.hashCode()) return false;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getRegexp(), that.getRegexp());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), regexp);
        return (int) bufferedHashCode;
    }
}