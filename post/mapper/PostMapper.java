package board.post.mapper;



import board.post.dto.PostDto;
import board.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    // 포스트에 사용
    Post postPostDtoToPost(PostDto.Post requestBody);

    // 패치에 사용
    Post postPatchToPost(PostDto.Patch requestBody);

    // getPost에서 사용예정 1건의 질문조회(0순위)



    PostDto.Response postToPostResponse(Post post);


    // 여러건의 질문 목록 조회하는 기능을 위한 포스트 모음 (0순위)
    // -> Controller의 getPosts에서 사용예정
    List<PostDto.Response> postsToPostResponse(List<Post> posts);






}
