package alphaparse.grammar;

import java.util.List;

/**
 * Options for deciding what to do when a production is added that already exists. This enum is specifically used in {@link Grammar#fromProductions(List, ProductionRedefinitionOption)}.
 * <p>
 * Consider the following grammar:
 * <pre>
 * {@code
 *      S = A
 *      S = B
 *      S = C
 * }
 * </pre>
 * The question this class tries to answer is "what to do?". The parser knows thanks to these options.
 * <ul>
 *     <li>{@link ProductionRedefinitionOption#OVERRIDE}: {@code S = C}</li>
 *     <li>{@link ProductionRedefinitionOption#ERROR}: Fails.</li>
 *     <li>{@link ProductionRedefinitionOption#CHOICE}: {@code S = A | B | C}</li>
 *     <li>{@link ProductionRedefinitionOption#KEEP}: {@code S = A}</li>
 * </ul>
 */
public enum ProductionRedefinitionOption {
    /**
     * Ignore existing. Replace and forget.
     * <p>
     * Example: Adding Grammar productions "S = A" and "S = "B" results in "S = B" and discards the first.
     */
    OVERRIDE,

    /**
     * Throw exception if a duplicate is added.
     * <p>
     * Example: Adding Grammar productions "S = A" and "S = "B" results in an error.
     */
    ERROR,

    /**
     * Throw exception if a duplicate is added.
     * <p>
     * Example: Adding Grammar productions "S = A" and "S = "B" creates a new production "S = A | B".
     */
    CHOICE,

    /**
     * Keep old value.
     * <p>
     * Example: Adding Grammar productions "S = A" and "S = "B" keeps "S = A".
     */
    KEEP;

    /**
     * Default setting.
     */
    public final static ProductionRedefinitionOption defaultOption = ERROR;
}
