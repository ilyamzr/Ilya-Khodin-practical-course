package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Faction implements Runnable {
    private final String name;
    private final Factory factory;
    private Map<DetailType, Integer> detailStorage = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private int robotsProduced = 0;
    private final Random random;

    public Faction(String name, Factory factory) {
        this.name = name;
        this.factory = factory;
        detailStorage.put(DetailType.HEAD, 0);
        detailStorage.put(DetailType.TORSO, 0);
        detailStorage.put(DetailType.HAND, 0);
        detailStorage.put(DetailType.FEET, 0);
        random = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++)
        {
            getDetail();
            buildRobot();
        }
    }

    public void getDetail() {
        factory.getLock().lock();
        try {
            factory.getDetailsProducedCondition().await();
            List<DetailType> available = new ArrayList<>();

            for (Map.Entry<DetailType, Integer> entry : factory.detailStorage.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    available.add(entry.getKey());
                }
            }
            Collections.shuffle(available, random);

            int partsToTake = Math.min(5, available.size());
            for (int i = 0; i < partsToTake; i++) {
                DetailType detail = available.get(i);
                detailStorage.put(detail, detailStorage.get(detail) + 1);
                factory.reduceDetailQuantity(detail);
                System.out.println(name + " got " + detail);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            factory.getLock().unlock();
        }

    }

    public void buildRobot()
    {
        lock.lock();
        try {
            while (detailStorage.get(DetailType.HEAD) >= 1
                    && detailStorage.get(DetailType.TORSO) >= 1
                    && detailStorage.get(DetailType.HAND) >= 2
                    && detailStorage.get(DetailType.FEET) >= 2)
            {
                detailStorage.put(DetailType.HEAD, detailStorage.get(DetailType.HEAD) - 1);
                detailStorage.put(DetailType.TORSO, detailStorage.get(DetailType.TORSO) - 1);
                detailStorage.put(DetailType.HAND, detailStorage.get(DetailType.HAND) - 2);
                detailStorage.put(DetailType.FEET, detailStorage.get(DetailType.FEET) - 2);

                System.out.println(name + " built a robot ");

                robotsProduced++;
            }
        }
        finally {
            lock.unlock();
        }
    }

    public int getRobotsProduced() {
        return robotsProduced;
    }
}
