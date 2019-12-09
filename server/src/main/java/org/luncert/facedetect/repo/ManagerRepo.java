package org.luncert.facedetect.repo;

import org.luncert.facedetect.model.Manager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepo extends CrudRepository<Manager, Integer> {
}