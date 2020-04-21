package de.tostsoft.certchecker.repository;

import de.tostsoft.certchecker.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User,Long>{
    @Override
    ArrayList<User> findAll();

    User findById(long id);

    User findByUsername(String name);

    User findByConfirmUUID(UUID uuid);
}
