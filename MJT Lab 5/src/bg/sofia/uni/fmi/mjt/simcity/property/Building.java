package bg.sofia.uni.fmi.mjt.simcity.property;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.BuildableType;
import bg.sofia.uni.fmi.mjt.simcity.utility.UtilityType;

import java.util.HashMap;
import java.util.Map;

public abstract class Building implements Billable {
    protected Map<UtilityType, Double> consumptions;
    protected BuildableType buildableType;
    protected int area;

    public Building(int area, BuildableType type) {
        this.area = area;
        buildableType = type;
        consumptions = new HashMap<>();
        consumptions.put(UtilityType.WATER, 0.0);
        consumptions.put(UtilityType.ELECTRICITY, 0.0);
        consumptions.put(UtilityType.NATURAL_GAS, 0.0);
    }

    public Building(int area, BuildableType type,
                    double waterConsumptions, double electricityConsumption, double naturalGasConsumption) {
        this.area = area;
        buildableType = type;
        consumptions = new HashMap<>();
        consumptions.put(UtilityType.WATER, waterConsumptions);
        consumptions.put(UtilityType.ELECTRICITY, electricityConsumption);
        consumptions.put(UtilityType.NATURAL_GAS, naturalGasConsumption);
    }

    @Override
    public double getWaterConsumption() {
        return consumptions.get(UtilityType.WATER);
    }

    @Override
    public double getElectricityConsumption() {
        return consumptions.get(UtilityType.ELECTRICITY);
    }

    @Override
    public double getNaturalGasConsumption() {
        return consumptions.get(UtilityType.NATURAL_GAS);
    }

    @Override
    public BuildableType getType() {
        return buildableType;
    }

    @Override
    public int getArea() {
        return area;
    }

    public void setWaterConsumption(double waterConsumption) {
        if (waterConsumption < 0) {
            throw new IllegalArgumentException("Water consumption must be non-negative number.");
        }

        consumptions.put(UtilityType.WATER, waterConsumption);
    }

    public void setElectricityConsumption(double electricityConsumption) {
        if (electricityConsumption < 0) {
            throw new IllegalArgumentException("Electricity consumption must be non-negative number.");
        }

        consumptions.put(UtilityType.ELECTRICITY, electricityConsumption);
    }

    public void setNaturalGasConsumption(double naturalGasConsumption) {
        if (naturalGasConsumption < 0) {
            throw new IllegalArgumentException("Electricity consumption must be non-negative number.");
        }

        consumptions.put(UtilityType.NATURAL_GAS, naturalGasConsumption);
    }

    @Override
    public String toString() {
        return "[area: " + area
            + "], [water consumption: " + consumptions.get(UtilityType.WATER)
            + "], [electricity consumption: " + consumptions.get(UtilityType.ELECTRICITY)
            + "], [natural gas consumption: " + consumptions.get(UtilityType.NATURAL_GAS);
    }
}
