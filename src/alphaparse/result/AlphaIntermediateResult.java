package alphaparse.result;

import org.jetbrains.annotations.Nullable;

public interface AlphaIntermediateResult {
    int index();

    @Nullable Object getResult();
}
