package org.example.objects;

import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.List;

// singleton
@Log4j2
public class DCTower {
    private final ElevatorController controller;

    private static final DCTower instance = new DCTower();

    private DCTower() {
        // dc tower has seven elevators
        this.controller = new ElevatorController(7);
    }

    public static DCTower getInstance() {
        return instance;
    }

    /**
     * pipe the request for an elevator to the controller asynchronously
     * @param currentFloor the current floor where the request occurred
     * @param newFloor the floor where the elevator should travel
     */
    public void addElevatorRequest(final int currentFloor, final int newFloor) {
        if (newFloor < 0 || newFloor > 55) {
            log.warn(MessageFormat.format("floor not available: {0}", newFloor));
            return;
        }

        log.info(MessageFormat.format("new request from floor {0} to floor {1}", currentFloor, newFloor));

        // According to the task description the direction is also needed.
        // However, basically the direction can be inferred by checking the current and the new floor against each other
        new Thread(() -> {
            try {
                this.controller.sendElevator(currentFloor, newFloor);
            } catch (InterruptedException e) {
                log.error(MessageFormat.format("error occurred when traveling from {0} to {1}", currentFloor, newFloor));
            }
        }).start();
    }

    /**
     * sends all elevators to the first floor
     */
    public void sendElevatorsToFirstFloor() {
        try {
            this.controller.sendElevatorsToFirstFloor();
        } catch (InterruptedException e) {
            log.error(MessageFormat.format("sending all elevators to the ground floor led to the following error: {0}", e.getMessage()));
        }
    }

    public List<Elevator> getAvailableElevators() {
        return this.controller.getAvailableElevators();
    }
}
