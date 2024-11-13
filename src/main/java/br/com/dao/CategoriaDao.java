package br.com.dao;

import br.com.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaDao {
	
	private EntityManager em;

	public CategoriaDao(EntityManager em) {
		this.em = em;
	}
	
	public void cadastrar(Categoria categoria) {
		this.em.getTransaction().begin();
		this.em.persist(categoria);
		this.em.getTransaction().commit();
	}
	
	public void atualizar(Categoria categoria) {
		this.em.getTransaction().begin();
		this.em.merge(categoria);
		this.em.getTransaction().commit();
	}
	
	public void remover(Categoria categoria) {
		this.em.getTransaction().begin();
		this.em.remove(categoria);
		this.em.getTransaction().commit();
	}

	public Categoria buscarPorId(Long id) {
		return em.find(Categoria.class, id);
	}

	public List<Categoria> buscarTodos() {
		String jpql = "SELECT c FROM Categoria c";
		return em.createQuery(jpql, Categoria.class).getResultList();
	}

	public List<Categoria> buscarPorNome(String nome) {
		String jpql = "SELECT c FROM Categoria c WHERE c.nome = :nome";
		return em.createQuery(jpql, Categoria.class)
				.setParameter("nome", nome)
				.getResultList();
	}

}
