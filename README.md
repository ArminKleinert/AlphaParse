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
- [ ] Instaparse's `:optimize :memory` is not implemented yet. The need for it needs to be investigated as Alphaparse
  does require less memory already.
- [ ] PEG-like ordered alternative `rule1 / rule2` needs to be investigated. I am not entirely sure whether it does or
  does not work.

## Usage

TODO

## Grammar elements

| Category                                | Notations                                                                        | Example                 | Note                                      | Option           |
|-----------------------------------------|----------------------------------------------------------------------------------|-------------------------|-------------------------------------------|------------------|
| <td colspan=5><h5>Default elements</h5> |
| Rule                                    | `: := ::= =`                                                                     | `S = A`                 |                                           |                  |
| End of rule                             | `;` `.` (optional)                                                               | `S = A;`                |                                           |                  |
| Alternation                             | <code>&#124;</code>                                                              | <code>A &#124; B</code> | Also known as "Choice"                    | `CHOICE`         |
| Concatenation                           | whitespace or `,`                                                                | `A B`                   |                                           |                  |
| Grouping                                | `()`                                                                             | `(A  B)+ C`             |                                           |                  |
| Optional                                | `?` `[]`                                                                         | `A?` `[A]`              |                                           | `OPTIONAL`       |
| One or more                             | `+`                                                                              | `A+`                    |                                           | `PLUS`           |
| Zero or more                            | `*` `{}`                                                                         | `A*` `{A}`              |                                           | `STAR`           |
| String terminal                         | `""`                                                                             | `"a"`                   |                                           |                  |
| String terminal (alt)                   | `''`                                                                             | `'a'`                   |                                           | `SINGLY_QUOTED`  |
| Regex terminal                          | `#""` `#''`                                                                      | `#"[0-9]"` `#'[0-9]'`   |                                           | `REGEX`          |
| Epsilon                                 | `Epsilon epsilon EPSILON eps ε "" ''`                                            | `S = epsilon`           |                                           | `EPSILON`        |
| Comment                                 | `(* *)`                                                                          | `(* Comment *)`         |                                           |                  |
| <td colspan=5><h5>Extended options</h5> |
| Variable repetition (zero or more)      | `*`                                                                              | `* A`                   | ABNF, not available if `STAR` is enabled. | `COUNTED_REPEAT` |
| Variable repetition (n or more)         | `n*`                                                                             | `5* A`                  | ABNF                                      | `COUNTED_REPEAT` |
| Variable repetition (zero to m)         | `*m`                                                                             | `*5 A`                  | ABNF                                      | `COUNTED_REPEAT` |
| Variable repetition (n to m)            | `n*m`                                                                            | `5*19 A`                | ABNF                                      | `COUNTED_REPEAT` |
| Variable repetition (exactly n)         | `n`                                                                              | `5 A`                   | ABNF                                      | `COUNTED_REPEAT` |
| Value range                             | `%xXXXX` `%xXXXX-XXXX`, `%bBBBB`, `%bBBBB-BBBB`, `%dDDDD`, `%dDDDD-DDDD`         | `%x41-5a`               | ABNF                                      | `CHAR_RANGE`     |
| ABNF core rules                         | [See the specification](https://datatracker.ietf.org/doc/html/rfc5234#autoid-25) |                         |                                           | `ABNF_CORE`      |

## Problems

- Grammars like `S : S` or `S : A\nA : S` will produce no output, but also not log a failure.
- Grammars like `S : epsilon | S` produce an infinite number of results if the input is empty. This is technically
  correct behavior, but very confusing to users.

## Differences from Instaparse

The biggest difference to Instaparse is that Alphaparse does not require Clojure. Jokes aside, there are a few important
internal differences.

### Production redefinitions

When you write a grammar like

```
S = A
S = B
S = C
```

the question arises: What is the right-hand side of the production `S`?

Instaparse chooses to override the previous definitions silently. Alphaparse allows the user to choose between options:

```java
import alphaparse.Alpha;
import alphaparse.grammar.RedefinitionOption;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;

class RedefTest {
  static void main(String[] args) {
    ParserCreationOptions opts = ParserCreationOptions
            .getDefault()
            .withRedefinitionOption(RedefinitionOption.OVERRIDE);
    String gr = "S : 'A'\nS : 'B'\nS : 'C'";
    Parser p;

    p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.OVERRIDE));
    IO.println(p.parse("A").isSuccess()); // false
    IO.println(p.parse("B").isSuccess()); // false
    IO.println(p.parse("C").isSuccess()); // true

    p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.ERROR)); // Fails
    IO.println(p.parse("A").isSuccess()); // n.a.
    IO.println(p.parse("B").isSuccess()); // n.a.
    IO.println(p.parse("C").isSuccess()); // n.a.

    p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.CHOICE));
    IO.println(p.parse("A").isSuccess()); // true
    IO.println(p.parse("B").isSuccess()); // true
    IO.println(p.parse("C").isSuccess()); // true

    p = Alpha.parser(gr, opts.withRedefinitionOption(RedefinitionOption.KEEP));
    IO.println(p.parse("A").isSuccess()); // true
    IO.println(p.parse("B").isSuccess()); // false
    IO.println(p.parse("C").isSuccess()); // false
  }
}
```

## Style considerations

When starting this project, I made a few decisions that are all over the code.

### `@NotNull` and `@Nullable` everywhere.

Despite some people
[being very unhappy with the use of these annotations](https://news.ycombinator.com/item?id=37534184)
, I found the compiler warnings helpful. These annotations ultimately help
[make the code more readable](https://stackoverflow.com/a/70817574).

### `final` classes, methods and variables

Despite it apparently cluttering the code, marking final things `final` is simply good style.

### Preferring `record`, but not always

Sometimes, I decided to convert a class to a `record` if it made sense. I always carefully measured performance and
converted back to classes if it made the code faster. This was rare, but very impactful. If you can explain to me why
using records is sometimes 60% slower than equivalent classes, let me know. :)

### `Sym` vs `String`

Strings in Java are very optimized. Still, Alphaparse uses its own type `Sym` for production names. Like Clojure's
Keyword, `Sym` instances are interned. Clojure embeds the Keywords into the code directly, while Alphaparse always
instantiates them when needed. The advantage that interning provides is the possibility of a constant `O(1)` equality
check.

The decision comes down to time and readability.

Strings at first use reference equality (`==`) for checks, then falls back on the `O(n)` char-by-char check. Sym always
uses reference checking. `Sym` looses time when it has to wrap strings. Doing some measurements, the times were almost
equal.

So it came down to readability and personal preference.

### Use `LinkedHashMap` and `LinkedHashSet`

I use these types because they are ordered. Some test cases showed inconsistent behavior when using the non-sequenced
types. But only *sometimes*. I prefer deterministic behavior.

### New interfaces for some functions

```java
// Could be java.util.function.Consumer<AlphaParseMessage>. New type for clarity.
alphaparse.functions.Listener listener = (AlphaParseMessage o) -> { System.out.println("Listener"); };

// Could be java.lang.Runnable. New type because Runnable is associated with Threads.
alphaparse.functions.NegativeListener negativeListener = () -> { System.out.println("NegativeListener"); };
alphaparse.functions.Procedure procedure = () -> { System.out.println("Procedure"); };
```

### New collection types

I implemented a few new collection types for special purposes.

A part of the code used a TreeMap indexed by `Integer`. I replaced that with a specialized Map which uses primitive
`int` as keys. This prevents the wrapping and unwrapping of the primitive.

```java
// Equivalent to Map<Integer, T>, but
// - Java's collection types introduce a lot of wrapping and unwrapping of the primitive type.
// - the new type probably has a smaller memory-footprint.
// Can be removed if project valhalla ever gets finished.
alphaparse.collections.IntMap<T> m; 
```

When constructing parse trees, the program needs to differentiate between List types sometimes. This type makes it clear
which behavior is needed when. It is also immutable with easy readability for additions, which Java's core types provide
either of these, but never both.

```java
alphaparse.collections.FlatSeq<T> flatSeq;
```

When returning a parse forest, a lazy list is used. Java (to my knowledge) does not have these. The only alternative I
can think of are `Stream`s, but those can only be iterated once. Implementing a construct like `Cons` could achieve the
same purpose while being simpler, but after testing each approach, I found this new type to be substantially faster.

```java
alphaparse.collections.LazySupplierList<T> lazySupplierList;
```


