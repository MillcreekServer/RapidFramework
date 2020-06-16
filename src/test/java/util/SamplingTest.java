package util;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SamplingTest {
    @Test
    public void getReciprocalPossibility() {
    }

    @Test
    public void isWin() {
        double possibility = 0.3;
        int alpha = 100;

        for (int x = 0; x < 5; x++) {
            int win = 0, lose = 0;
            for (int i = 0; i < 1000; i++) {
                if (Sampling.isWin(possibility, 2))
                    win++;
                else
                    lose++;
            }

            if (Math.abs(win - lose) > 400 + alpha) { // expect 300 win and 700 lose. Allow errors within alpha
                fail();
            }
        }
    }

    @Test
    public void uniform() {
        Set<Integer> set = new HashSet<>();
        for (int val : Sampling.uniform(1000, 1000))
            set.add(val);

        // Probability of sampling all unique number with replacement is infinitesimally small.
        // p(x=1) * p(x=2) * ... = 1/1000 * 1/1000 * ...
        if (set.size() == 1000)
            fail();
    }

    @Test
    public void uniform2() {
        Set<Integer> set = new HashSet<>();
        for (int val : Sampling.uniform(1000, 1000, false))
            set.add(val);
        assertEquals(1000, set.size());
    }
}