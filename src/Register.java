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
        //if (loadable.get())
            for(int i=0;i<bits.length && i<bitfield.length;i++)
                bitfield[i] = bits[i];
    }

    public boolean canWrite() {
        return true;
        //return (boolean)loadable.get();
    }

    public int asInt() {
        int a = 1;
        int res = 0;
        for (boolean bit : bitfield) {
            if (bit)
                res += a;
            a *= 2;
        }
        return res;
    }

    public void fromInt(int value) {
        for (int i=0; i<bitfield.length; i++) {
            bitfield[i] = (value%2==0)?false:true;
            value /= 2;
        }
    }
}
