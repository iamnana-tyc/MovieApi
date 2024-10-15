package com.iamnana.movieApi.auth.service;

import com.iamnana.movieApi.auth.entities.RefreshToken;
import com.iamnana.movieApi.auth.entities.User;
import com.iamnana.movieApi.auth.repository.RefreshTokenRepository;
import com.iamnana.movieApi.auth.repository.UserRepository;
import com.iamnana.movieApi.exception.RefreshTokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String username){
        User user =  userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with this "+ username));

        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null){
            // generate a refreshToken
            long refreshTokenValidity = 30 * 1000;
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new RefreshTokenNotFoundException("Refresh Token not found!"));

        if(findRefreshToken.getExpirationTime().compareTo(Instant.now()) < 0){
            refreshTokenRepository.delete(findRefreshToken);
            throw new RuntimeException("Refresh Token expired!.");
        }
        return findRefreshToken;
    }

}
