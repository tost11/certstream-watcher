package de.tostsoft.certchecker.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DomainWatcher{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String searchTerm;

    private Boolean regex;
    private Boolean sendMail;
    private Boolean active;
    private Boolean mailOnUpdate;

    //TODO change later to LAZY
    @ManyToOne(fetch=FetchType.EAGER)
    private User user;

    @ManyToMany(fetch=FetchType.LAZY)
    private List<LoggedDomain> loggedDomains=new ArrayList<>();

}
