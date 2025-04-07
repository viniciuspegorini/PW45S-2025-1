package br.edu.utfpr.pb.pw45s.server.controller;

import br.edu.utfpr.pb.pw45s.server.service.CrudService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CrudController<T, D, ID extends Serializable>{
	
	protected abstract CrudService<T, ID> getService();
	protected abstract ModelMapper getModelMapper();
	private final Class<T> typeClass;
	private final Class<D> typeDtoClass;

	public CrudController(Class<T> typeClass, Class<D> typeDtoClass) {
		this.typeClass = typeClass;
		this.typeDtoClass = typeDtoClass;
	}

	@GetMapping // https://localhost/categories.. /products... etc
	public ResponseEntity<List<D>> findAll() {
		return ResponseEntity.ok(getService().findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList()));
	}
	@GetMapping("page") // https://localhost/categories?page=1&size=10&order=name&asc=true
	public ResponseEntity<Page<D>> findAll(@RequestParam int page,
													 @RequestParam int size,
													 @RequestParam(required = false) String order,
													 @RequestParam(required = false) Boolean asc) {

		PageRequest pageRequest = PageRequest.of(page, size);
		if (order != null && asc != null) {
			pageRequest = PageRequest.of(page, size,
					asc ? Sort.Direction.ASC : Sort.Direction.DESC, order);
		}
		return ResponseEntity.ok(getService().findAll(pageRequest).map(this::convertToDto));
	}

	@GetMapping("{id}") // https://localhost/categories/1
	public ResponseEntity<D> findOne(@PathVariable ID id) {
		T entity = getService().findOne(id);
		if (entity != null) {
			return ResponseEntity.ok(convertToDto(getService().findOne(id)));
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@PostMapping
	public ResponseEntity<D> create(@RequestBody @Valid D entity) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
				convertToDto(getService().save( convertToEntity(entity) )));
	}

	@PutMapping("{id}")
	public ResponseEntity<D> update(@PathVariable ID id, @RequestBody @Valid D entity) {
		return ResponseEntity.ok(convertToDto( getService().save( convertToEntity(entity) ) ) );
	}

	@GetMapping("exists/{id}")
	public ResponseEntity<Boolean> exists(@PathVariable ID id) {
		return ResponseEntity.ok(getService().exists(id));
	}

	@GetMapping("count")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(getService().count());
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Void> delete(@PathVariable ID id) {
		getService().delete(id);
		return ResponseEntity.noContent().build();
	}

	private D convertToDto(T entity) {
		return getModelMapper().map(entity, this.typeDtoClass);
	}

	private T convertToEntity(D entityDto) {
		return getModelMapper().map(entityDto, this.typeClass);
	}

}














