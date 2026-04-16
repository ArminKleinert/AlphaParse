package alphaparse.result.success;

import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessParseResult extends AlphaParseSuccess {
    private final @NotNull ParseTree result;

    AlphaParseSuccessParseResult(final int index, final @NotNull ParseTree result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull ParseTree getResult() {
        return result;
    }
}
