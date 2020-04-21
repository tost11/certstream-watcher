package de.tostsoft.certchecker.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainWatcherDTO{
    private Long id;
    private String searchTerm;
    private Boolean sendMail;
    private Boolean mailOnUpdate;
    private Boolean regex;
    private Boolean active;
    private List<LoggedDomainDTO> loggedDomains;
}
