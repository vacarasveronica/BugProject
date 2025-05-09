package iss.bugproject.repository;

import iss.bugproject.domain.Bug;
import iss.bugproject.domain.Role;
import iss.bugproject.domain.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDbRepo implements UserInterface {
    private String url;
    private String username;
    private String password;

    public UserDbRepo(String url,String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }
    @Override
    public Optional<User> findOne(Integer id1) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from bugs " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(id1));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                User u = new User(username,password, Role.valueOf(role));
                u.setId(id);
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() throws SQLException {
        Set<User> utilizatori = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             ResultSet resultSet = statement.executeQuery()) {  // Folosește executeQuery pentru a obține ResultSet

            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                User u = new User(username,password, Role.valueOf(role));
                u.setId(id);
                utilizatori.add(u);
            }
            return utilizatori;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> save(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        // Criptează parola cu BCrypt
        String hashedPassword = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt());

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users(id, username, password, role) VALUES (?, ?, ?, ?)")) {
            statement.setInt(1, entity.getId());
            statement.setString(2, entity.getUsername());
            statement.setString(3, hashedPassword);
            statement.setString(4, entity.getRole().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }



    @Override
    public Optional<User> delete(Integer id) {
        Optional<User> userToDelete = findOne(id);
        if (userToDelete.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userToDelete;
    }



    @Override
    public Optional<User> update(User entity) {
        if (entity == null)
            throw new IllegalArgumentException("entity must not be null");

        Optional<User> existing = findOne(entity.getId());
        if (existing.isEmpty())
            return Optional.empty();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET username = ?, password = ?, role = ? WHERE id = ?")) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getRole().toString());
            statement.setInt(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }


}
