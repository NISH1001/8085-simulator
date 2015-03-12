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

    private boolean eval(String expression) {
        if (has(expression))
            return get(expression);
        if (expression.contains("|")) {
            String[] items = expression.split("\\|");
            for (String item : items)
                if (eval(item))
                    return true;
        } else if (expression.contains("&")) {
            String[] items = expression.split("&");
            for (String item : items)
                if (!eval(item))
                    return false;
        }
        return false;
    }

    private String inverseKey(String key) {
        if (key.charAt(0) == '!')
            return key.substring(1);
        else
            return "!"+key;
    }

    // Set a value for a specific line
    public void set(String key, boolean value) {
        String ikey = inverseKey(key);
        if (lines.containsKey(ikey))
            lines.put(ikey,!value);
        else
            lines.put(key,value);

        for (String expr : reflectors.keySet())
            for (BooleanMutable bm : reflectors.get(expr))
                bm.set(eval(expr));
    }

    // Get a value for a specific line. False if doesn't exist
    public boolean get(String key) {
        String invertedkey = inverseKey(key);
        if (lines.containsKey(key))
            return lines.get(key);
        else if (lines.containsKey(invertedkey))
            return lines.get(invertedkey);
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

    public boolean has(String key) {
        String ikey = inverseKey(key);
        if (lines.containsKey(key) || lines.containsKey(ikey))
            return true;
        else
            return false;
    }
}
