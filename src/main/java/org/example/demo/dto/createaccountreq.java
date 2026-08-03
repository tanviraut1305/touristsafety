package org.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.example.demo.enums.Touristtype;

@Getter
@Setter
public class createaccountreq {

    private Touristtype touristType;

    private String aadhaarNumber;

    private String passportNumber;
    private String password;
    private String country;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile number must contain 10 digits"
    )
    private String mobileNumber;

}