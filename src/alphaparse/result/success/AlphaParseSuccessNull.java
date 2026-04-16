package alphaparse.result.success;

import org.jetbrains.annotations.Nullable;

final class AlphaParseSuccessNull extends AlphaParseSuccess {
    AlphaParseSuccessNull(final int index) {
        super(index);
    }

    @Override
    public @Nullable Object getResult() {
        return null;
    }
}
