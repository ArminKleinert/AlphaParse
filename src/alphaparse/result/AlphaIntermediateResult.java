package alphaparse.result;

import org.jetbrains.annotations.Nullable;

public interface AlphaIntermediateResult {
    int getIndex();

    @Nullable Object getResult();
}
