package pkg8085;

import java.io.*;
import java.util.*;
import java.lang.String;

public class Parser
{
    //where the parsed hexcodes gets stored as RAM
    private Opcode opcode;

    //to store the parsed hexcodes
    private ArrayList<Integer> data;

    //store m_label and their correspoinding address
    private HashMap<String,Integer> m_label;

    //store lines to be used in second pass
    private HashMap<Integer,String> m_parsedLines;

    private ArrayList<String> m_originalLines;

    //m_offset for m_labels
    private int m_offset;

    public Parser()
    {
        m_offset = (int)0;
        m_parsedLines = new HashMap<Integer,String>();
        m_label = new HashMap<String, Integer>();
        data = new ArrayList<Integer>();
        m_originalLines = new ArrayList<String>();
        opcode = new Opcode();
    }

    public boolean InitializeFile(String filename,
            int start_addr)
        throws NumberFormatException,IOException,ParseException
    {
        boolean first = FirstPass(filename, start_addr,true);
        if(!first)
            return false;

        boolean second = SecondPass();
        if(!second)
            return false;
        return true;
    }

    public boolean InitializeString(String data,int start_addr)
        throws NumberFormatException,IOException,ParseException
    {
        if (!FirstPass(data,start_addr,false))
            return false;
        if (!SecondPass())
            return false;
        return true;
    }

    public boolean FirstPass(String filename,
            int start_address, boolean isFilename)
        throws NumberFormatException,IOException,ParseException
    {
        int start_addr = (int)start_address;
        int linenumber = 0;
        String line = new String();
        //try {
            BufferedReader reader;
            if (isFilename)
                reader = new BufferedReader(
                        new FileReader(filename));
            else
                reader = new BufferedReader(
                        new StringReader(filename));

            while((line = reader.readLine()) != null)
            {
                //trim leading and trailing spaces
                String copy = line;
                line = line.trim();
                ++linenumber;

                if(line.length()>0)
                {
                    //reduce multiple spaces to single
                    line = line.replaceAll("\\s+", " ");

                    //if whole line is a comment
                    if(line.charAt(0) == ';')
                    {
                        m_parsedLines.put(linenumber, "");
                        m_originalLines.add( copy );
                        continue;
                    }

                    //else if line has got some comments at its end
                    int commentindex = line.indexOf(';');

                    if(commentindex > 0)
                    {
                        line = line.substring(0, commentindex);
                        line = line.trim();
                    }

                    line = line.toUpperCase();
                    String [] labelsplit = line.split(":");
                    if(labelsplit.length == 2)
                        line = ConvertToHex(labelsplit[1].trim());
                    else
                        line  = ConvertToHex(line);

                    //convert to hex opcodes
                    String label = "";

                    //check if there is a label
                    if(labelsplit.length == 2)
                    {
                        String[] labelsplitted = line.split(":");
                        String l1 = labelsplit[0].trim().toUpperCase();
                        String l2 = line;

                        if(l1.indexOf(" ")>0)
                        {
                            throw new ParseException("Label must not contain spaces -> " + copy + " :: linenumber -> " + linenumber);
                        }


                        if(!l1.matches("[A-Z][0-9A-Z]*"))
                        {
                            throw new ParseException("Invalid label in line -> " + copy + " :: linenumber -> " + linenumber);
                        }
                        label = l1;
                        line = l2;
                    }

                    // get the opcode -> first byte of our line is always an opcode
                    String op = (line.split(" "))[0];
                    int code = Integer.parseInt(op,16);
                    int off = m_offset;

                    //to calculate offset increment
                    if(opcode.onebyte.get(code) != null)
                    {
                        m_offset++;
                    }
                    if(opcode.twobyte.get(code) != null)
                    {
                        m_offset += 2;
                    }
                    if(opcode.threebyte.get(code) != null)
                    {
                        m_offset += 3;
                    }

                    if(m_label.get(label) != null && label.length()>0)
                    {
                        throw new ParseException("possible label duplication :: " + copy + " :: line ->" + linenumber);
                    }

                    m_label.put(label, start_addr + off);
                }

                m_parsedLines.put(linenumber, line);
                m_originalLines.add(copy);
            }

            reader.close();
        //}
        /*catch ( IOException e)
        {
            System.out.println("invalid file");
            return false;
        }
        catch (NumberFormatException e)
        {
            System.out.println("invalid line -> " + line + " :: " + "linenumber -> " + linenumber);
            return false;
        }
        catch (ParseException err)
        {
            System.out.println(err.getMessage());
            return false;
        }*/
        return true;
    }

