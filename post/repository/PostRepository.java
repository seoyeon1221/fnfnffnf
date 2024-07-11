package board.post.repository;

import board.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByQuestionStatusNot(Post.QuestionStatus questionStatus, Pageable pageable);
}
