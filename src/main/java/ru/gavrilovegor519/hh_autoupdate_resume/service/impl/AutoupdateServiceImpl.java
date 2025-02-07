package ru.gavrilovegor519.hh_autoupdate_resume.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.gavrilovegor519.hh_autoupdate_resume.dto.TokenDto;
import ru.gavrilovegor519.hh_autoupdate_resume.service.AutoupdateService;
import ru.gavrilovegor519.hh_autoupdate_resume.utils.HhApiUtils;

import java.util.prefs.Preferences;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class AutoupdateServiceImpl implements AutoupdateService {

    private final HhApiUtils hhApiUtils;
    private final String resumeId;

    private final Preferences preferences = Preferences.userRoot().node("hh-autoupdate-resume");

    public AutoupdateServiceImpl(HhApiUtils hhApiUtils, @Value("${ru.gavrilovegor519.hh-autoupdate-resume.resumeId}") String resumeId) {
        this.hhApiUtils = hhApiUtils;
        this.resumeId = resumeId;
    }

    @Override
    @Scheduled(fixedRate = 14500)
    public void updateResume() {
        String accessToken = preferences.get("access_token", "");
        String refreshToken = preferences.get("refresh_token", "");

        if (!accessToken.isEmpty() || !refreshToken.isEmpty()) {
            try {
                hhApiUtils.updateResume(resumeId, accessToken);
            } catch (Exception e) {
                TokenDto tokenDto = hhApiUtils.getNewToken(refreshToken);

                if (tokenDto != null && !tokenDto.getAccess_token().isEmpty() &&
                        !tokenDto.getRefresh_token().isEmpty()) {
                    preferences.put("access_token", tokenDto.getAccess_token());
                    preferences.put("refresh_token", tokenDto.getRefresh_token());
                    hhApiUtils.updateResume(resumeId, tokenDto.getAccess_token());
                }
            }
        } else {
            TokenDto tokenDto = hhApiUtils.getInitialToken();

            if (tokenDto != null && !tokenDto.getAccess_token().isEmpty() &&
                    !tokenDto.getRefresh_token().isEmpty()) {
                preferences.put("access_token", tokenDto.getAccess_token());
                preferences.put("refresh_token", tokenDto.getRefresh_token());
                hhApiUtils.updateResume(resumeId, tokenDto.getAccess_token());
            }
        }
    }

}
