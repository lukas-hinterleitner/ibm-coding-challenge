package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Getter
@Log4j2
public class Elevator {
    private int currentFloor;
    private ElevatorState state = ElevatorState.IDLE;
    public void changeFloor(final int newFloor, Direction direction) throws InterruptedException {
        if (direction == Direction.UP) state = ElevatorState.UP;
        else if (direction == Direction.DOWN) state = ElevatorState.DOWN;
        else log.error("unknown direction");

        // simulate time for floor change
        Thread.sleep(100L * Math.abs(currentFloor - newFloor));

        this.currentFloor = newFloor;
        this.state = ElevatorState.IDLE;

        log.info(MessageFormat.format("elevator arrived at floor {0}", newFloor));
    }
}
