import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.ResultSetMetaData;
import org.mariadb.jdbc.Driver;

public class Problem3 {
	public static String input(String msg){
		Reader rdr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(rdr);
		String inputstr = "";			
		System.out.print("\n" + msg);
		try {
			inputstr = br.readLine().trim();
			// TODO: Put a check to make sure input exists as a row
		} catch (IOException e) {
			System.out.println("That input didn't work.");
		}
		return inputstr;
	}

	public static void printRow(ResultSet rs, ResultSetMetaData rsm, int columns) throws SQLException{
		String str;
		for (int i = 1; i <= columns; i++){
			if (rs.getObject(i) != null){
				if (rsm.getColumnType(i) == -3){
					java.sql.Blob ablob = rs.getBlob(i);
					str = new String(ablob.getBytes(1l, (int) ablob.length()));
				}
				else{					
					str = rs.getObject(i).toString();
				}
				System.out.print(((str.length() <= 30) ? str : str.substring(0, 30) + "...") + "\t");
			}
		}
		System.out.println();
	}
	
	public static void submitQuery(Connection conn, String query){
    	try{
    		Statement s = conn.createStatement();    		
    		ResultSet rs = s.executeQuery(query);
    		ResultSetMetaData rsm = rs.getMetaData();
    		int count = rsm.getColumnCount();
    		if (rs.next()){
    			for (int i = 1; i <= count; i++){
    				System.out.print(rsm.getColumnName(i) + "\t");
    			}    			
    			System.out.println();
    			printRow(rs, rsm, count);
    		}
    		else{
    			System.out.println("No results found.");
    		}
    		while (rs.next()){
    			printRow(rs, rsm, count);
    		}
    		rs.close();
    		s.close();
    	}
		catch(SQLException e) {
			System.out.println("Can't do that. " + e.getMessage());
		}
	}
	
	public static void printTables(Connection conn, List<String> tables, String dbname){
		for (int i = 0; i < tables.size(); i++){
			System.out.println("--- " + tables.get(i) + " ---");
			submitQuery(conn, "select * from " + dbname + "." + tables.get(i));
			System.out.println();
		}
		System.out.println("Thats all the tables in " + dbname + ".");
	}
	
    public static void main(String[] args) throws ClassNotFoundException{
    	Connection conn = null;
    	try{
    		// make the connection to MariaDB
    		Driver d = new Driver();
    		conn = d.connect("jdbc:mariadb://localhost/PatentDB?user=root", null);
    		if (conn == null){
    			throw new NullPointerException();
    		}

    		// print out tables before insetions
    		List<String> tables = Arrays.asList("Abstracts", "Claims", "References", "Descriptions", "Summaries", "Patents", "Assignees", "Inventors");
    		System.out.println("Before the insertions...");
    		printTables(conn, tables, "PatentDb");
    		
    		// read the JSON file using Jackson
    		ArrayList<Patent> patents = PatentReader.ReadPatentJSON();
			System.out.println(patents.get(0).getReferences().get(0));
    		
    	}
		catch(SQLException e) {
			e.printStackTrace();
		}
		catch(NullPointerException e) {
			System.out.println("Couldn't make the connection. " + e.getMessage());
		}

		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
    }
}