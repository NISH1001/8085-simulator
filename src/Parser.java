import java.io.*;
import java.util.*;

public class Parser
{
	private Memory memory;

	public Parser()
	{
		memory = new Memory();
	}

	public boolean Initialize(String filename)
	{
		//System.out.println(memory.onebyte.get(0));
		try
		{
    		BufferedReader reader = new BufferedReader( new FileReader(filename));

    		String line;

    		int index = 0;
    		while((line = reader.readLine()) != null)
    		{
    			
    			line = line.trim();
    			if(line.length()>0)
    			{
    				line = line.replaceAll("\\s+", " ");
    				if(line.charAt(0) == ';')
    					continue;

    				int commentindex = line.indexOf(';');

    				if(commentindex > 0)
    				{
    					line = line.substring(0, commentindex);
    					line = line.trim();
    				}

    				String[] splitted = line.split("\\s");
    				int len = splitted.length;
    				short[] codes = new short[len];

    				try
    				{
    					if(len > 3)
    					{
    						throw new ParseException("Invalid insturcion line -> " + line);
    					}
    				}

    				catch(ParseException err)
    				{
    					System.out.println(err.getMessage());
    					reader.close();
    					return false;
    				}

    				for(int i=0; i<len; ++i)
    				{
    					splitted[i] = splitted[i].toUpperCase();
    					try
    					{
    						if(!splitted[i].matches("[0-9A-F][0-9A-F]"))
    						{
    							throw new ParseException("Invalid code -> " + splitted[i]);
    						}
    					}	

    					catch(ParseException err)
    					{
    						System.out.println(err.getMessage());
    						reader.close();
    						return false;
    					}

    					codes[i] = Short.parseShort(splitted[i],16);
    				}

    				if(len == 1)
    				{
    					//System.out.println(codes[0] + "\n");
    					try
    					{
    						if((memory.onebyte.get((int)codes[0])) == null)
    							throw new ParseException("Invalid onebyte insturcion -> " + splitted[0]);
    					}

    					catch(ParseException err)
    					{
    						System.out.println(err.getMessage());
    						reader.close();
    						return false;
    					}

    				}

    				if(len == 2)
    				{
    					try
    					{
    						if(memory.twobyte.get((int)codes[0]) == null)
    							throw new ParseException("Invalid twobyte insturcion -> " + splitted[0]);
    					}

    					catch(ParseException err)
    					{
    						System.out.println(err.getMessage());
    						reader.close();
    						return false;
    					}
    				}


    				if(len == 3)
    				{
    					try
    					{
    						if(memory.threebyte.get((int)codes[0]) == null)
    							throw new ParseException("Invalid threebyte insturcion -> " + splitted[0]);
    					}

    					catch(ParseException err)
    					{
    						System.out.println(err.getMessage());
    						reader.close();
    						return false;
    					}
    				}

    				for(int i=0; i<len; ++i)
    				{
    					memory.RAM[index++] = codes[i];
    				}
    				
    			}
    		}

    		reader.close();

		}
		catch ( IOException e)
		{
			return false;
		}

		return true;
	}

	public Memory GetMemory()
	{
		return this.memory;
	}
}