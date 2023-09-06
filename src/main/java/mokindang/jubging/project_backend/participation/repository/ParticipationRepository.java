package mokindang.jubging.project_backend.participation.repository;

import mokindang.jubging.project_backend.participation.domain.Participation;
import mokindang.jubging.project_backend.participation.domain.ParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {


    void deleteAllByRecruitmentBoardId(final Long boardId);
}
