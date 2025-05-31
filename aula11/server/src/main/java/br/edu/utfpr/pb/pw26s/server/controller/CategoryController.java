package br.edu.utfpr.pb.pw26s.server.controller;

import br.edu.utfpr.pb.pw26s.server.dto.CategoryDto;
import br.edu.utfpr.pb.pw26s.server.model.Category;
import br.edu.utfpr.pb.pw26s.server.service.CategoryService;
import br.edu.utfpr.pb.pw26s.server.service.CrudService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("categories")
public class CategoryController extends CrudController<Category, CategoryDto, Long> {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        super(Category.class, CategoryDto.class);
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CrudService<Category, Long> getService() {
        return this.categoryService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return this.modelMapper;
    }

}