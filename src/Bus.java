import java.util.ArrayList;
import java.util.Arrays;

/* The Bus class represents a physical bus in the system,
 * which is a path for bits to travel. Multiple registers (or
 * other devices) can write to the bus, and multiple can
 * listen. Devices that write need to register with the bus
 * and get a writer id (bus selector) and use that for writing.
 * To read, devices just call the getPins() method.
 * The pins of the bus can be in any of three states :
 * high, low, and floating (tri-stated). In the floating state
 * they don't affect any register bits.
 */
public class Bus
{
    // The three logic states that the pins can be in
    public enum LogicStates { high, low, floating };

    // All the registers that can write to the bus
    ArrayList<BitsReadable> writers;

    // All the registers that needed to be written by the bus
    ArrayList<BitsWritable> readers;

    // The array representing the pins of the bus
    LogicStates[] pins;

    public Bus(int width) {
        pins = new LogicStates[width];
        Arrays.fill(pins,LogicStates.floating);
    }

    // Register a writer. Returns the bus selector id.
    public int registerWriter(BitsReadable breadable) {
        writers.add(breadable);
        return writers.size();
    }

    // Register a reader. Returns an id that is useless.
    public int registerReader(BitsWritable bwritable) {
        readers.add(bwritable);
        return readers.size();
    }

    // Select the writer, i.e. load data to the bus. Argument
    // is the selector id returned by registerWriter().
    public void select(int writerid) {
        // Zero means none, tri-state the bus.
        if (writerid==0) {
            for (int i=0; i<pins.length; i++) {
                pins[i] = LogicStates.floating;
            }
        } else if (writerid<=writers.size()) {
            boolean[] bits = writers.get(writerid-1).read();
            for (int i=0;i<pins.length && i<bits.length;i++) {
                pins[i] = (bits[i]==true)?
                    LogicStates.high:LogicStates.low;
            }
        }
        // Write out to the readers
        for (BitsWritable wr : readers) {
            if (wr.canWrite()) {
                boolean[] bits = new boolean[pins.length];
                for (int i=0; i<pins.length; i++)
                    if (pins[i]!=LogicStates.floating)
                        bits[i] = (pins[i]==LogicStates.high)?
                            true:false;
                wr.write(bits);
            }

        }
    }

    // Get the pins
    public LogicStates[] getPins() {
        return pins;
    }
}
