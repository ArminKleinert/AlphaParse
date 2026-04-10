package alphaparse.parser.combinator;

import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
/usr/lib/jvm/default-java/bin/java -javaagent:/snap/intellij-idea-community/737/lib/idea_rt.jar=35023 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /home/tpk/Desktop/programming/AlphaParse/out/production/AlphaParse:/home/tpk/.m2/repository/org/jetbrains/annotations/26.0.2/annotations-26.0.2.jar:/home/tpk/.m2/repository/junit/junit/4.13.1/junit-4.13.1.jar:/home/tpk/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/home/tpk/.m2/repository/org/junit/jupiter/junit-jupiter/5.14.0/junit-jupiter-5.14.0.jar:/home/tpk/.m2/repository/org/junit/jupiter/junit-jupiter-api/5.14.0/junit-jupiter-api-5.14.0.jar:/home/tpk/.m2/repository/org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar:/home/tpk/.m2/repository/org/junit/platform/junit-platform-commons/1.14.0/junit-platform-commons-1.14.0.jar:/home/tpk/.m2/repository/org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar:/home/tpk/.m2/repository/org/junit/jupiter/junit-jupiter-params/5.14.0/junit-jupiter-params-5.14.0.jar:/home/tpk/.m2/repository/org/junit/jupiter/junit-jupiter-engine/5.14.0/junit-jupiter-engine-5.14.0.jar:/home/tpk/.m2/repository/org/junit/platform/junit-platform-engine/1.14.0/junit-platform-engine-1.14.0.jar alphaparse.Main
[:S]
[[:S, [:failure, could not parse "ABD"]]]
[0, [{tag=:string, expecting=ABC, full=true}], 1, 1, ABD, []]
[0, [{tag=:string, expecting=ABC, full=true}], 1, 1, ABD, []]
[]

[[:S, ABC]]
[[:S, ABC]]
[[:S, ABC]]

[:S, ABC]
class alphaparse.result.ParseTree

[[:S, [:B, 1, 1]], [:S, [:A, 1, 1]]]

[:S, 1, 1]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a1, []]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a, []]

[:S, 1, 1]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a1, []]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a, []]

[:S, 1, 1]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a1, []]
[0, [{tag=:regex, expecting=\d, full=false}], 1, 1, a, []]

Preparing performance tests...

