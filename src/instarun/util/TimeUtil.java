package instarun.util;

import instarun.functions.Procedure;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public final class TimeUtil {
    public static @NotNull String measureTimeMillis(final int n, final @NotNull Procedure f) {
        if (n < 1)
            throw new IllegalArgumentException();

        final double[] stream = DoubleStream.generate(() -> {
            final long start = System.nanoTime();
            f.execute();
            final long end = System.nanoTime();
            return (end - start) / 1000000.0;
        }).limit(n).sorted().toArray();

        final double min = stream[0];
        final double max = stream[stream.length - 1];;
        final double diff = max - min;
        final double sum = Arrays.stream(stream).sum();
        final double avg = sum / n;
        final double mid = stream[stream.length / 2];
        final double median = (stream[n / 4] + stream[(n / 4) * 3]) / 2;

        return String.format("{:lowest %.3f, :highest %.3f, :diff %.3f, :average %.3f, :mid %.3f, :median %.3f, :total %.3f}",
                min, max, diff, avg, mid, median, sum);
    }
}
