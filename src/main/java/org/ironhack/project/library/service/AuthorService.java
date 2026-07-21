package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.AuthorRequest;
import org.ironhack.project.library.dto.response.AuthorResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.mapper.AuthorMapper;
import org.ironhack.project.library.repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(AuthorMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        return AuthorMapper.toResponse(author);
    }

    public AuthorResponse createAuthor(AuthorRequest request) {

        Author author = AuthorMapper.toEntity(request);

        Author savedAuthor = authorRepository.save(author);

        return AuthorMapper.toResponse(savedAuthor);
    }

    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setName(request.getName());

        Author updatedAuthor = authorRepository.save(author);

        return AuthorMapper.toResponse(updatedAuthor);
    }

    public void deleteAuthor(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        authorRepository.delete(author);
    }
}