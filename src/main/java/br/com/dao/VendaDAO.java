package br.com.dao;

import br.com.exception.DataAccessException;
import br.com.vo.RelatorioDeVendasVo;
import br.com.vo.RelatorioFinanceiroVo;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VendaDAO {

    private EntityManager em;

    public VendaDAO(EntityManager em) {
        this.em = em;
    }

    public BigDecimal retornaValorTotalVendidoEmUmPeriodo(LocalDate dataIni, LocalDate dataFim) {
        try{
            String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.data BETWEEN :dataIni AND :dataFim";
            BigDecimal total = em.createQuery(jpql, BigDecimal.class)
                .setParameter("dataIni", dataIni)
                .setParameter("dataFim", dataFim)
                .getSingleResult();
            if (total == null) {
                return BigDecimal.ZERO;
            }
            return total;
        } catch (Exception e) {
            throw new DataAccessException("Erro ao retornar valor total vendido em um período", e);
        }
    }

    /* SELECT NEW em JPQL é indicado em situações onde se quer apenas uma parte dos dados das entidades e
       se quer encapsulá-los num objeto específico, como um VO (Value Object), por exemplo. É muito utilizado
       para gerar relatórios ou resumos que utilizam funções de agregação tais: como sum, max, min, count.
       Resumindo: utiliza-se o select new quando o resultado da consulta não é uma entidade mapeada,
       desta forma, é necessário indicar a classe que será retornada. */
    public List<RelatorioDeVendasVo> relatorioDeVendas() {
        try{
            String jpql = "SELECT new br.com.vo.RelatorioDeVendasVo("
                + "produto.nome, "
                + "SUM(item.quantidade), "
                + "MAX(pedido.data)) "
                + "FROM Pedido pedido "
                + "JOIN pedido.itens item "
                + "JOIN item.produto produto "
                + "GROUP BY produto.nome "
                + "ORDER BY SUM(item.quantidade) DESC";
            return em.createQuery(jpql, RelatorioDeVendasVo.class)
                .getResultList();
        } catch (Exception e) {
            throw new DataAccessException("Erro ao retornar o relatório de vendas", e);
        }
    }

    public List<RelatorioFinanceiroVo> relatorioFinanceiro() {
        try{
            String jpql = "SELECT new br.com.vo.RelatorioFinanceiroVo("
                + "cliente.nome, "
                + "SUM(pedido.valorTotal)) "
                + "FROM Pedido pedido "
                + "JOIN pedido.cliente cliente "
                + "GROUP BY cliente.nome "
                + "ORDER BY SUM(pedido.valorTotal) DESC";
            return em.createQuery(jpql, RelatorioFinanceiroVo.class)
                .getResultList();
        } catch (Exception e) {
            throw new DataAccessException("Erro ao retornar o relatório financeiro", e);
        }
    }
}
