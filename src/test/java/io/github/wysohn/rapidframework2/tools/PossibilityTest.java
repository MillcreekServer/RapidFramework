package io.github.wysohn.rapidframework2.tools;

import org.junit.Test;

import static org.junit.Assert.fail;

public class PossibilityTest {

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
                if (Possibility.isWin(possibility, 2))
                    win++;
                else
                    lose++;
            }

            if (Math.abs(win - lose) > 400 + alpha) { // expect 300 win and 700 lose. Allow errors within alpha
                fail();
            }
        }
    }
}