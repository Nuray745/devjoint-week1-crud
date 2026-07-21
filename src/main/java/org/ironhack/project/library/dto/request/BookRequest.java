package org.ironhack.project.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "Book title is required.")
    private String title;

    @NotBlank(message = "ISBN is required.")
    private String isbn;

    @NotNull(message = "Author ID is required.")
    private Long authorId;
}