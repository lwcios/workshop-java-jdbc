package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;
	private PreparedStatement pst =null;
	private ResultSet rs = null;
	
	//criando a injeçao de dependencia da conexão através do construtor 
	public DepartmentDaoJDBC(Connection connection) {
		this.conn = connection;
	}

	
	
	
	@Override
	public void insert(Department department) {
		
		try {
			
			conn =DB.getConnection();
			conn.setAutoCommit(false);
			pst =conn.prepareStatement("INSERT INTO department (Id,Name) VALUES(?,?)  ",
					PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setInt(1, department.getId());
			pst.setString(2, department.getName());
			
			int rowsAffected =pst.executeUpdate();
			
			/*populando o departamento com o id gerado pelo banco de dados*/
			if(rowsAffected > 0) {
				
				System.out.println("rowsAffected " +  rowsAffected);
				rs = pst.getGeneratedKeys();
				if(rs.next()) {
					
					int id =rs.getInt(1);
					department.setId(id);	
				}else {
					
					throw new DbException("No rowsAffected ");
				}
			}
			conn.commit();
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("Error caused by" + e.getMessage());
			} catch (SQLException e1) {
	           throw new DbException("Error trying rolling back" + e1.getMessage());
			}
		}finally {
			
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			
		}
		

	}

	@Override
	public void update(Department department) {
	
		try {
			conn =DB.getConnection();
			conn.setAutoCommit(false);
			pst =conn.prepareStatement("UPDATE department " 
					+"SET Id =? , Name =? "
					+"WHERE " 
					+"Id = ?",
					PreparedStatement.RETURN_GENERATED_KEYS);
			pst.setInt(1, department.getId());
			pst.setString(2, department.getName());
			pst.setInt(3, department.getId());
			
			
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected >0) {
			System.out.println("rowsAfected " + rowsAffected );
			}else {
				throw new DbException("Unexpected error ");
				
			}
		
			conn.commit();
			
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("Unexpected error caused by " + e.getMessage());
			} catch (SQLException e1) {
				
				throw new DbException("ERROR trying rollingback" + e1.getMessage()); 
			}
		}finally {
			
			DB.closeStatement(pst);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public void deleteById(Integer id) {
		
		try {
			conn =DB.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement("DELETE FROM department "
					+"WHERE id =? ",
					PreparedStatement.RETURN_GENERATED_KEYS
					);
			pst.setInt(1, id);
			
			int rowsAffected = pst.executeUpdate();
			rs =pst.getGeneratedKeys();
			if( rowsAffected == 0 ) {
				throw new DbException("Id not found");
				
			}else {
				
				System.out.println("Department delected rowsAffected " + rowsAffected);   
				
			}
			
			conn.commit();
			
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("unexpected error caused by" + e.getMessage());
			} catch (SQLException e1) {
				throw new DbException("Error trying" + e1.getMessage());
			}
			
		}finally {
			
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			
		}

	}

	@Override
	public Department findById(Integer id) {
	    
		try {
			Department department = new Department();
			conn =DB.getConnection();
			pst =conn.prepareStatement("SELECT *FROM  department WHERE Id = ? ");
			pst.setInt(1, id);
			
			rs =pst.executeQuery();
			if(rs.next()) {
				department.setId(rs.getInt("Id"));
				department.setName(rs.getString("Name"));
				return department;
			}else {
				
				throw new DbException("Department no exist:");
			}
			
			
		}catch(SQLException e) {
			
			throw new DbException("Unexpected error caused by: " + e.getMessage()); 
		}finally {
			
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
		}
		
		
		
	}

	@Override
	public List<Department> findAll() {
		  List<Department> list = new ArrayList<>();
		  
		try {
			String sql ="SELECT * FROM department ";
			Department department =null;
			conn =DB.getConnection();
			
			pst =conn.prepareStatement(sql);
			rs =pst.executeQuery(); 
			
			while(rs.next()) {
				department =instaciaDepartment(rs);
			    list.add(department);
			
			}
			
			return list;
			
		}catch(SQLException e) {
			
			throw new DbException(e.getMessage());
		}finally {
			
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
		}
		
		
	}
	
	
	
	public Department instaciaDepartment(ResultSet rs) {
		try {
			if(rs !=null) {
		Department department = new Department(rs.getInt("Id"),rs.getString("Name"));
		return department;
		}else {
			
		 throw new DbException("Error in result set");
		 
		}

		}catch(SQLException e) {
			
			throw new DbException("Eroro caused by " + e.getMessage());
		}
	}

	}
