package org.ironhack.project.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ironhack.project.library.dto.request.AuthorRequest;
import org.ironhack.project.library.dto.response.AuthorResponse;
import org.ironhack.project.library.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Tag(name = "Authors", description = "Operations related to authors")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Get all authors, paginated")
    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(authorService.getAllAuthors(page, size, sortBy));
    }

    @Operation(summary = "Get an author by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @Operation(summary = "Search authors by name")
    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponse>> searchAuthorsByName(@RequestParam String name) {
        return ResponseEntity.ok(authorService.searchAuthorsByName(name));
    }

    @Operation(summary = "Get authors with at least the given number of books")
    @GetMapping("/min-books")
    public ResponseEntity<List<AuthorResponse>> getAuthorsWithAtLeastBooks(@RequestParam int minBooks) {
        return ResponseEntity.ok(authorService.getAuthorsWithAtLeastBooks(minBooks));
    }

    @Operation(summary = "Get authors with no books")
    @GetMapping("/no-books")
    public ResponseEntity<List<AuthorResponse>> getAuthorsWithNoBooks() {
        return ResponseEntity.ok(authorService.getAuthorsWithNoBooks());
    }

    @Operation(summary = "Create a new author")
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.createAuthor(request));
    }

    @Operation(summary = "Update an existing author")
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.updateAuthor(id, request));
    }

    @Operation(summary = "Delete an author")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}