package iss.bugproject.service;

import iss.bugproject.domain.Bug;
import iss.bugproject.domain.File;
import iss.bugproject.repository.FileInterface;

import java.sql.SQLException;
import java.util.Optional;

public class FileService {
    private final FileInterface fileRepository;

    public FileService(FileInterface fileRepository) {
        this.fileRepository = fileRepository;
    }
    public Integer getNewId()  throws SQLException {
        Integer id;
        Integer max = 0;
        for(File o : getAllFiles()) {
            id = o.getId();
            if(id > max)
                max = id;
        }

        return max+1;
    }

    // Create
    public Optional<File> addFile(File file) throws SQLException {
        file.setId(getNewId());
        return fileRepository.save(file);
    }

    // Read all
    public Iterable<File> getAllFiles() throws SQLException {
        return fileRepository.findAll();
    }

    // Read one
    public Optional<File> findFileById(Integer id) throws SQLException {
        return fileRepository.findOne(id);
    }

    // Update
    public Optional<File> updateFile(File file) {
        return fileRepository.update(file);
    }

    // Delete
    public Optional<File> deleteFile(Integer id) {
        return fileRepository.delete(id);
    }

    public Optional<File> findFileByBugId(int bugId) throws SQLException {
        return fileRepository.findFileByBugId(bugId);
    }
}

