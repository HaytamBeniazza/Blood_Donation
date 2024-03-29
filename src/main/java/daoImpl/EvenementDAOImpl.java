package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beans.Donnateur;
import com.beans.Evenement;
import com.beans.Institution;
import com.beans.Pochette;
import com.dao.EvenementDAO;
import com.dao.InstitutionDAO;

import databaseConfig.ConnectionInstance;

public class EvenementDAOImpl implements EvenementDAO {

    private Connection conn = ConnectionInstance.getConnection();
    private InstitutionDAO institutionDao = new InstitutionDAOImpl() ; 

    @Override
    public void ajouter(Evenement evenement) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "INSERT INTO evenement (id_event, titre, description, date, adresse, centre_id) VALUES (?, ?, ?, ?, ?,?);");

            preparedStatement.setInt(1, evenement.getIdEvent());
            preparedStatement.setString(2, evenement.getTitre());
            preparedStatement.setString(3, evenement.getDescription());

            // Convert Java Date to SQL Date
            java.sql.Date sqlDate = new java.sql.Date(evenement.getDate().getTime());
            preparedStatement.setDate(4, sqlDate);

            preparedStatement.setString(5, evenement.getAdresse());
            preparedStatement.setInt(6, evenement.getInstitution().getId());

      

            preparedStatement.executeUpdate();
            System.out.println("Insertion of Evenement successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Evenement getEvenementById(int idEvenement) {
        Evenement evenement = null;
        try {
            PreparedStatement preparedStatement = conn
                    .prepareStatement("SELECT * FROM evenement WHERE id_event = ?");
            preparedStatement.setInt(1, idEvenement);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                evenement = extractEvenementFromResultSet(resultSet);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenement;
    }

    @Override
    public List<Evenement> getAllEvenements() {
        List<Evenement> evenements = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn
                    .prepareStatement("SELECT * FROM evenement");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Evenement evenement = extractEvenementFromResultSet(resultSet);
                evenements.add(evenement);
            }

            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    @Override
    public void updateEvenement(Evenement evenement) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE evenement SET titre = ?, description = ?, date = ?, adresse = ?, institution_id = ? WHERE id_event = ?");

            preparedStatement.setString(1, evenement.getTitre());
            preparedStatement.setString(2, evenement.getDescription());

            // Convert Java Date to SQL Date
            java.sql.Date sqlDate = new java.sql.Date(evenement.getDate().getTime());
            preparedStatement.setDate(3, sqlDate);

            preparedStatement.setString(4, evenement.getAdresse());
            preparedStatement.setInt(5, evenement.getInstitution().getId());
            preparedStatement.setInt(7, evenement.getIdEvent());

            preparedStatement.executeUpdate();
            System.out.println("Update of Evenement successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEvenement(int idEvenement) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM evenement WHERE id_event = ?");
            preparedStatement.setInt(1, idEvenement);

            preparedStatement.executeUpdate();
            System.out.println("Deletion of Evenement successful");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Evenement extractEvenementFromResultSet(ResultSet resultSet) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setIdEvent(resultSet.getInt("id_event"));
        evenement.setTitre(resultSet.getString("titre"));
        evenement.setDescription(resultSet.getString("description"));
        evenement.setDate(resultSet.getDate("date"));
        evenement.setAdresse(resultSet.getString("adresse"));

        Institution institution = institutionDao.getById(resultSet.getInt("centre_id"))  ;
        evenement.setInstitution(institution);


        return evenement; 
    }

	@Override
	public List<Evenement> getEventsByCenter(int id) {
		 List<Evenement> evenements = new ArrayList<>();
	        try {
	            PreparedStatement preparedStatement = conn
	                    .prepareStatement("SELECT * FROM evenement WHERE evenement.centre_id = ?");
	            
	            preparedStatement.setInt(1, id);

	            ResultSet resultSet = preparedStatement.executeQuery();

	            while (resultSet.next()) {
	                Evenement evenement = extractEvenementFromResultSet(resultSet);
	                evenements.add(evenement);
	            }

	            resultSet.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return evenements;
	}
}
