package nextstep.subway.auth.application;

import nextstep.subway.auth.domain.InvalidTokenException;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.auth.infrastructure.JwtTokenProvider;
import nextstep.subway.member.domain.Member;
import nextstep.subway.member.domain.MemberRepository;
import nextstep.subway.member.domain.NotFoundMemberException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse login(TokenRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(AuthorizationException::new);
        member.checkPassword(request.getPassword());

        String token = jwtTokenProvider.createToken(request.getEmail());
        return new TokenResponse(token);
    }

    public LoginMember findMemberByTokenElseThrow(String credentials) {
        if (!jwtTokenProvider.validateToken(credentials)) {
            throw new InvalidTokenException();
        }

        return createLoginMemberFromToken(credentials);
    }

    public LoginMember findMemberByTokenElseDefault(String credentials) {
        if (!jwtTokenProvider.validateToken(credentials)) {
            return LoginMember.NO_LOGIN;
        }

        return createLoginMemberFromToken(credentials);
    }

    private LoginMember createLoginMemberFromToken(String credentials) {
        String email = jwtTokenProvider.getPayload(credentials);
        Member member = memberRepository.findByEmail(email)
                                        .orElseThrow(NotFoundMemberException::new);
        return new LoginMember(member.getId(), member.getEmail(), member.getAge());
    }
}
