package alphaparse.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.regex.Pattern;

import static java.lang.Character.isWhitespace;

public final class StrParser {

    // Converts a single-quoted string to a double-quoted one.
    private static @NotNull String escape(final @NotNull CharSequence s) {
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
        return sb.toString();
    }

    public static @NotNull String parse(final @NotNull CharSequence s) {
        final @NotNull StringBuilder sb = new StringBuilder();
        final @NotNull PushbackReader r = new PushbackReader(new StringReader(s.toString()));

        try {
            for (int ch = r.read(); ch != '"'; ch = r.read()) {
                if (ch == -1) {
                    throw new IllegalArgumentException("EOF while reading string");
                }

                if (ch == '\\') {
                    ch = r.read();
                    if (ch == -1) {
                        throw new IllegalArgumentException("EOF while reading string");
                    }

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
                            ch = r.read();
                            if (Character.digit(ch, 16) == -1) {
                                throw new IllegalArgumentException("Invalid unicode escape: \\u" + (char) ch);
                            }

                            ch = readUnicodeChar(r, ch, 16, 4, true);
                            break;
                        default:
                            if (!Character.isDigit(ch)) {
                                throw new IllegalArgumentException("Unsupported escape character: \\" + (char) ch);
                            }

                            ch = readUnicodeChar(r, ch, 8, 3, false);
                            if (ch > 255) {
                                throw new IllegalArgumentException("Octal escape sequence must be in range [0, 377].");
                            }
                            break;
                    }
                }

                sb.appendCodePoint(ch);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return sb.toString();
    }

    private static int readUnicodeChar(final @NotNull PushbackReader r,
                                       final int initChar,
                                       final int base,
                                       final int length,
                                       final boolean exact) throws IOException {
        int uc = Character.digit(initChar, base);
        if (uc == -1) {
            throw new IllegalArgumentException("Invalid digit: " + (char) initChar);
        } else {
            int i;
            for (i = 1; i < length; ++i) {
                final int ch = r.read();
                if (ch == -1 || isWhitespace(ch)) {
                    r.unread(ch);
                    break;
                }

                final int d = Character.digit(ch, base);
                if (d == -1) {
                    throw new IllegalArgumentException("Invalid digit: " + (char) ch);
                }

                uc = uc * base + d;
            }

            if (i != length && exact) {
                throw new IllegalArgumentException("Invalid character length: " + i + ", should be: " + length);
            } else {
                return uc;
            }
        }
    }

    /**
     * Converts single quoted string to double-quoted.
     *
     * @param s
     * @return
     */
    public static @NotNull String processString(final @NotNull CharSequence s) {
        final @NotNull CharSequence stripped = s.subSequence(1, s.length() - 1);
        final @NotNull String removeEscapedSingleQuotes = escape(stripped);
        return parse(removeEscapedSingleQuotes + '"');
    }

    /**
     * Converts single quoted regexp to double-quoted.
     *
     * @param s
     * @return
     */
    public static @NotNull Pattern processRegexp(final @NotNull CharSequence s) {
        final @NotNull CharSequence stripped = s.subSequence(2, s.length() - 1);
        final @NotNull String removeEscapedSingleQuotes = escape(stripped);
        return Pattern.compile(removeEscapedSingleQuotes);
    }
}
