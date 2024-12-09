package br.com.dao;

import br.com.exception.DataAccessException;
import br.com.model.Pedido;
import br.com.model.PedidoItem;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class PedidoDao extends GenericDao<Pedido>{

	public PedidoDao(EntityManager em) {
		super(em, Pedido.class);
	}

	public void removerItem(PedidoItem pedidoItem){
		this.em.getTransaction().begin();
		this.em.remove(pedidoItem);
		this.em.getTransaction().commit();
	}

	public List<Pedido> buscarPedidosPorPeriodo(LocalDate dataIni, LocalDate dataFim) {
		try{
			String jpql = "SELECT p FROM Pedido p WHERE p.data BETWEEN :dataIni AND :dataFim";
			return em.createQuery(jpql, Pedido.class)
				.setParameter("dataIni", dataIni)
				.setParameter("dataFim", dataFim)
				.getResultList();
		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar pedidos por período: ", e);
		}
	}

	public List<Pedido> buscarPedidosDeUmCliente(Long id) {
		try{
			String jpql = "SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.cliente.id = :id";
			return em.createQuery(jpql, Pedido.class)
				.setParameter("id", id)
				.getResultList();
		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar pedidos por id de cliente", e);
		}
	}
}
