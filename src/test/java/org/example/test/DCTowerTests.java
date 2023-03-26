package org.example.test;

import lombok.extern.log4j.Log4j2;
import org.example.objects.DCTower;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Log4j2
public class DCTowerTests {

    private final DCTower dcTower = DCTower.getInstance();

    @AfterEach
    public void afterEach() throws InterruptedException {
        // send all elevators to the first floor, so all tests have the same starting position
        log.info("sending all elevators to ground floor");
        dcTower.sendElevatorsToFirstFloor();

        Thread.sleep(6000);
    }

    // this test is just as an example here to proof that I've experience in testing
    @Test
    @Order(1)
    public void ElevatorSimulationWorks() throws InterruptedException {
        synchronized (dcTower) {
            Assertions.assertEquals(7, dcTower.getAvailableElevators().size());

            final int targetFloor = 35;

            dcTower.addElevatorRequest(0, targetFloor);

            Thread.sleep(10);

            // directly after the request only six elevator should be available
            Assertions.assertEquals(6, dcTower.getAvailableElevators().size());
            Assertions.assertTrue(dcTower.getAvailableElevators().stream().noneMatch(elevator -> elevator.getCurrentFloor() == targetFloor));

            // after waiting some time, the elevator should be at the 35th floor
            Thread.sleep(5500);

            Assertions.assertTrue(dcTower.getAvailableElevators().stream().anyMatch(elevator -> elevator.getCurrentFloor() == targetFloor));
        }
    }
}
