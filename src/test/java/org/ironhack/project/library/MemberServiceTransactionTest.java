package org.ironhack.project.library;

import jakarta.persistence.EntityManager;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;
import org.ironhack.project.library.entity.BorrowRecord;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.exception.BookUnavailableException;
import org.ironhack.project.library.exception.BorrowLimitExceededException;
import org.ironhack.project.library.repository.AuthorRepository;
import org.ironhack.project.library.repository.BookRepository;
import org.ironhack.project.library.repository.BorrowRecordRepository;
import org.ironhack.project.library.repository.MemberRepository;
import org.ironhack.project.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTransactionTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    private Author author;
    private Member member;
    private Book targetBook;

    @BeforeEach
    void setUp() {
        if (transactionManager instanceof AbstractPlatformTransactionManager aptm) {
            aptm.setNestedTransactionAllowed(true);
        }

        author = new Author();
        author.setName("J.K. Rowling");
        author = authorRepository.save(author);

        member = new Member();
        member.setName("Ali Aliyev");
        member.setEmail("ali@example.com");
        member = memberRepository.save(member);

        targetBook = new Book();
        targetBook.setTitle("Harry Potter");
        targetBook.setIsbn("ISBN-0001");
        targetBook.setAuthor(author);
        targetBook = bookRepository.save(targetBook);
    }

    @Test
    void borrowBook_whenBookAlreadyBorrowed_shouldRollbackAndKeepDataConsistent() {

        Member firstBorrower = new Member();
        firstBorrower.setName("Vusal Vusalov");
        firstBorrower.setEmail("vusal@example.com");
        firstBorrower = memberRepository.save(firstBorrower);

        memberService.borrowBook(firstBorrower.getId(), targetBook.getId());

        assertThrows(BookUnavailableException.class,
                () -> memberService.borrowBook(member.getId(), targetBook.getId()));

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertTrue(reloadedMember.getBorrowedBooks().isEmpty(),
                "The book was incorrectly added to the second member because rollback did not occur");

        Book reloadedBook = bookRepository.findById(targetBook.getId()).orElseThrow();
        assertEquals(1, reloadedBook.getMembers().size(),
                "An extra inconsistent row was written to the member_books table");
    }

    @Test
    void borrowBook_whenLimitExceeded_shouldRollbackAndNotAddExtraBook() {

        for (int i = 1; i <= 5; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setIsbn("ISBN-" + i);
            book.setAuthor(author);
            book = bookRepository.save(book);

            memberService.borrowBook(member.getId(), book.getId());
        }

        Book sixthBook = new Book();
        sixthBook.setTitle("Book 6");
        sixthBook.setIsbn("ISBN-6");
        sixthBook.setAuthor(author);
        sixthBook = bookRepository.save(sixthBook);

        Long sixthBookId = sixthBook.getId();

        assertThrows(BorrowLimitExceededException.class,
                () -> memberService.borrowBook(member.getId(), sixthBookId));

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertEquals(5, reloadedMember.getBorrowedBooks().size(),
                "An extra book was wrongly added despite exceeding the limit - rollback did not work");

        Book reloadedSixthBook = bookRepository.findById(sixthBookId).orElseThrow();
        assertTrue(reloadedSixthBook.getMembers().isEmpty(),
                "The 6th book was associated with the member despite the failed operation");
    }

    @Test
    void returnBook_whenBookNotBorrowedByMember_shouldRollbackAndNotChangeState() {

        assertThrows(RuntimeException.class,
                () -> memberService.returnBook(member.getId(), targetBook.getId()));

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertTrue(reloadedMember.getBorrowedBooks().isEmpty());

        Book reloadedBook = bookRepository.findById(targetBook.getId()).orElseThrow();
        assertTrue(reloadedBook.getMembers().isEmpty());
    }

    @Test
    void borrowBook_happyPath_shouldPersistBothSidesOfRelation() {

        var response = memberService.borrowBook(member.getId(), targetBook.getId());

        assertNotNull(response);

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        Book reloadedBook = bookRepository.findById(targetBook.getId()).orElseThrow();

        assertEquals(1, reloadedMember.getBorrowedBooks().size());
        assertEquals(1, reloadedBook.getMembers().size());
    }

    @Test
    void borrowBook_whenLimitExceeded_shouldRollbackBothMemberBooksAndBorrowRecords() {

        for (int i = 1; i <= 5; i++) {
            Book book = new Book();
            book.setTitle("Book " + i);
            book.setIsbn("ISBN-" + i);
            book.setAuthor(author);
            book = bookRepository.save(book);

            memberService.borrowBook(member.getId(), book.getId());
        }

        long borrowRecordCountBeforeFailure = borrowRecordRepository.count();

        Book sixthBook = new Book();
        sixthBook.setTitle("Book 6");
        sixthBook.setIsbn("ISBN-6");
        sixthBook.setAuthor(author);
        sixthBook = bookRepository.save(sixthBook);

        Long sixthBookId = sixthBook.getId();

        assertThrows(BorrowLimitExceededException.class,
                () -> memberService.borrowBook(member.getId(), sixthBookId));

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertEquals(5, reloadedMember.getBorrowedBooks().size(),
                "An extra row was written to member_books after a failed operation - rollback did not work");

        long borrowRecordCountAfterFailure = borrowRecordRepository.count();
        assertEquals(borrowRecordCountBeforeFailure, borrowRecordCountAfterFailure,
                "A row remained written to borrow_records after a failed operation - rollback is incomplete");

        boolean hasRecordForSixthBook = borrowRecordRepository.findAll().stream()
                .anyMatch(r -> r.getBook().getId().equals(sixthBookId));
        assertFalse(hasRecordForSixthBook,
                "A record remained in borrow_records for the 6th book - multi-table rollback is not proven");
    }

    @Test
    void borrowBook_whenBookAlreadyBorrowed_shouldRollbackBorrowRecordToo() {

        Member firstBorrower = new Member();
        firstBorrower.setName("Vusal Vusalov");
        firstBorrower.setEmail("vusal@example.com");
        firstBorrower = memberRepository.save(firstBorrower);

        memberService.borrowBook(firstBorrower.getId(), targetBook.getId());

        long borrowRecordCountBeforeFailure = borrowRecordRepository.count();

        assertThrows(BookUnavailableException.class,
                () -> memberService.borrowBook(member.getId(), targetBook.getId()));

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertTrue(reloadedMember.getBorrowedBooks().isEmpty());

        long borrowRecordCountAfterFailure = borrowRecordRepository.count();
        assertEquals(borrowRecordCountBeforeFailure, borrowRecordCountAfterFailure,
                "The second (failed) borrow attempt remained in borrow_records - rollback is incomplete");
    }

    @Test
    void borrowBook_whenSecondWriteFailsAfterFirstWriteWasFlushed_shouldRollbackFirstWriteToo() {

        TransactionTemplate nestedTransactionTemplate = new TransactionTemplate(transactionManager);
        nestedTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

        assertThrows(RuntimeException.class, () ->
                nestedTransactionTemplate.execute(status -> {

                    member.borrowBook(targetBook);
                    memberRepository.saveAndFlush(member);

                    assertEquals(1, memberRepository.findById(member.getId()).orElseThrow()
                            .getBorrowedBooks().size(), "First write is not visible after flush");

                    BorrowRecord invalidRecord = new BorrowRecord();
                    invalidRecord.setMember(member);
                    invalidRecord.setBook(null);
                    invalidRecord.setBorrowedAt(Instant.now());

                    borrowRecordRepository.saveAndFlush(invalidRecord);

                    return null;
                })
        );

        entityManager.clear();

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        assertTrue(reloadedMember.getBorrowedBooks().isEmpty(),
                "Even though the first write was flushed, all writes up to the savepoint should have been rolled back due to the failure of the second write, but they were not");

        long recordCount = borrowRecordRepository.count();
        assertEquals(0, recordCount,
                "No rows should remain in the borrow_records table");
    }
}