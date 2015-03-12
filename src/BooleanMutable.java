/* The BooleanMutable class is a wrapper for the Boolean type.
 * Created so that its references may be passed around and
 * so that it could be mutated using its reference.
 */
public class BooleanMutable {

    private Boolean value;

    public BooleanMutable() {
        value = false;
    }

    public BooleanMutable(Boolean bl) {
        value = bl;
    }

    public void set(Boolean val) {
        value = val;
    }

    public Boolean get() {
        return value;
    }

    public void print() {
        System.out.println(value);
    }
}
