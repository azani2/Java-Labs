package bg.sofia.uni.fmi.mjt.trading.price;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceChart implements PriceChartAPI {
    private double microsoftStockPrice;
    private double googleStockPrice;
    private double amazonStockPrice;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice) {
        this.microsoftStockPrice = microsoftStockPrice;
        this.googleStockPrice = googleStockPrice;
        this.amazonStockPrice = amazonStockPrice;
    }

    @Override
    public double getCurrentPrice(String stockTicker) {
        if (stockTicker == null || stockTicker.isEmpty() || stockTicker.trim().isEmpty())
            return 0.0;

        double price = 0;
        switch (stockTicker) {
            case "MSFT" -> {
                price = this.microsoftStockPrice;
            }
            case "GOOG" -> {
                price = this.googleStockPrice;
            }
            case "AMZ" -> {
                price = this.amazonStockPrice;
            }
            default -> {
                return 0.0;
            }
        }

        BigDecimal priceFormat = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return priceFormat.doubleValue();
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if (stockTicker == null || stockTicker.isEmpty() || stockTicker.trim().isEmpty())
            return false;

        if (percentChange <= 0)
            return false;

        switch (stockTicker) {
            case "MSFT" -> {
                microsoftStockPrice = (microsoftStockPrice * (100 + percentChange)) / 100;
                return true;
            }
            case "AMZ" -> {
                amazonStockPrice = (amazonStockPrice * (100 + percentChange)) / 100;
                return true;
            }
            case "GOOG" -> {
                googleStockPrice = (googleStockPrice * (100 + percentChange)) / 100;
                return true;
            }
            default -> {
                return false;
            }
        }
    }
}
