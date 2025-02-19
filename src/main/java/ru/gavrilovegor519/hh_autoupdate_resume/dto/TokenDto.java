package ru.gavrilovegor519.hh_autoupdate_resume.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenDto (String access_token, String refresh_token) { }
