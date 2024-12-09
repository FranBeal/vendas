package br.com.dao;

import br.com.model.Cliente;
import jakarta.persistence.EntityManager;

public class ClienteDao extends GenericDao<Cliente>{

	private EntityManager em;

	public ClienteDao(EntityManager em) {
		super(em, Cliente.class);
	}
}
