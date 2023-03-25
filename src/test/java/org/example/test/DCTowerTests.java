package org.example.test;

import lombok.extern.log4j.Log4j2;
import org.example.objects.DCTower;
import org.example.objects.Elevator;
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

    @Test
    @Order(1)
    public void ElevatorSimulationWorks() throws InterruptedException {
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

    @Test
    @Order(2)
    public void NearestElevatorWillBeChosen() throws InterruptedException {
        dcTower.addElevatorRequest(0, 35);

        // after waiting some time, the elevator should be at the 35th floor
        Thread.sleep(5500);

        System.out.println(dcTower.getAvailableElevators().size());

        final Elevator elevator = dcTower.getAvailableElevators()
                .stream().filter(el -> el.getCurrentFloor() == 35).findFirst().orElseThrow(Assertions.fail("elevator has to be available"));

        System.out.println(dcTower.getAvailableElevators().size());

        Assertions.assertEquals(35, elevator.getCurrentFloor());

        dcTower.addElevatorRequest(33, 0);

        // after waiting some time, the elevator should be at the 35th floor
        Thread.sleep(5000);

        System.out.println(elevator.getCurrentFloor());
    }
}
