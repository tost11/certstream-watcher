package de.tostsoft.certchecker.repository;

import de.tostsoft.certchecker.model.DomainWatcher;
import de.tostsoft.certchecker.model.LoggedDomain;
import de.tostsoft.certchecker.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface DomainWatcherRepository extends CrudRepository<DomainWatcher,Long> {
    @Override
    ArrayList<DomainWatcher> findAll();

    @Query(value = "SELECT * FROM domain_watcher as dw WHERE ?1 like concat('%',dw.search_term,'%');", nativeQuery = true)
    List<DomainWatcher> findTest(String url);

    DomainWatcher findById(long id);

    List<DomainWatcher> findAllByLoggedDomainsContaining(LoggedDomain loggedDomain);

    List<DomainWatcher> findAllByUser(User user);

    DomainWatcher findOneById(long id);

    DomainWatcher findByIdAndUserId(long id,long userId);
}
