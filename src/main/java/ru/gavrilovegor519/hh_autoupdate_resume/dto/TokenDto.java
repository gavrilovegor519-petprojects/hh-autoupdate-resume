package ru.gavrilovegor519.hh_autoupdate_resume.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenDto {
    private String access_token;
    private String refresh_token;
}
