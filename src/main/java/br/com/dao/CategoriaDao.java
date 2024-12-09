package br.com.dao;

import br.com.exception.DataAccessException;
import br.com.model.Categoria;
import jakarta.persistence.EntityManager;
import java.util.List;

public class CategoriaDao extends GenericDao<Categoria>{

	public CategoriaDao(EntityManager em) {
		super(em, Categoria.class);
	}


	// Metodo para buscar categorias pelo nome.
	public List<Categoria> buscarPorNome(String nome) {
		try{
			// Consulta JPQL para buscar categorias por nome.
			String jpql = "SELECT c FROM Categoria c WHERE c.nome = :nome";

			return em.createQuery(jpql, Categoria.class)
				.setParameter("nome", nome) // Define o parâmetro "nome" na consulta.
				.getResultList(); // Executa a consulta e retorna os resultados.

		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar categorias por nome: " + nome, e);
		}
	}
}
