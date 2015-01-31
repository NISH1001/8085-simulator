import java.io.*;
import java.util.*;

/*
	Our Memory class :  has both ROM and RAM
	ROM -> contains hashtable for onebyteinstruction, twobyte, threebytes
	RAM -> holds the bytevalues as integer , of instructions provided by the user 
*/

public class Memory
{
	
	public Memory()
	{

	}

	public void Display()
	{
		Iterator <Map.Entry<Integer, String>> iter = onebyte.entrySet().iterator();

		/*
		for(Map.Entry<Integer, String> entry : onebyte.entrySet())
		{
			System.out.println(entry.getKey() + " : " + entry.getValue() + "\n");
		}
		*/

		while(iter.hasNext())
		{
			Map.Entry<Integer, String> opcode = iter.next();
			String hexcode = Integer.toHexString(opcode.getKey());
			String engcode = opcode.getValue();
			
			System.out.println(hexcode + " " + engcode + "\n");
		}
	}

	/*store onebyte instuction hashmap ie integer value (hex) and corresponding instruction
		Static {} is used for initializing a hashmap in rip JAVA
	*/
	public static HashMap<Integer, String> onebyte;
	static
	{
		onebyte = new HashMap<Integer, String>();

		//nop instruction
		onebyte.put(0x00, "NOP");

		//generate all the MOV Rd Rs instruction
		char[] registers = {'B', 'C', 'D', 'E', 'H', 'L', 'M', 'A'};
		String mov = "MOV ";
		
		for(int index = 0; index<8; ++index)
		{
			for(int i=0; i<8; ++i)
			{
				onebyte.put((i+64) + 8*index, mov + registers[index] + " " +  registers[i]);
			}
		}

		//any onebyte instruction -> add here
	}

	public static HashMap<Integer, String> twobyte;
	static
	{

	}

	public static HashMap<Integer, String> threebyte;
	static
	{

	}

}