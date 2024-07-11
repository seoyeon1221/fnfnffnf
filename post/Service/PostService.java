package board.post.Service;


import board.exception.BusinessLogicException;
import board.exception.ExceptionCode;
import board.member.service.MemberService;
import board.post.entity.Post;
import board.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static board.post.entity.Post.QuestionStatus.QUESTION_DELETED;


// 멤버가 존재하는지
//
@Transactional
@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberService memberService;

    public PostService(PostRepository postRepository, MemberService memberService) {
        this.postRepository = postRepository;
        this.memberService = memberService;
    }


    public Post createPost(Post post) {
        // 회원이 존재하는지 확인
        verifyPost(post);
        // 여기서 검증을 거친 포스트를 저장함 맞나?
        return postRepository.save(post);

    }

    // @Transactional 스프링의 트랜잭션 관리기능을 사용하기 위한 기능
    // propagation = Propagation.REQUIRED: 트랜잭션 전파 옵션을 설정합니다.
    // 트랜잭션 전파 옵션은 트랜잭션 경게에 한 동작정의
    // REQUIRED: 현재 트랜잭션이 있다면 해당 트랜잭션을 사용하고, 없다면 새로운 트랜잭션을 시작합니다.(데이터의 무결성)
    // SUPPORTS: 현재 트랜잭션이 있다면 해당 트랜잭션을 사용하고, 없다면 트랜잭션 없이 실행됩니다.(성능중요경우)
    // MANDATORY: 현재 트랜잭션이 있어야 하며, 없다면 예외가 발생합니다.
    // REQUIRES_NEW: 새로운 트랜잭션을 시작하며, 현재 트랜잭션이 있다면 해당 트랜잭션은 일시 중단됩니다.
    // NOT_SUPPORTED: 트랜잭션 없이 실행되며, 현재 트랜잭션이 있다면 일시 중단됩니다.
    // NEVER: 트랜잭션 없이 실행되며, 현재 트랜잭션이 있다면 예외가 발생합니다.
    // isolation = Isolation.SERIALIZABLE: 트랜잭션의 격리 수준을 설정합니다.
    // Isolation.SERIALIZABLE: 가장 높은 격리 수준으로,
    // 트랜잭션 간에 발생할 수 있는 모든 문제(Dirty Read, Non-Repeatable Read, Phantom Read)를 방지합니다
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Post updatePost(Post post) {
        Post findPost = findVerifiedPost(post.getPostId());

        // findPost에 상태 바꿀 수 있는거를 저장하고 레파지토리에보냄
        Optional.ofNullable(post.getPostStatus())
                .ifPresent(postStatus -> findPost.setPostStatus(postStatus));
        return postRepository.save(findPost);
    }


    // postId를 찾고 해당 포스트를 지우면 QuestionStatus의
    // 상태값은 QUESTION_DELETED로 나오게끔
    // 근데 이미 지운 포스트를 또 지우게 할 수 없기때문에 그걸 검증한 메서드를
    // 우선 해당 포스트 있는지 -> findVerifiedPost
    // 그게있다면 이미 지워진 포스트튼 아닌지-> ensurePostNotDeleted
    // 앞에서 걸린게 없다면 상태가 변경됨 QUESTION_DELETED로
    // 상태 변경된 포스트를 레파지토리에 저장
    public Post deletePost(long postId) {
        Post findPost = findVerifiedPost(postId);
        ensurePostNotDeleted(findPost);
        findPost.setQuestionStatus(QUESTION_DELETED);
        postRepository.save(findPost);

        return findPost;
    }

    // 해당 포스트가 있는지 확인하는 메서드
    // 이미 지워진 포스트를 조회할 수 없게끔 구현하는 조건 추가
    // 여기에 우선 findPost 변수에 해당 포스트 있는지 1차 거름
    // 있으면 ensurePostNotDeleted통해 이미 지워진 포스트를
    // 2차로 거를 수 있도록해서 안걸러지고 존재하는 포스트를 반환하게됨 == findPost
    public Post findPost(long postId) {
        Post findPost = findVerifiedPost(postId);
        ensurePostNotDeleted(findPost);
        return findPost;
    }

    public Page<Post> findPosts(int page, int size, long postId) {

        // 서비스에 내가 만든 예외 코드를 통해 삭제되지 않은 포스트를 조회하지 않도록
        // 거른 포스트를 findPost 생성
//        Post findPost = findVerifiedPost(postId);
//        ensurePostNotDeleted(findPost);
//
//        // 리스트를 만들어 왜냐 거른 포스트들이 계속 담기는 리스트를 만드는거야(검증 끝난것들)
//        List<Post> posts = new ArrayList<>();
//        posts.add(findPost);

        // ascending() 오래된 글부터 최신순으로 정렬하는 메서드를
        // 통해 검증 끝난것들이 역정렬되도록 큰틀잡아
        // postRepository.findAll()을 통해 역정렬한 Page<Post> 객체를 얻어,
        Page<Post> findPosts = postRepository.findByQuestionStatusNot(QUESTION_DELETED, PageRequest.of(
                        page, size, Sort.by("postId").descending()
                ))    ;
//        Page<Post> posts = postRepository.findAll(PageRequest.of(
//                        page, size, Sort.by("postId").descending()));// 멥으로 순회해서 내가 위에서 거른것만 역정렬에 담을 수 있도록함
                // Page<Post> 객체를 순회하면서 각 Post객체를 새로운 page<Post> 객체에 담아
//                .map(post -> {
//                    // 이때 내가 만든(검증을 거친) findPost일 경우 이 자체를 반환 왜냐 검증 거친거니
//                    if (post.getPostId().equals(findPost.getPostId())) {
//                        return findPost;
//                    } // 근데 그렇지 않을 경우 원래의 Post 객체를 반횐한데 이게 무슨말??
//                    // 뭐냐면
//                    return post;
//                });
        return findPosts;
    }


    @Transactional
    // 패치에서 포스트를 수정할 때 해당 포스트가 있는지 확인해야해서 먼저 포스트 찾는 메서드 생성
    public Post findVerifiedPost(long postId) {
        Optional<Post> optionalPost =
                postRepository.findById(postId);
        Post findPost =
                optionalPost.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
        return findPost;
    }

    // 이미 삭제된 포스트이기 때문에 반환값 필요없이 예외만 던지는거임
    public void ensurePostNotDeleted(Post postId) {
        //만약 삭제상태라면 또 삭제할 수 없고 예외 던지는 메서드 구현
        // postId에서 상태를 가져오고 그게 QUESTION_DELETED랑 같다면
        // 예외 던짐 포스트를 찾을 수 없다고
        if (postId.getQuestionStatus().equals(QUESTION_DELETED)) {
            throw new BusinessLogicException(ExceptionCode.POST_NOT_FOUND);
        }
    }

    private void verifyPost(Post post) {
        // 회원이 존재하는지만 확인 --> 게시글 회원만 써야하니까
        memberService.findVerifiedMember(post.getMember().getMemberId());
    }


}
