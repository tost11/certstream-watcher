package de.tostsoft.certchecker.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResult<T>{
    List<T> content;
    Long totalElements;
    Integer numPages;
    Integer page;
}
