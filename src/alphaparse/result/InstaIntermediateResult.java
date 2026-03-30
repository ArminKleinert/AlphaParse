package alphaparse.result;

import org.jetbrains.annotations.Nullable;

public interface InstaIntermediateResult {
    int getIndex();

    @Nullable Object getResult();
}
