public class Main
{
    public static void main(String[] args)
    {
        /*memory object
          if parsing is successful the memory gets passed to
          (to-be-build ) simulator class
          */
        Memory mem = new Memory();
        Parser p = new Parser();

        //if parser is successful get memory
        if(p.Initialize("test.txt"))
        {
            mem = p.GetMemory();
            mem.DisplayRAMHex();
        }
        BooleanMutable bm = new BooleanMutable();
        ControlLines cl = new ControlLines();
        Test tst = new Test();
        cl.reflect("CL_TEST",bm);
        cl.reflect("CL_TEST",tst.bm);
        bm.print();
        tst.bm.print();
        cl.set("CL_TEST",true);
        bm.print();
        tst.bm.print();
    }
}
