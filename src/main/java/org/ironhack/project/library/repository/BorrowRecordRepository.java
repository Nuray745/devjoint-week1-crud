package org.ironhack.project.library.repository;

import org.ironhack.project.library.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findFirstByMemberIdAndBookIdAndReturnedAtIsNullOrderByBorrowedAtDesc(
            Long memberId, Long bookId);
}