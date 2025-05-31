package br.edu.utfpr.pb.pw26s.server.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class CategoryDto {

    private Long id;

    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    private LocalDate testDate;
}
