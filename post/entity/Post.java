package board.post.entity;


import board.audit.Auditable;
import board.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


// 포스트 등록날짜 필요 -->Auditable(날짜가 포함된 class extends하고 있음)
// 포스트는 고객만 등록가능 --> 서비스 계층에서 검증메서드 만들고 아니면 예외로 던지기?// ~만 조건은 인증인가이므로 시큐리티에셔!
// 포스트 제목과 내용은 필수 --> @NotBlank로 널방지
// 포스트는 비밀글과 공개글 중 둘 중 하나의 상태로 설정 되어야함(PUBLIC, SECRET) -- PostStatus만듬
// 포스트의 상태 값 필요 --- QuestionStatus만듬
// QUESTION_REGISTERED_질문 등록 상태(포스트 등록시 초기값), QUESTION_ANSWERED_답변 완료 상태
// QUESTION_DELETED_질문 삭제상태, QUESTION_DEACTIVED_질문비활성화 상태: 회원 탈퇴 시 질문 비활성화
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Enumerated(EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus = PostStatus.PostStatus_PUBLIC;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    // 질문등록 상태가 글게시판에 보이는거
    public enum QuestionStatus{
        QUESTION_REGISTERED( 1, "질문 등록 상태"),
        QUESTION_ANSWERED( 2, "답변 완료 상태"),
        QUESTION_DELETED(3, "질문 삭제상태"),
        QUESTION_DEACTIVATED (4, "질문 비활성화 상태");

        @Getter
        private int stepNumber;

        @Getter
        private String stepDescription;


        QuestionStatus(int stepNumber, String stepDescription) {
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }

    }

    public enum PostStatus{
        PostStatus_PUBLIC(1, "공개"),
        PostStatus_SECRET(2, "비공개");

        @Getter
        private int stepNumber;

        @Getter
        private String stepDescription;

        PostStatus(int stepNumber, String stepDescription){
            this.stepNumber = stepNumber;
            this.stepDescription = stepDescription;
        }
    }



}
