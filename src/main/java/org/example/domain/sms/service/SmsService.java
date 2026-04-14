package org.example.domain.sms.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.example.domain.sms.exception.SmsErrorCode;
import org.example.domain.sms.exception.SmsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SmsService {

    private static final String CODE_PREFIX = "sms:code:";
    private static final String VERIFIED_PREFIX = "sms:verified:";
    private static final Duration CODE_TTL = Duration.ofMinutes(3);
    private static final Duration VERIFIED_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${coolsms.api-key}")
    private String apiKey;

    @Value("${coolsms.api-secret}")
    private String apiSecret;

    @Value("${coolsms.from}")
    private String fromNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public void sendCode(String phoneNumber) {
        String code = generateCode();

        try {
            Message message = new Message();
            message.setFrom(fromNumber);
            message.setTo(phoneNumber);
            message.setText("[사고팔고] 인증번호 [" + code + "]를 입력해주세요. (3분 이내 입력)");
            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            throw new SmsException(SmsErrorCode.SMS_SEND_FAILED);
        }

        redisTemplate.opsForValue().set(CODE_PREFIX + phoneNumber, code, CODE_TTL);
    }

    public void verifyCode(String phoneNumber, String code) {
        String savedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phoneNumber);

        if (savedCode == null) {
            throw new SmsException(SmsErrorCode.EXPIRED_VERIFICATION_CODE);
        }
        if (!savedCode.equals(code)) {
            throw new SmsException(SmsErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(CODE_PREFIX + phoneNumber);
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + phoneNumber, "true", VERIFIED_TTL);
    }

    public void checkVerified(String phoneNumber) {
        String verified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber);
        if (!"true".equals(verified)) {
            throw new SmsException(SmsErrorCode.PHONE_NOT_VERIFIED);
        }
    }

    public void clearVerified(String phoneNumber) {
        redisTemplate.delete(VERIFIED_PREFIX + phoneNumber);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
