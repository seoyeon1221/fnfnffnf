package board.comment.Service;

import board.comment.entity.Comment;
import board.post.Service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class CommentService {
 private final PostService postService;

    public CommentService(PostService postService) {
        this.postService = postService;
    }

    @Transactional
    public Comment createComment(Comment comment) {
        // 존재하는 글에 답글 적어야하니까 우선 포스트가 존재하는지 확인
        // -> 근데 이거 포스트 서비스에 구현해놈 개꿀딱 그래서 이거 쓰기위해 의존성주입 ㄱ
        // 1차 거름 : 해당 포스트 존재하는지, 2차 거름 : 이미 지워진 포스트
        postService.findPost(comment.getPost().getPostId());
    }

}
