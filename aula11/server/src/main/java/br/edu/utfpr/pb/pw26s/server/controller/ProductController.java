package br.edu.utfpr.pb.pw26s.server.controller;

import br.edu.utfpr.pb.pw26s.server.dto.ProductDto;
import br.edu.utfpr.pb.pw26s.server.model.Product;
import br.edu.utfpr.pb.pw26s.server.service.CrudService;
import br.edu.utfpr.pb.pw26s.server.service.ProductService;
import br.edu.utfpr.pb.pw26s.server.specification.ProductSpecification;
import br.edu.utfpr.pb.pw26s.server.specification.core.GenericSpecificationsBuilder;
import br.edu.utfpr.pb.pw26s.server.specification.core.SearchOperation;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("products")
@Slf4j
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

    @GetMapping("search")
    public List<ProductDto> search(@RequestParam(value = "search") String search) {
        Specification<Product> spec = resolveSpecification(search);
        return productService.findAll(spec).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    protected Specification<Product> resolveSpecification(String searchParameters) {
        GenericSpecificationsBuilder<Product> specBuilder = new GenericSpecificationsBuilder<>();
        Pattern pattern = Pattern.compile("(\\p{Punct}?)(\\w+?)(" + Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET) + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(searchParameters + ",");
        while (matcher.find()) {
            specBuilder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(5), matcher.group(4), matcher.group(6));
        }
        return specBuilder.build(ProductSpecification::new);
    }
}
