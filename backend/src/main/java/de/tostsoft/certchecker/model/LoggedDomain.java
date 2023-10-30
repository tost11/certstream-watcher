package de.tostsoft.certchecker.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = @Index(columnList = "name"))
public class LoggedDomain{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch=FetchType.EAGER, mappedBy="loggedDomain")
    private List<DomainUpdate> domainUpdates=new ArrayList<>();
}
