import java.util.ArrayList;

public class Timer
{
    // Frequency of the system clock in megahertz
    double frequency;
    // Time between successive ticks (1/frequency)
    // in nanoseconds
    long period;

    // Listeners of the SYNC event, which signals that a rising
    // edge is occuring in the clock pulse
    ArrayList<Synchronous> listeners;

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

    // Start and run the timer thread
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
}
