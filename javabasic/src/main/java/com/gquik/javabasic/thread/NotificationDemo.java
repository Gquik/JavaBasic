package com.gquik.javabasic.thread;

public class NotificationDemo {
    //volatile修饰线程共享
    private volatile boolean go = false;

    public static void main(String args[]) throws InterruptedException {
        final NotificationDemo test = new NotificationDemo();

        Runnable waitTask = () -> {
            try {
                test.shouldGo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finished Execution");
        };
        Runnable notifyTask = () -> {
            test.go();
            System.out.println(Thread.currentThread().getName() + " finished Execution");
        };
        Thread t1 = new Thread(waitTask, "WT1");
        Thread t2 = new Thread(waitTask, "WT2");
        Thread t3 = new Thread(waitTask, "WT3");
        Thread t4 = new Thread(notifyTask, "NT1");

        //starting all waiting thread
        t1.start();
        t2.start();
        t3.start();

        //pause to ensure all waiting thread started successfully
        Thread.sleep(200);

        //starting notifying thread
        t4.start();

    }

    private synchronized void shouldGo() throws InterruptedException {
        while (go != true) {
            System.out.println(Thread.currentThread()
                    + " is going to wait on this object");
            wait(); //release lock and reacquires on wakeup
            System.out.println(Thread.currentThread() + " is woken up");
        }
        //resetting condition
        go = false;
    }

    private synchronized void go() {
        while (go == false) {
            System.out.println(Thread.currentThread()
                    + " is going to notify all or one thread waiting on this object");
            //making condition true for waiting thread
            go = true;
            //notify(); // only one out of three waiting thread WT1, WT2,WT3 will woke up
            notifyAll(); // all waiting thread  WT1, WT2,WT3 will woke up
        }

    }
}
