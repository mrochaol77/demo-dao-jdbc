package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	// implementing dependency to connection that is passed by on constructor
	// to all class SellerDaoJDBC
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		
		PreparedStatement st = null;
		
		try {
		
			// preparing insert query
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			// passing by parameters
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			// executing and capturing number of rows affected
			int rowsAffected = st.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error ! No rows affected on insert a new Seller");
			}
			
		}
		catch (SQLException e ) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void update(Seller obj) {

		PreparedStatement st = null;
		
		try {
		
			// preparing insert query
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			// passing by parameters
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			// executing query
			st.executeUpdate();
			
		}
		catch (SQLException e ) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			// query
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			// parameters
			st.setInt(1, id);
			
			// executing query
			rs = st.executeQuery();
			
			// if have any row
			if (rs.next()) {
				
				// reusing code with instantiate function
				Department department = instantiateDepartment(rs); 
				Seller seller = instantiateSeller(rs, department);
				
				// returning seller object
				return seller;
			}			
			
			// no rows returned
			return null;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));				
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setDepartment(department);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department department = new Department();
		department.setId(rs.getInt("DepartmentId"));
		department.setName(rs.getString("DepName"));
		return department;
	}

	@Override
	public List<Seller> findAll() {

		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			// query
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			
			// executing query
			rs = st.executeQuery();
			
			// creating seller list
			List<Seller> list = new ArrayList<>();
			
			// declaration to controlling if department already exists on department object
			Map<Integer, Department> map = new HashMap<>();
			
			// while have any row, add data in list
			while (rs.next()) {
				
				// searching if department already exists on department object
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null ) {
					// reusing code with instantiate function
					dep = instantiateDepartment(rs);
					// adding department on map to not duplicate on next rows of resultset
					map.put(rs.getInt("DepartmentId"), dep);
				}
						
				Seller seller = instantiateSeller(rs, dep);
				list.add(seller);
			}			

			// returning list of seller object
			return list;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Seller> findByDepartment(Department department) {

		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			
			// query
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			// parameters
			st.setInt(1, department.getId());
			
			// executing query
			rs = st.executeQuery();
			
			// creating seller list
			List<Seller> list = new ArrayList<>();
			
			// declaration to controlling if department already exists on department object
			Map<Integer, Department> map = new HashMap<>();
			
			// while have any row, add data in list
			while (rs.next()) {
				
				// searching if department already exists on department object
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null ) {
					// reusing code with instantiate function
					dep = instantiateDepartment(rs);
					// adding department on map to not duplicate on next rows of resultset
					map.put(rs.getInt("DepartmentId"), dep);
				}
						
				Seller seller = instantiateSeller(rs, dep);
				list.add(seller);
			}			

			// returning list of seller object
			return list;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}

	}

}
