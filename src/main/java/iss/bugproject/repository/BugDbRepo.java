package iss.bugproject.repository;

import iss.bugproject.domain.Bug;
import iss.bugproject.domain.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class BugDbRepo implements BugInterfae {
    private String url;
    private String username;
    private String password;

    public BugDbRepo(String url,String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
    @Override
    public Optional<Bug> findOne(Integer id) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from bugs " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Integer id1 = resultSet.getInt("id");
                String denumire = resultSet.getString("denumire");
                String descriere = resultSet.getString("descriere");
                String status = resultSet.getString("status");
                Integer idProgramator = resultSet.getInt("idprogramator");
                Bug b = new Bug(denumire,descriere,status,idProgramator);
                b.setId(id1);
                return Optional.of(b);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Bug> findAll() throws SQLException {
        Set<Bug> bugs = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bugs");
             ResultSet resultSet = statement.executeQuery()) {  // Folosește executeQuery pentru a obține ResultSet

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String denumire = resultSet.getString("denumire");
                String descriere = resultSet.getString("descriere");
                String status = resultSet.getString("status");
                Integer idProgramator = resultSet.getInt("idprogramator");
                Bug b = new Bug(denumire,descriere,status,idProgramator);
                b.setId(id);
                bugs.add(b);
            }
            return bugs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Bug> save(Bug entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO bugs(id, denumire, descriere, status, idprogramator) VALUES (?, ?, ?, ?, ?)")) {

            statement.setInt(1, entity.getId());
            statement.setString(2, entity.getDenumire());
            statement.setString(3, entity.getDescriere());
            statement.setString(4, entity.getStatus());

            if (entity.getIdProgramator() != null) {
                statement.setInt(5, entity.getIdProgramator());
            } else {
                statement.setNull(5, java.sql.Types.INTEGER);
            }

            statement.executeUpdate();
            return Optional.of(entity);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save bug: " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<Bug> delete(Integer id) {
        Optional<Bug> bugToDelete = findOne(id);
        if (bugToDelete.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM bugs WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bugToDelete;
    }


    @Override
    public Optional<Bug> update(Bug entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        Optional<Bug> existing = findOne(entity.getId());
        if (existing.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bugs SET denumire = ?, descriere = ?, status = ?, idprogramator = ? WHERE id = ?")) {
            statement.setString(1, entity.getDenumire());
            statement.setString(2, entity.getDescriere());
            statement.setString(3, entity.getStatus());
            statement.setInt(4, entity.getIdProgramator());
            statement.setInt(5, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }


}
