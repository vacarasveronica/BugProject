package iss.bugproject.repository;

import iss.bugproject.domain.File;

import java.sql.SQLException;
import java.util.Optional;

public interface FileInterface extends Repository<Integer, File> {
    Optional<File> findFileByBugId(int bugId) throws SQLException;

}
