package org.ironhack.project.library.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    public static Specification<Book> hasIsbn(String isbn) {
        return (root, query, cb) -> {
            if (isbn == null || isbn.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isbn"), isbn);
        };
    }

    public static Specification<Book> hasAuthorName(String authorName) {
        return (root, query, cb) -> {
            if (authorName == null || authorName.isBlank()) {
                return cb.conjunction();
            }
            Join<Book, Author> authorJoin = root.join("author", JoinType.INNER);
            return cb.like(cb.lower(authorJoin.get("name")), "%" + authorName.toLowerCase() + "%");
        };
    }

    public static Specification<Book> isAvailable(Boolean available) {
        return (root, query, cb) -> {
            if (available == null) {
                return cb.conjunction();
            }
            return Boolean.TRUE.equals(available)
                    ? cb.isEmpty(root.get("members"))
                    : cb.isNotEmpty(root.get("members"));
        };
    }

    public static Specification<Book> buildFilter(String title, String isbn, String authorName, Boolean available) {
        return Specification.where(hasTitle(title))
                .and(hasIsbn(isbn))
                .and(hasAuthorName(authorName))
                .and(isAvailable(available));
    }
}