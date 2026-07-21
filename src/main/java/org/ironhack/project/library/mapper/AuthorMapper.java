package org.ironhack.project.library.mapper;

import org.ironhack.project.library.dto.request.AuthorRequest;
import org.ironhack.project.library.dto.response.AuthorResponse;
import org.ironhack.project.library.entity.Author;

public class AuthorMapper {

    public static Author toEntity(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        return author;
    }

    public static AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }
}