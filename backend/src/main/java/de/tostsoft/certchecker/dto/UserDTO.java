package de.tostsoft.certchecker.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO{
    private Long id;
    private String username;
    private String notifyMail;
    private boolean mailConfirmed;
}
