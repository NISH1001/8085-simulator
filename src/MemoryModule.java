public class MemoryModule implements Device {

    private int start_addr,addr_size;
    private int data[];

    public MemoryModule(int start, int size) {
        start_addr = (int)start;
        addr_size = (int)size;
        data = new int[size];
    }

    public boolean hasAddress(int address) {
        if(address>=start_addr && address<start_addr+addr_size)
            return true;
        else
            return false;
    }

    public boolean IO_Mbar() {
        return false;
    }

    public void writeByte(int address, int databyte) {
        data[address-start_addr] = (int)databyte;
    }

    public int readByte(int address) {
        return data[address-start_addr];
    }

    public void hexDump(int start, int size) {
        for (int i=0; i<size; i++) {
            if (hasAddress(start+i)) {
                Integer d = readByte(start+i);
                System.out.println(Integer.toHexString(d));
            }
        }
    }
}
