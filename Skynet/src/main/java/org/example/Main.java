package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Factory factory = new Factory();
        Faction world = new Faction("World", factory);
        Faction wednesday = new Faction("Wednesday", factory);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> {
            for (int day = 1; day <= 100; day++) {
                System.out.println("Day " + day);
                factory.dailyProducingDetails();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }});
        executor.submit(world);
        executor.submit(wednesday);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        System.out.println("Results:");
        System.out.println("World built " + world.getRobotsProduced() + " robots");
        System.out.println("Wednesday built " + wednesday.getRobotsProduced() + " robots");
        if (world.getRobotsProduced() > wednesday.getRobotsProduced()) {
            System.out.println("World won");
        }
        else if (world.getRobotsProduced() < wednesday.getRobotsProduced())
        {
            System.out.println("Wednesday won");
        }
        else System.out.println("Draw");
    }
}