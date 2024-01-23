package daoImpl;

import com.beans.Institution;
import com.dao.InstitutionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import databaseConfig.ConnectionInstance;


public class InstitutionDAOImpl implements InstitutionDAO {
	
	private Connection conn = ConnectionInstance.getConnection() ; 

	public InstitutionDAOImpl() {
	}
	
    @Override
    public Institution getById(int id) {
    	Institution institution = null;

	    try {
	        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `institution` WHERE `id` = ?");
	        preparedStatement.setInt(1, id);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            String nom = resultSet.getString("nom");
	            String tel = resultSet.getString("tel");
	            String adresse = resultSet.getString("adresse");
	            String email = resultSet.getString("email");
	            String espace = resultSet.getString("espace");

	            institution = new Institution() ; 
	            institution.setId(id);
	            institution.setNom(nom);
	            institution.setTel(tel);
	            institution.setAdresse(adresse);
	            institution.setEspace(espace);
	        }

	        resultSet.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return institution;
    }

    @Override
    public Institution getByEmail(String email) {
    	Institution institution = null;

	    try {
	        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM `institution` WHERE `email` = ?");
	        preparedStatement.setString(1, email);

	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	        	int id = resultSet.getInt("id");
	            String nom = resultSet.getString("nom");
	            String emailRs = resultSet.getString("email");
	            String tel = resultSet.getString("tel");
	            String adresse = resultSet.getString("adresse");
	            String password = resultSet.getString("password");
	            String espace = resultSet.getString("espace");

	            institution = new Institution() ; 
	            institution.setId(id);
	            institution.setNom(nom);
	            institution.setTel(tel);
	            institution.setAdresse(adresse);
	            institution.setEmail(emailRs);
	            institution.setPassword(password);
	            institution.setEspace(espace);
	        }

	        resultSet.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    

	    return institution;
    }

    @Override
    public boolean createInstitution(Institution espace) {
	    try {
	        PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO `institution`(`nom`, `tel`, `adresse`, `email`, `password`,`espace`) VALUES (?,?,?,?,?,?);");

	        preparedStatement.setString(1, espace.getNom());
	        preparedStatement.setString(2, espace.getTel());
	        preparedStatement.setString(3, espace.getAdresse());
	        preparedStatement.setString(4, espace.getEmail());
	        preparedStatement.setString(5, espace.getPassword());
	        preparedStatement.setString(6, espace.getEspace());

	        preparedStatement.executeUpdate();
	        
	        System.out.println("Insertion of Institution successful");
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
    }

    @Override
    public boolean updateInstitution(Institution centre) {

        return false;
    }

    @Override
    public boolean deleteInstitution(int id) {

        return false;
    }


}