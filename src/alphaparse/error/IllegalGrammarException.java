package alphaparse.error;

/**
 * Exception which occurs if a grammar is invalid.
 */
public class IllegalGrammarException extends RuntimeException {
    /**
     * Exception which occurs if a grammar is invalid.
     *
     * @param message Message.
     */
    public IllegalGrammarException(String message) {
        super(message);
    }
}
