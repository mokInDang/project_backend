package mokindang.jubging.project_backend.repository;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findOneByEmail(String email);
}
