package alphaparse.viz;

import alphaparse.parsetree.Node;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Viztool {
    public static int dumpParseTreeHelp(final @NotNull PrintStream printer,
                                        final @NotNull List<Node> parseRes,
                                        final @NotNull AtomicInteger count) {
        final int currentId = count.getAndIncrement();
        final @NotNull String label = getLabel(parseRes);

        printer.print(currentId + "[shape=box, label=\"" + label + "\"];");
        parseRes.stream().skip(1)
                .mapToInt(child -> dumpParseTreeHelp(
                        printer,
                        child instanceof Node.NodeParseTree ? ((Node.NodeParseTree) child).content() : List.of(child),
                        count))
                .forEach(childId -> printer.append(String.valueOf(currentId))
                        .append(" -> ")
                        .append(String.valueOf(childId))
                        .append(";"));
        return currentId;
    }

    private static @NotNull String getLabel(@NotNull List<Node> parseRes) {
        final @NotNull Node fpr = parseRes.getFirst();
        final @NotNull var label = switch (fpr) {
            case Node.NodeString nodeString -> nodeString.content();
            case Node.NodeTreeTag nodeTreeTag -> nodeTreeTag.content().getName();
            case Node.NodeFail ignored1 ->
                    throw new IllegalStateException("Cannot create parse-tree visualization " + fpr.getClass() + " (TODO).");
            case Node.NodeParseTree ignored2 ->
                    throw new IllegalStateException("This case should be handled in dumpParseTreeHelp.");
        };
        return label.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static int dumpParseTree(
            final @NotNull String dotFileNamePrefix,
            final @NotNull ParseTree parseRes) throws IOException, InterruptedException {
        final @NotNull var dotFileName = dotFileNamePrefix + ".dot";
        final @NotNull var pngFileName = dotFileNamePrefix + ".png";
        final @NotNull var args = new String[]{"dot", "-Tpng", dotFileName, "-o", pngFileName};

        try (final @NotNull var printer = new PrintStream(dotFileName)) {
            printer.print("digraph G {");
            dumpParseTreeHelp(printer, parseRes, new AtomicInteger());
            printer.print("}");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        var ret = Runtime.getRuntime().exec(args).waitFor();

        return ret;
    }
}
//      APENOESCAPENOE
//   ESCAPENOESCAPENOESCA
// NOESCAPENOESCAPENOESCAPE
// NOESCAP NOESCAPE OESCAPE
// NOESCA   OESCAP   ESCAPE
// NOESCAP NOESCAPE OESCAPE
//   ESCAPENOESCAPENOESCA
//     CAPENOESCAPENOES
//     CAPE OESCAP NOES
//     CAPE OESCAP NOES