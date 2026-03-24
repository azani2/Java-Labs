package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI {
    private final String owner;
    private final PriceChartAPI priceChart;
    private double budget;
    private final StockPurchase[] stockPurchases;
    private final int maxSize;
    private int size;

    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget;
        this.stockPurchases = new StockPurchase[maxSize];
        this.maxSize = maxSize;
        size = 0;
    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget,
                     int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;

        this.stockPurchases = new StockPurchase[maxSize];
        System.arraycopy(stockPurchases, 0, this.stockPurchases, 0, stockPurchases.length);

        this.budget = budget;
        size = stockPurchases.length;
        this.maxSize = maxSize;
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if (this.size >= this.maxSize)
            return null;

        if (stockTicker == null || stockTicker.isEmpty() || stockTicker.trim().isEmpty())
            return null;

        if (quantity <= 0)
            return null;

        double stockPrice = this.priceChart.getCurrentPrice(stockTicker);

        if (stockPrice > budget)
            return null;

        StockPurchase toPurchase;

        switch (stockTicker) {
            case "MSFT" -> toPurchase = new MicrosoftStockPurchase(quantity, LocalDateTime.now(), stockPrice);
            case "AMZ" -> toPurchase = new AmazonStockPurchase(quantity, LocalDateTime.now(), stockPrice);
            case "GOOG" -> toPurchase = new GoogleStockPurchase(quantity, LocalDateTime.now(), stockPrice);
            default -> {
                return null;
            }
        }

        this.stockPurchases[size++] = toPurchase;
        this.budget -= toPurchase.getTotalPurchasePrice();
        this.priceChart.changeStockPrice(stockTicker, 5);

        return toPurchase;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return this.stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        StockPurchase[] purchasesInRange = new StockPurchase[size];
        int count = 0;

        for (int i = 0; i < size; i++) {
            LocalDateTime currentPurchaseTimeStamp = this.stockPurchases[i].getPurchaseTimestamp();
            if ((currentPurchaseTimeStamp.isAfter(startTimestamp) && currentPurchaseTimeStamp.isBefore(endTimestamp))
                || currentPurchaseTimeStamp.equals(startTimestamp)
                || currentPurchaseTimeStamp.equals(endTimestamp)) {

                purchasesInRange[count++] = this.stockPurchases[i];
            }
        }

        StockPurchase[] purchasesInRangeTrimmed = new StockPurchase[count];
        System.arraycopy(purchasesInRange, 0, purchasesInRangeTrimmed, 0, count);
        return purchasesInRangeTrimmed;
    }

    @Override
    public double getNetWorth() {
        double netWorth = 0;

        for (int i = 0; i < size; i++) {
            StockPurchase purchase = stockPurchases[i];
            netWorth += purchase.getQuantity() * this.priceChart.getCurrentPrice(purchase.getStockTicker());
        }

        return netWorth;
    }

    @Override
    public double getRemainingBudget() {
        BigDecimal budgetFormat = new BigDecimal(this.budget).setScale(2, RoundingMode.HALF_UP);
        return budgetFormat.doubleValue();
    }

    @Override
    public String getOwner() {
        return this.owner;
    }
}
