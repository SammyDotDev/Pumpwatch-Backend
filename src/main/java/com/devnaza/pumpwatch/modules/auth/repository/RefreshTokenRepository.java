package com.devnaza.pumpwatch.modules.auth.repository;

import com.devnaza.pumpwatch.modules.auth.model.RefreshToken;
import com.devnaza.pumpwatch.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,
                                                                   Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(UUID user_id);

    void deleteAllByUser(User user);

    void deleteAllByExpiresAtBefore(Instant now);
}
