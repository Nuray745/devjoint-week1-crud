package org.ironhack.project.library.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(memberService.getAllMembers(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberResponse>> searchMembersByName(@RequestParam String name) {
        return ResponseEntity.ok(memberService.searchMembersByName(name));
    }

    @GetMapping("/min-borrowed")
    public ResponseEntity<List<MemberResponse>> getMembersWithAtLeastBorrowedBooks(
            @RequestParam int minBooks) {
        return ResponseEntity.ok(memberService.getMembersWithAtLeastBorrowedBooks(minBooks));
    }

    @GetMapping("/borrowed/{bookId}")
    public ResponseEntity<List<MemberResponse>> getMembersByBorrowedBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.getMembersByBorrowedBook(bookId));
    }

    @GetMapping("/no-borrowed-books")
    public ResponseEntity<List<MemberResponse>> getMembersWithNoBorrowedBooks() {
        return ResponseEntity.ok(memberService.getMembersWithNoBorrowedBooks());
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<MemberResponse> borrowBook(
            @PathVariable Long memberId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.borrowBook(memberId, bookId));
    }


    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<MemberResponse> returnBook(
            @PathVariable Long memberId,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.returnBook(memberId, bookId));
    }
}