package org.ironhack.project.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Members", description = "Operations related to library members and book borrowing")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Get all members, paginated")
    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(memberService.getAllMembers(page, size, sortBy));
    }

    @Operation(summary = "Get a member by ID")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMemberById(id));
    }

    @Operation(summary = "Search members by name")
    @GetMapping("/search")
    public ResponseEntity<List<MemberResponse>> searchMembersByName(@RequestParam String name) {
        return ResponseEntity.ok(memberService.searchMembersByName(name));
    }

    @Operation(summary = "Get members who borrowed at least the given number of books")
    @GetMapping("/min-borrowed")
    public ResponseEntity<List<MemberResponse>> getMembersWithAtLeastBorrowedBooks(@RequestParam int minBooks) {
        return ResponseEntity.ok(memberService.getMembersWithAtLeastBorrowedBooks(minBooks));
    }

    @Operation(summary = "Get members who borrowed a specific book")
    @GetMapping("/borrowed/{bookId}")
    public ResponseEntity<List<MemberResponse>> getMembersByBorrowedBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.getMembersByBorrowedBook(bookId));
    }

    @Operation(summary = "Get members with no borrowed books")
    @GetMapping("/no-borrowed-books")
    public ResponseEntity<List<MemberResponse>> getMembersWithNoBorrowedBooks() {
        return ResponseEntity.ok(memberService.getMembersWithNoBorrowedBooks());
    }

    @Operation(summary = "Create a new member")
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.createMember(request));
    }

    @Operation(summary = "Update an existing member")
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    @Operation(summary = "Delete a member")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Borrow a book for a member")
    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<MemberResponse> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.borrowBook(memberId, bookId));
    }

    @Operation(summary = "Return a borrowed book")
    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<MemberResponse> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        return ResponseEntity.ok(memberService.returnBook(memberId, bookId));
    }
}