/* The class Register implements a physical register. It will
 * be capable of doing all kinds of stuff an actual register
 * does.
 */
public class Register implements
    BitsWritable, BitsReadable {

    private boolean[] bitfield;
    public BooleanMutable loadable;

    public Register(int size) {
        bitfield = new boolean[size];
        loadable = new BooleanMutable();
    }

    public boolean[] read() {
        return bitfield;
    }

    public void write(boolean[] bits) {
        if (loadable.get())
            for(int i=0;i<bits.length && i<bitfield.length;i++)
                bitfield[i] = bits[i];
    }

    public boolean canWrite() {
        return (boolean)loadable.get();
    }
}
