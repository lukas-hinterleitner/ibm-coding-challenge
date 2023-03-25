package org.example;

import org.example.objects.DCTower;

public class Main {
    public static void main(String[] args) {
        final DCTower dcTower = DCTower.getInstance();

        dcTower.addElevatorRequest(0, 35);
    }
}