import java.util.ArrayList;

public class RegisterGroup extends Synchronous {
    ArrayList<Register> reglist;

    public RegisterGroup() {
        reglist = new ArrayList<Register>();
    }

    public void add(Register reg) {
        reglist.add(reg);
    }

    public void run() {
    }
}
