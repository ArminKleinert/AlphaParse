package instarun;

import instarun.reduction.ReductionType;
import instarun.result.InstaFailure;
import instarun.result.FormatUtils;
import instarun.result.failure.failureReason.InstaFailureReasonOptional;
import instarun.result.failure.failureReason.InstaFailureReasonRegex;
import instarun.result.failure.failureReason.InstaFailureReasonString;
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

    private static Object cljNestedVecChangeKeywordType(Object pv) {
//        if (pv instanceof List v)
//            return v.stream().map(Main::cljNestedVecChangeKeywordType).toList();
//        if (pv instanceof Keyword)
//            return Keyword.intern(((Keyword) pv).sym);
        return pv;
    }

    public static void main(String[] args) {
        final @NotNull String c99GrammarText = readFile("grammars/c99.g");
        var i = 0;
//
//        System.exit(0);

        /**/
//        {
//            var p = Insta.parser("S : A | B\nA :E #'\\d'*\nB : A D A #'\\d'*");
//            IO2.println(p.getGrammar().values());
//            IO2.println(p.getGrammar().analyze());
//            IO2.println();
//        }

//        System.exit(0);

        /**/
//        {
//            var p = Insta.parser(c99GrammarText, Insta.ParserCreationOptions.newWithStandardWhitespace());
//            var text = "struct test ttt;\nint a(int r){return \"\\\"\"|r(77)+1+0.9f+.8;}";
//            InstaParseResult parseTree = Insta.parse(p, text, Insta.ParsingOptions.DEFAULT);
//            IO2.println(parseTree);
//            //IO2.println(Viztool.dumpParseTreeHelp(System.out, parseTree.castToParseSuccess(), new AtomicInteger(0)));
//            try {
//                IO2.println(Viztool.dumpParseTree("dottest", parseTree.castToParseSuccess()));
//            } catch (InterruptedException | IOException e) {
//                throw new RuntimeException(e);
//            }
//            IO2.println();
//        }

//        System.exit(0);

//        {
//            var grammar = """
//                    S : T+
//                    T : r1 | r2 | r3
//                    r1 : 'a'
//                    r2 : 'b'
//                    r3 : 'a'
//                    """;
//            var text = "aba";
//            var p = Insta.parser(grammar);
//            IO2.println(FormatUtils.parserToMap(p));
//        }
//
//        System.exit(0);

        /**/
        {
            var p = Insta.parser("S : 'ABC'");
            IO2.println(Insta.parses(p, "ABD", new Insta.ParsingOptions(null, false, Insta.UnhideOptions.content, true, false)));
            IO2.println(Insta.parses(p, "ABD"));
            IO2.println();
        }

        /**/
        {
            var p = Insta.parser("S : 'ABC'",
                    new Insta.ParserCreationOptions(null, null, Cfg.GlobalCaseInsensitivity.TRUE, ReductionType.ReductionTypesAvailable.defaultType));
            IO2.println(Insta.parses(p, "ABC"));
            IO2.println(Insta.parses(p, "AbC"));
            IO2.println(Insta.parses(p, "abc"));
            IO2.println();
        }

        /**/
        {
            IO2.println(FormatUtils.parseTreeFromHiccup(List.of(Keyword.intern("S"), "ABC")));
            IO2.println(FormatUtils.parseTreeFromHiccup(List.of(Keyword.intern("S"), "ABC")).getClass());
        }

        /**/
        {
            var p = Insta.parser("S : A | B\nA : #'\\d'*\nB : #'\\d'*");
            IO2.println(Insta.parses(p, "11"));
            IO2.println();
        }

        /**/
        {
            var p = Insta.parser("S : #'\\d'+");
            IO2.println(Insta.parse(p, "11", Insta.ParsingOptions.optMemory()));
            IO2.println(((InstaFailure) Insta.parse(p, "a1", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((InstaFailure) Insta.parse(p, "a", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        {
            var p = Insta.parser("S : #'\\d'*");
            IO2.println(Insta.parse(p, "11", Insta.ParsingOptions.optMemory()));
            IO2.println(((InstaFailure) Insta.parse(p, "a1", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((InstaFailure) Insta.parse(p, "a", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        {
            var p = Insta.parser("S : 1*3 #'\\d'");
            IO2.println(Insta.parse(p, "11", Insta.ParsingOptions.optMemory()));
            IO2.println(((InstaFailure) Insta.parse(p, "a1", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println(((InstaFailure) Insta.parse(p, "a", Insta.ParsingOptions.optMemory())).contentsToString());
            IO2.println();
        }

        /**/
        PerfTest.fullTest(true, c99GrammarText, 10, 100);

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
            var p = Insta.parser(grammar);

            IO2.println(Insta.parses(p, text));
            IO2.println(Insta.parses(p, text, new Insta.ParsingOptions(null, false, Insta.UnhideOptions.none, true, false)));
            IO2.println(Insta.parses(p, text, new Insta.ParsingOptions(null, true, Insta.UnhideOptions.none, false, false)));
            var commonParseOpts = new Insta.ParsingOptions(null, true, Insta.UnhideOptions.none, true, false);
            IO2.println(Insta.parses(p, "c", commonParseOpts));
            IO2.println(Insta.parses(p, "c", commonParseOpts).getFirst().getClass());
            IO2.println(Insta.parse(p, "c", commonParseOpts));
        }

        /**/
        {
            IO2.println("\n----------------------------------\n---        Other tests         ---\n----------------------------------");

            var p = Insta.parser("S : '1' | '11' | '111' | '1111'", Insta.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(
                    Insta.parses(p, "11", new Insta.ParsingOptions(null, true, Insta.UnhideOptions.none, false, false))
                            + " // Expect: [[:S, 11], [:S, 1]]");
            IO2.println(Insta.parses(p, "11111", new Insta.ParsingOptions(null, true, Insta.UnhideOptions.none, false, false))
                    + " // Expect: [[:S, 1111], [:S, 111], [:S, 11], [:S, 1]]");

            p = Insta.parser("S : #'\\d\\d[\\d]?'", Insta.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(Insta.parse(p, "11") + " // Expect: [:S, 11]");
            IO2.println(Insta.parse(p, "111") + " // Expect: [:S, 111]");
            IO2.println(Insta.parse(p, "1111").castToParseFailure().contentsToString());

            p = Insta.parser("S : !'2' A*\n<A> : '1' | '2'", Insta.ParserCreationOptions.newWithStandardWhitespace());
            IO2.println(Insta.parse(p, "21").castToParseFailure().contentsToString());
            IO2.println(Insta.parse(p, "11") + " // Expect: [:S, 1, 1]");
            IO2.println(Insta.parse(p, "111") + " // Expect: [:S, 1, 1, 1]");
            IO2.println(Insta.parse(p, "1111").castToParseSuccess().flattenRawProductions() + " // Expect: [:S, 1, 1, 1, 1]");
        }

        /**/
        {
            IO2.println("\n----------------------------------\n---  Failure attribute tests   ---\n----------------------------------");

            var p = Insta.parser("S : &'1' S S | '1'+", Insta.ParserCreationOptions.newWithStandardWhitespace());
            var parse = Insta.parse(p, "112").castToParseFailure();
            var failIndex = parse.getIndex();
            var failColumn = parse.getColumn();
            var failLine = parse.getLine();
            var failReasonList = parse.getReasonList();
            var failText = parse.getText();
            var failResult = List.of();//parse.getResult();
            IO2.println(failIndex + " " + failColumn + " " + failLine + " " + failReasonList + " " + failText + " " + failResult);

            IO2.println(parse.checkCorrectness(2, 3, 1, "112",
                    List.of(new InstaFailureReasonOptional(Keyword.intern("end-of-string")),
                            new InstaFailureReasonRegex(Pattern.compile("\\s+"), true),
                            new InstaFailureReasonString("1"),
                            new InstaFailureReasonRegex(Pattern.compile("\\s+"))
                    )));
        }

        /**/
        while (i == 0) {
            IO2.println("\n----------------------------------\n--- Version compatibility test ---\n----------------------------------");

            {
                var text = "int a(int r){return r;}int a(int r, int a){return r;}";
                var startTime = System.nanoTime();
                var p = Insta.parser(c99GrammarText, Insta.ParserCreationOptions.newWithStandardWhitespace());
                var parses = Insta.parses(p, text).castToParsesSuccess().hiccup();
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
                var p = Insta.parser(c99GrammarText, Insta.ParserCreationOptions.newWithStandardWhitespace());
                var parses = Insta.parses(p, text).castToParsesSuccess().hiccup();
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
            var p = Insta.parser(grammar);
            var parses = Insta.parses(p, text);

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
            IO2.println(Insta.parses(p, text, new Insta.ParsingOptions(null, false, Insta.UnhideOptions.none, true, false)) + " // Expected: " + expected);
            IO2.println(Insta.parses(p, text, new Insta.ParsingOptions(null, true, Insta.UnhideOptions.none, false, false)));
            IO2.println(Insta.parses(p, text, Insta.ParsingOptions.optMemory()) + " // Expected: " + expected);
        }

        /**/
        //while (i != 0)
        {
            IO2.println("\n----------------------------------\n---      Big input test 1      ---\n----------------------------------");

            @NotNull String text = readFile("ctest1.c");
            var p = Insta.parser(c99GrammarText, Insta.ParserCreationOptions.newWithStandardWhitespace());
            var startTime = System.nanoTime();
            var c = Insta.parse(p, text).castToParseSuccess();
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
            var p = Insta.parser(c99GrammarText, Insta.ParserCreationOptions.newWithStandardWhitespace());
            var startTime = System.nanoTime();
            var c = Insta.parses(p, text);
            c.getFirst();
            var endTime = System.nanoTime();
            IO2.println(c.getClass());
            IO2.println("Time taken (ms): " + (endTime - startTime) / 1000000.0 + " (Consider 15000.000ms good)");
        }
    }
}
