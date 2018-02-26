import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.mariadb.jdbc.Driver;

import com.fasterxml.jackson.core.JsonParseException;

// Author: Ronnie Ward

public class ClassifyPatents {
    
    public static void main(String[] args) throws Exception {

		// make the connection to MariaDB
		Driver d = new Driver();
		Connection conn = d.connect("jdbc:mariadb://localhost/PatentDB?user=root", null);
		if (conn == null) {
			throw new NullPointerException();
		}
		
		// get the patent titles we will use for training
		int[] knownDByes = { 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 17, 18, 19, 20, 21, 23, 24, 27, 28, 29, 30, 31, 34, 36, 37,
				38, 39, 41, 46, 50 };
		int[] knownDBno = { 0, 1, 9, 10, 11, 15, 16, 22, 25, 26, 32, 33, 35, 40, 42, 43, 44, 45, 47, 48, 49 };
		List<String> yesDBList = new ArrayList<String>();
		List<String> noDBList = new ArrayList<String>();
		Statement s = conn.createStatement();
		ResultSet rs;
		for (int i : knownDByes){
			rs = s.executeQuery("SELECT title FROM Patents WHERE idx = " + (i - 1));
			rs.first();
			yesDBList.add(rs.getString("title"));
		}
		for (int i : knownDBno){
			rs = s.executeQuery("SELECT title FROM Patents WHERE idx = " + (i - 1));
			rs.first();
			noDBList.add(rs.getString("title"));
		}
		
        // get a classifier
        Classifier c = new Classifier();
        
        // train the classifier
        c.train(yesDBList, noDBList);

        //read index of desired start patent    
        while(true){
            System.out.println("Enter patent start number: ");
            Scanner input = new Scanner(System.in);
            String inputLine = input.nextLine();
            int pn = Integer.parseInt(inputLine.trim());
            
            for (int i = pn; i < 1000; i++){
                String patent = pdf.getPatent(i);
                double pr = c.classify(patent);
                PatentParser pp = new PatentParser(patent);
                System.out.println("Patent index:"+i+"\t"+"Class: "+pr+"\t"+pp.findFieldValue("title"));
                inputLine = input.nextLine();
                if(inputLine.toLowerCase().contains("exit")) 
                    break;   
            }
        }
    }
}
