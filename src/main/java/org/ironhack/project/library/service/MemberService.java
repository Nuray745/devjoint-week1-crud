package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.mapper.MemberMapper;
import org.ironhack.project.library.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MemberResponse getMemberById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        return MemberMapper.toResponse(member);
    }

    public MemberResponse createMember(MemberRequest request) {

        Member member = MemberMapper.toEntity(request);

        Member savedMember = memberRepository.save(member);

        return MemberMapper.toResponse(savedMember);
    }

    public MemberResponse updateMember(Long id, MemberRequest request) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setName(request.getName());
        member.setEmail(request.getEmail());

        Member updatedMember = memberRepository.save(member);

        return MemberMapper.toResponse(updatedMember);
    }

    public void deleteMember(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        memberRepository.delete(member);
    }
}