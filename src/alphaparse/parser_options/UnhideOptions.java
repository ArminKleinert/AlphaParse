package alphaparse.parser_options;

/**
 * Options for unhiding parts of the output from a parse. A thorough description can be found in the description of the {@link ParsingOptions} class.
 * <p>
 * As an example, take the grammar
 * <pre>
 * {@code
 *      S : 'a' <B> C <D> 'a'
 *      B : 'b'+
 *      <C> : 'c'
 *      <D> : 'd'
 * }
 * </pre>
 * Now, parsing the text {@code "abcda"}, the expected tree would be {@code [:S, "a", "c", "a"]}. Where the `B` subtree is completely hidden, `C` is flattened (merged into `S`) and `D` is also completely hidden.
 * <pre>
 * {@code
 *      var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
 *
 *      // [:S, "a", "c", "a"]
 *      Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.NONE));
 *
 *      // [:S, "a", [:C, "c"], "a"]
 *      Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.TAGS));
 *
 *      // [:S, "a", [:B, "b"], "c", "d", "a"]
 *      Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.CONTENT));
 *
 *      // [:S, "a", [:B, "b"], [:C, "c"], [:D, "d"], "a"]
 *      Alpha.parse(p, "abcda", ParsingOptions.getDefault().withUnhide(UnhideOptions.ALL));
 * }
 * </pre>
 *
 * @see ParsingOptions#unhide()
 */
public enum UnhideOptions {
    /**
     * Do nothing.
     *
     * @see ParsingOptions#unhide()
     */
    NONE,
    /**
     * Unhide tags, do not show hidden contents.
     *
     * @see ParsingOptions#unhide()
     */
    TAGS,
    /**
     * Unhide contents, but keep tags hidden.
     *
     * @see ParsingOptions#unhide()
     */
    CONTENT,
    /**
     * Show both contents and tags.
     *
     * @see ParsingOptions#unhide()
     */
    ALL
}
