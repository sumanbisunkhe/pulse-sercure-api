package com.pulse.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pulse.api.enums.RelationshipStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.pulse.api.enums.RoleName;
import com.pulse.api.enums.Gender;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private Long id;


    @NotBlank(message = "First name is mandatory")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be at least 8 characters long")
    private String password;

    private String phone;

    private String country;

    private Gender gender;

    private RelationshipStatus relationshipStatus;

    @Lob
    private MultipartFile profilePicture;

    private String profilePictureUrl;

    private String bio;

    private Set<RoleName> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
