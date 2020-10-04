package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly=true)
	public List<CategoryDTO> findAll(){
		List<Category> list =  categoryRepository.findAll();
		return  list.stream().map(x-> new CategoryDTO(x)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly=true)
	public CategoryDTO findById(long id){
		Optional<Category> obj = categoryRepository.findById(id);
		Category entity  = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return  new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());		
		entity = categoryRepository.save(entity);
		return  new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		// GetOne -> ele nao vai ao banco, somente instancia um objeto de uma determinada entity com o valor passado 
		try {
			Category entity = categoryRepository.getOne(id);
			entity.setName(dto.getName());
			categoryRepository.save(entity);
			return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e ) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(long id) {
		try {
			categoryRepository.deleteById(id);
		}catch(EmptyResultDataAccessException e ) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch(DataIntegrityViolationException e ) {
			throw new DataBaseException("Integrity violation");
		}
	}
}
