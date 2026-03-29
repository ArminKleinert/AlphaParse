package instarun.delay;

import instarun.functions.Procedure;
import org.jetbrains.annotations.NotNull;

public final class Delay {
    private volatile Throwable exception;
    private volatile Procedure fn;

    public Delay(final @NotNull Procedure fn) {
        this.fn = fn;
        this.exception = null;
    }

    public void execute() {
        if (this.fn != null) {
            synchronized (this) {
                if (this.fn != null) {
                    try {
                        this.fn.execute();
                    } catch (final @NotNull Exception t) {
                        this.exception = t;
                    }

                    this.fn = null;
                }
            }
        }

        if (this.exception != null) {
            throw new RuntimeException(exception);
        }
    }
}
