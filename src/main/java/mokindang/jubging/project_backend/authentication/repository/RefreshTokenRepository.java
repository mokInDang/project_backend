package mokindang.jubging.project_backend.authentication.repository;

import mokindang.jubging.project_backend.authentication.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberId(Long memberId);

    Optional<RefreshToken> findByToken(String token);

    void deleteAllByMemberId(Long memberId);
}
