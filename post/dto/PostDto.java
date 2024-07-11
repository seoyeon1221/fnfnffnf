package board.post.dto;

import board.member.entity.Member;
import board.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class PostDto {
    @Getter
    @AllArgsConstructor
    public static class Post{
        private long memberId;

        private String title;

        private String body;

    }


    public static class Patch{
        @Getter
        private long postId;
        private board.post.entity.Post.QuestionStatus questionStatus;

        public void setPostId(long postId) {
            this.postId = postId;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private long postId;
        private long memberId;
        private board.post.entity.Post.QuestionStatus questionStatus;
        private board.post.entity.Post.PostStatus postStatus;
        private LocalDateTime createdAt;

        public void setMember(Member member) {
            this.memberId = member.getMemberId();
        }
        public void setPost(board.post.entity.Post post) {
            this.postId = post.getPostId();
        }

    }
}
