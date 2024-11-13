package br.com.vo;

/*No contexto de JPA e JPQL, o padrão Value Object (VO) é frequentemente utilizado em
  consultas que precisam retornar um subconjunto de dados das entidades.
  Ao invés de retornar a própria entidade (como Pedido ou Produto),
  um VO é utilizado para encapsular apenas os dados relevantes, evitar carregar entidades completas,
  reduzindo a carga de dados. Além disso, são imutáveis, reduzindo o risco de alteração acidental de dados e
  expondo apenas os dados necessários.*/

import java.time.LocalDate;

public class RelatorioDeVendasVo {
	
	private String nomeProduto;
	private Long quantidadeVendida;
	private LocalDate dataUltimaVenda;
	
	public RelatorioDeVendasVo(String nomeProduto, Long quantidadeVendida, LocalDate dataUltimaVenda) {
		this.nomeProduto = nomeProduto;
		this.quantidadeVendida = quantidadeVendida;
		this.dataUltimaVenda = dataUltimaVenda;
	}
	
	@Override
	public String toString() {
		return "RelatorioDeVendasVo [nomeProduto=" + nomeProduto + ", quantidadeVendida=" + quantidadeVendida
				+ ", dataUltimaVenda=" + dataUltimaVenda + "]";
	}
	
}
