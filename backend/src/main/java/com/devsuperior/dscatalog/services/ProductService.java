package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Transactional(readOnly=true)
	public Page<ProductDTO> findAll(PageRequest pageRequest){
		Page<Product> list =  productRepository.findAll(pageRequest);
		return  list.map(x-> new ProductDTO(x));
	}
	
	@Transactional(readOnly=true)
	public ProductDTO findById(long id){
		Optional<Product> obj = productRepository.findById(id);
		Product entity  = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return  new ProductDTO(entity, entity.getCategories());
	}
	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		//entity.setName(dto.getName());
	
		entity = productRepository.save(entity);
		return  new ProductDTO(entity);
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		// GetOne -> ele nao vai ao banco, somente instancia um objeto de uma determinada entity com o valor passado 
		try {
			Product entity = productRepository.getOne(id);
			//entity.setName(dto.getName());
			
			productRepository.save(entity);
			return new ProductDTO(entity);
		}
		catch(EntityNotFoundException e ) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(long id) {
		try {
			productRepository.deleteById(id);
		}catch(EmptyResultDataAccessException e ) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
		catch(DataIntegrityViolationException e ) {
			throw new DataBaseException("Integrity violation");
		}
	}
}
