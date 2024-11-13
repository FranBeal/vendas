package br.com.vo;

/*No contexto de JPA e JPQL, o padrão Value Object (VO) é frequentemente utilizado em
  consultas que precisam retornar um subconjunto de dados das entidades.
  Ao invés de retornar a própria entidade (como Pedido ou Produto),
  um VO é utilizado para encapsular apenas os dados relevantes, evitar carregar entidades completas,
  reduzindo a carga de dados. Além disso, são imutáveis, reduzindo o risco de alteração acidental de dados e
  expondo apenas os dados necessários.*/

import java.math.BigDecimal;

public class RelatorioFinanceiroVo {
    String nomeCliente;
    BigDecimal totalPedidosDoCliente;

    public RelatorioFinanceiroVo(String nomeCliente, BigDecimal totalPedidosDoCliente) {
        this.nomeCliente = nomeCliente;
        this.totalPedidosDoCliente = totalPedidosDoCliente;
    }

    @Override
    public String toString() {
        return "RelatorioFinanceiroVo [nomeCliente=" + nomeCliente +
                ", TotalPedidosCliente=" + totalPedidosDoCliente + "]";
    }
}
