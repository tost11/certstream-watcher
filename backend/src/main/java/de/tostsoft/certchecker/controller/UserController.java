package de.tostsoft.certchecker.controller;

import de.tostsoft.certchecker.Converter;
import de.tostsoft.certchecker.dto.RegistrationDTO;
import de.tostsoft.certchecker.dto.UserDTO;
import de.tostsoft.certchecker.dto.UserUpdateDTO;
import de.tostsoft.certchecker.model.User;
import de.tostsoft.certchecker.security.JwtResponse;
import de.tostsoft.certchecker.security.JwtTokenUtil;
import de.tostsoft.certchecker.security.JwtUserDetailsService;
import de.tostsoft.certchecker.security.MyUserPrincipal;
import de.tostsoft.certchecker.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@RestController
@RequestMapping("api/user")
public class UserController{

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${mail.redirect-url:http://localhost:3000/user}")
    private String mailRedirectUrl;

    private void validateUserDTO(RegistrationDTO registrationAttendessDTO){
        if(StringUtils.isEmpty(registrationAttendessDTO.getUsername()) || !StringUtils.isAlphanumeric(registrationAttendessDTO.getUsername()) || registrationAttendessDTO.getUsername().length() < 4 || registrationAttendessDTO.getUsername().length() > 20){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username have to between 4 and 20 characters and alphanumeric");
        }
        if(!StringUtils.isEmpty(registrationAttendessDTO.getNotifyMail()) && !registrationAttendessDTO.getNotifyMail().matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid email format");
        }
        if(StringUtils.isEmpty(registrationAttendessDTO.getPassword()) || !registrationAttendessDTO.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password dose not match conditions");
        }
    }

    private void validateUserDTO(UserUpdateDTO registrationAttendessDTO){
        if(!StringUtils.isEmpty(registrationAttendessDTO.getNotifyMail()) && !registrationAttendessDTO.getNotifyMail().matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid email format");
        }
        if(!StringUtils.isEmpty(registrationAttendessDTO.getPassword()) && !registrationAttendessDTO.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password dose not match conditions");
        }
    }

    private void fixUserDTO(RegistrationDTO registrationDTO){
        if(StringUtils.isEmpty(registrationDTO.getNotifyMail())){
            registrationDTO.setNotifyMail(null);
        }
    }

    @PostMapping(value="/create")
    public ResponseEntity<JwtResponse> createUser(@RequestBody RegistrationDTO registrationDTO){
        validateUserDTO(registrationDTO);
        fixUserDTO(registrationDTO);
        User user=userService.createUser(registrationDTO);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already Taken");
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registrationDTO.getUsername(), registrationDTO.getPassword()));
        final UserDetails userDetails=userDetailsService
                .loadUserByUsername(registrationDTO.getUsername());
        final String token=jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(userUpdateDTO.getPassword() == null && userUpdateDTO.getNotifyMail() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one update paramter is required");
        }
        validateUserDTO(userUpdateDTO);
        User user=userService.updateUser(myUserPrincipal.getUser(), userUpdateDTO);
        return ResponseEntity.ok(Converter.convertUser(user));
    }

    @GetMapping
    ResponseEntity<UserDTO> getOwnAttendeesInfo(){
        MyUserPrincipal myUserPrincipal=(MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(myUserPrincipal == null){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserDTO userDTO=Converter.convertUser(myUserPrincipal.getUser());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/confirm/{uuid}")
    public RedirectView confirmMail(@PathVariable UUID uuid){
        if(!userService.confirmMail(uuid)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mail already confirmed or not found");
        }
        RedirectView redirectView=new RedirectView();
        redirectView.setUrl(mailRedirectUrl);
        return redirectView;
    }
}
