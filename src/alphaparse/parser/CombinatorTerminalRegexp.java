package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonRegex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record CombinatorTerminalRegexp(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Pattern regexp) implements CombinatorTerminal {

    public CombinatorTerminalRegexp(@NotNull Pattern regexp) {
        this(defaultHidden, defaultRed, regexp);
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
    public @NotNull CombinatorTerminalRegexp withHideTag(boolean hide) {
        return isHidden() == hide ? this :  new CombinatorTerminalRegexp(hide, red, regexp);
    }

    @Override
    public @NotNull CombinatorTerminalRegexp withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorTerminalRegexp(hide, red, regexp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CombinatorTerminalRegexp(boolean hide1, ReductionType red1, Pattern regexp1))) return false;
        return hide == hide1 && Objects.equals(regexp.pattern(), regexp1.pattern()) && Objects.equals(red, red1);
    }
}
