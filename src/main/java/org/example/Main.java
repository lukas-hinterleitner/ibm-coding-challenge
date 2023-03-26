package org.example;

import org.example.objects.DCTower;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final DCTower dcTower = DCTower.getInstance();

        // show that intermediate stops are working

        dcTower.addElevatorRequest(0, 40);

        Thread.sleep(6000);

        dcTower.addElevatorRequest(40, 0);
        Thread.sleep(50);
        dcTower.addElevatorRequest(5, 0);

        Thread.sleep(6000);

        // some more code to simulate
        System.out.println("\n======================= start simulation =======================\n");

        final Random random = new Random(10);

        final int maxDurationBetweenRequests = 2000; // change this variable to alter request rate
        // simulate elevator behaviour in DC tower
        for (int i = 0; i < 20; i++) {
            // randomly choose between up and down
            if (random.nextBoolean()) {
                dcTower.addElevatorRequest(0, random.nextInt(1, 56));
            } else {
                dcTower.addElevatorRequest(random.nextInt(1, 56), 0);
            }

            Thread.sleep(random.nextInt(0, maxDurationBetweenRequests));
        }
    }
}