package org.example.objects;

import lombok.extern.log4j.Log4j2;
import org.example.Utils;
import org.example.enums.ElevatorState;

import java.util.*;

@Log4j2
public class ElevatorController {
    private final Set<Elevator> availableElevators = new HashSet<>();
    private final Map<Elevator, ElevatorState> busyElevators = new HashMap<>();
    private final int amountElevators;

    public ElevatorController(final int amountElevators) {
        // initialization for DC tower --> seven elevators
        for (int i = 0; i < amountElevators; i++) {
            availableElevators.add(Elevator.builder().id(i).state(ElevatorState.IDLE).build());
        }

        this.amountElevators = amountElevators;
    }

    /**
     * handle elevator when it becomes available again
     * @param elevator elevator
     */
    private void makeAvailable(final Elevator elevator) {
        synchronized (this.availableElevators) {
            this.availableElevators.add(elevator);
            this.availableElevators.notify();
        }

        synchronized (this.busyElevators) {
            this.busyElevators.remove(elevator);
            this.busyElevators.notify();
        }
    }

    /**
     * gets next best elevator based on distance from current floor and prioritize already traveling elevator for intermediate stops
     * @param origin the floor where people should be picked up
     * @param destination the floor where people should be dropped of
     * @return returns the best elevator from the current position
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied,
     *                              and the thread is interrupted, either before or during the activity
     */
    private Elevator getNextElevator(final int origin, final int destination) throws InterruptedException {
        final ElevatorState state = Utils.determineElevatorState(origin, destination, ElevatorState.IDLE);

        synchronized (this.busyElevators) {
            // get elevators that travel in the same direction and are still on the way to obtain elevator for intermediate stops
            final Optional<Elevator> elevator = this.busyElevators.entrySet().stream().filter(entrySet -> {
                final ElevatorState stateOfOtherElevator = entrySet.getValue();

                final boolean goingUp = state == ElevatorState.UP && stateOfOtherElevator == ElevatorState.UP;
                final boolean goingDown = state == ElevatorState.DOWN && stateOfOtherElevator == ElevatorState.DOWN;

                final Elevator ev = entrySet.getKey();

                // check direction and if stop is still possible
                return (goingUp && origin > ev.getCurrentFloor()) || (goingDown && origin < ev.getCurrentFloor());
            }).map(Map.Entry::getKey).findFirst();

            if (elevator.isPresent()) {
                elevator.get().addStop(origin);
                elevator.get().addStop(destination);
                return elevator.get();
            }
        }

        synchronized (this.availableElevators) {
            while (this.availableElevators.isEmpty()) {
                log.info("wait until elevator is available");
                this.availableElevators.wait();
                log.info("elevator available -> continue");
            }

            // find the nearest elevator of the current floor
            final Elevator nearestElevator = availableElevators.stream().min((elevator1, elevator2) -> {
                final int distance1 = Math.abs(elevator1.getCurrentFloor() - origin);
                final int distance2 = Math.abs(elevator2.getCurrentFloor() - origin);

                return (int) Math.signum(distance1 - distance2);
            }).orElseThrow();

            synchronized (this.busyElevators) {
                this.busyElevators.put(nearestElevator, Utils.determineElevatorState(origin, destination, ElevatorState.IDLE));
                this.busyElevators.notify();
            }

            this.availableElevators.remove(nearestElevator);
            this.availableElevators.notify();
            return nearestElevator;
        }
    }

    /**
     * @param origin      the floor where the request occurred
     * @param destination the floor where the elevator should travel
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied,
     *                              and the thread is interrupted, either before or during the activity
     */
    public void startElevator(final int origin, final int destination) throws InterruptedException {
        final Elevator elevator = this.getNextElevator(origin, destination);

        // only start elevator when it is not running (when it's already running, future intermediate stops were added)
        if (elevator.getState() == ElevatorState.IDLE) {
            elevator.startElevator(origin, destination);
        }

        this.makeAvailable(elevator);
    }

    /**
     * sends all elevators back to the first floor
     *
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied,
     *                              and the thread is interrupted, either before or during the activity
     */
    public void sendElevatorsToFirstFloor() throws InterruptedException {
        // wait until all elevators are available
        synchronized (this.availableElevators) {
            while (this.availableElevators.size() != this.amountElevators) {
                this.availableElevators.wait();
            }

            this.availableElevators.stream().filter(elevator -> elevator.getCurrentFloor() != 0).forEach(elevator -> {
                try {
                    elevator.startElevator(elevator.getCurrentFloor(), 0);
                } catch (InterruptedException e) {
                    log.error("thread interrupted");
                }
            });
        }
    }

    /**
     * gets the available elevators at the moment
     * @return available elevators
     */
    public Set<Elevator> getAvailableElevators() {
        synchronized (this.availableElevators) {
            return this.availableElevators;
        }
    }
}

