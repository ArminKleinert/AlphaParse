package alphaparse.parser_options;

/**
 * Options for unhiding parts of the output from a parse. A thorough description can be found in the description of the {@link ParsingOptions} class.
 *
 * @see ParsingOptions#unhide()
 */
public enum UnhideOptions {
    /**
     * Do nothing.
     *
     * @see ParsingOptions#unhide()
     */
    none,
    /**
     * Unhide tags, do not show hidden contents.
     *
     * @see ParsingOptions#unhide()
     */
    tags,
    /**
     * Unhide contents, but keep tags hidden.
     *
     * @see ParsingOptions#unhide()
     */
    content,
    /**
     * Show both contents and tags.
     *
     * @see ParsingOptions#unhide()
     */
    all
}
