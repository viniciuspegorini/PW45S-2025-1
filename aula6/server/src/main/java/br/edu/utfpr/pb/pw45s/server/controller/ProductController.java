package br.edu.utfpr.pb.pw45s.server.controller;

import br.edu.utfpr.pb.pw45s.server.dto.ProductDto;
import br.edu.utfpr.pb.pw45s.server.model.Product;
import br.edu.utfpr.pb.pw45s.server.service.CrudService;
import br.edu.utfpr.pb.pw45s.server.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
