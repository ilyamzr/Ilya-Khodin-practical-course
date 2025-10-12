package org.example;

import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Factory {

    public Map<DetailType, Integer> detailStorage = new ConcurrentHashMap<>();
    public final ReentrantLock lock = new ReentrantLock();
    private final Condition detailsProduced = lock.newCondition();

    public Factory()
    {
        detailStorage.put(DetailType.HEAD,0);
        detailStorage.put(DetailType.TORSO,0);
        detailStorage.put(DetailType.HAND,0);
        detailStorage.put(DetailType.FEET,0);
    }

    public void putDetail(DetailType detail)
    {
        detailStorage.put(detail, detailStorage.get(detail) + 1);
    }

    public void makeDetail()
    {
        DetailType[] values = DetailType.values();
        Random random = new Random();
        int randomIndex = random.nextInt(values.length);
        DetailType detail = values[randomIndex];
        putDetail(detail);
        System.out.println("Factory produced: " + detail);
    }

    public void dailyProducingDetails()
    {
        lock.lock();
        try {
            for (int i = 0; i < 10; i++)
            {
                makeDetail();
            }
            detailsProduced.signalAll();
        }
        finally
        {
            lock.unlock();
        }
    }

    public Condition getDetailsProducedCondition() {
        return detailsProduced;
    }

    public void reduceDetailQuantity(DetailType detail)
    {
        detailStorage.put(detail, detailStorage.get(detail) - 1);
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
