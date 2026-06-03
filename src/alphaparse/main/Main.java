package alphaparse.main;

import alphaparse.*;
import alphaparse.parser_options.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

final class Main {

    private static String readFile(String path) {
        final @NotNull String text;
        try {
            text = Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private static void println(Object o) {
        System.out.println(o);
    }

    private static void println() {
        System.out.println();
    }

    public static void main(String[] args) {
        final @NotNull String c99GrammarText = readFile("testres/grammars/c99.g");

        int i = 0;

//        System.out.println(TimeUtil.measureExecutionsPer(
//                10000,
//                () -> Alpha.parser(c99GrammarText)
//        ));
//
//        System.exit(0);

//        /**/
//        //while (i != 0)
//        {
//            println("\n----------------------------------\n---      Big input test 2     ---\n----------------------------------");
//
//            @NotNull String text = readFile("ctest1.c");
//            final @NotNull var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
//            final @NotNull var startTime = System.nanoTime();
//            final @NotNull var c = Alpha.parse(p, text);
//            final @NotNull var endTime = System.nanoTime();
//            println(c.getClass());
//            println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 15000.000ms good)");
//            println(Viztool.dumpParseTree("vizoutput", c.castToParseSuccess()));
//        }
//
//        System.exit(0);

        /**/
        {
            final var p = Alpha.parser("S := <'a'>");
            println(p.parse("a"));
        }

        /**/
        {
            final @NotNull var p = Alpha.parser("S := 'ABC'");
            println(Alpha.parses(p, "ABD", new ParsingOptions(null, false, Unhide.UnhideOptions.CONTENT, true, false)));
            println(Alpha.parsesOrFailure(p, "ABD", ParsingOptions.getDefault()).castToParsesFailure().asFailure().contentsToString());
            println(Alpha.parse(p, "ABD", ParsingOptions.getDefault()).castToParseFailure().contentsToString());
            println(Alpha.parses(p, "ABD"));
            println();
        }

        /**/
        {
            final @NotNull var opts = new ParserCreationOptions(
                    null,
                    null,
                    GlobalCaseInsensitivity.TRUE,
                    true,
                    null,
                    null,
                    true,
                    null,
                    null);
            final @NotNull var p = Alpha.parser("S := 'ABC'", opts);
            println(Alpha.parses(p, "ABC"));
            println(Alpha.parses(p, "AbC"));
            println(Alpha.parses(p, "abc"));
            println();
        }

//        /**/
//        {
//            println(ParseConverterUtils.parseTreeFromHiccup(List.of("S", "ABC")));
//            println(ParseConverterUtils.parseTreeFromHiccup(List.of("S", "ABC")).getClass());
//            println();
//        }

        /**/
        {
            final @NotNull var p = Alpha.parser("S := A | B\nA := #'\\d'*\nB := #'\\d'*");
            println(Alpha.parses(p, "11"));
            println();
        }

//        /**/
//        {
//            final @NotNull var p = Alpha.parser("S := 1*3 #'\\d'");
//            println(Alpha.parse(p, "11", Alpha.ParsingOptions.optMemory()));
//            println(((AlphaParseFailure) Alpha.parse(p, "a1", Alpha.ParsingOptions.optMemory())).contentsToString());
//            println(((AlphaParseFailure) Alpha.parse(p, "a", Alpha.ParsingOptions.optMemory())).contentsToString());
//            println();
//        }

        /**/
        PerfTest.fullTest(true, c99GrammarText, 100, 1000);

        /**/
        PerfTest.testNumberOfParses(true, 23);

        /**/
        {
            println("\n----------------------------------\n---    Partial/Total tests     ---\n----------------------------------");
            final @NotNull var grammar = """
                    S  := T+
                    T  := r1 | r2 | r3
                    r1 := 'a'
                    r2 := 'b'
                    r3 := 'a'
                    """;
            final @NotNull var text = "aba";
            final @NotNull var p = Alpha.parser(grammar);

            println(Alpha.parses(p, text));
            println(Alpha.parses(p, text, new ParsingOptions(null, false, Unhide.UnhideOptions.NONE, true, false)));
            println(Alpha.parses(p, text, new ParsingOptions(null, true, Unhide.UnhideOptions.NONE, false,  false)));
            final @NotNull var commonParseOpts = new ParsingOptions(null, true, Unhide.UnhideOptions.NONE, true, false);
            println(Alpha.parses(p, "c", commonParseOpts));
            println(Alpha.parses(p, "c", commonParseOpts).getFirst().getClass());
            println(Alpha.parse(p, "c", commonParseOpts));
        }

        /**/
        {
            println("\n----------------------------------\n---        Other tests         ---\n----------------------------------");

            @NotNull var p = Alpha.parser("S := '1' | '11' | '111' | '1111'", ParserCreationOptions.newWithStandardWhitespace());
            println(
                    Alpha.parses(p, "11", new ParsingOptions(null, true, Unhide.UnhideOptions.NONE, false,  false))
                            + " // Expect: [[:S, 11], [:S, 1]]");
            println(Alpha.parses(p, "11111", new ParsingOptions(null, true, Unhide.UnhideOptions.NONE, false,  false))
                    + " // Expect: [[:S, 1111], [:S, 111], [:S, 11], [:S, 1]]");

            p = Alpha.parser("S := #'\\d\\d[\\d]?'", ParserCreationOptions.newWithStandardWhitespace());
            println(Alpha.parse(p, "11") + " // Expect: [:S, 11]");
            println(Alpha.parse(p, "111") + " // Expect: [:S, 111]");
            println(Alpha.parse(p, "1111").castToParseFailure().contentsToString());

            p = Alpha.parser("S := !'2' A*\n<A> := '1' | '2'", ParserCreationOptions.newWithStandardWhitespace());
            println(Alpha.parse(p, "21").castToParseFailure().contentsToString());
            println(Alpha.parse(p, "11") + " // Expect: [:S, 1, 1]");
            println(Alpha.parse(p, "111") + " // Expect: [:S, 1, 1, 1]");
            println(Alpha.parse(p, "1111") + " // Expect: [:S, 1, 1, 1, 1]");
        }

        /**/
        {
            println("\n----------------------------------\n---  Failure attribute tests   ---\n----------------------------------");

            final @NotNull var p = Alpha.parser("S := &'1' S S | '1'+", ParserCreationOptions.newWithStandardWhitespace());
            final @NotNull var parse = Alpha.parse(p, "112").castToParseFailure();
            final var failIndex = parse.index();
            final var failColumn = parse.column();
            final var failLine = parse.line();
            final @NotNull var failReasonList = parse.reasonList();
            final var failText = parse.text();
            final @NotNull var failResult = List.of();//parse.getResult();
            println(failIndex + " " + failColumn + " " + failLine + " " + failReasonList + " " + failText + " " + failResult);

//            println(parse.checkCorrectness(2, 3, 1, "112",
//                    List.of(new ParseFailureReasonOptional(Keyword.intern("end-of-string")),
//                            new ParseFailureReasonRegex(Pattern.compile("\\s+"), true),
//                            new ParseFailureReasonString("1"),
//                            new ParseFailureReasonRegex(Pattern.compile("\\s+"))
//                    )));
        }

        /**/
        if (i != 0) {
            println("\n----------------------------------\n--- Version compatibility test ---\n----------------------------------");

            {
                final @NotNull var text = "int a(int r){return r;}int a(int r, int a){return r;}";
                final var startTime = System.nanoTime();
                final @NotNull var p = Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace());
                final @NotNull var parses = Alpha.parses(p, text).castToParsesSuccess().toRawList();
                final var endTime = System.nanoTime();
//                List<Object> old = (List<Object>) cljNestedVecChangeKeywordType(EdnReader.readString(
//                        readFile("testout.edn"),
//                        PersistentHashMap.create(Keyword.intern("eof"), null)));
                List<Object> old = List.of(1);
                println("Count:            " + parses.size());
                println("Count of Set:     " + new HashSet<>(parses).size());
                println("Old Count:        " + old.size());
                println("Count of old Set: " + new HashSet<>(old).size());
                println("Quasi-equals:     " + new HashSet<>(parses).equals(new HashSet<>(old)));
                println("First equals:     " + parses.getFirst().equals(old.getFirst()));
                println("Equals:           " + parses.equals(old));
                println("Time taken (ms):  " + (endTime - startTime) / 1000000.0);
                println();
            }
            {
                final @NotNull var text = "struct test ttt;\nint a(int r){return \"\\\"\"|r(77)+1+0.9f+.8;}";
                final var startTime = System.nanoTime();
                final @NotNull var p = Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace());
                final @NotNull var parses = Alpha.parses(p, text).castToParsesSuccess().toRawList();
                final var endTime = System.nanoTime();
//                List<Object> old = (List<Object>) cljNestedVecChangeKeywordType(EdnReader.readString(
//                        readFile("testout2.edn"),
//                        PersistentHashMap.create(Keyword.intern("eof"), null)));
                List<Object> old = List.of(1);
                println("Count:            " + parses.size());
                println("Count of Set:     " + new HashSet<>(parses).size());
                println("Old Count:        " + old.size());
                println("Count of old Set: " + new HashSet<>(old).size());
                println("Quasi-equals:     " + new HashSet<>(parses).equals(new HashSet<>(old)));
                println("First equals:     " + parses.getFirst().equals(old.getFirst()));
                println("Equals:           " + parses.equals(old));
                println("Time taken (ms):  " + (endTime - startTime) / 1000000.0);
            }
        }

        /**/
        {
            println("\n----------------------------------\n--- Total/partial/memory tests ---\n----------------------------------");

            final @NotNull var grammar = "S := T+\nT := r1 / r2 / r3\nr1 := 'a'\nr2 := 'b'\nr3 := 'a'";
            final @NotNull var text = "ab";
            final @NotNull var p = Alpha.parser(grammar);
            final @NotNull var parses = Alpha.parses(p, text);

            final @NotNull var expected = List.of(
                    List.of("S",
                            List.of("T", List.of("r1", "a")),
                            List.of("T", List.of("r2", "b"))),
                    List.of("S",
                            List.of("T", List.of("r3", "a")),
                            List.of("T", List.of("r2", "b"))));

            println(Objects.equals(
                    parses.castToParsesSuccess().toRawList(),
                    expected));
            println(parses + " // Expected: " + expected);
            println(Alpha.parses(p, text, new ParsingOptions(null, false, Unhide.UnhideOptions.NONE, true,  false)) + " // Expected: " + expected);
            println(Alpha.parses(p, text, new ParsingOptions(null, true, Unhide.UnhideOptions.NONE, false, false)));
            println(Alpha.parses(p, text) + " // Expected: " + expected);
        }

        /**/
        //while (i != 0)
        {
            println("\n----------------------------------\n---      Big input test 1      ---\n----------------------------------");

            @NotNull String text = readFile("ctest1.c");
            final @NotNull var p = Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace());
            final var startTime = System.nanoTime();
            final @NotNull var c = Alpha.parse(p, text).castToParseSuccess();
            final var endTime = System.nanoTime();
            println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 75.000ms good)");
        }

        /**/
        //while (i != 0)
        {
            println("\n----------------------------------\n---      Big input test 2     ---\n----------------------------------");

            @NotNull String text = readFile("ctest.c");
            final @NotNull var p = Alpha.parser(c99GrammarText, ParserCreationOptions.newWithStandardWhitespace());
            final var startTime = System.nanoTime();
            final @NotNull var c = Alpha.parses(p, text);
            c.getFirst();
            final var endTime = System.nanoTime();
            println(c.getClass());
            println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 15000.000ms good)");
        }
    }
}
