package alphaparse.result.success;

import alphaparse.flat.AutoFlattenSeq;
import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessList extends AlphaParseSuccess {
    private final @NotNull AutoFlattenSeq<Object> result; // TODO Do not use raw objects

    AlphaParseSuccessList(final int index, final @NotNull AutoFlattenSeq<Object> result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull AutoFlattenSeq<Object> getResult() {
        return result;
    }
}
