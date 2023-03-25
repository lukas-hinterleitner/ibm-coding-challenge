package org.example.objects;

import lombok.extern.log4j.Log4j2;
import org.example.enums.ElevatorState;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ElevatorController {
    private final List<Elevator> availableElevators = new ArrayList<>();
    private final List<Elevator> busyElevators = new ArrayList<>();
    private final int amountElevators;

    public ElevatorController(final int amountElevators) {
        // initialization for DC tower --> seven elevators
        for (int i = 0; i < amountElevators; i++) {
            availableElevators.add(Elevator.builder().id(i).state(ElevatorState.IDLE).build());
        }

        this.amountElevators = amountElevators;
    }

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

    private Elevator getNextElevator(final int currentFloor) throws InterruptedException {
        synchronized (this.availableElevators) {
            while (this.availableElevators.isEmpty()) {
                log.info("wait until elevator is available");
                this.availableElevators.wait();
                log.info("elevator available -> continue");
            }

            // find the nearest elevator of the current floor
            final Elevator nearestElevator = availableElevators.stream().min((elevator1, elevator2) -> {
                final int distance1 = Math.abs(elevator1.getCurrentFloor() - currentFloor);
                final int distance2 = Math.abs(elevator2.getCurrentFloor() - currentFloor);

                return (int) Math.signum(distance1 - distance2);
            }).orElseThrow();

            synchronized (this.busyElevators) {
                this.busyElevators.add(nearestElevator);
                this.busyElevators.notify();
            }

            //TODO: maybe implement intermediate stops

            this.availableElevators.remove(nearestElevator);
            return nearestElevator;
        }
    }

    /**
     *
     * @param currentFloor the floor where the request occurred
     * @param newFloor the floor where the elevator should travel
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity
     */
    public void sendElevator(final int currentFloor, final int newFloor) throws InterruptedException {
        final Elevator elevator = this.getNextElevator(currentFloor);
        elevator.changeFloor(currentFloor, newFloor);
        this.makeAvailable(elevator);
    }

    /**
     * sends all elevators back to the first floor
     * @throws InterruptedException if any thread interrupted the current thread before or while
     * the current thread was waiting. The interrupted status of the current thread is cleared
     * when this exception is thrown.
     */
    public void sendElevatorsToFirstFloor() throws InterruptedException {
        // wait until all elevators are available
        synchronized (this.availableElevators) {
            while (this.availableElevators.size() != this.amountElevators) {
                this.availableElevators.wait();
            }

            this.availableElevators.stream().filter(elevator -> elevator.getCurrentFloor() != 0).forEach(elevator -> {
                try {
                    elevator.changeFloor(elevator.getCurrentFloor(), 0);
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
    public List<Elevator> getAvailableElevators() {
        synchronized (this.availableElevators) {
            return this.availableElevators;
        }
    }
}

