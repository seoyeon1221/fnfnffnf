package board.comment.dto;

import board.comment.entity.Comment;
import board.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


public class CommentDto {
    @Getter
    @AllArgsConstructor
    public static class Post{

        private long memberId;

        @NotBlank(message = "공백불가")
        private String comment;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch{


        private long commentId;

        // 코멘트에서 수정할 꺼는 답변하나
        private String comment;

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }



    }
    @Getter
    @AllArgsConstructor
    // 응답할 때 뭘 보내나야하나 (담겨있는 정보)
    // 뭐냐면 말그대로 답변
    public static class Response{
        private long commentId;
        private long memberId;
        private long postId;
        private String comment;
        private LocalDateTime createdAt;

        public void setMember(Member member) {
            this.memberId = member.getMemberId();
        }
        public void setPostId(board.post.entity.Post post) {
            this.postId = post.getPostId();
        }

        public void setComment(Comment comment) {
            this.commentId = comment.getCommentId();
        }


    }


}