----------------------------------
--- Standard performance tests ---
----------------------------------
Make parser: {:lowest 48.095, :highest 65.408, :diff 17.314, :average 49.851, :mid 48.942, :median 49.179, :total 9970.224}
Previous:    {:lowest 49.696, :highest 72.810, :diff 23.114, :average 51.662, :mid 50.159, :median 50.327, :total 103323.969}
Original:    {:lowest 105.916, :highest 214.071, :diff 108.155, :average 112.053, :mid 110.125, :median 110.065, :sum 224105.047} // n=2000
---
First parse: {:lowest 1.190, :highest 3.456, :diff 2.266, :average 1.232, :mid 1.224, :median 1.224, :total 24633.775}
Previous:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Original:    {:lowest 3.238, :highest 9.338, :diff 6.100, :average 3.299, :mid 3.279, :median 3.280, :sum 65981.479} // n=20000
---
All parses:  {:lowest 0.000, :highest 1.212, :diff 1.212, :average 0.001, :mid 0.000, :median 0.000, :total 11.262}
Previous:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Original:    {:lowest 3.235, :highest 6.767, :diff 3.532, :average 3.296, :mid 3.281, :median 3.282, :sum 65926.257} // n=20000
---
To array:    {:lowest 10.033, :highest 18.047, :diff 8.013, :average 10.258, :mid 10.186, :median 10.198, :total 20516.766}
Previous:    {:lowest 13.913, :highest 19.723, :diff 5.810, ::average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Original:    {:lowest 37.369, :highest 60.060, :diff 22.691, :average 38.122, :mid 37.937, :median 37.943, :sum 762444.823} // n=20000
---
Iteration:   {:lowest 10.012, :highest 13.829, :diff 3.817, :average 10.255, :mid 10.190, :median 10.210, :total 20509.177}
Previous:    {:lowest 13.906, :highest 23.016, :diff 9.110, :average 14.232, :mid 14.153, :median 14.155, :total 284642.717}
Original:    {:lowest 37.264, :highest 46.080, :diff 8.816, :average 38.005, :mid 37.834, :median 37.833, :sum 760096.656} // n=20000
---
ArrayList:   {:lowest 10.036, :highest 13.393, :diff 3.357, :average 10.311, :mid 10.231, :median 10.251, :total 20621.695}
Previous:    {:lowest 13.909, :highest 23.171, :diff 9.262, :average 14.210, :mid 14.143, :median 14.141, :total 284207.290}
Original:    {:lowest 37.336, :highest 45.696, :diff 8.360, :average 38.093, :mid 37.921, :median 37.919, :sum 761850.134} // n=20000
---
Cnt parses:  {:lowest 9.999, :highest 14.812, :diff 4.813, :average 10.210, :mid 10.152, :median 10.164, :total 20420.836}
Previous:    {:lowest 13.960, :highest 23.369, :diff 9.409, :average 14.266, :mid 14.191, :median 14.191, :total 285313.230}
Original:    -
Count of parses: 4096

----------------------------------
---   Memory optimized tests   ---
----------------------------------
First parse: {:lowest 1.195, :highest 3.432, :diff 2.237, :average 1.235, :mid 1.228, :median 1.228, :total 24695.658}
Std prev:    {:lowest 1.274, :highest 3.420, :diff 2.146, :average 1.309, :mid 1.303, :median 1.303, :total 26187.008}
Previous:    -
Original:    {:lowest 3.243, :highest 6.779, :diff 3.537, :average 3.300, :mid 3.284, :median 3.284, :sum 65992.776} // n=20000
---
All parses:  {:lowest 0.000, :highest 0.017, :diff 0.016, :average 0.000, :mid 0.000, :median 0.000, :total 4.880}
Std prev:    {:lowest 1.246, :highest 3.653, :diff 2.407, :average 1.287, :mid 1.280, :median 1.280, :total 25736.796}
Previous:    -
Original:    {:lowest 3.238, :highest 6.909, :diff 3.671, :average 3.308, :mid 3.291, :median 3.291, :sum 66157.123} // n=20000
---
List parses: {:lowest 10.013, :highest 18.737, :diff 8.724, :average 10.329, :mid 10.205, :median 10.225, :total 206583.956}
Std prev:    {:lowest 13.913, :highest 19.723, :diff 5.810, :average 14.222, :mid 14.141, :median 14.144, :total 284433.845}
Previous:    -
Original:    {:lowest 37.350, :highest 50.681, :diff 13.331, :average 38.118, :mid 37.937, :median 37.938, :sum 762350.525} // n=20000
---

Process finished with exit code 130 (interrupted by signal 2:SIGINT)

 */

public final class CombinatorBuffer {
    private static final ConcurrentHashMap<@NotNull Combinator, Reference<Combinator>> table = new ConcurrentHashMap<>();
    private static final ReferenceQueue<@NotNull Combinator> rq = new ReferenceQueue<>();
    private static boolean cachingDisabled = false;

    public static void disableCaching() {
        cachingDisabled = true;
        table.clear();
    }

    private <T extends Combinator> @NotNull T buffer(final @NotNull Map<@NotNull T, @NotNull T> buff, final @NotNull T c) {
//        final T temp = buff.get(c);
//        if (temp != null) return temp;
//        buff.put(c, c);
//        return c;
        return (T) getOrAdd((Combinator) c);
    }

    public @NotNull <T extends Combinator> Combinator getOrAdd(final @NotNull T combinator1) {
        if (cachingDisabled)
            return combinator1;

        @Nullable Reference<Combinator> existingRef = table.get(combinator1);
        if (existingRef == null) {
            ClassUtil.clearReferenceCache(rq, table);
            existingRef = table.putIfAbsent(combinator1, new WeakReference<>(combinator1, rq));
        }

        if (existingRef == null) {
            return combinator1;
        } else {
            @Nullable Combinator existingC = existingRef.get();
            if (existingC != null) {
                return existingC;
            }
            table.remove(combinator1, existingRef);
            return getOrAdd(combinator1);
        }

//        return switch (combinator1) {
//            case NonTerminal combinator -> getOrAdd(combinator);
//            case RegexpTerminal combinator -> getOrAdd(combinator);
//            case StringTerminal combinator -> getOrAdd(combinator);
//            case AlternationCombinator combinator -> getOrAdd(combinator);
//            case CatCombinator combinator -> getOrAdd(combinator);
//            case OptCombinator combinator -> getOrAdd(combinator);
//            case OrderedCombinator combinator -> getOrAdd(combinator);
//            case PlusCombinator combinator -> getOrAdd(combinator);
//            case RepetitionCombinator combinator -> getOrAdd(combinator);
//            case StarCombinator combinator -> getOrAdd(combinator);
//            case UnicodeCharTerminal combinator -> getOrAdd(combinator);
//            case EpsilonCombinator combinator -> getOrAdd(combinator);
//            case NegateCombinator combinator -> getOrAdd(combinator);
//            case LookaheadCombinator combinator -> getOrAdd(combinator);
//        };
    }

    private final @NotNull Map<@NotNull NonTerminal, @NotNull NonTerminal> nonTerminalSet = new HashMap<>();

    public @NotNull NonTerminal getOrAdd(final @NotNull NonTerminal combinator) {
        return buffer(nonTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull RegexpTerminal, @NotNull RegexpTerminal> regexpTerminalSet = new HashMap<>();

    public @NotNull RegexpTerminal getOrAdd(final @NotNull RegexpTerminal combinator) {
        return buffer(regexpTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull StringTerminal, @NotNull StringTerminal> combStringTerminalSet = new HashMap<>();

    public @NotNull StringTerminal getOrAdd(final @NotNull StringTerminal combinator) {
        return buffer(combStringTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull AlternationCombinator, @NotNull AlternationCombinator> alternationCombinatorSet = new HashMap<>();

    public @NotNull AlternationCombinator getOrAdd(final @NotNull AlternationCombinator combinator) {
        return buffer(alternationCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull CatCombinator, @NotNull CatCombinator> catCombinatorSet = new HashMap<>();

    public @NotNull CatCombinator getOrAdd(final @NotNull CatCombinator combinator) {
        return buffer(catCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull OptCombinator, @NotNull OptCombinator> alternationCombinators = new HashMap<>();

    public @NotNull OptCombinator getOrAdd(final @NotNull OptCombinator combinator) {
        return buffer(alternationCombinators, combinator);
    }

    private final @NotNull Map<@NotNull OrderedCombinator, @NotNull OrderedCombinator> orderedCombinatorSet = new HashMap<>();

    public @NotNull OrderedCombinator getOrAdd(final @NotNull OrderedCombinator combinator) {
        return buffer(orderedCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull PlusCombinator, @NotNull PlusCombinator> plusCombinatorSet = new HashMap<>();

    public @NotNull PlusCombinator getOrAdd(final @NotNull PlusCombinator combinator) {
        return buffer(plusCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull RepetitionCombinator, @NotNull RepetitionCombinator> repetitionCombinatorSet = new HashMap<>();

    public @NotNull RepetitionCombinator getOrAdd(final @NotNull RepetitionCombinator combinator) {
        return buffer(repetitionCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull StarCombinator, @NotNull StarCombinator> starCombinatorSet = new HashMap<>();

    public @NotNull StarCombinator getOrAdd(final @NotNull StarCombinator combinator) {
        return buffer(starCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull UnicodeCharTerminal, @NotNull UnicodeCharTerminal> unicodeCharTerminalSet = new HashMap<>();

    public @NotNull UnicodeCharTerminal getOrAdd(final @NotNull UnicodeCharTerminal combinator) {
        return buffer(unicodeCharTerminalSet, combinator);
    }

    private final @NotNull Map<@NotNull EpsilonCombinator, @NotNull EpsilonCombinator> epsilonCombinatorSet = new HashMap<>();

    public @NotNull EpsilonCombinator getOrAdd(final @NotNull EpsilonCombinator combinator) {
        return buffer(epsilonCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull NegateCombinator, @NotNull NegateCombinator> negateCombinatorSet = new HashMap<>();

    public @NotNull NegateCombinator getOrAdd(final @NotNull NegateCombinator combinator) {
        return buffer(negateCombinatorSet, combinator);
    }

    private final @NotNull Map<@NotNull LookaheadCombinator, @NotNull LookaheadCombinator> lookaheadCombinatorSet = new HashMap<>();

    public @NotNull LookaheadCombinator getOrAdd(final @NotNull LookaheadCombinator combinator) {
        return buffer(lookaheadCombinatorSet, combinator);
    }

}
