package org.example.objects;

import lombok.extern.log4j.Log4j2;
import org.example.enums.ElevatorState;

import java.text.MessageFormat;
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

    /**
     * adds the elevator back to the list of available elevators
     * @param elevator the elevator to be added
     */
    private void makeAvailable(final Elevator elevator) {
        synchronized (this.availableElevators) {
            this.availableElevators.add(elevator);
            this.availableElevators.notify();
        }
    }

    /**
     *
     * @param currentFloor the floor where the request occurred
     * @return returns the nearest elevator to the current floor of the request
     * @throws InterruptedException Thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted, either before or during the activity
     */
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
        if (newFloor < 0 || newFloor > 55) {
            log.warn(MessageFormat.format("floor not available: {0}", newFloor));
            return;
        }

        final Elevator elevator = this.getNextElevator(currentFloor);
        elevator.changeFloor(newFloor);
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

            // initially, the .forEach function was used, but then I had to add a try/catch clause
            // hence, a normal foreach loop will do the trick
            for (final Elevator elevator: availableElevators) { elevator.changeFloor(0); }
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

