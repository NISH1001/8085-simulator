import java.io.*;
import java.util.*;

public class Parser
{
	private ArrayList<String> m_lines;

	public Parser(String filename)
	{
		m_lines = new ArrayList<String>();
		Initialize(filename);
	}

	public Parser()
	{
		m_lines = new ArrayList<String>();
	}

	public boolean Initialize(String filename)
	{
		try
		{
    		BufferedReader reader = new BufferedReader( new FileReader( "test.txt"));
    		String line;

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
    					m_lines.add(line);
    				}

    				else
    				{
    					m_lines.add(line);
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

	public void DisplayLines()
	{
		for(int i=0; i<m_lines.size(); ++i)
		{
			System.out.println(m_lines.get(i));
		}
	}
}