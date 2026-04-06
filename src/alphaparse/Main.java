package alphaparse;

import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.FormatUtils;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.result.failure.failureReason.ParseFailureReasonRegex;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;
import alphaparse.viz.Viztool;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public final class Main {

    private static String readFile(String path) {
        final @NotNull String text;
        try {
            text = Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final @NotNull String c99GrammarText = readFile("grammars/c99.g");
        var i = 0;


//        /**/
//        //while (i != 0)
//        {
//            IO2.println("\n----------------------------------\n---      Big input test 2     ---\n----------------------------------");
//
//            @NotNull String text = readFile("ctest1.c");
//            var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
//            var startTime = System.nanoTime();
//            var c = Alpha.parse(p, text);
//            var endTime = System.nanoTime();
//            IO2.println(c.getClass());
//            IO2.println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 15000.000ms good)");
//            IO2.println(Viztool.dumpParseTree("vizoutput", c.castToParseSuccess()));
//        }
//
//        System.exit(0);

        /**/
        {
            var p = Alpha.parser("S : 'ABC'");
            IO2.println(Alpha.parses(p, "ABD", new Alpha.ParsingOptions(null, false, Alpha.UnhideOptions.content, true, false)));
            IO2.println(Alpha.parsesOrFailure(p, "ABD", Alpha.ParsingOptions.DEFAULT).castToParsesFailure().asFailure().contentsToString());
            IO2.println(Alpha.parse(p, "ABD", Alpha.ParsingOptions.DEFAULT).castToParseFailure().contentsToString());
            IO2.println(Alpha.parses(p, "ABD"));
            IO2.println();
        }

        /**/
        {
            var p = Alpha.parser("S : 'ABC'",
                    new Alpha.ParserCreationOptions(null, null, Cfg.GlobalCaseInsensitivity.TRUE, ReductionType.ReductionTypesAvailable.defaultType));
            IO2.println(Alpha.parses(p, "ABC"));
            IO2.println(Alpha.parses(p, "AbC"));
            IO2.println(Alpha.parses(p, "abc"));
            IO2.println();
        }

        /**/
        {
            IO2.println(FormatUtils.parseTreeFromHiccup(List.of(Keyword.intern("S"), "ABC")));
            IO2.println(FormatUtils.parseTreeFromHiccup(List.of(Keyword.intern("S"), "ABC")).getClass());
        }

        /**/
        {
            var p = Alpha.parser("S : A | B\nA : #'\\d'*\nB : #'\\d'*");
            IO2.println(Alpha.parses(p, "11"));
            IO2.println();
        }

        /**/
        {
            var p = Alpha.parser("S : #'\\d'+");
            IO2.println(Alpha.parse(p, "11", Alpha.ParsingOptions.optMemory()));
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a1", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        {
            var p = Alpha.parser("S : #'\\d'*");
            IO2.println(Alpha.parse(p, "11", Alpha.ParsingOptions.optMemory()));
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a1", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        {
            var p = Alpha.parser("S : 1*3 #'\\d'");
            IO2.println(Alpha.parse(p, "11", Alpha.ParsingOptions.optMemory()));
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a1", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((AlphaParseFailure) Alpha.parse(p, "a", Alpha.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        PerfTest.fullTest(true, c99GrammarText, 100, 1000);

        /**/
        PerfTest.testNumberOfParses(true, 23);

        /**/
        {
            IO2.println("\n----------------------------------\n---    Partial/Total tests     ---\n----------------------------------");
            var grammar = """
                    S : T+
                    T : r1 | r2 | r3
                    r1 : 'a'
                    r2 : 'b'
                    r3 : 'a'
                    """;
            var text = "aba";
            var p = Alpha.parser(grammar);

            IO2.println(Alpha.parses(p, text));
            IO2.println(Alpha.parses(p, text, new Alpha.ParsingOptions(null, false, Alpha.UnhideOptions.none, true, false)));
            IO2.println(Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false)));
            var commonParseOpts = new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, true, false);
            IO2.println(Alpha.parses(p, "c", commonParseOpts));
            IO2.println(Alpha.parses(p, "c", commonParseOpts).getFirst().getClass());
            IO2.println(Alpha.parse(p, "c", commonParseOpts));
        }

        /**/
        {
            IO2.println("\n----------------------------------\n---        Other tests         ---\n----------------------------------");

            var p = Alpha.parser("S : '1' | '11' | '111' | '1111'", Alpha.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(
                    Alpha.parses(p, "11", new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false))
                            + " // Expect: [[:S, 11], [:S, 1]]");
            IO2.println(Alpha.parses(p, "11111", new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false))
                    + " // Expect: [[:S, 1111], [:S, 111], [:S, 11], [:S, 1]]");

            p = Alpha.parser("S : #'\\d\\d[\\d]?'", Alpha.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(Alpha.parse(p, "11") + " // Expect: [:S, 11]");
            IO2.println(Alpha.parse(p, "111") + " // Expect: [:S, 111]");
            IO2.println(Alpha.parse(p, "1111").castToParseFailure().contentsToString());

            p = Alpha.parser("S : !'2' A*\n<A> : '1' | '2'", Alpha.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(Alpha.parse(p, "21").castToParseFailure().contentsToString());
            IO2.println(Alpha.parse(p, "11") + " // Expect: [:S, 1, 1]");
            IO2.println(Alpha.parse(p, "111") + " // Expect: [:S, 1, 1, 1]");
            IO2.println(Alpha.parse(p, "1111").castToParseSuccess().flattenRawProductions() + " // Expect: [:S, 1, 1, 1, 1]");
        }

        /**/
        {
            IO2.println("\n----------------------------------\n---  Failure attribute tests   ---\n----------------------------------");

            var p = Alpha.parser("S : &'1' S S | '1'+", Alpha.ParserCreationOptions.newWithStandardWhitespace());
            var parse = Alpha.parse(p, "112").castToParseFailure();
            var failIndex = parse.getIndex();
            var failColumn = parse.getColumn();
            var failLine = parse.getLine();
            var failReasonList = parse.getReasonList();
            var failText = parse.getText();
            var failResult = List.of();//parse.getResult();
            IO2.println(failIndex + " " + failColumn + " " + failLine + " " + failReasonList + " " + failText + " " + failResult);

            IO2.println(parse.checkCorrectness(2, 3, 1, "112",
                    List.of(new ParseFailureReasonOptional(Keyword.intern("end-of-string")),
                            new ParseFailureReasonRegex(Pattern.compile("\\s+"), true),
                            new ParseFailureReasonString("1"),
                            new ParseFailureReasonRegex(Pattern.compile("\\s+"))
                    )));
        }

        /**/
        if (i != 0) {
            IO2.println("\n----------------------------------\n--- Version compatibility test ---\n----------------------------------");

            {
                var text = "int a(int r){return r;}int a(int r, int a){return r;}";
                var startTime = System.nanoTime();
                var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
                var parses = Alpha.parses(p, text).castToParsesSuccess().hiccup();
                var endTime = System.nanoTime();
//                List<Object> old = (List<Object>) cljNestedVecChangeKeywordType(EdnReader.readString(
//                        readFile("testout.edn"),
//                        PersistentHashMap.create(Keyword.intern("eof"), null)));
                List<Object> old = List.of(1);
                IO2.println("Count:            " + parses.size());
                IO2.println("Count of Set:     " + new HashSet<>(parses).size());
                IO2.println("Old Count:        " + old.size());
                IO2.println("Count of old Set: " + new HashSet<>(old).size());
                IO2.println("Quasi-equals:     " + new HashSet<>(parses).equals(new HashSet<>(old)));
                IO2.println("First equals:     " + parses.getFirst().equals(old.getFirst()));
                IO2.println("Equals:           " + parses.equals(old));
                IO2.println("Time taken (ms):  " + (endTime - startTime) / 1000000.0);
                IO2.println();
            }
            {
                var text = "struct test ttt;\nint a(int r){return \"\\\"\"|r(77)+1+0.9f+.8;}";
                var startTime = System.nanoTime();
                var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
                var parses = Alpha.parses(p, text).castToParsesSuccess().hiccup();
                var endTime = System.nanoTime();
//                List<Object> old = (List<Object>) cljNestedVecChangeKeywordType(EdnReader.readString(
//                        readFile("testout2.edn"),
//                        PersistentHashMap.create(Keyword.intern("eof"), null)));
                List<Object> old = List.of(1);
                IO2.println("Count:            " + parses.size());
                IO2.println("Count of Set:     " + new HashSet<>(parses).size());
                IO2.println("Old Count:        " + old.size());
                IO2.println("Count of old Set: " + new HashSet<>(old).size());
                IO2.println("Quasi-equals:     " + new HashSet<>(parses).equals(new HashSet<>(old)));
                IO2.println("First equals:     " + parses.getFirst().equals(old.getFirst()));
                IO2.println("Equals:           " + parses.equals(old));
                IO2.println("Time taken (ms):  " + (endTime - startTime) / 1000000.0);
            }
        }

        /**/
        {
            IO2.println("\n----------------------------------\n--- Total/partial/memory tests ---\n----------------------------------");

            var grammar = "S : T+\nT : r1 / r2 / r3\nr1 : 'a'\nr2 : 'b'\nr3 : 'a'";
            var text = "ab";
            var p = Alpha.parser(grammar);
            var parses = Alpha.parses(p, text);

            var expected = List.of(List.of(Keyword.intern("S"),
                            List.of(Keyword.intern("T"), List.of(Keyword.intern("r1"), "a")),
                            List.of(Keyword.intern("T"), List.of(Keyword.intern("r2"), "b"))),
                    List.of(Keyword.intern("S"),
                            List.of(Keyword.intern("T"), List.of(Keyword.intern("r3"), "a")),
                            List.of(Keyword.intern("T"), List.of(Keyword.intern("r2"), "b"))));

            IO2.println(Objects.equals(
                    parses.castToParsesSuccess().hiccup(),
                    expected));
            IO2.println(parses + " // Expected: " + expected);
            IO2.println(Alpha.parses(p, text, new Alpha.ParsingOptions(null, false, Alpha.UnhideOptions.none, true, false)) + " // Expected: " + expected);
            IO2.println(Alpha.parses(p, text, new Alpha.ParsingOptions(null, true, Alpha.UnhideOptions.none, false, false)));
            IO2.println(Alpha.parses(p, text, Alpha.ParsingOptions.optMemory()) + " // Expected: " + expected);
        }

        /**/
        //while (i != 0)
        {
            IO2.println("\n----------------------------------\n---      Big input test 1      ---\n----------------------------------");

            @NotNull String text = readFile("ctest1.c");
            var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
            var startTime = System.nanoTime();
            var c = Alpha.parse(p, text).castToParseSuccess();
            var endTime = System.nanoTime();
            try (FileWriter fw = new FileWriter("bigoutput2.edn")) {
                fw.append(c.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            IO2.println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 75.000ms good)");
        }

        /**/
        //while (i != 0)
        {
            IO2.println("\n----------------------------------\n---      Big input test 2     ---\n----------------------------------");

            @NotNull String text = readFile("ctest.c");
            var p = Alpha.parser(c99GrammarText, Alpha.ParserCreationOptions.newWithStandardWhitespace());
            var startTime = System.nanoTime();
            var c = Alpha.parses(p, text);
            c.getFirst();
            var endTime = System.nanoTime();
            IO2.println(c.getClass());
            IO2.println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 15000.000ms good)");
        }
    }
}
