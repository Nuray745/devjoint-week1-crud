package org.ironhack.project.library;


import org.ironhack.project.library.dto.request.AuthorRequest;
import org.ironhack.project.library.dto.response.AuthorResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.repository.AuthorRepository;
import org.ironhack.project.library.service.AuthorService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorRequest authorRequest;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Nizami Ganjavi");

        authorRequest = new AuthorRequest();
        authorRequest.setName("Nizami Ganjavi");
    }

    @Test
    @DisplayName("getAllAuthors - Should return paginated authors successfully")
    void getAllAuthors_Success() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Author> authorPage = new PageImpl<>(List.of(author), pageable, 1);

        when(authorRepository.findAll(any(Pageable.class))).thenReturn(authorPage);

        Page<AuthorResponse> response = authorService.getAllAuthors(0, 10, "id");

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getName()).isEqualTo("Nizami Ganjavi");
        verify(authorRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("getAuthorById - Should return author when ID exists")
    void getAuthorById_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponse response = authorService.getAuthorById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Nizami Ganjavi");
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getAuthorById - Should throw ResourceNotFoundException when ID does not exist")
    void getAuthorById_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getAuthorById(1L));
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("createAuthor - Should save and return author response")
    void createAuthor_Success() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponse response = authorService.createAuthor(authorRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Nizami Ganjavi");
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    @DisplayName("updateAuthor - Should update and return author response")
    void updateAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponse response = authorService.updateAuthor(1L, authorRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Nizami Ganjavi");
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    @DisplayName("updateAuthor - Should throw ResourceNotFoundException when author to update not found")
    void updateAuthor_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.updateAuthor(1L, authorRequest));
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    @DisplayName("deleteAuthor - Should delete author successfully")
    void deleteAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        doNothing().when(authorRepository).delete(author);

        authorService.deleteAuthor(1L);

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    @DisplayName("deleteAuthor - Should throw ResourceNotFoundException when author to delete not found")
    void deleteAuthor_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(1L));
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, never()).delete(any(Author.class));
    }
}
