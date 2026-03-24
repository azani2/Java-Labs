package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {
    private final Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = taxRates;
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null) {
            throw new IllegalArgumentException("Utility type was null.");
        }

        if (billable == null) {
            throw new IllegalArgumentException("Billable was null.");
        }

        double consumption = switch (utilityType) {
            case WATER -> billable.getWaterConsumption();
            case ELECTRICITY -> billable.getElectricityConsumption();
            case NATURAL_GAS -> billable.getNaturalGasConsumption();
        };

        return taxRates.get(utilityType) * consumption;
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException("Billable was null.");
        }
        double water = taxRates.get(UtilityType.WATER) * billable.getWaterConsumption();
        double electricity = taxRates.get(UtilityType.ELECTRICITY) * billable.getElectricityConsumption();
        double gas = taxRates.get(UtilityType.NATURAL_GAS) * billable.getNaturalGasConsumption();
        return water + electricity + gas;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("A billable was null.");
        }

        Map<UtilityType, Double> consumptionsDifferences = new HashMap<>();

        double difference = Math.abs(getUtilityCosts(UtilityType.WATER, firstBillable)
            -  getUtilityCosts(UtilityType.WATER, secondBillable));
        consumptionsDifferences.put(UtilityType.WATER, difference);

        difference = Math.abs(getUtilityCosts(UtilityType.ELECTRICITY, firstBillable)
            -  getUtilityCosts(UtilityType.ELECTRICITY, secondBillable));
        consumptionsDifferences.put(UtilityType.ELECTRICITY, difference);

        difference = Math.abs(getUtilityCosts(UtilityType.NATURAL_GAS, firstBillable)
            -  getUtilityCosts(UtilityType.NATURAL_GAS, secondBillable));
        consumptionsDifferences.put(UtilityType.NATURAL_GAS, difference);
        return Collections.unmodifiableMap(consumptionsDifferences);
    }
}
