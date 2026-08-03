package org.ironhack.project.library.repository;

import org.ironhack.project.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    List<Member> findByNameContainingIgnoreCase(String name);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);


    @Query("SELECT m FROM Member m WHERE SIZE(m.borrowedBooks) >= :minBooks")
    List<Member> findMembersWithAtLeastBorrowedBooks(@Param("minBooks") int minBooks);


    @Query("SELECT m FROM Member m JOIN m.borrowedBooks b WHERE b.id = :bookId")
    List<Member> findMembersByBorrowedBookId(@Param("bookId") Long bookId);


    @Query(value = "SELECT m.* FROM members m " +
            "WHERE m.id NOT IN (SELECT member_id FROM member_books)", nativeQuery = true)
    List<Member> findMembersWithNoBorrowedBooks();
}