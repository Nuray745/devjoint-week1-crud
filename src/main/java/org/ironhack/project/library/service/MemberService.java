package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.MemberRequest;
import org.ironhack.project.library.dto.response.MemberResponse;
import org.ironhack.project.library.entity.Book;
import org.ironhack.project.library.entity.BorrowRecord;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.exception.BookUnavailableException;
import org.ironhack.project.library.exception.BorrowLimitExceededException;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.mapper.MemberMapper;
import org.ironhack.project.library.repository.BookRepository;
import org.ironhack.project.library.repository.BorrowRecordRepository;
import org.ironhack.project.library.repository.MemberRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final int MAX_BOOKS_PER_MEMBER = 5;

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final NotificationService notificationService;

    public Page<MemberResponse> getAllMembers(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return memberRepository.findAll(pageable).map(MemberMapper::toResponse);
    }

    public MemberResponse getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        return MemberMapper.toResponse(member);
    }

    public List<MemberResponse> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name).stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    public List<MemberResponse> getMembersWithAtLeastBorrowedBooks(int minBooks) {
        return memberRepository.findMembersWithAtLeastBorrowedBooks(minBooks).stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    public List<MemberResponse> getMembersByBorrowedBook(Long bookId) {
        return memberRepository.findMembersByBorrowedBookId(bookId).stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    public List<MemberResponse> getMembersWithNoBorrowedBooks() {
        return memberRepository.findMembersWithNoBorrowedBooks().stream()
                .map(MemberMapper::toResponse)
                .toList();
    }

    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        Member member = MemberMapper.toEntity(request);
        Member savedMember = memberRepository.save(member);
        return MemberMapper.toResponse(savedMember);
    }

    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        member.setName(request.getName());
        member.setEmail(request.getEmail());
        Member updatedMember = memberRepository.save(member);
        return MemberMapper.toResponse(updatedMember);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        memberRepository.delete(member);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "books", key = "#bookId"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public MemberResponse borrowBook(Long memberId, Long bookId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.getMembers().isEmpty()) {
            throw new BookUnavailableException("Book is already borrowed by another member.");
        }

        if (member.getBorrowedBooks().size() >= MAX_BOOKS_PER_MEMBER) {
            throw new BorrowLimitExceededException(
                    "Member has reached the maximum borrow limit of " + MAX_BOOKS_PER_MEMBER + " books.");
        }

        member.borrowBook(book);
        Member updatedMember = memberRepository.save(member);

        BorrowRecord record = new BorrowRecord();
        record.setMember(member);
        record.setBook(book);
        record.setBorrowedAt(Instant.now());
        borrowRecordRepository.save(record);

        notificationService.sendBorrowConfirmation(member.getName(), book.getTitle());

        return MemberMapper.toResponse(updatedMember);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "books", key = "#bookId"),
            @CacheEvict(value = "books", allEntries = true)
    })
    public MemberResponse returnBook(Long memberId, Long bookId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!member.getBorrowedBooks().contains(book)) {
            throw new ResourceNotFoundException("This book is not currently borrowed by this member.");
        }

        member.returnBook(book);
        Member updatedMember = memberRepository.save(member);

        BorrowRecord record = borrowRecordRepository
                .findFirstByMemberIdAndBookIdAndReturnedAtIsNullOrderByBorrowedAtDesc(memberId, bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Active borrow record not found"));
        record.setReturnedAt(Instant.now());
        borrowRecordRepository.save(record);

        notificationService.sendReturnConfirmation(member.getName(), book.getTitle());

        return MemberMapper.toResponse(updatedMember);
    }
}