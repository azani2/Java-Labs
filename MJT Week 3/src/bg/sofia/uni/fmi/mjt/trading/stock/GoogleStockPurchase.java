package bg.sofia.uni.fmi.mjt.trading.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class GoogleStockPurchase implements StockPurchase {
    private final int quantity;
    private final LocalDateTime purchaseTimeStamp;
    private final double purchasePricePerUnit;
    private final String ticker;

    public GoogleStockPurchase(int quantity, LocalDateTime purchaseTimeStamp, double purchasePricePerUnit) {
        this.quantity = quantity;
        this.purchaseTimeStamp = purchaseTimeStamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
        this.ticker = "GOOG";
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
