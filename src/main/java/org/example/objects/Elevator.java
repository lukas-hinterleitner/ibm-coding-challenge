package org.example.objects;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.example.Utils;
import org.example.enums.ElevatorState;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@Log4j2
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Elevator {
    @EqualsAndHashCode.Include
    private int id;
    private int currentFloor;
    private ElevatorState state;

    private ElevatorState previousState;
    private final Set<Integer> intermediateStops = new HashSet<>();

    /**
     *
     * @param origin the floor where people want to be picked up
     * @param destination the floor where the people will be dropped off
     * @param peopleInside this variable is just for changing the console output depending on if people are inside the elevator or not
     * @throws InterruptedException
     */
    private void travel(final int origin, final int destination, final boolean peopleInside) throws InterruptedException {
        this.previousState = this.state;
        this.state = Utils.determineElevatorState(origin, destination, this.previousState);

        // do nothing when elevator gets request to travel to the same floor
        if (this.state == ElevatorState.IDLE) {
            return;
        }

        if (peopleInside) {
            log.info(MessageFormat.format("elevator {0} started transport from floor {1}", this.id, origin));
        } else {
            log.info(MessageFormat.format("elevator {0} started moving from floor {1}", this.id, origin));
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

            if (this.intermediateStops.contains(this.currentFloor) && this.currentFloor != destination) {
                openAndCloseDoors();
                log.info(MessageFormat.format("elevator {0} made an intermediate stop at floor {1}", this.id, this.currentFloor));
            }
        }

        if (peopleInside) {
            log.info(MessageFormat.format("elevator {0} finished transport at floor {1}", this.id, destination));
        } else {
            log.info(MessageFormat.format("elevator {0} finished moving at floor {1}", this.id, destination));
        }
    }

    /**
     * simulate doors
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    private void openAndCloseDoors() throws InterruptedException {
        this.state = ElevatorState.BUSY;
        Thread.sleep(200);
    }

    /**
     * starts the elevator
     * @param origin the floor where people want to be picked up
     * @param destination the floor where the people will be dropped off
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity.
     */
    public void startElevator(final int origin, final int destination) throws InterruptedException {
        // travel from current floor to origin
        this.travel(this.currentFloor, origin, false);
        this.openAndCloseDoors();

        // travel from origin to destination
        this.travel(origin, destination, true);
        this.openAndCloseDoors();

        this.previousState = this.state;
        this.state = ElevatorState.IDLE;
        this.intermediateStops.clear();
    }

    /**
     * adds an intermediate stop to the elevator
     * @param stop the floor where the elevator should make an intermediate stop
     */
    public void addStop(final int stop) {
        this.intermediateStops.add(stop);
    }
}
