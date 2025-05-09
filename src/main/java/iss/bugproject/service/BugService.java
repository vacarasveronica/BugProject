package iss.bugproject.service;

import iss.bugproject.domain.Bug;
import iss.bugproject.repository.BugInterfae;

import java.sql.SQLException;
import java.util.Optional;

public class BugService {
    private final BugInterfae bugRepository;

    public BugService(BugInterfae bugRepository) {
        this.bugRepository = bugRepository;
    }

    public Integer getNewId()  throws SQLException {
        Integer id;
        Integer max = 0;
        for(Bug o : getAllBugs()) {
            id = o.getId();
            if(id > max)
                max = id;
        }

        return max+1;
    }

    // Create
    public Optional<Bug> addBug(Bug bug) throws SQLException {
        bug.setId(getNewId());
        bugRepository.save(bug);
        return Optional.of(bug);
    }

    // Read all
    public Iterable<Bug> getAllBugs() throws SQLException {
        return bugRepository.findAll();
    }

    // Read one
    public Optional<Bug> findBugById(Integer id) throws SQLException {
        return bugRepository.findOne(id);
    }

    // Update
    public Optional<Bug> updateBug(Bug bug) {
        return bugRepository.update(bug);
    }

    // Delete
    public Optional<Bug> deleteBug(Integer id) {
        return bugRepository.delete(id);
    }
}
