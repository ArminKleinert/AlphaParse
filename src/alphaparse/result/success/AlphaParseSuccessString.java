package alphaparse.result.success;

import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessString extends AlphaParseSuccess {
    private final @NotNull String result;

    AlphaParseSuccessString(final int index, final @NotNull String result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull String getResult() {
        return result;
    }
}
