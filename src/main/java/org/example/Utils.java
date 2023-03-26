package org.example;

import org.example.enums.ElevatorState;

public class Utils {
    public static ElevatorState determineElevatorState(final int origin, final int destination, final ElevatorState fallback) {
        if (destination - origin > 0) return ElevatorState.UP;
        else if (destination - origin < 0) return ElevatorState.DOWN;
        else return fallback;
    }
}
