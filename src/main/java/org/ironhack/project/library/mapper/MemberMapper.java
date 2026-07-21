package org.ironhack.project.library.mapper;

import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.entity.Member;

public class MemberMapper {

    public static Member toEntity(MemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        return member;
    }

    public static MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }
}