package de.tostsoft.certchecker.controller;

import de.tostsoft.certchecker.Converter;
import de.tostsoft.certchecker.dto.LoggedDomainDTO;
import de.tostsoft.certchecker.model.LoggedDomain;
import de.tostsoft.certchecker.model.PageResult;
import de.tostsoft.certchecker.security.MyUserPrincipal;
import de.tostsoft.certchecker.service.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/domains")
public class DomainController{

    @Autowired
    private DomainService domainService;

    @GetMapping()
    public ResponseEntity<PageResult<LoggedDomainDTO>> getLoggedDomains(@RequestParam(required=false) Integer size){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if(size == null || size < 0){
            size=100;
        }else if(size > 10000){
            size=10000;
        }
        Page<LoggedDomain> domains=domainService.getLoggedDomainsByUser(myUserPrincipal.getUser(), size);
        if(domains == null){
            return ResponseEntity.badRequest().build();
        }
        PageResult pageResult=new PageResult<LoggedDomainDTO>();
        pageResult.setContent(Converter.convertLoggedDomains(domains.getContent()));
        pageResult.setNumPages(domains.getTotalPages());
        pageResult.setPage(domains.getNumber());
        pageResult.setTotalElements(domains.getTotalElements());
        return ResponseEntity.ok(pageResult);
    }

}
