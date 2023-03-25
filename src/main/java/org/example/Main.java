package org.example;

public class Main {
    public static void main(String[] args) {
        final ElevatorController controller = new ElevatorController(7);

        controller.addRequest(0, 34, Direction.UP);
    }
}