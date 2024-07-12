package board.comment.entity;

import board.audit.Auditable;
import board.member.entity.Member;
import board.post.dto.PostDto;
import board.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
// Auditable안에 생성시간 있
public class Comment extends Auditable {
    // entity 게터 세터 기본 생성자 필수
    // 생성전략 및 아이디를 알려줘야함
    // 멤버의 활동 상태를 내부 이넘 클래스로 하고
    // 내부클래스 안에 필드값으로 게터를 붙여서 가져올 수 있도록 함



    // 아이디 자동생성전략
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    // 답변입력 필수사항조건
    @Column(nullable = false)
    private String comment;


    @OneToOne(mappedBy = "comment", cascade = {CascadeType.PERSIST})
    private Member member;


    //  포스트랑 코멘트는 쳐피 일대일 관계 양뱡향으로 이을필요 있나?
    // 그럼 코멘트만 포스트에 종속되도록 등록??
    // 하나의 댓글이 하나의 포스트에 종속 왜냐 질문이 삭제되면 답변은 없어지자나 일반적으로
    @OneToOne(mappedBy = "comment", cascade = { CascadeType.PERSIST, CascadeType.REMOVE})
    private Post post;




}
