import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatentReader {
	@SuppressWarnings("unchecked")
	public static ArrayList<Patent> ReadPatentJSON(){
		ObjectInputStream objectinputstream = null;
		ArrayList<String> patentList = null;
		ArrayList<Patent> patents = new ArrayList<Patent>();
		
		try {
			String inputFile = "patent1-1000JSON.ser"; 
			FileInputStream streamIn;
			streamIn = new FileInputStream(inputFile);
			objectinputstream = new ObjectInputStream(streamIn);
			patentList  = (ArrayList<String>) objectinputstream.readObject();
			if (patentList == null){
				System.out.println("Patent Data not read.");
				throw new NullPointerException();
			}
			System.out.println("Number of patents read as a list of strings: " + patentList.size());
			ObjectMapper mapper = new ObjectMapper();

	        for (int i = 0; i < patentList.size(); i++){
	        	try{	        		
	        		// references at patent #864 and #972 dont actually map to 
	        		// an object but a string. Fix this by replacing "" with {}
	        		if (i == 864 || i == 972){
	        			patentList.set(i, patentList.get(i).replaceFirst("\"references\":\"\"", "\"references\":{}"));
	        		}
	        		patents.add(mapper.readValue(patentList.get(i), Patent.class));
	        	}
	        	catch (JsonMappingException e){
	        		System.out.println(String.format("Can't read patent (%s): %s ", i, e));
	        		System.out.println(patentList.get(i));
	        	}
	        }
	        System.out.println("Number of patents read into POJOs (using jackson): " + patents.size());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return patents;
	}
}