package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao dao = DaoFactory.createSellerDao();
	public List<Seller> findall(){
		
		return dao.findAll();
	}
	
	
	public void saveOrUpdate(Seller obj) {
		
		if(obj.getId()==null) {
			/*como na cria��o do DB o id � notNull colocamos o id =0*/
			obj.setId(0);
			dao.insert(obj);
		}else {
			
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj) {
		
		dao.deleteById(obj.getId());
		
	}
	
}
