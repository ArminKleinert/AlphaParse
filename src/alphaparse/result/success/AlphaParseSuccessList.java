package alphaparse.result.success;

import alphaparse.flat.FlatSeq;
import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessList extends AlphaParseSuccess {
    private final @NotNull FlatSeq<Object> result; // TODO Do not use raw objects

    AlphaParseSuccessList(final int index, final @NotNull FlatSeq<Object> result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull FlatSeq<Object> getResult() {
        return result;
    }
}
