package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.mapper.MemberMapper;
import org.ironhack.project.library.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Page<MemberResponse> getAllMembers(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return memberRepository.findAll(pageable)
                .map(MemberMapper::toResponse);
    }

    public MemberResponse getMemberById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        return MemberMapper.toResponse(member);
    }

    public MemberResponse createMember(MemberRequest request) {

        Member member = MemberMapper.toEntity(request);

        Member savedMember = memberRepository.save(member);

        return MemberMapper.toResponse(savedMember);
    }

    public MemberResponse updateMember(Long id, MemberRequest request) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        member.setName(request.getName());
        member.setEmail(request.getEmail());

        Member updatedMember = memberRepository.save(member);

        return MemberMapper.toResponse(updatedMember);
    }

    public void deleteMember(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        memberRepository.delete(member);
    }
}