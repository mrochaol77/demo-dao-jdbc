package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {

		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		System.out.println("=== TEST 1: department findById ===");
		Department department = departmentDao.findById(1);
		System.out.println(department);
		
		System.out.println("\n=== TEST 2: department findAll ===");
		List<Department> listDepartment = departmentDao.findAll();
		listDepartment.forEach(System.out::println);
		
		System.out.println("\n=== TEST 3: department insert ===");
		Department newDepartment = new Department(null, "Shoes");
		departmentDao.insert(newDepartment);
		System.out.println("Inserted! New Id = " + newDepartment.getId());

		System.out.println("\n=== TEST 4: department update ===");
		department = departmentDao.findById(7);
		department.setName("Glasses");
		departmentDao.update(department);
		System.out.println("Update completed !");
		
		System.out.println("\n=== TEST 5: department deleteById ===");
		departmentDao.deleteById(8);
		System.out.println("Delete completed !");

	}

}
