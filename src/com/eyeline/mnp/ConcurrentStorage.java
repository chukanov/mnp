package com.eyeline.mnp;

import com.eyeline.mnp.mask.Mask;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Chukanov
 */
public class ConcurrentStorage implements Storage {
    private Storage realStorage;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public ConcurrentStorage(Storage realStorage) {
        this.realStorage = realStorage;
    }

    public void setRealStorage(Storage realStorage) {
        try{
            writeLock.lock();
            this.realStorage = realStorage;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Mno put(Mask mask, Mno mno) {
        try{
            writeLock.lock();
            return realStorage.put(mask, mno);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Mno remove(Mask mask) {
        try{
            writeLock.lock();
            return realStorage.remove(mask);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        try{
            writeLock.lock();
            realStorage.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Mno lookup(String subscriber) {
        try{
            readLock.lock();
            return realStorage.lookup(subscriber);
        } finally {
            readLock.unlock();
        }
    }
}
