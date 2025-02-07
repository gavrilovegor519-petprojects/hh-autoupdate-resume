package ru.gavrilovegor519.hh_autoupdate_resume.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "accessToken", nullable = false)
    private String access_token;

    @Column(name = "refreshToken", nullable = false)
    private String refresh_token;
}
