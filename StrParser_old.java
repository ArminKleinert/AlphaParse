package alphaparse.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Helper functions for creating parsers from strings.
 */
public final class StrParser {
    final @NotNull StringBuilder sb = new StringBuilder();

    public StrParser() {
    }

    // Converts a single-quoted string to a double-quoted one.
    private @NotNull String prepare(final int offset, final char starter, final @NotNull String s) {
        sb.setLength(0);
        for (int i = offset; i < s.length()-1; i++) {
            int c = s.charAt(i);

            if (c == starter)
            {
                break;
            }

            if (c == '\\')
            {
                if (i + 1 >= s.length())
                    throw new IllegalArgumentException("Encountered backslash character at end of string: " + s);

                i++;
                final char c2 = s.charAt(i);

                if (c2 == starter) {
                    c = c2;
                }
                else
                {
                    c = switch (c2) {
                        case 'b' -> '\b';
                        case 'f' -> '\f';
                        case 'n' -> '\n';
                        case 'r' -> '\r';
                        case 't' -> '\t';
                        case 'u' -> {
                            i += 5;
                            yield Integer.parseInt(s, i - 4, i, 16);
                        }
                        default -> {
                            sb.append('\\');
                            yield c2;
                        }
                    };
                }
            }

            sb.appendCodePoint(c);
        }
        //sb.append('"');
        return sb.toString();
    }

    /**
     * Converts single quoted string to double-quoted.
     * The expected input has the format {@code '...'}.
     *
     * @param s The input string.
     * @return A string.
     */
    public @NotNull String processString(final @NotNull String s) {
        return prepare(1, s.charAt(0), s);
    }

    /**
     * Converts single quoted string to double-quoted and then compiles it to a regex.
     * The expected input has the format {@code #'...'}.
     *
     * @param s The input string.
     * @return A regex.
     */
    public @NotNull Pattern processRegexp(final @NotNull String s) {
        return Pattern.compile(prepare(2, s.charAt(1), s));
    }
}
