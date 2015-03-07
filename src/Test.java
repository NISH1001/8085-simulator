public class Test implements Synchronous {
    static long a = 0;
    public void sync() {
        System.out.println("I'm syncing!");
        a += 1;
        System.out.println(a);
    }
}
