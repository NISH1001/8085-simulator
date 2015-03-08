public abstract class Synchronous implements Runnable {
    Integer ticks;
    Thread thread;

    abstract public void run();

    public Synchronous() {
        ticks = new Integer(0);
    }

    public void sync() {
        synchronized (ticks) {
            ticks++;
        }
    }

    public void start() {
        if (thread==null) {
            thread =  new Thread(this,
                    this.getClass().getName()+"_thread");
            thread.start();
        }
    }
}
