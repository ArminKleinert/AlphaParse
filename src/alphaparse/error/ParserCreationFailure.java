package alphaparse.error;

import alphaparse.Alpha;
import alphaparse.parser_options.ParserCreationOptions;
import org.jetbrains.annotations.NotNull;

/**
 * {@link RuntimeException} which occurs if the parser could not be created.
 *
 * @see Alpha#parser(String, ParserCreationOptions)
 */
public final class ParserCreationFailure extends RuntimeException {
    /**
     * {@link RuntimeException} which occurs if the parser could not be created.
     *
     * @param exception Argument.
     * @see Alpha#parser(String, ParserCreationOptions)
     */
    public ParserCreationFailure(@NotNull IllegalArgumentException exception) {
        super(exception);
    }

    /**
     * {@link RuntimeException} which occurs if the parser could not be created.
     *
     * @param exception Argument.
     * @see Alpha#parser(String, ParserCreationOptions)
     */
    public ParserCreationFailure(@NotNull IllegalGrammarException exception) {
        super(exception);
    }

    /**
     * {@link RuntimeException} which occurs if the parser could not be created.
     *
     * @param message Argument.
     * @see Alpha#parser(String, ParserCreationOptions)
     */
    public ParserCreationFailure(String message) {
        super(message);
    }
}
