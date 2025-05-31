package br.edu.utfpr.pb.pw26s.server.service.impl;

import br.edu.utfpr.pb.pw26s.server.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class CrudServiceImpl 
		<T, ID extends Serializable>
		implements CrudService<T, ID> {

	protected abstract JpaRepository<T, ID>  getRepository();

	@Override
	@Transactional(readOnly = true)
	public List<T> findAll() {
		return getRepository().findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> findAll(Sort sort) {
		return getRepository().findAll(sort);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<T> findAll(Pageable pageable) {
		return getRepository().findAll(pageable);
	}

	@Override
	@Transactional(readOnly = false)
	public void flush() {
		getRepository().flush();
	}
	
	@Override
	@Transactional(readOnly = true)
	public T findOne(ID id) {
		return getRepository().findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean exists(ID id) {
		return getRepository().existsById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public long count() {
		return getRepository().count();
	}
	
	@Override
	@Transactional()
	public void delete(ID id) {
		getRepository().deleteById(id);
	}
	
	@Override
	@Transactional()
	public void delete(T entity) {
		getRepository().delete(entity);
	}
	
	@Override
	@Transactional()
	public void delete(Iterable<? extends T> iterable) {
		getRepository().deleteAll(iterable);
	}
	
	@Override
	@Transactional()
	public void deleteAll() {
		getRepository().deleteAll();
	}
	
	@Override
	@Transactional()
	public Iterable<T> save(Iterable<T> iterable) {
		return getRepository().saveAll(iterable);
	}
	
	@Override
	@Transactional()
	public T saveAndFlush(T entity) {
		return getRepository().saveAndFlush(entity);
	}
	
	@Override
	@Transactional()
	public T save(T entity) {
		return getRepository().save(entity);
	}
	
}
