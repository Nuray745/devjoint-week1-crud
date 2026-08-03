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
import org.ironhack.project.library.specification.BookSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public Page<BookResponse> getAllBooks(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return bookRepository.findAll(pageable).map(BookMapper::toResponse);
    }

    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return BookMapper.toResponse(book);
    }

    public Page<BookResponse> searchBooks(String title, String isbn, String authorName,
                                          int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return bookRepository.searchBooks(title, isbn, authorName, pageable)
                .map(BookMapper::toResponse);
    }


    public Page<BookResponse> filterBooks(String title, String isbn, String authorName, Boolean available,
                                          int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Specification<Book> spec = BookSpecification.buildFilter(title, isbn, authorName, available);

        return bookRepository.findAll(spec, pageable)
                .map(BookMapper::toResponse);
    }

    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findAvailableBooks().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public List<BookResponse> getBorrowedBooks() {
        return bookRepository.findBorrowedBooks().stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public List<Object[]> getBookCountPerAuthor() {
        return bookRepository.countBooksPerAuthor();
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        Book book = BookMapper.toEntity(request, author);
        Book savedBook = bookRepository.save(book);
        return BookMapper.toResponse(savedBook);
    }

    @Transactional
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

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        bookRepository.delete(book);
    }
}