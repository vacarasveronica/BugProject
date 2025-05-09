package iss.bugproject.service;


import iss.bugproject.domain.Bug;
import iss.bugproject.domain.User;
import iss.bugproject.repository.UserInterface;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UserInterface userRepository;

    public UserService(UserInterface userRepository) {
        this.userRepository = userRepository;
    }
    public Integer getNewId()  throws SQLException {
        Integer id;
        Integer max = 0;
        for(User o : getAllUsers()) {
            id = o.getId();
            if(id > max)
                max = id;
        }

        return max+1;
    }

    // Create
    public Optional<User> addUser(User user) throws SQLException {
        user.setId(getNewId());
        return userRepository.save(user);
    }

    // Read all
    public Iterable<User> getAllUsers() throws SQLException {
        return userRepository.findAll();
    }

    // Read one
    public Optional<User> findUserById(Integer id) throws SQLException {
       return userRepository.findOne(id);
    }

    // Update
    public Optional<User> updateUser(User user) {
        return userRepository.update(user);
    }

    // Delete
    public Optional<User> deleteUser(Integer id) {
        return userRepository.delete(id);
    }
}

