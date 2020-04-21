package de.tostsoft.certchecker.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoggedDomainDTO{

    private Long id;

    private String domain;

    private Date lastUpdateDate;
}
