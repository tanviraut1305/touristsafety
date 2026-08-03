package org.example.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.example.demo.enums.Touristtype;

@Entity
@Table(name = "tourists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tourist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Touristtype touristType;

    @Column(unique = true)
    private String aadhaarNumber;

    @Column(unique = true)
    private String passportNumber;

    private String country;
    private String password;
    @Column(nullable = false, unique = true)
    private String mobileNumber;

    private Boolean verified = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}