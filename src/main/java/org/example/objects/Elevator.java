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

    private void travel(final int origin, final int destination) throws InterruptedException {
        if (destination - origin > 0) this.state = ElevatorState.UP;
        else if (destination - origin < 0) this.state = ElevatorState.DOWN;
        else this.state = ElevatorState.IDLE;

        // do nothing when elevator gets request to travel to the same floor
        if (this.state == ElevatorState.IDLE) {
            return;
        }

        // simulate elevator travel
        for (int i = 0; i < Math.abs(origin - destination); i++) {
            if (this.state == ElevatorState.UP) {
                this.currentFloor++;
            } else {
                this.currentFloor--;
            }

            // assume that it takes the elevator 100 ms to travel one floor
            Thread.sleep(100);
        }

        this.state = ElevatorState.IDLE;

        log.info(MessageFormat.format("elevator {0} went from floor {1} floor {2}", this.id, origin, destination));
    }

    /**
     * sends the elevator to corresponding floor
     * @param origin the floor where people want to be picked up
     * @param destination the floor where the elevator should travel
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    public void changeFloor(final int origin, final int destination) throws InterruptedException {
        // travel from current floor to origin
        this.travel(this.currentFloor, origin);

        // travel from the origin floor to destination floor
        this.travel(origin, destination);
    }
}
