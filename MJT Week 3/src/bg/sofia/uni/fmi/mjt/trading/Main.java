package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.price.PriceChart;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;


public class Main {
    public static void main(String[] args) {
        PriceChart chart = new PriceChart(1001.11, 1002.22, 1003.34);

        Portfolio investor = new Portfolio("Ivan", chart, 1000000, 15);

        System.out.println(investor.getOwner());
        System.out.println("Budget: " + investor.getRemainingBudget());

        for (int i = 0; i < 15; i++) {
            int remainder = i % 3;
            switch (remainder) {
                case 0 -> investor.buyStock("AMZ", remainder + 1);
                case 1 -> investor.buyStock("MSFT", remainder + 1);
                case 2 -> investor.buyStock("GOOG", remainder + 1);
            }
        }

        for (int i = 0; i < 15; i++) {
            StockPurchase currentPurchase = investor.getAllPurchases()[i];
            System.out.println(
                currentPurchase.getPurchaseTimestamp().toString() + ": " + currentPurchase.getStockTicker() + " " +
                    currentPurchase.getQuantity() + "x " + currentPurchase.getPurchasePricePerUnit() + " = " + currentPurchase.getTotalPurchasePrice());
        }
        System.out.println("Remaining budget: " + investor.getRemainingBudget() + ", net worth:  " + investor.getNetWorth());
    }
}