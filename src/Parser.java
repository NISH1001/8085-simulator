import java.io.*;
import java.util.*;

public class Parser
{
	private ArrayList<String> m_lines;

	public Parser(String filename)
	{
		m_lines = new ArrayList<String>();

		try
		{
    		BufferedReader reader = new BufferedReader( new FileReader( "test.txt"));
    		String line;

    		while((line = reader.readLine()) != null)
    		{
    			//System.out.println(line);
    			line = line.trim();
    			if(line.length()>0)
    			{
    				line = line.replaceAll("\\s+", " ");
    				if(line.charAt(0) == ';')
    					continue;
    				m_lines.add(line);
    			}
    		}

    		reader.close();

		}
		catch ( IOException e)
		{
		}
	}

	public void DisplayLines()
	{
		for(int i=0; i<m_lines.size(); ++i)
		{
			System.out.println(m_lines.get(i) + " " + m_lines.get(i).length());
		}
	}
}