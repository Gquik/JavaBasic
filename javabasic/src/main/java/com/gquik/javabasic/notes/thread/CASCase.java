package com.gquik.javabasic.notes.thread;

public class CASCase {
    public volatile int value;

    public synchronized void add() {
        value++;
    }
}
