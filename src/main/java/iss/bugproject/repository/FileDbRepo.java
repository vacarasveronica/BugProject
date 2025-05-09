package iss.bugproject.repository;

import iss.bugproject.domain.Bug;
import iss.bugproject.domain.File;
import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FileDbRepo implements FileInterface {
    private String url;
    private String username;
    private String password;

    public FileDbRepo(String url,String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
    @Override
    public Optional<File> findOne(Integer id1) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from bugs " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id1));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String path = resultSet.getString("path");
                String descriere = resultSet.getString("descriere");
                Integer bugid = resultSet.getInt("bugid");
                File f = new File(path,descriere,bugid);
                f.setId(id);
                return Optional.of(f);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<File> findAll() throws SQLException {
        Set<File> files = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bugfile");
             ResultSet resultSet = statement.executeQuery()) {  // Folosește executeQuery pentru a obține ResultSet

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String path = resultSet.getString("path");
                String descriere = resultSet.getString("descriere");
                Integer bugid = resultSet.getInt("bugid");
                File f = new File(path,descriere,bugid);
                f.setId(id);
                files.add(f);
            }
            return files;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<File> save(File entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO bugfile(id, path,descriere,bugid) VALUES ('"+entity.getId()+"', '"+entity.getPath()+"', '"+entity.getDescriere()+"', '"+entity.getBugId()+"')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<File> delete(Integer id) {
        Optional<File> fileToDelete = findOne(id);
        if (fileToDelete.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM bugfile WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return fileToDelete;
    }



    @Override
    public Optional<File> update(File entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        Optional<File> existing = findOne(entity.getId());
        if (existing.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE bugfile SET path = ?, descriere = ?, bugid = ? WHERE id = ?")) {
            statement.setString(1, entity.getPath());
            statement.setString(2, entity.getDescriere());
            statement.setInt(3, entity.getBugId());
            statement.setInt(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<File> findFileByBugId(int bugId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM bugfile WHERE bugid = ?")) {

            statement.setInt(1, bugId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String path = resultSet.getString("path");
                String descriere = resultSet.getString("descriere");
                Integer bugIdFromDb = resultSet.getInt("bugid");

                File file = new File(path, descriere, bugIdFromDb);
                file.setId(id);

                return Optional.of(file);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving file by bug ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }



}
