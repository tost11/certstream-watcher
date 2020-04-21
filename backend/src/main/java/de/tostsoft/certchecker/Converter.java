package de.tostsoft.certchecker;

import de.tostsoft.certchecker.dto.DomainWatcherDTO;
import de.tostsoft.certchecker.dto.LoggedDomainDTO;
import de.tostsoft.certchecker.dto.UserDTO;
import de.tostsoft.certchecker.model.DomainUpdate;
import de.tostsoft.certchecker.model.DomainWatcher;
import de.tostsoft.certchecker.model.LoggedDomain;
import de.tostsoft.certchecker.model.User;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Converter{

    static public List<DomainWatcherDTO> convertDomainWatchersToDomainWatcherDTOs(Collection<DomainWatcher> domainWatchers){
        return domainWatchers.stream().map(Converter::convertDomainWatcherToDomainWatcherTDO).collect(Collectors.toList());
    }

    static public DomainWatcherDTO convertDomainWatcherToDomainWatcherTDO(DomainWatcher domainWatcher){
        return DomainWatcherDTO.builder()
                .id(domainWatcher.getId())
                .regex(domainWatcher.getRegex())
                .active(domainWatcher.getActive())
                .searchTerm(domainWatcher.getSearchTerm())
                .sendMail(domainWatcher.getSendMail())
                .mailOnUpdate(domainWatcher.getMailOnUpdate())
                .build();
    }

    static public UserDTO convertUser(User user){
        UserDTO userDTO=UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .notifyMail(user.getNotifyMail())
                .mailConfirmed(!StringUtils.isEmpty(user.getNotifyMail()) && user.getConfirmUUID() == null)
                .build();
        return userDTO;
    }

    static public List<LoggedDomainDTO> convertLoggedDomains(List<LoggedDomain> loggedDomains){
        return loggedDomains.stream().map(Converter::convertLoggedDomain).collect(Collectors.toList());
    }

    static public LoggedDomainDTO convertLoggedDomain(LoggedDomain loggedDomain){
        Optional<DomainUpdate> domainUpdate=loggedDomain.getDomainUpdates().stream().min((u1, u2)->u1.getDate().compareTo(u2.getDate()));
        return LoggedDomainDTO.builder()
                .id(loggedDomain.getId())
                .domain(loggedDomain.getName())
                .lastUpdateDate(domainUpdate.isPresent()?domainUpdate.get().getDate():null)
                .build();
    }

}
