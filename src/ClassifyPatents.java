import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mariadb.jdbc.Driver;


// Author: Ronnie Ward and Josiah Coad

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
		
		for (int i : knownDByes){
			yesDBList.add(getTitle(s, i));
		}
		for (int i : knownDBno){
			noDBList.add(getTitle(s, i));
		}
		
        // get a classifier
        Classifier c = new Classifier();
        
        // train the classifier
        c.train(yesDBList, noDBList);

        //read index of desired start patent
        while(true){
            System.out.println("Enter patent start number: ");
            @SuppressWarnings("resource")
			Scanner input = new Scanner(System.in);
            String inputLine = input.nextLine();
            int pn = Integer.parseInt(inputLine.trim());
            
            for (int i = pn; i < 1000; i++){
                String title = getTitle(s, i);
                double pr = c.classify(title);
                System.out.println("Patent index:"+i+"\t"+"Class: "+pr+"\t"+title);
                inputLine = input.nextLine();
                if(inputLine.toLowerCase().contains("exit")) 
                    break;   
            }
        }
    }
    private static String getTitle(Statement s, int i) throws SQLException{
    	ResultSet rs = s.executeQuery("SELECT title FROM Patents WHERE idx = " + (i + 1));
		rs.first();
		return rs.getString("title");
    }
}
