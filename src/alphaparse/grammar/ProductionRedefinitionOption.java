package alphaparse.grammar;

import java.util.List;

/**
 * Options for deciding what to do when a production is added that already exists. This enum is specifically used in {@link Grammar#fromProductions(List, ProductionRedefinitionOption)}.
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
