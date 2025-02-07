package ru.gavrilovegor519.hh_autoupdate_resume.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.gavrilovegor519.hh_autoupdate_resume.dto.TokenDto;
import ru.gavrilovegor519.hh_autoupdate_resume.entity.Token;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenMapper {
    Token toEntity(TokenDto tokenDto);
}
