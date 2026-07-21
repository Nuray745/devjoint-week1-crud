package org.ironhack.project.library.mapper;

import org.ironhack.project.library.dto.request.BookRequest;
import org.ironhack.project.library.dto.response.BookResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;

public class BookMapper {

    public static Book toEntity(BookRequest request, Author author) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAuthor(author);
        return book;
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}