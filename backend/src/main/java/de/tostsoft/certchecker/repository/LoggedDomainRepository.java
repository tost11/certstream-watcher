package de.tostsoft.certchecker.repository;

import de.tostsoft.certchecker.model.LoggedDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface LoggedDomainRepository extends CrudRepository<LoggedDomain,Long>{
    @Override
    ArrayList<LoggedDomain> findAll();

    @Query(nativeQuery=true, value="SELECT ld.* FROM logged_domain AS ld INNER join domain_watcher_logged_domains as z ON ld.id = z.logged_domains_id INNER JOIN domain_watcher as dw ON z.domain_watcher_id = dw.id WHERE dw.user_id = ?1 AND dw.active = true")
    ArrayList<LoggedDomain> findAllByUserAndActive(long userId);

    @Query(nativeQuery=true, value="SELECT ld.* FROM logged_domain AS ld INNER join domain_watcher_logged_domains as z ON ld.id = z.logged_domains_id INNER JOIN domain_watcher as dw ON z.domain_watcher_id = dw.id WHERE dw.user_id = ?1 AND dw.active = true",
            countQuery="SELECT ld.* FROM logged_domain AS ld INNER join domain_watcher_logged_domains as z ON ld.id = z.logged_domains_id INNER JOIN domain_watcher as dw ON z.domain_watcher_id = dw.id WHERE dw.user_id = ?1 AND dw.active = true")
    Page<LoggedDomain> findAllByUserAndActiveOnPage(Pageable pageable, long userId);

    LoggedDomain findByName(String name);

    List<LoggedDomain> findAllByNameContaining(String name);
}
