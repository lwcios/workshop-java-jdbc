package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.entities.Seller;
import model.dao.SellerDao;
import model.entities.Department;

public class SellerDaoJDBC implements SellerDao {
   /*Aqui estamo criando uma injeção de dependencia
    * para podermos forças a classe DaoJDBC a depender de uma conexao*/  
	private Connection conn;
	public SellerDaoJDBC(Connection conn) {
		
		this.conn =conn;
		
	}
	
	SimpleDateFormat sdf =  new SimpleDateFormat("dd/MM/yyyy");
	
	PreparedStatement pst = null;
	
	@Override
	public void insert(Seller seller) {
		 ResultSet rs = null;
		
		try {
			conn = DB.getConnection();
			conn.setAutoCommit(false);
			
			pst = conn.prepareStatement("INSERT INTO seller "
					+"(Name,Email,BirthDate,BaseSalary,DepartmentId)"
					+"VALUES"
					+ "(?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS );
			//transformando data em string
			String data =sdf.format(( seller.getBirthDate()));
			
			pst.setString(1,seller.getName());
			pst.setString(2,seller.getEmail());
			
			//inserindo a data no db
			try {
				pst.setDate(3, new java.sql.Date(sdf.parse(data).getTime()));
			} catch (ParseException e) {
				throw new DbException(e.getMessage());	
			}
			
			pst.setDouble(4,seller.getBaseSalary() );
			pst.setInt(5, seller.getDepartment().getId());
			

		  int ArrowsAffected = pst.executeUpdate();
		  /*verificar se teve linha afetada e popular
		   * o objeto com o id gerado*/
		  if(ArrowsAffected >0) {
			  System.out.println("Arrows Effected " + ArrowsAffected);
			  rs = pst.getGeneratedKeys();
		       if(rs.next()) {
		    	   int id = rs.getInt(1);
		    	   seller.setId(id);
		       }else {
		    	   //no caso de algum erro lançar uma exception
		    	   throw new DbException("unexpected error! no rows affected! ");
		    	   
		       }
		  }
		  conn.commit();
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("Process Error caused by "+ e.getMessage());
			} catch (SQLException e1) {
				throw new DbException("Error rolling back cused by" + e1.getMessage() );
				
			}
			
			
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			
			
		}
		
	}

	@Override
	public void update(Seller seller) {
		
		try {
		conn =DB.getConnection();
		conn.setAutoCommit(false);
		pst =conn.prepareStatement("UPDATE seller " 
				+"SET Name = ?, Email =?, BirthDate=?, BaseSalary=?, DepartmentId=? "
				+"WHERE Id = ? ",
				
				PreparedStatement.RETURN_GENERATED_KEYS
				);
		
	          String data =sdf.format(seller.getBirthDate());
		      pst.setString(1, seller.getName());
		      pst.setString(2, seller.getEmail());
		      try {
				pst.setDate(3, new java.sql.Date(sdf.parse(data).getTime()));
			} catch (ParseException e) {
				throw new DbException(e.getMessage());
			}
		      pst.setDouble(4, seller.getBaseSalary());
		      pst.setInt(5,seller.getDepartment().getId());
		      pst.setInt(6, seller.getId());
		      int rowsEffected = pst.executeUpdate();
		     
		  conn.commit();
		
		   System.out.println("rowsAffectd:  " + rowsEffected);
		
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("Error caused by " + e.getMessage());
			} catch (SQLException e1) {
				
				throw new DbException(" Error rolling back caused bay " + e1.getMessage());
				
			}
			
		}finally {
			
			DB.closeStatement(pst);
			
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		String sql ="DELETE FROM seller WHERE Id = ? ";
		PreparedStatement pst = null;
		
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setInt(1, id);
			boolean cont= pst.execute();
			if(!cont) {
				System.out.println("Id not found : ");
			}
			conn.commit();
		}catch(SQLException e) {
			
			try {
				conn.rollback();
				throw new DbIntegrityException("Error caudes by: " + e.getMessage());
			} catch (SQLException e1) {
                throw new DbException("Error trying rolling back caused bay: " + e1.getMessage()); 
			}
			
		}finally {
			
			DB.closeStatement(pst);
		}
		
		
	}

	@Override
	public Seller findById(Integer id) {
		ResultSet rs = null;
		
		try {			
			pst =conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?"
					);
			pst.setInt(1, id);
			rs = pst.executeQuery();
			
			 if(rs.next()) {
				 
				 Department dep = instantiateDepartment(rs);
				 
				 Seller seller = instantiatSeller(rs,dep);
				 return seller;
				  
			 }
			 return null;
			
			
		}catch(SQLException e) {
		
		throw new DbException("Error caused by: " + e.getMessage());
			}
			finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			
		}
		
	}

	//helper method to instantiate a seller
	private Seller instantiatSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller =new Seller(rs.getInt("Id"),
	             rs.getString("Name"),
	             rs.getString("Email"),
	             rs.getDate("BirthDate"), 
	             rs.getDouble("BaseSalary"), dep);
        return seller;
	}
  //helper method to instantiate a department
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		 dep.setId(rs.getInt("DepartmentId"));
		 dep.setName(rs.getString("DepName"));
		 return dep;
	}

	@Override
	public List<Seller> findAll() {
		List<Seller> list = new ArrayList<Seller>();
		
		ResultSet rs =null;
		
		try {
			pst =conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name ");
			
			rs=pst.executeQuery();
				
			 Map<Integer,Department> map = new HashMap<>();
			  while(rs.next()) {
				  //verificação do department pelo map
				  Department dep = map.get(rs.getInt("DepartmentId"));
				  
				  if(dep ==null) {
					  dep = instantiateDepartment(rs);
					  map.put(rs.getInt("DepartmentId"), dep);
		            	   
				  }
				  
				  Seller seller = instantiatSeller(rs, dep);
				  list.add(seller);
			  }
			
		}catch(SQLException e) {
			
			throw new DbException("Error caused by:" + e.getMessage());
			
		}	
		
		return list;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		      List<Seller> list = new ArrayList<Seller>();
		      
		      ResultSet rs = null;
		try {
			
			pst = conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+"FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name ");
			
			
			    pst.setInt(1, department.getId());
			    rs=pst.executeQuery();
			    /*Para evitar a repetição dos departamentos usamos a estrutura map antes 
			     * de instanciar um seller */
			    Map<Integer,Department> map = new HashMap<>();
			  while(rs.next()) {
				  //verificação do department pelo map
				  Department dep = map.get(rs.getInt("DepartmentId"));
				  
				  if(dep ==null) {
					  dep = instantiateDepartment(rs);
					  map.put(rs.getInt("DepartmentId"), dep);
		            	   
				  }
				  
				  Seller seller = instantiatSeller(rs, dep);
				  list.add(seller);
			
			  }
			    
		}catch(SQLException e) {
			
			throw new DbException("Error caused bay:" + e.getMessage());
		}finally {
			
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			
		}
		
		return list;
	}

	
}
