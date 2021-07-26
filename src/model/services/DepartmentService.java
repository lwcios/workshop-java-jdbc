package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	public List<Department> findall(){
		
		return dao.findAll();
	}
	
	
	public void saveOrUpdate(Department obj) {
		
		if(obj.getId()==null) {
			/*como na criação do DB o id é notNull colocamos o id =0*/
			obj.setId(0);
			dao.insert(obj);
		}else {
			
			dao.update(obj);
		}
	}
	
}
