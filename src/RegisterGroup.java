import java.util.ArrayList;

public class RegisterGroup implements Synchronous {
    ArrayList<Register> reglist;

    public RegisterGroup() {
        reglist = new ArrayList<Register>();
    }

    public void add(Register reg) {
        reglist.add(reg);
    }

    public void sync() {
    }
}
