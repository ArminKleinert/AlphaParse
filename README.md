# AlphaParse 0.9

A tool to generate and use parsers at runtime.

This project started as a conversion of the great Clojure-library [Instaparse](https://github.com/engelberg/instaparse).

## Features

- [x] Handles any kind of context-free grammar (left-recursive, right-recursive, ambiguous). For caveats see below.
- [x] Works for EBNF and ABNF.
- [x] Supports PEG-like syntax for lookahead and negative lookahead.
- [x] Detailed reporting of parse errors.
- [x] Can produce lazy sequences of all parses. This is useful for ambiguous grammars.
- [x] Grammars can be built using combinators.
- [x] Heavily optimized, with some tradeoffs where good style demanded it.

Missing:

- [ ] ABNF variable repetition does not support the `rule{n, m}` syntax yet.  
- [ ] ABNF variable repetition `n*m rule` currently requires a space before the rule name.
- [ ] Does not support the `%i` (case-insensitive) and `%s` (case-sensitive) prefixes for ABNF strings yet.
- [ ] ABNF line comments are not implemented yet.
- [ ] Instaparse's `:optimize :memory` is not implemented yet. The need for it needs to be investigated as Alphaparse does require less memory already.
- [ ] PEG-like ordered alternative `rule1 / rule2` needs to be investigated. I am not entirely sure whether it does or does not work.

## Usage

TODO

## Grammar elements

| Category              | Notations                             | Example                 | Note                   | Option          |
|-----------------------|---------------------------------------|-------------------------|------------------------|-----------------|
| Rule                  | `: := ::= =`                          | `S = A`                 |                        |                 |
| End of rule           | `;` `.` (optional)                    | `S = A;`                |                        |                 |
| Alternation           | <code>&#124;</code>                   | <code>A &#124; B</code> | Also known as "Choice" | `CHOICE`        |
| Concatenation         | whitespace or `,`                     | `A B`                   |                        |                 |
| Grouping              | `()`                                  | `(A  B)+ C`             |                        |                 |
| Optional              | `?` `[]`                              | `A?` `[A]`              |                        | `OPTIONAL`      |
| One or more           | `+`                                   | `A+`                    |                        | `PLUS`          |
| Zero or more          | `*` `{}`                              | `A*` `{A}`              |                        | `STAR`          |
| String terminal       | `""`                                  | `"a"`                   |                        |                 |
| String terminal (alt) | `''`                                  | `'a'`                   |                        | `SINGLY_QUOTED` |
| Regex terminal        | `#""` `#''`                           | `#"[0-9]"` `#'[0-9]'`   |                        | `REGEX`         |
| Epsilon               | `Epsilon epsilon EPSILON eps ε "" ''` | `S = epsilon`           |                        | `EPSILON`       |
| Comment               | `(* *)`                               | `(* Comment *)`         |                        |                 |

## Extensions

| Category                           | Notations                                                                        | Example   | Note                                                     | Option           |
|------------------------------------|----------------------------------------------------------------------------------|-----------|----------------------------------------------------------|------------------|
| Variable repetition (zero or more) | `*`                                                                              | `* A`     | ABNF, not available if `STAR` is allowed in the options. | `COUNTED_REPEAT` |
| Variable repetition (n or more)    | `n*`                                                                             | `5* A`    | ABNF                                                     | `COUNTED_REPEAT` |
| Variable repetition (zero to m)    | `*m`                                                                             | `*5 A`    | ABNF                                                     | `COUNTED_REPEAT` |
| Variable repetition (n to m)       | `n*m`                                                                            | `5*19 A`  | ABNF                                                     | `COUNTED_REPEAT` |
| Variable repetition (exactly n)    | `n`                                                                              | `5 A`     | ABNF                                                     | `COUNTED_REPEAT` |
| Value range                        | `%xXXXX` `%xXXXX-XXXX`, `%bBBBB`, `%bBBBB-BBBB`, `%dDDDD`, `%dDDDD-DDDD`         | `%x41-5a` | ABNF                                                     | `CHAR_RANGE`     |
| ABNF core rules                    | [See the specification](https://datatracker.ietf.org/doc/html/rfc5234#autoid-25) |           |                                                          | `ABNF_CORE`      |



## Problems

### Infinite epsilon

If a production has the choice between epsilon and a recursion going back to itself, it will result in an infinite loop. This is technically correct behavior, but very confusing.

```java
Parser p = Alpha.parser("S : S");
int numParses = Alpha.parses(p, "").size(); // Works. (No parses found.)

Parser p = Alpha.parser("S : epsilon");
int numParses = Alpha.parses(p, "").size(); // Also works.

Parser p = Alpha.parser("S : epsilon | S"); // Same for "S : S | epsilon"
int numParses = Alpha.parses(p, "").size(); // Freezes, then runs out of memory eventually.
```