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

	public static void insertPatentIntoDB(Connection conn, Patent p) throws SQLException{
		String query;
		conn.setAutoCommit(false);
		try{
			Statement s = conn.createStatement();
			query = String.format("INSERT INTO"
					+ " Patents(patNum, iDate, title, familyID, applNum, dateFiled, docID, pubDate, USclass, examiner, legalfirm)"
					+ " VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
					p.getPatNum(), p.getIDate(), p.getTitle().replaceAll("'", "''"), p.getFamilyID(), p.getApplNum(), p.getDateFiled(), 
					p.getDocID(), p.getPubDate(), p.getUSclass(), p.getExaminer().replaceAll("'", "''"), p.getLegalfirm().replaceAll("'", "''"));
			s.executeUpdate(query);					
			query = String.format("INSERT INTO Abstracts(patNum, abstract) VALUES('%s', '%s')", p.getPatNum(), p.getAbstract().replaceAll("'", "''"));
			s.executeUpdate(query);
			query = String.format("INSERT INTO Descriptions(patNum, description) VALUES('%s', '%s')", p.getPatNum(), p.getDescription().replaceAll("'", "''"));
			s.executeUpdate(query);
			query = String.format("INSERT INTO Summaries(patNum, summary) VALUES('%s', '%s')", p.getPatNum(), p.getSummary().replaceAll("'", "''"));
			s.executeUpdate(query);

			for (String claim : p.getClaims()){
				int claimNum = Integer.parseInt(claim.split(". ")[0]);
				query = String.format("INSERT INTO Claims(patNum, claimnum, claim) VALUES('%s', '%s', '%s')", p.getPatNum(), claimNum, claim.replaceAll("'", "''"));
				s.executeUpdate(query);
			}
			
			for (String ref : p.getReferences()){
				query = String.format("INSERT INTO PatentDB.References(patNum, reference) VALUES('%s', '%s')", p.getPatNum(), ref.replaceAll("'", "''"));
				s.executeUpdate(query);
			}
	
			for (String inventor : p.getInventors()){
				query = String.format("INSERT INTO Inventors(patNum, inventor, assignee) VALUES('%s', '%s', '%s')", p.getPatNum(), inventor.replaceAll("'", "''"), p.getAssignee().replaceAll("'", "''"));
				s.executeUpdate(query);		
					query = String.format("INSERT INTO Assignees(patNum, assignee, inventor) VALUES('%s', '%s', '%s')", p.getPatNum(), p.getAssignee().replaceAll("'", "''"), inventor.replaceAll("'", "''"));
					s.executeUpdate(query);		
			}
			conn.commit();
		}
		catch (SQLException e){
			System.out.println("Patents insertion unsuccessful. " + e.getMessage());
		}
		conn.setAutoCommit(true);
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
    		List<String> tables = Arrays.asList("Abstracts", "Claims", "patentDB.References", "Descriptions", "Summaries", "Assignees", "Inventors", "Patents");

    		for (String t : tables){
    			Statement s = conn.createStatement();    		
        		s.executeUpdate("DELETE FROM " + t);
    		}

    		System.out.println("Before the insertions...");
    		for (String t : tables){
    			Statement s = conn.createStatement();    		
        		ResultSet rs = s.executeQuery("SELECT * FROM " + t);
        		rs.last();
        		System.out.println("Number of rows in " + t + ": " + rs.getRow());
        		rs.beforeFirst();
    		}
    		
    		// read the JSON file using Jackson
    		System.out.println();
    		ArrayList<Patent> patents = PatentReader.ReadPatentJSON();
   
    		//insert JSON records into the DB
    		System.out.println("\nNow inserting into DB...");
    		for (int i = 0; i < patents.size(); i++){
    			insertPatentIntoDB(conn, patents.get(i));
			}
    		
    		// print results
    		System.out.println();
    		System.out.println("After the insertions...");
    		for (String t : tables){
    			Statement s = conn.createStatement();		
        		ResultSet rs = s.executeQuery("SELECT * FROM " + t);
        		rs.last();
        		System.out.println("Number of rows in " + t + ": " + rs.getRow());
        		rs.beforeFirst();
    		}

    		
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