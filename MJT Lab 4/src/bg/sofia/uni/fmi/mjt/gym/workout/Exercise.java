package bg.sofia.uni.fmi.mjt.gym.workout;

import java.util.Objects;

public record Exercise(String name, int sets, int repetitions) {
    public Exercise {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Exercise name was empty.");
        }

        if (sets <= 0) {
            throw new IllegalArgumentException("Sets must be a positive number.");
        }

        if (repetitions <= 0) {
            throw new IllegalArgumentException("Repetitions must be a positive number.");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Exercise that)) {
            return false;
        }

        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
