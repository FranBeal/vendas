package br.com.dao;

import br.com.modelo.Cliente;
import jakarta.persistence.EntityManager;

public class ClienteDao {

	private EntityManager em;

	public ClienteDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Cliente cliente) {
		this.em.persist(cliente);
	}
	
	public Cliente buscarPorId(Long id) {
		return em.find(Cliente.class, id);
	}

}
