import java.util.ArrayList;

/* The Timer class represents the system clock generator.
 * It provides synchronization for the entire system,
 * by periodically sending out ticks to device that listen.
 * The tick-sending is actually just a call to a sync() method
 * on a class that implements the Synchronous interface.
 */
public class Timer implements Runnable
{
    // Frequency of the system clock in megahertz
    double frequency;
    // Time between successive ticks (1/frequency)
    // in nanoseconds
    long period;

    // Listeners of the SYNC event, which signals that a rising
    // edge is occuring in the clock pulse
    ArrayList<Synchronous> listeners;

    // Thread object of self
    private Thread thr;

    public Timer(double mhz) {
        if (mhz<=0)
            mhz = 1.0;
        frequency = mhz;
        period = (long)(1000.0/mhz);
        listeners = new ArrayList();
    }

    // Register to listen the SYNC event
    public void register(Synchronous lstnr) {
        listeners.add(lstnr);
    }

    // worker for the timer thread
    public void run() {
        long startTime = System.nanoTime();
        long accTime;
        while (true) {
            accTime = System.nanoTime();
            if (accTime-startTime>=period) {
                for (Synchronous snc : listeners) {
                    snc.sync();
                }
                startTime = accTime;
            }
            Thread.yield();
        }
    }

    // Start thread
    public void start() {
        if (thr==null) {
            thr = new Thread(this,"Timer_thread");
            thr.start();
        }
    }
}
