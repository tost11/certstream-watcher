package de.tostsoft.certchecker.service;

import de.tostsoft.certchecker.model.LoggedDomain;
import de.tostsoft.certchecker.model.User;
import de.tostsoft.certchecker.repository.LoggedDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DomainService{

    @Autowired
    private LoggedDomainRepository loggedDomainRepository;

    public Page<LoggedDomain> getLoggedDomainsByUser(User user, Integer size){
        Pageable pageable=PageRequest.of(0, size);
        Page<LoggedDomain> page=loggedDomainRepository.findAllByUserAndActiveOnPage(pageable, user.getId());
        return page;
    }
}
