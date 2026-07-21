package org.ironhack.project.library;


import org.ironhack.project.library.dto.request.BookRequest;
import org.ironhack.project.library.dto.response.BookResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.entity.Book;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.repository.AuthorRepository;
import org.ironhack.project.library.repository.BookRepository;
import org.ironhack.project.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Author author;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Nizami Ganjavi");

        book = new Book();
        book.setId(10L);
        book.setTitle("Khamsa");
        book.setIsbn("978-3-16-148410-0");
        book.setAuthor(author);

        bookRequest = new BookRequest();
        bookRequest.setTitle("Khamsa");
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setAuthorId(1L);
    }

    @Test
    @DisplayName("getAllBooks - Should return paginated books successfully")
    void getAllBooks_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        Page<BookResponse> response = bookService.getAllBooks(0, 10, "id");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Khamsa");
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getBookById - Should return book when ID exists")
    void getBookById_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.getBookById(10L);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Khamsa");
        verify(bookRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("getBookById - Should throw ResourceNotFoundException when book not found")
    void getBookById_NotFound() {
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(10L));
        verify(bookRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("createBook - Should save and return book when author exists")
    void createBook_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.createBook(bookRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Khamsa");
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("createBook - Should throw ResourceNotFoundException when author not found")
    void createBook_AuthorNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.createBook(bookRequest));
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - Should update book when both book and author exist")
    void updateBook_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.updateBook(10L, bookRequest);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Khamsa");
        verify(bookRepository, times(1)).findById(10L);
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook - Should throw ResourceNotFoundException when book not found")
    void updateBook_BookNotFound() {
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.updateBook(10L, bookRequest));
        verify(bookRepository, times(1)).findById(10L);
        verify(authorRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("deleteBook - Should delete book successfully")
    void deleteBook_Success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBook(10L);

        verify(bookRepository, times(1)).findById(10L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("deleteBook - Should throw ResourceNotFoundException when book not found")
    void deleteBook_NotFound() {
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.deleteBook(10L));
        verify(bookRepository, times(1)).findById(10L);
        verify(bookRepository, never()).delete(any(Book.class));
    }
}
