package br.edu.utfpr.pb.pw45s.server.dto;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder
public class ProductDto {

    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(min = 2, max = 1024)
    private String description;

    private Double price;

    private CategoryDto category;

    private String imageName;

    private String contentType;

}
