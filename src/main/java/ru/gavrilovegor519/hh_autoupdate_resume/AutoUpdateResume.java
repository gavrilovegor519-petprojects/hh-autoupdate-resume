package ru.gavrilovegor519.hh_autoupdate_resume;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.gavrilovegor519.hh_autoupdate_resume.dto.TokenDto;
import ru.gavrilovegor519.hh_autoupdate_resume.util.HhApiUtils;
import ru.gavrilovegor519.hh_autoupdate_resume.util.SendTelegramNotification;

import java.util.prefs.Preferences;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class AutoUpdateResume {

    private final HhApiUtils hhApiUtils;
    private final String resumeId;
    private final SendTelegramNotification sendTelegramNotification;

    private final Preferences preferences = Preferences.userRoot().node("hh-autoupdate-resume");

    private String accessToken = preferences.get("access_token", null);
    private String refreshToken = preferences.get("refresh_token", null);

    public AutoUpdateResume(HhApiUtils hhApiUtils, SendTelegramNotification sendTelegramNotification,
                            @Value("${ru.gavrilovegor519.hh-autoupdate-resume.resumeId}") String resumeId) {
        this.hhApiUtils = hhApiUtils;
        this.sendTelegramNotification = sendTelegramNotification;
        this.resumeId = resumeId;
    }

    @Scheduled(fixedRate = 14400000)
    public void updateResume() {
        if (accessToken != null && refreshToken != null) {
            try {
                updateResumeInternal();
            } catch (Exception e) {
                updateTokens(false);
                updateResumeInternal();
            }
        } else {
            updateTokens(true);
            updateResumeInternal();
        }
    }

    private void updateResumeInternal() {
        try {
            hhApiUtils.updateResume(resumeId, accessToken);
            sendTelegramNotification.send("Резюме обновлено");
        } catch (Exception e) {
            sendTelegramNotification.send("Ошибка обновления резюме: " + e.getMessage());
            throw e;
        }
    }

    private void updateTokens(boolean isInitial) {
        try {
            if (isInitial) {
                updateTokensInPreferences(hhApiUtils.getInitialToken());
            } else {
                updateTokensInPreferences(hhApiUtils.getNewToken(refreshToken));
            }
            sendTelegramNotification.send("Токены обновлены");
        } catch (Exception e) {
            sendTelegramNotification.send("Ошибка обновления токенов: " + e.getMessage());
            throw e;
        }
    }

    private void updateTokensInPreferences(TokenDto tokenDto) {
        if (tokenDto != null && !tokenDto.getAccess_token().isEmpty() &&
                !tokenDto.getRefresh_token().isEmpty()) {
            preferences.put("access_token", tokenDto.getAccess_token());
            preferences.put("refresh_token", tokenDto.getRefresh_token());
            accessToken = preferences.get("access_token", null);
            refreshToken = preferences.get("refresh_token", null);
        }
    }

}
