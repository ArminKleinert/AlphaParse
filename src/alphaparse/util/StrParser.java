package alphaparse.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Helper functions for creating parsers from strings.
 */
public final class StrParser {
    private StrParser() {
    }

    // Converts a single-quoted string to a double-quoted one.
    private static @NotNull StringBuilder escape(final @NotNull CharSequence s) {
        final @NotNull StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c == '\\') {
                if (i + 1 >= s.length())
                    throw new IllegalArgumentException("Encountered backslash character at end of string: " + s);
                final char c2 = s.charAt(i + 1);
                i++;
                if (c2 != '\'') sb.append(c);
                sb.append(c2);
            } else if (c == '"') {
                sb.append('\\').append('"');
            } else {
                sb.append(c);
            }
        }
        //sb.append('"');
        return sb;
    }

    /**
     * Unescapes a string.
     *
     * @param inputCharSequence The input string.
     * @return An unescaped string.
     */
    public static @NotNull String parse(final @NotNull StringBuilder inputCharSequence) {
        final int inputLength = inputCharSequence.length();
        long lenAndCh;

        try {
            for (int i = 0; i < inputLength; i++) {
                int ch = inputCharSequence.charAt(i);

                if (ch == '"')
                    break;

                if (ch == '\\') {
                    i++;
                    if (i >= inputLength)
                        throw new ArrayIndexOutOfBoundsException();
                    ch = inputCharSequence.charAt(i);

                    switch (ch) {
                        case '"':
                        case '\\':
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        case 'n':
                            ch = '\n';
                            break;
                        case 'r':
                            ch = '\r';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'u':
                            i++;
                            ch = inputCharSequence.charAt(i);
                            if (Character.digit(ch, 16) == -1) {
                                throw new IllegalArgumentException("Invalid unicode escape: \\u" + (char) ch);
                            }

                            lenAndCh = readUnicodeChar(inputCharSequence, i, inputLength, ch, 16, 4, true);
                            ch = (int) lenAndCh;
                            i += (int) (lenAndCh >> 32) - 1;
                            break;
                        default:
                            if (!Character.isDigit(ch)) {
                                throw new IllegalArgumentException("Unsupported escape character: \\" + (char) ch);
                            }

                            lenAndCh = readUnicodeChar(inputCharSequence, i, inputLength, ch, 8, 3, false);
                            ch = (int) lenAndCh;
                            i += (int) (lenAndCh >> 32) - 1;
                            if (ch > 255) {
                                throw new IllegalArgumentException("Octal escape sequence must be in range [0, 377].");
                            }
                            break;
                    }
                }

                inputCharSequence.appendCodePoint(ch);
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("EOF while reading string",exception);
        }

        return inputCharSequence.substring(inputLength);
    }

    private static long readUnicodeChar(final @NotNull CharSequence sb,
                                        int indexInCharSequence,
                                        final int inputLength,
                                        final int initChar,
                                        final int numericBase,
                                        final int expectedLength,
                                        final boolean requireExactLength) {
        int outputNumber = Character.digit(initChar, numericBase);
        int parsedLength = 1;
        if (outputNumber == -1) {
            throw new IllegalArgumentException("Invalid digit: " + (char) initChar);
        } else {
            indexInCharSequence++;
            for (; parsedLength < expectedLength; parsedLength++, indexInCharSequence++) {
                final char ch = sb.charAt(indexInCharSequence);
                if (Character.isWhitespace(ch)) {
                    parsedLength--;
                    break;
                }

                final int d = Character.digit(ch, numericBase);
                if (d == -1) {
                    throw new IllegalArgumentException("Invalid digit: " + ch);
                }

                outputNumber = outputNumber * numericBase + d;
            }

            if (parsedLength != expectedLength && requireExactLength) {
                throw new IllegalArgumentException("Invalid character length: " + parsedLength + ", should be: " + expectedLength);
            } else {
                return ((long) parsedLength <<32) + outputNumber;
            }
        }
    }

    /**
     * Converts single quoted string to double-quoted.
     * The expected input has the format {@code '...'}.
     *
     * @param s The input string.
     * @return A string.
     */
    public static @NotNull String processString(final @NotNull CharSequence s) {
        final @NotNull CharSequence stripped = s.subSequence(1, s.length() - 1);
        final @NotNull StringBuilder removeEscapedSingleQuotes = escape(stripped);
        return parse(removeEscapedSingleQuotes.append('"'));
    }

    /**
     * Converts single quoted string to double-quoted and then compiles it to a regex.
     * The expected input has the format {@code #'...'}.
     *
     * @param s The input string.
     * @return A regex.
     */
    public static @NotNull Pattern processRegexp(final @NotNull CharSequence s) {
        final @NotNull CharSequence stripped = s.subSequence(2, s.length() - 1);
        final @NotNull StringBuilder removeEscapedSingleQuotes = escape(stripped);
        return Pattern.compile(removeEscapedSingleQuotes.toString());
    }
}
