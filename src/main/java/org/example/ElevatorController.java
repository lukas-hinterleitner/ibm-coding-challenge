package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Log4j2
public class ElevatorController {
    private final List<Elevator> elevators = new ArrayList<>();

    public ElevatorController(final int amountElevators) {
        // initialization for DC tower --> seven elevators
        for (int i = 0; i < amountElevators; i++) {
            elevators.add(new Elevator());
        }
    }

    public void makeAvailable(final Elevator elevator) {
        synchronized (this.elevators) {
            this.elevators.add(elevator);
            this.elevators.notify();
        }
    }

    public Elevator getElevator() throws InterruptedException {
        synchronized (this.elevators) {
            while (this.elevators.isEmpty()) {
                log.info("wait until elevator is available");
                this.elevators.wait();
            }

            log.info("elevator available -- continue");

            //TODO: find best elevator
            //TODO: maybe implement intermediate stops

            return this.elevators.remove(0);
        }
    }

    public void addRequest(final int currentFloor, final int newFloor, final Direction direction) {
        log.info(MessageFormat.format("new request from floor {0} to floor {1}", currentFloor, newFloor));

        final Thread thread = new Thread(() -> {
            if (newFloor < 0 || newFloor > 55) {
                log.warn(MessageFormat.format("floor not available: {0}", newFloor));
                return;
            }

            try {
                final Elevator elevator = this.getElevator();

                elevator.changeFloor(newFloor, direction);

                this.makeAvailable(elevator);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
    }
}

