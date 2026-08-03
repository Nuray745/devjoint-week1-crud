package org.ironhack.project.library.repository;

import org.ironhack.project.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByNameContainingIgnoreCase(String name);

    @Query("SELECT a FROM Author a WHERE SIZE(a.books) >= :minBooks")
    List<Author> findAuthorsWithAtLeastBooks(@Param("minBooks") int minBooks);

    @Query("SELECT a FROM Author a WHERE a.books IS EMPTY")
    List<Author> findAuthorsWithNoBooks();
}