package br.com.dao;

import br.com.exception.DataAccessException;
import br.com.model.Produto;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProdutoDao extends GenericDao<Produto>{

	public ProdutoDao(EntityManager em) {
		super(em, Produto.class);
	}

	public List<Produto> buscarPorNome(String nome) {
		try{
			String jpql = "SELECT p FROM Produto p WHERE p.nome = :nome";
			return em.createQuery(jpql, Produto.class)
				.setParameter("nome", nome)
				.getResultList();
		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar produto por nome: " + nome, e);
		}
	}

	public List<Produto> buscarPorCategoria(long idCategoria) {
		try{
			String jpql = "SELECT p FROM Produto p WHERE p.categoria.id = :id";
			return em.createQuery(jpql, Produto.class)
				.setParameter("id", idCategoria)
				.getResultList();
		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar produto por categoria", e);
		}
	}
}
