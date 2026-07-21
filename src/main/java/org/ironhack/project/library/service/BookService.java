package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.BookRequest;
import org.ironhack.project.library.dto.response.BookResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.mapper.BookMapper;
import org.ironhack.project.library.repository.AuthorRepository;
import org.ironhack.project.library.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public Page<BookResponse> getAllBooks(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return bookRepository.findAll(pageable)
                .map(BookMapper::toResponse);
    }

    public BookResponse getBookById(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return BookMapper.toResponse(book);
    }

    public BookResponse createBook(BookRequest request) {

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        Book book = BookMapper.toEntity(request, author);

        Book savedBook = bookRepository.save(book);

        return BookMapper.toResponse(savedBook);
    }

    public BookResponse updateBook(Long id, BookRequest request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setAuthor(author);

        Book updatedBook = bookRepository.save(book);

        return BookMapper.toResponse(updatedBook);
    }

    public void deleteBook(Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        bookRepository.delete(book);
    }
}