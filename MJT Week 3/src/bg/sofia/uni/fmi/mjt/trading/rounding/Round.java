package bg.sofia.uni.fmi.mjt.trading.rounding;

abstract public class Round {
    public static double roundTwoDecimalUp(double num) {
        return ((int) (num * 100)) / 100.0;
    }
}
