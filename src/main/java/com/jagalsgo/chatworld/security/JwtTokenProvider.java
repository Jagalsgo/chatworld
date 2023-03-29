package com.jagalsgo.chatworld.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 토큰 비밀키
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 1시간
    @Value("${jwt.access-token-validity-in-minutes}")
    private long tokenValidTime;

    // refresh 토큰 유효시간 7일
    @Value("${jwt.refresh-token-validity-in-minutes}")
    private long refreshTokenValidTime;

    private static final String TOKEN_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;

    // secretKey Base64로 인코딩
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public TokenInfo createToken(String userId, String role){
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setExpiration(new Date(now.getTime() + tokenValidTime * 60000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime * 60000L))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // JWT 인증 정보 조회
    public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserId(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserId(String token){
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Token is invalid or expired");
        }
    }

    public String getUserIdFromRefreshToken(String refreshToken){
        try {
            return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().getSubject();
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Refresh token is invalid or expired");
        }
    }

    public String getRoleFromRefreshToken(String refreshToken){
        try {
            return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().get("role");
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException("Refresh token is invalid or expired");
        }
    }

    // Request 의 Header 에서 token 값 가져옴
    public String resolveToken(HttpServletRequest request){

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    // 토큰 유효성 만료시간 확인
    public boolean validateToken(String jwtToken){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

}
