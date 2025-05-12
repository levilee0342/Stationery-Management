package com.project.stationery_be_server.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.project.stationery_be_server.Error.AuthErrorCode;
import com.project.stationery_be_server.Error.NotExistedErrorCode;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.Error.BaseErrorCode;
import com.project.stationery_be_server.repository.InvalidatedTokenRepository;
import com.project.stationery_be_server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtils {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @Value("${jwt.signerKey}")
    @NonFinal
    String SIGNER_KEY;

    @Value("${jwt.valid-duration}")
    @NonFinal
    int VALID_DURATION;

    @Value("${jwt.refreshable-duration}")
    @NonFinal
    int REFRESHABLE_DURATION;

    public String generateToken(String id) {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256).build();
        var user = userRepository
                .findById(id)
                .orElseThrow(() -> new AppException(NotExistedErrorCode.USER_NOT_EXISTED));
        System.out.println("user: " + user.getEmail());
        System.out.println("Role: " + user.getRole());
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(id)
                .issuer("ltn.com")
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", user.getRole().getRoleName()) // scope sẻ đc tự động nhân trong getAuthorities
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWT object", e);
            throw new RuntimeException(e);
        }
    }
    // nạp chồng phương thức verifyToken
    public Boolean verifyToken(String token) throws JOSEException, ParseException {
        return verifyToken(token, false); // Mặc định isRefresh = false
    }
    public Boolean verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY);
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = isRefresh ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        String jid = signedJWT.getJWTClaimsSet().getJWTID();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date())) || invalidatedTokenRepository.existsById(jid))
            throw new AppException(AuthErrorCode.INVALID_TOKEN);
        return true;
    }

}