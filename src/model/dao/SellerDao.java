package model.dao;

import java.util.List;

import model.entities.Seller;
import model.entities.Department;

public interface SellerDao {

	public void insert(Seller seller);
	public void update(Seller seller);
	public void deleteById(Integer id);
	public Seller findById(Integer id );
	public List<Seller> findAll();
	public List<Seller> findByDepartment(Department department);
}
