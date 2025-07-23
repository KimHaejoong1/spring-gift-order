package gift.service;

import gift.dto.*;
import gift.entity.Member;
import gift.exception.LoginFailedException;
import gift.repository.MemberRepository;
import gift.security.JwtTokenProvider;
import gift.security.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthTokenResponseDTO register(MemberRequestDTO request) {
        if (memberRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = new Member(request.email(), encodedPassword, request.role());
        Member savedMember = memberRepository.save(member);

        String token = jwtTokenProvider.generateJwtToken(savedMember);

        return new AuthTokenResponseDTO(token);
    }

    public AuthTokenResponseDTO login(MemberLoginRequestDTO request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new LoginFailedException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new LoginFailedException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.generateJwtToken(member);

        return new AuthTokenResponseDTO(token);
    }

    public List<MemberResponseDTO> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> new MemberResponseDTO(
                        member.getId(),
                        member.getEmail(),
                        member.getRole()
                ))
                .collect(Collectors.toList());
    }

    public MemberResponseDTO getMemberById(Integer id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new MemberResponseDTO(
                member.getId(),
                member.getEmail(),
                member.getRole()
        );
    }

    public Member getMemberEntityById(Integer id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public AuthenticatedMemberDTO getMemberByEmail(String email) {
        Member member = findByEmail(email);
        return new AuthenticatedMemberDTO(
                member.getId(),
                member.getEmail(),
                member.getRole()
        );
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 Email의 사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public MemberResponseDTO update(Integer id, MemberRequestDTO request) {
        if(!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member updated = new Member(
                id,
                request.email(),
                encodedPassword,
                request.role()
        );

        Member updatedMember = memberRepository.save(updated);

        return new MemberResponseDTO(
                updatedMember.getId(),
                updatedMember.getEmail(),
                updatedMember.getRole()
        );
    }

    @Transactional
    public void delete(Integer id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        memberRepository.deleteById(id);
    }
}
