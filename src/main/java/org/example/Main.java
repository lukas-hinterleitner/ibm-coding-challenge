package org.example;

import org.example.objects.DCTower;

import java.security.SecureRandom;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final DCTower dcTower = DCTower.getInstance();

        final SecureRandom random = new SecureRandom("seed".getBytes());

        final int maxDurationBetweenRequests = 1000; // change this variable to increase request rate

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