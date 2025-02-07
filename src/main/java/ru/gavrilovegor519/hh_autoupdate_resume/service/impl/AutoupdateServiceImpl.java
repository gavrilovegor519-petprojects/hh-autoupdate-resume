package ru.gavrilovegor519.hh_autoupdate_resume.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.hh_autoupdate_resume.dto.TokenDto;
import ru.gavrilovegor519.hh_autoupdate_resume.entity.Token;
import ru.gavrilovegor519.hh_autoupdate_resume.mapper.TokenMapper;
import ru.gavrilovegor519.hh_autoupdate_resume.repo.TokenRepo;
import ru.gavrilovegor519.hh_autoupdate_resume.service.AutoupdateService;
import ru.gavrilovegor519.hh_autoupdate_resume.utils.HhApiUtils;

import java.util.Optional;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class AutoupdateServiceImpl implements AutoupdateService {

    private final TokenRepo tokenRepo;
    private final HhApiUtils hhApiUtils;
    private final String resumeId;
    private final TokenMapper tokenMapper;

    public AutoupdateServiceImpl(TokenRepo tokenRepo, HhApiUtils hhApiUtils, TokenMapper tokenMapper,
                                 @Value("${ru.gavrilovegor519.hh-autoupdate-resume.resumeId}") String resumeId) {
        this.tokenRepo = tokenRepo;
        this.hhApiUtils = hhApiUtils;
        this.tokenMapper = tokenMapper;
        this.resumeId = resumeId;
    }

    @Override
    @Scheduled(fixedRate = 14500)
    public void updateResume() {
        Optional<Token> token = tokenRepo.findById(0L);

        if (token.isPresent()) {
            try {
                hhApiUtils.updateResume(resumeId, token.get().getAccess_token());
            } catch (Exception e) {
                TokenDto tokenDto = hhApiUtils.getNewToken(token.get().getRefresh_token());

                if (tokenDto != null && !tokenDto.getAccess_token().isEmpty() &&
                        !tokenDto.getRefresh_token().isEmpty()) {
                    tokenRepo.save(tokenMapper.toEntity(tokenDto));
                    hhApiUtils.updateResume(resumeId, tokenDto.getAccess_token());
                }
            }
        } else {
            TokenDto tokenDto = hhApiUtils.getInitialToken();

            if (tokenDto != null && !tokenDto.getAccess_token().isEmpty() &&
                    !tokenDto.getRefresh_token().isEmpty()) {
                tokenRepo.save(tokenMapper.toEntity(tokenDto));
                hhApiUtils.updateResume(resumeId, tokenDto.getAccess_token());
            }
        }
    }

}
