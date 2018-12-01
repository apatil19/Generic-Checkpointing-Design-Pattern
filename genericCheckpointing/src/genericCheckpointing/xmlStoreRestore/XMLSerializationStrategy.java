package genericCheckpointing.xmlStoreRestore;

//Uppercase first char: https://www.geeksforgeeks.org/java-program-convert-first-character-uppercase-sentence/

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import genericCheckpointing.util.Results;
import genericCheckpointing.util.SerializableObject;
import genericCheckpointing.util.SerializeTypes;

public class XMLSerializationStrategy implements SerStrategy{

	SerializeTypes serTypes;
	
	public XMLSerializationStrategy() {
		this.serTypes= new SerializeTypes();
	}

	
	
	public String convert(String str) 
    { 
  
        // Create a char array of given String 
        char ch[] = str.toCharArray(); 
        for (int i = 0; i < str.length(); i++) 
        { 
            // If first character of a word is found 
            if (i == 0 && ch[i] != ' ' ||  
                ch[i] != ' ' && ch[i - 1] == ' ') 
            { 
                // If it is in lower-case 
                if (ch[i] >= 'a' && ch[i] <= 'z') 
                { 
                    // Convert into Upper-case 
                    ch[i] = (char)(ch[i] - 'a' + 'A'); 
                } 
            }                        
        } 
  
        // Convert the char array to equivalent String 
        String st = new String(ch); 
        return st; 
    } 
	
	
	
	@Override
	public void processInput(SerializableObject sObject, Results res) 
	{
		
		try
		{
			Class<?> classType = sObject.getClass();
			String clsName =classType.getName();
			
			Field[] fields = sObject.getClass().getDeclaredFields();
			res.addToFinalResult("<DPSerialization>");
			res.addToFinalResult("	<complexType xsi:type=\""+clsName+"\">");
			
			for(int f=0; f<fields.length; f++)
			{
				String fieldNameOg = fields[f].getName();
				String fieldName = convert(fieldNameOg);
				//System.out.println(fieldName);
				String method="";
				if(fieldName.equalsIgnoreCase("mybool"))
				{
					method ="is"+fieldName;
				}
				else
				{
					method ="get"+fieldName;					
				}
				
				//System.out.println(method);
				Method meth = classType.getMethod(method);
				Object obj = meth.invoke(sObject);
				String value= obj.toString();
				
				//System.out.println(value);
				
				String xmlLine= serTypes.serializeDataType(fieldNameOg,value, clsName);
				res.addToFinalResult(xmlLine);
			}
			
			res.addToFinalResult("	</complexType>");
			res.addToFinalResult("</DPSerialization>");
			
			
			res.writeToFile();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		finally
		{
			
		}
		
	}

}