package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    private int totalPlotArea;
    private int remainingBuildableArea;
    private Map<String, E> buildables;

    public Plot(int buildableArea) {
        this.totalPlotArea = buildableArea;
        this.remainingBuildableArea = buildableArea;
        buildables = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isEmpty() || address.isBlank()) {
            throw new IllegalArgumentException("Address was null or empty.");
        }

        if (buildable == null) {
            throw new IllegalArgumentException("Buildable was null.");
        }

        if (buildables.containsKey(address)) {
            throw new BuildableAlreadyExistsException("Buildable already exist on given address;");
        }

        if (remainingBuildableArea < buildable.getArea()) {
            throw new InsufficientPlotAreaException(
                "Not enough remaining plot area to construct buildable on this plot.");
        }

        buildables.put(address, buildable);
        remainingBuildableArea -= buildable.getArea();
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address was null or blank.");
        }

        if (!buildables.containsKey(address)) {
            throw new BuildableNotFoundException("Buildable with this address does not exist on this plot.");
        }

        remainingBuildableArea += buildables.get(address).getArea();
        buildables.remove(address);
    }

    @Override
    public void demolishAll() {
        if (buildables == null) {
            return;
        }

        buildables.clear();
        remainingBuildableArea = totalPlotArea;
    }

    @Override
    public Map<String, E> getAllBuildables() {
        Map<String, E> buildablesCopy = new HashMap<>();
        buildablesCopy.putAll(buildables);
        return Collections.unmodifiableMap(buildablesCopy);
    }

    @Override
    public int getRemainingBuildableArea() {
        return remainingBuildableArea;
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("Buildables was nulll or empty.");
        }

        int totalAreaToBuild = 0;
        for (Map.Entry<String, E> e : buildables.entrySet()) {
            if (e.getValue() == null) {
                throw new IllegalArgumentException("A buildable was null or empty.");
            }

            if (e.getKey() == null || e.getKey().isBlank() || e.getKey().isEmpty()) {
                throw new IllegalArgumentException("An address was null or blank.");
            }

            if (this.buildables.containsKey(e.getKey())) {
                throw new BuildableAlreadyExistsException("An address was already built on.");
            }

            totalAreaToBuild += e.getValue().getArea();
        }

        if (totalAreaToBuild > remainingBuildableArea) {
            throw new InsufficientPlotAreaException("Not enough remaining plot area to construct all buildables.");
        }

        for (Map.Entry<String, E> e : buildables.entrySet()) {
            construct(e.getKey(), e.getValue());
        }
    }
}
