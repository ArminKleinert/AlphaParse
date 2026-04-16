package alphaparse.result.success;

import alphaparse.result.TotalParsesFailureNode;
import org.jetbrains.annotations.NotNull;

final class AlphaParseSuccessWithTotalFailure extends AlphaParseSuccess {
    private final @NotNull TotalParsesFailureNode result;

    AlphaParseSuccessWithTotalFailure(final int index, final @NotNull TotalParsesFailureNode result) {
        super(index);
        this.result = result;
    }

    @Override
    public @NotNull TotalParsesFailureNode getResult() {
        return result;
    }
}
