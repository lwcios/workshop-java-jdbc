package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
/*Declarou uma constante to tipo connection*/	
private static Connection conn = null;


/*  Metodo para carregar as propriedades do banco de dados*/

private static Properties loadProperties() {
	
	try(FileInputStream fs = new FileInputStream("db.properties")){
		
	Properties props = new Properties();
	props.load(fs);
	return props;
		
	}catch(IOException e ) {
		throw new DbException(e.getMessage());
		
	}
	
}


/* Metodo para iniciar uma conexão com o banco de dados
 * onde é passado como parametro o dburl que é o endereço 
 * do banco de dados*/
public static Connection getConnection() {
	
	if(conn ==null) {
		try {
		Properties props =loadProperties();
		String url =props.getProperty("dburl");
		
		conn = DriverManager.getConnection(url,props);
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
			
		}
	}
	return conn;
}
	

/*Metodo para fechar a conecxão e utilizando a exception  personalizada */
public static void closeConnection() {
	if(conn != null) {
		try {
		conn.close();
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
	
}


	
	/*Esse é o método que usamos para fechar o objetos do tipo Stetment
	 * que utilizamos para executar nossa querys no banco de dados*/
     
	
	public static void closeStatement(Statement st ) {
		
		
			if(st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					throw new DbException(e.getMessage());
				}
			}
				
	}
	
	
	/*Esse é o método que utilizamos para fenahr o objetodo da classe ResultSet
	 * que usamos no programa principal para capturar os resultados das nossa querys*/
	
	public static void closeResultSet(ResultSet rs) {
		
		if(rs != null) {
			
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
			
		}
		
	}
	
	
}
