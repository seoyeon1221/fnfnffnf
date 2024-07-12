package board.comment.mapper;

import board.comment.dto.CommentDto;
import board.comment.entity.Comment;
import board.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    // dto를 엔티티로 바꿔야해서 엔티티 정보 들어감
    Comment commentPostToComment(CommentDto.Post requestBody);

    Comment commentPatchToComment(CommentDto.Patch requestBody);

    // 엔티티를 디티오로 바꾸는 왜냐 응답할 때 디티오로 나가니까
    // 디티오에 엔티티 정보가 들어오게끔
    CommentDto.Response postToPostResponse(Comment comment);


    }

