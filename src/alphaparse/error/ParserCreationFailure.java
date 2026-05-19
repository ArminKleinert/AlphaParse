package alphaparse.error;

import org.jetbrains.annotations.NotNull;

public class ParserCreationFailure extends RuntimeException {
    public ParserCreationFailure(@NotNull IllegalArgumentException exception) {
        super(exception);
    }
    public ParserCreationFailure(@NotNull IllegalGrammarException exception) {
        super(exception);
    }
    public ParserCreationFailure(String message) {
        super(message);
    }
    public ParserCreationFailure() {
        super();
    }
}
