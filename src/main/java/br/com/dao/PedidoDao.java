package br.com.dao;

import br.com.model.Pedido;
import br.com.model.PedidoItem;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class PedidoDao {

	private EntityManager em;

	public PedidoDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Pedido pedido) {
		if(pedido.getItens().isEmpty())
			throw new RuntimeException("Pedido precisa de pelo menos um item");
		else {
			this.em.getTransaction().begin();
			this.em.persist(pedido);
			this.em.getTransaction().commit();
		}
	}

	public void atualizar(Pedido pedido){
		this.em.getTransaction().begin();
		this.em.merge(pedido);
		this.em.getTransaction().commit();
	}

	public void remover(Pedido pedido){
		this.em.getTransaction().begin();
		this.em.remove(pedido);
		this.em.getTransaction().commit();
	}

	public void removerItem(PedidoItem pedidoItem){
		this.em.getTransaction().begin();
		this.em.remove(pedidoItem);
		this.em.getTransaction().commit();
	}

	public Pedido buscarPedidoPoriD(long id){
		String jpql = "SELECT p FROM Pedido p WHERE p.id = :id";
		return em.createQuery(jpql, Pedido.class)
				.setParameter("id", id)
				.getSingleResult();
	}

	public List<Pedido> buscarPedidosPorPeriodo(LocalDate dataIni, LocalDate dataFim){
		String jpql = "SELECT p FROM Pedido p WHERE p.data BETWEEN :dataIni AND :dataFim";
		return em.createQuery(jpql, Pedido.class)
				.setParameter("dataIni", dataIni)
				.setParameter("dataFim", dataFim)
				.getResultList();
	}

	public List<Pedido> buscarPedidosDeUmCliente(Long id) {
		//String jpql = "SELECT p FROM Pedido p JOIN FETCH p.cliente c WHERE c.id = :idCliente"; \\ou
		String jpql = "SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.cliente.id = :id";
		return em.createQuery(jpql, Pedido.class)
				.setParameter("id",id)
				.getResultList();
	}

}
