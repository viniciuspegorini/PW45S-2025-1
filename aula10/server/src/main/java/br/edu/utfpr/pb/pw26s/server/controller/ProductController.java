package br.edu.utfpr.pb.pw26s.server.controller;

import br.edu.utfpr.pb.pw26s.server.dto.ProductDto;
import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.service.CrudService;
import br.edu.utfpr.pb.pw26s.server.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("products")
public class ProductController extends CrudController<Product, ProductDto, Long> {

    private final ProductService productService;
    private final ModelMapper modelMapper;


    public ProductController(ProductService productService, ModelMapper modelMapper) {
        super(Product.class, ProductDto.class);
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CrudService<Product, Long> getService() {
        return this.productService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return this.modelMapper;
    }

    @PostMapping(value = "upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Product saveProduct(@RequestPart("product") @Valid Product entity,
                               @RequestPart("image") @Valid MultipartFile file) {
        return productService.save(entity, file);
    }

    @GetMapping(value = "download/{id}")
    public void downloadFile(@PathVariable("id") Long id, HttpServletResponse response) {
        productService.downloadFile(id, response);
    }
}
