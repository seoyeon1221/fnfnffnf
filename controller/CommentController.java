package board.comment.controller;

import board.comment.Service.CommentService;
import board.comment.dto.CommentDto;
import board.comment.entity.Comment;
import board.comment.mapper.CommentMapper;
import board.member.entity.Member;
import board.post.Service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v11.comments")
@Validated
@Slf4j
public class CommentController {

    private final static String COMMENT_DEFAULT_URI = "/V11/comments";
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    // 코멘트에 어떤거를 주입받아야할지 생각해보십시다
    // 코멘트 컨트롤에서는 어떤게 필요하죠?
    // 우선 멤버를 일단 안쓰니까 멤버서비스는 필요없고 포스트랑으 연관있으니까 포스트는 주입받아야할듯!
    private final PostService postService;

    public CommentController(CommentService commentService, CommentMapper commentMapper, PostService postService) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
        this.postService = postService;
    }


// 답변등록 1순위 --> post 하려면 dto만들기
//- 답변은 관리자만 등록할 수 있다.(인증인가)
//- 답변은 관리자가 한 건만 등록할 수 있다. (인증인가)
//- 답변 등록시 답변 등록 날짜가 생성 되어야 한다. (entity에서 완료)
//            - 답변이 등록되면 , 질문의 상태 값은 QUESTION_ANSWERED로 변경되어야 한다. ( 등록수정 삭제조회--> 우선 컨트롤러에서 포스트를 보내야함! 서비스에서 어떻게 구현할까는 후에!)
//- 답변 내용은 필수입력 사항이다. (널어블 완료)
//            - 답변의 경우 질문이 비밀글이면 답변도 비밀글이 되어야 하고, 질문이 공개글이면 답변도 공개글이 되어야 한다 (서비스 + 비밀글 되면 본인 관리자밖에 못보니까 인증인가  후순위 )
    @PostMapping
    public ResponseEntity commentPost(@Valid @RequestBody CommentDto.Post requestBody){

        Comment comment = commentMapper.commentPostToComment(requestBody);
        // 메퍼에서 하는 일인데 우선 기능 구현이 먼저라 여기서 멤버매핑시켜줌
        Member member = new Member();
        member.setMemberId(requestBody.getMemberId());
        comment.setMember(member);

        Comment createdComment = commentService.createComment(comment);


    }

}
