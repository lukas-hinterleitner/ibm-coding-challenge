package org.example.objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.example.enums.ElevatorState;

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

    /**
     * determines the elevator state based on the current floor and the new floor
     * @param newFloor the new floor
     * @return the new elevator state
     */
    private ElevatorState determineElevatorState(final int newFloor) {
        if (newFloor - currentFloor > 0) return ElevatorState.UP;
        else if (newFloor - currentFloor < 0) return ElevatorState.DOWN;
        else return ElevatorState.IDLE;
    }

    /**
     * sends the elevator to corresponding floor
     * @param newFloor the floor where the elevator should travel
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    public void changeFloor(final int newFloor) throws InterruptedException {
        this.state = determineElevatorState(newFloor);

        // do nothing when elevator gets request to travel to the same floor
        if (this.state == ElevatorState.IDLE) {
            log.info("request to same floor -> doing nothing");
            return;
        }

        final int oldFloor = this.currentFloor;

        // simulate elevator travel
        for (int i = 0; i < Math.abs(oldFloor - newFloor); i++) {
            if (this.state == ElevatorState.UP) {
                this.currentFloor++;
            } else {
                this.currentFloor--;
            }

            // assume that it takes the elevator 100 ms to travel one floor
            Thread.sleep(100L);
        }

        log.info(MessageFormat.format("elevator {0} went from floor {1} floor {2}", this.id, oldFloor, newFloor));
        this.state = ElevatorState.IDLE;
    }
}
