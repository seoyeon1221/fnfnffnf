package board.post.controller;


import board.dto.SingleResponseDto;
import board.exception.BusinessLogicException;
import board.exception.ExceptionCode;
import board.member.entity.Member;
import board.member.service.MemberService;
import board.post.Service.PostService;
import board.post.dto.PostDto;
import board.post.entity.Post;
import board.post.mapper.PostMapper;
import board.utils.UriCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;


@RestController
@RequestMapping("/v11/posts")
@Validated
@Slf4j
public class PostController {

    private final static String POST_DEFAULT_URL = "/v11/posts";
    private final PostService postService;
    private final PostMapper postMapper;
    private final MemberService memberService;

    public PostController(PostService postService, PostMapper postMapper, MemberService memberService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.memberService = memberService;
    }

    // 0순위 질문 등록하는 기능 -> postPost()
    //- 질문은 회원(고객)만 등록할 수 있다. -->(~~만 구현 가능하다)--> 인증인가(시큐리티사용) 나중 구현
    //- 질문 등록시 등록 날짜가 생성 되어야 한다. -->(엔티티에서 생성 extends Auditable)
    //- 질문은 질문의 상태 값이 필요하다 --> (post.questionStatus)
    //- 질문 제목과 내용은 필수입력 사항이다.--> (NotBlank)
    //- 질문은 비밀글과 공개글 둘 중에 하나로 설정되어야 한다. --> (post.postStatus)
    @PostMapping
    // 벨리드(검증), 리퀘스트바디(제이슨타입받고) PostDto.Post(dto로 매핑한걸 변수 requeestBody받음)
    public ResponseEntity postPost(@Valid @RequestBody PostDto.Post requestBody) {
        // 포스트등록함(매퍼로 연결해서)

        Member member = new Member();
        member.setMemberId(requestBody.getMemberId());
        Post post = postMapper.postPostDtoToPost(requestBody);
        post.setMember(member);
        // 멤버서비스의 createPost()를 통해 회원만 등록할 수 있도록 검증거침
        Post createdPost = postService.createPost(post);
        // 만든 포스트를 Uri에다 등록함
        URI location = UriCreator.createUri(POST_DEFAULT_URL, createdPost.getPostId());

        // 만든 포스트가 등록된 uri를 응답엔티티로 반환함
        return ResponseEntity.created(location).build();
    }


    // 0순위 질문 수정하는 기능 (patch)
    //- 등록된 질문의 제목과 내용은 질문을 등록한 회원(고객)만 수정할 수 있어야 한다.-->(맨 후순위시큐리티(인증,인가))
    //- 회원이 등록한 질문을 비밀글로 변경할 경우, QUESTION_SECRET 상태로 수정되어야 한다. --> updatePost안에 있음
    //- 질문 상태 중에서 QUESTION_ANSWERED 로의 변경은 관리자만 가능하다.-->(어드민은 멤버?)
    //- 회원이 등록한 질문을 회원이 삭제할 경우, QUESTION_DELETED 상태로 수정되어야 한다.
    // --> 딜리트는 따로 아닌가? 맞음 딜리트에서 상태만 변환되도록(패치처럼)
    //- 답변 완료된 질문은 수정할 수 없다. --> 코멘트로 연결이 되어야함(코멘트 만들고 후순위) 이것도 cancleOrder처럼 따로 구현
    @PatchMapping("/{post-id}")
    public ResponseEntity patchPost(
            @PathVariable("post-id") @Positive long postId,
            @Valid @RequestBody PostDto.Patch requestBody) {
        requestBody.setPostId(postId);

        Post post =
                // updatePost안에 포스트 상태 수정 담음
                postService.updatePost(postMapper.postPatchToPost(requestBody));
        return new ResponseEntity<>(
                new SingleResponseDto<>(postMapper.postToPostResponse(post)),
                HttpStatus.OK);

    }

    @DeleteMapping("/{post-id}")
    // 회원이 등록한 질문을 회원이 삭제할 경우, QUESTION_DELETED 상태로 수정되어야 한다.
    //
    //
    //- 1건의 질문은 회원(고객)만 삭제할 수 있다. --> 인증인가(후순위)
    //- 1건의 질문 삭제는 질문을 등록한 회원만 가능하다. -->인증인가(후순위)
    //- 질문 삭제 시, 테이블에서 row 자체가 삭제되는 것이 아니라
    // 질문 상태 값이(QUESTION_DELETE)으로 변경되어야 한다.--> deletePost에 있음
    //- 이미 삭제 상태인 질문은 삭제할 수 없다.  --> 서비스에 ensurePostNotDeleted() 구현
    public ResponseEntity deletePost(
            @PathVariable("post-id") @Positive long postId) {
        postService.deletePost(postId);


        // 해당 ResponseEntity<>는 클라이언트(프론트딴)로 보내는거기 때문에
        // 상태코드가 담겨있는 HttpStatus.NO_CONTENT가 필요
        // 근데 응답객체이름이 엔티티일뿐, 엔티티가 아니다.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    //
    // - 1건의 특정 질문은 회원(고객)과 관리자 모두 조회할 수 있다. --> 해당포스트 있는지 검증한 메서드사용
    // - 비밀글 상태인 질문은 질문을 등록한 회원(고객)과 관리자만 조회할 수 있다. (인증인가 후순위)
    // - 1건의 질문 조회 시, 해당 질문에 대한 답변이 존재한다면 답변도 함께 조회되어야 한다.(코멘트 만들고 나서)
    // - 이미 삭제 상태인 질문은 조회할 수 없다. --> 이것도 서비스에 findPost로 구현
    @GetMapping("/{post-id}")
    public ResponseEntity getPost(@PathVariable("post-id") @Positive long postId) {
        Post post = postService.findPost(postId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(postMapper.postToPostResponse(post)),
                HttpStatus.OK
        );


    }



//- 여러 건의 질문 목록은 회원(고객)과 관리자 모두 조회할 수 있다. --> getposts
//            - 삭제 상태가 아닌 질문만 조회할 수 있다. --> getpost랑똑같음
//- 여러 건의 질문 목록에서 각각의 질문에 답변이 존재한다면 답변도 함께 조회할 수 있어야 한다. --> 이거는 코멘트 만들고 후순위
//            - 여러 건의 질문 목록은 페이지네이션 처리가 되어 일정 건수 만큼의 데이터만 조회할 수 있어야 한다. --> 후순위
//- 여러 건의 질문 목록은 아래의 조건으로 정렬해서 조회할 수 있어야 한다. --> sort?
//    ㄴ 최신글 순으로
//    ㄴ 오래된 글 순으로
//    ㄴ 좋아요가 많은 순으로(좋아요 구현 이후 적용)
//    ㄴ 좋아요가 적은 순으로(좋아요 구현 이후 적용)
//    ㄴ 조회수가 많은 순으로(조회수 구현 이후 적용)
//    ㄴ 조회수가 적은 순으로(조회수 구현 이후 적용)
    @GetMapping()
    public ResponseEntity getPosts(@Positive @RequestParam int page,
                                   @Positive @RequestParam int size) {
        Page<Post> pagePosts = postService.findPosts(page-1, size, 0);
        return new ResponseEntity(pagePosts.getContent().size(), HttpStatus.ACCEPTED);
    }


}
