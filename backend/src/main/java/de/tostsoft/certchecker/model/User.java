package de.tostsoft.certchecker.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity(name="users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false)
    private String password;

    private String notifyMail;

    private UUID confirmUUID;

    @ElementCollection
    private Set<String> confirmedMails;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="user")
    private List<DomainWatcher> domainWatches=new ArrayList<>();

}