    public boolean SecondPass() throws ParseException
    {
        //try {
            int linenumber = 0;
            while(linenumber < m_parsedLines.size())
            {
                linenumber++;

                String original = m_originalLines.get(linenumber-1);

                String line = m_parsedLines.get(linenumber);

                if(line.length() == 0)
                    continue;

                line = SubstituteLabel(line);

                //now split the line by spaces
                String[] splitted = line.split("\\s");
                int len = splitted.length;

                //to store codes as integers -> int = 16bit
                int[] codes = new int[len];

                //im assuming we dont have a single instruction whose
                //length is greater than 3 bytes
                if(len > 3)
                {
                    throw new ParseException("Invalid insturcion line -> " + original + " :: linenumber -> " + linenumber);
                }


                //now check for genuine characters after splitting
                for(int i=0; i<len; ++i)
                {
                    splitted[i] = splitted[i].toUpperCase();
                    if(!splitted[i].matches("([0-9A-F][0-9A-F])|([0-9A-F])"))
                    {
                        String error = "Invalid code -> " + splitted[i] + " :: in line -> " + original + " :: linenumber -> " + linenumber ;
                        throw new ParseException(error);
                    }

                    //convert to integer value if success
                    codes[i] = Integer.parseInt(splitted[i],16);
                }

                //if single byte instruction check if genuine opcode
                if(len == 1)
                {
                    if((opcode.onebyte.get((int)codes[0])) == null)
                        throw new ParseException("Invalid insturcion line -> " + original+ " :: linenumber -> " + linenumber);

                }

                //if two byte instruction check if genuine opcode
                if(len == 2)
                {
                    if(opcode.twobyte.get((int)codes[0]) == null)
                        throw new ParseException("Invalid insturcion line -> " + original + " :: linenumber -> " + linenumber);

                }

                //if three byte instructon check if genuine opcode
                if(len == 3)
                {
                    if(opcode.threebyte.get((int)codes[0]) == null)
                        throw new ParseException("Invalid insturcion line -> " + original+ " :: linenumber -> " + linenumber);

                }

                //if everthing is success store into RAM
                data.add(codes[0]);
                for(int i=codes.length-1; i>0; --i)
                {
                    data.add(codes[i]);
                }
            }
        //}

        /*catch (ParseException err)
        {
            System.out.println(err.getMessage());
            return false;
        }*/
        return true;
    }

    //to substitute label by address
    public String SubstituteLabel(String line)
    {
        Iterator <Map.Entry<String, Integer>> iter = m_label.entrySet().iterator();

        String converted = line;

        while(iter.hasNext())
        {
            Map.Entry<String, Integer> labeliter = iter.next();
            String label = labeliter.getKey();
            if(label.length() == 0)
                continue;

            Integer label_addr = labeliter.getValue();

            int index = line.indexOf(label);

            //if line contains a label
            if(index>0 && line.charAt(index-1)==' ')
            {
                //convert integer address to hex string
                String hexcode = Integer.toHexString(label_addr);
                int len = hexcode.length();

                //to fill with  zeroes
                hexcode = FillString(4, hexcode, '0');
                hexcode = hexcode.substring(0,2) + " " + hexcode.substring(2,4);
                converted = line.replaceAll(label, hexcode);
            }
        }
        return converted;
    }

