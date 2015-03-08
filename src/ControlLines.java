import java.util.HashMap;
import java.util.ArrayList;

/* The class ControlLines represents a group of control lines.
 * Unlike a bus, individual lines here are named. They should be
 * set and read by name instead of bit position. Also, you can
 * set objects of the 'BooleanMutable' type to reflect a certain
 * line, so that every time the logic level on the line changes
 * it gets reflected on that BooleanMutable.
 */
public class ControlLines {
    // The control lines
    HashMap<String,Boolean> lines;
    // The booleanmutables that reflect our lines
    HashMap<String,ArrayList<BooleanMutable>> reflectors;

    // Construct.
    public ControlLines() {
        lines = new HashMap<String,Boolean>();
        reflectors =
            new HashMap<String,ArrayList<BooleanMutable>>();
    }

    // Set a value for a specific line
    public void set(String key, boolean value) {
        lines.put(key,value);
        if (reflectors.containsKey(key))
            for (BooleanMutable bm : reflectors.get(key))
                bm.set(value);
    }

    // Get a value for a specific line. False if doesn't exist
    public boolean get(String key) {
        if (lines.containsKey(key))
            return lines.get(key);
        return false;
    }

    // Set a booleanmutable to reflect a line
    public void reflect(String linename, BooleanMutable ff) {
        if (reflectors.containsKey(linename))
            reflectors.get(linename).add(ff);
        else {
            ArrayList<BooleanMutable> nlist =
                new ArrayList<BooleanMutable>();
            nlist.add(ff);
            reflectors.put(linename,nlist);
        }
    }
}
