package de.tostsoft.certchecker.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DomainInfo {
    private String domain;
    private Date date;

    @Override
    public String toString() {
        return "DomainInfo{" +
                "domain='" + domain + '\'' +
                ", date=" + date +
                '}';
    }
}
