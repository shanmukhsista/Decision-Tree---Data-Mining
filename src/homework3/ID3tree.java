package homework3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ID3tree {

	public static void main(String[] args) throws IOException {
		//Read all lines from the input file -- passed as the program arguments. 
		List<String> lines = Files.readAllLines(Paths.get("tree.txt"));
		String inputFile = lines.get(0);
		String classifier = lines.get(1);
		String[] featuresArray = lines.get(2).split("\t");
		for ( String feature :featuresArray){
			
		}
		// TODO Auto-generated method stub
		//Read input form the the file. 
		Connection conn = null; 
		Statement s = null; 
		PreparedStatement ps = null; 
		ResultSet rs = null; 
		//Load jdbc driver		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

				conn = DriverManager
				         .getConnection("jdbc:mysql://localhost/?user=root&password=");

				s = conn.createStatement();
			    s.executeUpdate("drop database if exists homework3");
			    s.executeUpdate("create database homework3");
			    s.executeUpdate("use homework3");
			    //Read all headers from the text file. 
			    BufferedReader br = new BufferedReader(new FileReader("input.txt"));
			    //Read the first line as header	
			    String rLine = br.readLine();
			    StringTokenizer tk = new StringTokenizer(rLine,"\t");
			    StringBuilder createQuery =new StringBuilder();
			    createQuery.append("CREATE TABLE data ( ");
			    while ( tk.hasMoreTokens())
			    {
			    	String token = tk.nextToken();
			    	createQuery.append(token);
			    	if ( tk.hasMoreTokens()){
			    		createQuery.append(" varchar(200), ");
			    	}
			    	else{
			    		createQuery.append(" varchar(140) )"); 
			    	}
			    }
			    
			    //rs = s.executeQuery("select occupation, count(occupation) as countoccupation from users group by occupation"); 
				//while ( rs.next()){
				s.executeUpdate(createQuery.toString());
				//Read the remaing lines of the file. 
				String line = null;
				while ( (line = br.readLine()) != null){
					StringTokenizer insTk = new StringTokenizer(line,"\t");
					StringBuilder insertQuery = new StringBuilder();
					insertQuery.append("INSERT INTO data values ( "); 
					while ( insTk.hasMoreTokens()){
						String token = insTk.nextToken();
						insertQuery.append("'");
						insertQuery.append(token);
						if ( insTk.hasMoreTokens()){
							insertQuery.append("',");
							
				    	}
				    	else{
				    		insertQuery.append("')");
				    	}
					}
					s.executeUpdate(insertQuery.toString());
				}
				br.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
