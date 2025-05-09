package iss.bugproject.repository;

import iss.bugproject.domain.User;

import java.util.Optional;

public interface UserInterface extends Repository<Integer, User> {
    Optional<User> findOne(Integer aLong);

    Optional<User> delete(Integer aLong);

}
