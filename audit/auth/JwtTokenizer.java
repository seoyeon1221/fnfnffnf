package board.audit.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtTokenizer {
    // 메서드는  Plain Text 형태인 Secret Key의
    // byte[]를 Base64 형식의 문자열로 인코딩해줍니다.
    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    //generateAccessToken() 인증된 사용자에게 jwt를 최초로 발급해주기위한 JWT생성 메서드
    public String generateAccessToken(Map<String, Objects> claims,
                                      String subject,
                                      Date expiration,

                                      String base64EncodedSecurityKey) {
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecurityKey);

        return Jwts.builder()
                // JWT에 포함시킬 Custom Claims를 추가함
                // Custom Claims에는 주로 인증된 사용자와 관련된 정보 추가
                // Claims에는 사용자는 토큰이기 때문에(AccessToken에 있는 클레임이고 이게 페이로드 안에 있음)
                // 근데 시그니처안에 페이로드와 헤더기 담겨있어서 토큰으로 비교가 가능한거.
                // 즉, 토큰에는 민감한 정보인 패스워드는 없고 패스워드가 맞는지 검증을 통한 정보가 담겨있음
                // 또한 claims는 로그인을 한다음에 호출됨
                // 왜냐 로그인을 한 후 정보가 담긴 JWT 토큰이 생김
                // 이 토큰으로 결제하거나 뭐할 때 권한이 맞는지 확인(비교)함
                // 이 토큰안에 시그니처가 담겨있는데 시그니처 안에 헤더, 페이로드(Claims)가
                // 담겨있으므로 어떤 권한을 요청할 때(인가) 토큰을 확인하는거
                .setClaims(claims)
                // JWT에 대한 제목을 추가합니다.
                .setSubject(subject)
                // setExpiration() JWT의 만료일시 저장, 파라미터 타입은 java.util.Date타입
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(expiration)
                // signWith()에 서명을 위한  Key(java.security.Key) 객체를 설정
                .signWith(key)
                // compact()를 통해 jwt를 생성하고 직렬화로 이렇게 생성된 문자열이 최종적으로 JWT가 됨
                // 여기서 직렬화한다는 것은? 복잡한 데이터구조(객체 배열)를 단순한 문자열로 변환하는 과정
                // 이렇게 변환딘 문자열은 전송이나 저장에 용이해짐
                // 직렬화된 JWT는 전송이나 저장이 용이하며, 나중에 다시 역직렬화(deserialize) 될 수 있음
                // 원래의 데이터 구조로 복원할 수 있다
                .compact();
    }









    // getKeyFromBase64EncodedKey() JWT 서명에 사용할 시크릿키를 생성해줌
    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        // Base64형식으로 인코딩 된 시크릿키를 디코딩한 후, byte array를 반환해줌
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);
        // Keys.hmacShaKeyFor() 메서드는 key byte array를 기반으로 적절한
        // HMAC 알고리즘을 적용한 key(java.security.Key) 객체 생성
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }
}
