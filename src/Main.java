public class Main
{
    public static void main(String[] args)
    {
        /*memory object
          if parsing is successful the memory gets passed to
          (to-be-build ) simulator class
          */
        Memory mem = new Memory(0,0x10000);
        Parser p = new Parser();

        //if parser is successful get memory
        if(p.Initialize("test.txt"))
        {
            mem = p.GetMemory();
            mem.DisplaydataHex();
        }
    }
}