    // convert the line to our primitive hex code
    public String ConvertToHex(String line)
    {
        //first iterate over onebyte instruction
        Iterator <Map.Entry<Integer, String>> iter = opcode.onebyte.entrySet().iterator();

        //for converted line into hexcode
        line = line.replaceFirst("\\s*,\\s*", ",");
        String converted = line;
        boolean matched = false;

        //loop through onebyte insturction
        while(iter.hasNext())
        {
            Map.Entry<Integer, String> opcode = iter.next();
            String hexcode = Integer.toHexString(opcode.getKey());
            String engcode = opcode.getValue().toUpperCase();

            //search for substring like MOV A,B
            int index = line.indexOf(engcode);
            if(index > 0)
                matched = (line.charAt(index-1) == ' ') ? true : false;
            else
                matched = (index == 0) ? true : false;

            //if line contains instruction then replace with hex value
            if(matched)
            {
                converted = line.replaceAll(engcode, hexcode);
                break;
            }
        }

        //if one byte instruciton failed -> do same for two byte instruction
        if(!matched)
        {
            iter = opcode.twobyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue().toUpperCase();

                matched = line.contains(engcode);

                if(matched)
                {
                    //replae first occurence of *,* with just a space
                    line = line.replaceFirst("\\s*,\\s*", " ");
                    converted = line.replaceAll(engcode, hexcode);

                    //if it is a decimal data ie suffexed wit 'd'
                    String[] splitted = converted.split(" ");
                    if(splitted[1].matches("[0-9]+'D"))
                    {
                        int value = Integer.parseInt(splitted[1].replaceAll("'D",""));
                        String valhex = Integer.toHexString(value);
                        converted = converted.replaceAll(splitted[1],valhex);
                    }
                    break;
                }
            }
        }

        //if two  byte instruciton failed -> do same for three  byte instruction
        if(!matched)
        {
            iter = opcode.threebyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue().toUpperCase();
                matched = line.contains(engcode);

                if(matched)
                {
                    //replae first occurence of *,* with just a space
                    line = line.replaceFirst("\\s*,\\s*", " ");
                    converted = line.replaceAll(engcode, hexcode);

                    String[] splitted = converted.split(" ");
                    if(splitted.length == 2)
                    {
                        if(splitted[1].matches("[0-9]{1,4}"))
                        {
                            String toreplace = FillString(4,splitted[1],'0');
                            toreplace = toreplace.substring(0,2) + " " + toreplace.substring(2,4);
                            converted = converted.replaceAll(splitted[1], toreplace);
                        }

                        if(splitted[1].matches("[0-9]+'D"))
                        {
                            int value = Integer.parseInt(splitted[1].replaceAll("'D",""));
                            String valhex = Integer.toHexString(value);
                            String toreplace = FillString(4, valhex,'0');
                            toreplace = toreplace.substring(0,2) + " " + toreplace.substring(2,4);
                            converted = converted.replaceAll(splitted[1], toreplace);
                        }
                    }
                    break;
                }
            }
        }
        return converted;
    }

    //helper function to fill the string with specified character at the beginning
    private String FillString(int len, String str, char c)
    {
        if(len<str.length()) len=str.length();
        char[] z = new char[len - str.length()];
        Arrays.fill(z,c);
        String temp = new String(z);
        return temp+str;
    }


    public void ShowOriginalLines()
    {
        for(String o : m_originalLines)
            System.out.println(o);
    }

    //to show parsed data
    public void ShowData()
    {
        for (Integer d : data)
            System.out.println(Integer.toHexString(d));
    }

    //to write to memory
    public void WriteToMemory(MemoryModule mem, int start_addr)
    {
        int i = 0;
        for(Integer d : data)
        {
            mem.writeByte(start_addr+i, d);
            i++;
        }
    }

    // Return the size
    public int size() {
        return data.size();
    }
}
