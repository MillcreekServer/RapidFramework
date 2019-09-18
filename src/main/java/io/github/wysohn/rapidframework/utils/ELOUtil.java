package io.github.wysohn.rapidframework.utils;

public class ELOUtil {
    private static final double CONSTANT_TRANSFORM = 400.0;

    /**
     * Calculate new ELO based on given data and game result.
     *
     * @param data            the current ELO data as pair.
     * @param scoreCandidate1 the game result. 1 if win, 0.5 for draw, and 0 for
     *                        lose
     * @param scoreCandidate2 the game result. 1 if win, 0.5 for draw, and 0 for
     *                        lose
     * @return new ELO calculated.
     */
    public static ELOData caculateNewElo(ELOData data, int scoreCandidate1, int scoreCandidate2) {
        return caculateNewElo(data, scoreCandidate1, scoreCandidate2, 32);
    }

    /**
     * Calculate new ELO based on given data and game result.
     *
     * @param data            the current ELO data as pair.
     * @param scoreCandidate1 the game result. 1 if win, 0.5 for draw, and 0 for
     *                        lose
     * @param scoreCandidate2 the game result. 1 if win, 0.5 for draw, and 0 for
     *                        lose
     * @param K_factor        how influential this game is. 32 is used in chess.
     * @return new ELO calculated.
     */
    public static ELOData caculateNewElo(ELOData data, int scoreCandidate1, int scoreCandidate2, int K_factor) {
        double transform1 = Math.pow(10, data.candidate1 / CONSTANT_TRANSFORM);
        double transform2 = Math.pow(10, data.candidate2 / CONSTANT_TRANSFORM);

        double transformSum = transform1 + transform2;

        double expected1 = floor(transform1 / transformSum, 100);
        double expected2 = floor(transform2 / transformSum, 100);

        return new ELOData(data.candidate1 + K_factor * (scoreCandidate1 - expected1),
                data.candidate2 + K_factor * (scoreCandidate2 - expected2));
    }

    /**
     * Get floor value of given value up to the given factor. The factor should be
     * multiple of 10 which represents up to that decimal points. 10 will floor up
     * to one decimal places.
     *
     * @param value
     * @param factor
     * @return
     */
    private static double floor(double value, double factor) {
        return Math.floor(value * factor) / factor;
    }

    public static void main(String[] ar) {
        System.out.println(caculateNewElo(new ELOData(2400, 2000), 1, 0));
    }

    public static class ELOData {
        private final int candidate1;
        private final int candidate2;

        public ELOData(int candidate1, int candidate2) {
            super();
            this.candidate1 = candidate1;
            this.candidate2 = candidate2;
        }

        public ELOData(double candidate1, double candidate2) {
            super();
            this.candidate1 = (int) candidate1;
            this.candidate2 = (int) candidate2;
        }

        public int getCandidate1() {
            return candidate1;
        }

        public int getCandidate2() {
            return candidate2;
        }

        @Override
        public String toString() {
            return "candidate1=" + candidate1 + ", candidate2=" + candidate2;
        }
    }
}
