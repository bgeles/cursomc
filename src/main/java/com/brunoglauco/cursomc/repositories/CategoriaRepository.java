package com.brunoglauco.cursomc.repositories;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brunoglauco.cursomc.domain.Categoria;
import com.brunoglauco.cursomc.domain.Produto;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	
	
}
