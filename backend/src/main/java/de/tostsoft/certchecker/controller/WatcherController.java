package de.tostsoft.certchecker.controller;

import de.tostsoft.certchecker.Converter;
import de.tostsoft.certchecker.dto.DomainWatcherDTO;
import de.tostsoft.certchecker.model.DomainWatcher;
import de.tostsoft.certchecker.repository.DomainWatcherRepository;
import de.tostsoft.certchecker.security.MyUserPrincipal;
import de.tostsoft.certchecker.service.DomainWatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@EnableSpringConfigured
@RequestMapping("/api/watchers")
public class WatcherController{

    @Autowired
    DomainWatcherRepository domainWatcherRepository;

    @Autowired
    private DomainWatcherService domainWatcherService;

    @GetMapping(value="/{watcherId}")
    public ResponseEntity<DomainWatcherDTO> getUser(@PathVariable Long watcherId){
        DomainWatcher domainWatcher=domainWatcherRepository.findOneById(watcherId);
        if(domainWatcher == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(Converter.convertDomainWatcherToDomainWatcherTDO(domainWatcher));
    }

    @GetMapping
    public ResponseEntity<List<DomainWatcherDTO>> getUserWatchers(){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<DomainWatcher> ret=domainWatcherService.getDomainWatchers(myUserPrincipal.getUser());
        return ResponseEntity.ok().body(Converter.convertDomainWatchersToDomainWatcherDTOs(ret));
    }

    @PostMapping
    public ResponseEntity<DomainWatcherDTO> editCreateWatcher(@RequestBody DomainWatcherDTO domainWatcherDTO){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(domainWatcherDTO == null || domainWatcherDTO.getSearchTerm() == null || domainWatcherDTO.getMailOnUpdate() == null || domainWatcherDTO.getRegex() == null || domainWatcherDTO.getSendMail() == null){
            return ResponseEntity.badRequest().build();
        }
        if(domainWatcherDTO.getSearchTerm().length() < 4){
            return ResponseEntity.badRequest().build();
        }
        DomainWatcher domainWatcher=domainWatcherService.saveWatcher(myUserPrincipal.getUser(), domainWatcherDTO);
        return ResponseEntity.ok(Converter.convertDomainWatcherToDomainWatcherTDO(domainWatcher));
    }

    @PostMapping(value="/{id}")
    public ResponseEntity<DomainWatcherDTO> setWatcherActive(@PathVariable Long id, @RequestParam(value="active") boolean active){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        DomainWatcher domainWatcher=domainWatcherService.setWatcherActive(myUserPrincipal.getUser(), id, active);
        if(domainWatcher == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Converter.convertDomainWatcherToDomainWatcherTDO(domainWatcher));
    }

}
