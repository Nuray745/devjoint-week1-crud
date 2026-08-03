package org.ironhack.project.library.repository;

import org.ironhack.project.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByIsbn(String isbn);

    List<Book> findByAuthor_NameContainingIgnoreCase(String authorName);

    List<Book> findByTitleContainingIgnoreCaseAndAuthor_NameContainingIgnoreCase(String title, String authorName);

    @Query("SELECT b FROM Book b WHERE b.members IS EMPTY")
    List<Book> findAvailableBooks();

    @Query("SELECT DISTINCT b FROM Book b WHERE b.members IS NOT EMPTY")
    List<Book> findBorrowedBooks();

    @Query("SELECT b FROM Book b JOIN b.author a " +
            "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:isbn IS NULL OR b.isbn = :isbn) " +
            "AND (:authorName IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%')))")
    Page<Book> searchBooks(@Param("title") String title,
                           @Param("isbn") String isbn,
                           @Param("authorName") String authorName,
                           Pageable pageable);

    @Query(value = "SELECT a.name AS author_name, COUNT(b.id) AS book_count " +
            "FROM authors a LEFT JOIN books b ON b.author_id = a.id " +
            "GROUP BY a.id, a.name " +
            "ORDER BY book_count DESC", nativeQuery = true)
    List<Object[]> countBooksPerAuthor();
}