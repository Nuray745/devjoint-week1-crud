package org.ironhack.project.library.service;

import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;
import org.ironhack.project.library.entity.Member;
import org.ironhack.project.library.exception.BookUnavailableException;
import org.ironhack.project.library.exception.BorrowLimitExceededException;
import org.ironhack.project.library.repository.AuthorRepository;
import org.ironhack.project.library.repository.BookRepository;
import org.ironhack.project.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

    private Author author;
    private Member member;
    private Book targetBook;

    @BeforeEach
    void setUp() {
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
                "Rollback baş vermədiyi üçün ikinci üzvə kitab səhv əlavə olunub");


        Book reloadedBook = bookRepository.findById(targetBook.getId()).orElseThrow();
        assertEquals(1, reloadedBook.getMembers().size(),
                "member_books cədvəlinə əlavə (uyğunsuz) sətir yazılıb");
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
                "Limit aşıldığı halda əlavə kitab yanlışlıqla əlavə olunub - rollback işləməyib");


        Book reloadedSixthBook = bookRepository.findById(sixthBookId).orElseThrow();
        assertTrue(reloadedSixthBook.getMembers().isEmpty(),
                "6-cı kitab uğursuz əməliyyata baxmayaraq member-ə bağlanıb");
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

        MemberService.class.getSimpleName();

        var response = memberService.borrowBook(member.getId(), targetBook.getId());

        assertNotNull(response);

        Member reloadedMember = memberRepository.findById(member.getId()).orElseThrow();
        Book reloadedBook = bookRepository.findById(targetBook.getId()).orElseThrow();

        assertEquals(1, reloadedMember.getBorrowedBooks().size());
        assertEquals(1, reloadedBook.getMembers().size());
    }
}