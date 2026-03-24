package bg.sofia.uni.fmi.mjt.simcity.property;

import bg.sofia.uni.fmi.mjt.simcity.property.buildable.BuildableType;

import java.util.Objects;

public class School extends Building {
    protected String name;
    protected int studentCapacity;

    public School(int area, BuildableType buildableType, String name, int studentCapacity) {
        super(area, buildableType);
        this.name = name;
        this.studentCapacity = studentCapacity;
    }

    public School(int area, BuildableType buildableType, double waterConsumptions,
                  double electricityConsumption, double naturalGasConsumption, String name, int studentCapacity) {
        super(area, buildableType, waterConsumptions, electricityConsumption, naturalGasConsumption);
        this.name = name;
        this.studentCapacity = studentCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof School s)) {
            return false;
        }

        return name.equals(s.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "[school name: " + name
            + "] , [capacity: " + studentCapacity
            + "], " + super.toString();
    }
}
