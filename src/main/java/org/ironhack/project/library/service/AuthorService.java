package org.ironhack.project.library.service;

import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.AuthorRequest;
import org.ironhack.project.library.dto.response.AuthorResponse;
import org.ironhack.project.library.entity.Author;
import org.ironhack.project.library.exception.ResourceNotFoundException;
import org.ironhack.project.library.mapper.AuthorMapper;
import org.ironhack.project.library.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Page<AuthorResponse> getAllAuthors(int page, int size, String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        return authorRepository.findAll(pageable)
                .map(AuthorMapper::toResponse);
    }

    public AuthorResponse getAuthorById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        return AuthorMapper.toResponse(author);
    }

    public AuthorResponse createAuthor(AuthorRequest request) {

        Author author = AuthorMapper.toEntity(request);

        Author savedAuthor = authorRepository.save(author);

        return AuthorMapper.toResponse(savedAuthor);
    }

    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        author.setName(request.getName());

        Author updatedAuthor = authorRepository.save(author);

        return AuthorMapper.toResponse(updatedAuthor);
    }

    public void deleteAuthor(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        authorRepository.delete(author);
    }
}