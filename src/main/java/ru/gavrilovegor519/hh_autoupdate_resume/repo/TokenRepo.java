package ru.gavrilovegor519.hh_autoupdate_resume.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilovegor519.hh_autoupdate_resume.entity.Token;

public interface TokenRepo extends JpaRepository<Token, Long> {

}
