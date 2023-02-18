package mokindang.jubging.project_backend.service.board;

import lombok.RequiredArgsConstructor;
import mokindang.jubging.project_backend.domain.board.Board;
import mokindang.jubging.project_backend.domain.member.Member;
import mokindang.jubging.project_backend.repository.board.BoardRepository;
import mokindang.jubging.project_backend.service.board.request.BoardCreateRequest;
import mokindang.jubging.project_backend.service.member.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final Clock clock;
    private final MemberService memberService;
    private final BoardRepository boardRepository;

    @Transactional
    public void write(final Long memberId, final BoardCreateRequest boardCreateRequest) {
        Member member = memberService.findByMemberId(memberId);
        LocalDate now = LocalDate.now(clock);
        Board board = new Board(member, boardCreateRequest.getStartingDate(), boardCreateRequest.getActivityCategory(),
                boardCreateRequest.getTitle(), boardCreateRequest.getContent(), now);
        boardRepository.save(board);
    }
}
