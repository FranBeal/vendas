# Persistência de Dados com Banco de Dados Relacional

**Linguagem de Programação:** Java

**JDK:** 21 ou superior

**IDE:** IntelliJ IDEA
  
**Banco de Dados Relacional:** SGDB PostgreSQL

## Conceitos abordados neste projeto: 
Persistência de dados em banco de dados relacional, JPA/Hibernate, mapeamento objeto-relacional, padrão MVC, padrões de projeto (DAO e VO) e testes unitários.

## Tema do Projeto:
Uma aplicação Java desktop, com interação por linha de comando, sobre Pedidos de Venda. Ela contém as seguintes funcionalidades:
1. Cadastrar Categoria
2. Alterar Categoria
3. Excluir Categoria
4. Consultar Categoria por ID
5. Listar todas as Categorias
6. Cadastrar Produto
7. Alterar Produto
8. Excluir Produto
9. Consultar Produto por ID
10. Consultar Produtos por Nome
11. Consultar Produtos por Categoria
12. Cadastrar Pedido
13. Consultar Pedido por ID
14. Consultar Pedidos por Período
15. Consultar Pedidos de um Cliente
16. Alterar a quantidade de um item do pedido
17. Excluir um item do pedido
18. Consultar Valor Total vendido
19. Consultar Relatório de Vendas
20. Consultar Relatório Financeiro

A base de dados, de nome **Pedidos**, segue a modelagem apresentada na figura abaixo:
![DER](https://github.com/user-attachments/assets/c9109f3c-28c9-486c-afa2-db140fcd9e07)

# Introdução à Persistência de Dados
A persistência de dados é um conceito fundamental em desenvolvimento de software, especialmente em aplicações que necessitam armazenar informações de forma permanente. Imagine um sistema de e-commerce, onde, os dados dos produtos, clientes e pedidos precisam ser armazenados.

A **Java Persistence API (JPA)**  é uma especificação do Java que define um conjunto de regras para o mapeamento Objeto-Relacional (ORM) e a persistência de dados em bancos de dados relacionais. Ela simplifica o mapeamento entre objetos Java e tabelas de um banco de dados, abstraindo a complexidade do SQL. Em termos simples, a JPA permite aos desenvolvedores salvar, atualizar e consultar dados em um banco de dados diretamente por meio de objetos Java, sem a necessidade de escrever SQL manualmente. 

**Por que a JPA foi criada?**

A JPA foi desenvolvida para oferecer uma solução padrão e simplificada para a persistência de dados em Java, resolvendo problemas que frameworks anteriores, como o JDBC, apresentavam, tais como:

  **1. Código Verboso:** Com o JDBC, o desenvolvedor precisa escrever muito código repetitivo para conectar-se ao banco de dados, executar consultas, tratar exceções e fechar conexões.
  
  **2. Alto Acoplamento com o Banco de Dados:** O código escrito com JDBC fica fortemente acoplado ao banco de dados específico, tornando difícil a troca ou manutenção do banco de dados.
    


O PostgreSQL é um sistema de gerenciamento de banco de dados relacional (SGDB) open-source que é amplamente utilizado e bem aceito pela comunidade de desenvolvedores.

# Conceitos Básicos
## **Mapeamento Objeto-Relacional (ORM)**

O ORM é a técnica que mapeia objetos de uma aplicação Java para tabelas de um banco de dados. A JPA utiliza anotações para definir esse mapeamento de forma declarativa.

**Annotações JPA:**

**@Entity:** Indica que uma classe representa uma entidade persistível, ou seja, um objeto que pode ser armazenado no banco de dados.

**@Id:** Identifica o atributo que será a chave primária da entidade.

**@Column:** Mapeia um atributo para uma coluna da tabela.

**@Table:** Mapeia a classe para uma tabela específica.

**@GeneratedValue:** Gera valores automaticamente para a chave primária.

**EntityManager:**
É a interface principal da JPA para interagir com o banco de dados. Ele permite realizar operações de persistência, como criar, ler, atualizar e excluir objetos.


[em construção...]



