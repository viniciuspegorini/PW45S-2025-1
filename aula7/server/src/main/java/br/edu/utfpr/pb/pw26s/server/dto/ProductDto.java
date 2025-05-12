package br.edu.utfpr.pb.pw26s.server.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
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

    private String imageName; // Upload no Sistema de Arquivos (disco)

    private byte[] imageFile; // Upload no Banco de dados

    private String imageFileName; // Upload no Banco de dados

}
