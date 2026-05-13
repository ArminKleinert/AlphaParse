package alphaparse.result.success;

import org.jetbrains.annotations.Nullable;

final class AlphaParseSuccessWithoutValue extends AlphaParseSuccess {
    AlphaParseSuccessWithoutValue(final int index) {
        super(index);
    }

    @Override
    public @Nullable Object getResult() {
        return null;
    }
}
