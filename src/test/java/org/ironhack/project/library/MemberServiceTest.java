package org.ironhack.project.library;


import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.repository.MemberRepository;
import org.ironhack.project.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("Ali Aliyev");
        member.setEmail("ali@example.com");

        memberRequest = new MemberRequest();
        memberRequest.setName("Ali Aliyev");
        memberRequest.setEmail("ali@example.com");
    }

    @Test
    @DisplayName("getAllMembers - Should return paginated members successfully")
    void getAllMembers_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Member> memberPage = new PageImpl<>(List.of(member), pageable, 1);

        when(memberRepository.findAll(any(Pageable.class))).thenReturn(memberPage);

        Page<MemberResponse> response = memberService.getAllMembers(0, 10, "id");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Ali Aliyev");
        verify(memberRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getMemberById - Should return member when ID exists")
    void getMemberById_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberResponse response = memberService.getMemberById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Ali Aliyev");
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getMemberById - Should throw ResourceNotFoundException when member not found")
    void getMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.getMemberById(1L));
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("createMember - Should save and return member response")
    void createMember_Success() {
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponse response = memberService.createMember(memberRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("ali@example.com");
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("updateMember - Should update member details")
    void updateMember_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponse response = memberService.updateMember(1L, memberRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Ali Aliyev");
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("updateMember - Should throw ResourceNotFoundException when member not found")
    void updateMember_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.updateMember(1L, memberRequest));
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("deleteMember - Should delete member successfully")
    void deleteMember_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(memberRepository).delete(member);

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    @DisplayName("deleteMember - Should throw ResourceNotFoundException when member not found")
    void deleteMember_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.deleteMember(1L));
        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, never()).delete(any(Member.class));
    }
}