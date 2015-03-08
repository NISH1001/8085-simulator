import java.util.HashMap;

public class ControlLines {
    HashMap<String,Boolean> lines;

    public ControlLines() {
        lines = new HashMap<String,Boolean>();
    }

    public void set(String key, boolean value) {
        lines.put(key,value);
    }

    public boolean get(String key) {
        if (lines.containsKey(key))
            return lines.get(key);
        return false;
    }
}
