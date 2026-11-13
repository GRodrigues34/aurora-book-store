package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.exception.userException.GoogleAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    public record GoogleUserInfo(String email, String name, boolean emailVerified) {
    }

    @Value("${api.security.google.client-id:}")
    private String googleClientId;

    public GoogleUserInfo verifyIdToken(String idTokenString) {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new GoogleAuthException("Google Sign-In is not configured on the server");
        }
        if (idTokenString == null || idTokenString.isBlank()) {
            throw new GoogleAuthException("Google credential token is missing");
        }

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new GoogleAuthException("Invalid or expired Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
            String name = (String) payload.get("name");

            if (email == null || email.isBlank() || !emailVerified) {
                throw new GoogleAuthException("Google account email is not verified");
            }

            return new GoogleUserInfo(email, (name != null && !name.isBlank()) ? name : email.split("@")[0], true);
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleAuthException("Failed to verify Google token");
        }
    }
}
