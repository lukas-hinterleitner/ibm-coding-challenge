package org.example;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;

@Getter
@Builder
@Log4j2
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Elevator {

    @EqualsAndHashCode.Include
    private int id;
    private int currentFloor;
    private ElevatorState state; // furthermore, this state can be used to implemented intermediate stops

    public void changeFloor(final int newFloor, Direction direction) throws InterruptedException {
        if (direction == Direction.UP) state = ElevatorState.UP;
        else if (direction == Direction.DOWN) state = ElevatorState.DOWN;
        else log.error("unknown direction");

        // simulate time for floor change
        Thread.sleep(100L * Math.abs(currentFloor - newFloor));

        log.info(MessageFormat.format("elevator {0} went from floor {1} floor {2}", this.id, this.currentFloor, newFloor));
        this.currentFloor = newFloor;
        this.state = ElevatorState.IDLE;
    }
}
