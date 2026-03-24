package bg.sofia.uni.fmi.mjt.trading.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class AmazonStockPurchase implements StockPurchase {
    int quantity;
    LocalDateTime purchaseTimeStamp;
    double purchasePricePerUnit;
    final String ticker;

    public AmazonStockPurchase(int quantity, LocalDateTime purchaseTimeStamp, double purchasePricePerUnit) {
        this.quantity = quantity;
        this.purchaseTimeStamp = purchaseTimeStamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
        this.ticker = "AMZ";
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return this.purchaseTimeStamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        BigDecimal priceFormat = new BigDecimal(this.purchasePricePerUnit).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return priceFormat.doubleValue();
    }

    @Override
    public double getTotalPurchasePrice() {
        BigDecimal priceFormat = new BigDecimal(this.purchasePricePerUnit * this.quantity).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return priceFormat.doubleValue();
    }

    @Override
    public String getStockTicker() {
        return this.ticker;
    }
}
