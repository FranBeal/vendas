# Persistência de Dados com Banco de Dados Relacional

## Objetivo:
O projeto **Vendas** tem como objetivo ensinar conceitos fundamentais de JPA (Java Persistence API), Mapeamento Objeto-Relacional (ORM), persistência de dados com PostgreSQL e a criação de testes unitários utilizando JUnit. Além desses, o padrão MVC e os padrões de projeto DAO e VO. A construção do projeto permite que os estudantes aprendam e apliquem esses conceitos na prática.

## Requisitos:
- **Linguagem de Programação:** Java
- **JDK:** 21 ou superior
- **IDE:** IntelliJ IDEA
- **Maven**: Ferramenta de automação e gerenciamento de projetos Java  
- **Banco de Dados Relacional:** SGDB PostgreSQL

## Sobre o Projeto:
Trata-se de uma aplicação Java desktop, com interação por linha de comando, sobre Pedidos de Venda, simplificada. Ela contém as seguintes funcionalidades:
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

### Por que a JPA foi criada?

A JPA foi desenvolvida para oferecer uma solução padrão e simplificada para a persistência de dados em Java, resolvendo problemas que frameworks anteriores, como o JDBC, apresentavam, tais como:

  **1. Código Verboso:** Com o JDBC, o desenvolvedor precisa escrever muito código repetitivo para conectar-se ao banco de dados, executar consultas, tratar exceções e fechar conexões.
  
  **2. Alto Acoplamento com o Banco de Dados:** O código escrito com JDBC fica fortemente acoplado ao banco de dados específico, tornando difícil a troca ou manutenção do banco de dados.
    
Abaixo, é apresentado um exemplo de código JDBC:
```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User getUserById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            // Abrindo conexão com o banco de dados (acoplamento ao banco)
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "password");
            // Criando a query (manualmente, o que pode gerar erros)
            String sql = "SELECT * FROM users WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            // Executando a query
            resultSet = statement.executeQuery();
            // Mapeando os resultados para o objeto
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fechando recursos (verbosidade)
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
```
Sobre os problemas que podem ser observados código acima, o primeiro deles é referete ao **código verboso**, onde são necessárias várias etapas, como abrir e fechar conexões, criar o *PreparedStatement*, tratar exceções e mapear os resultados. O padrão de projeto "DAO", Data Access Object, é usado para isolar toda a API do JDBC dentro de uma única classe, para não ficar com os códigos de *Connection*, *ResultSet*, que são classes complicadas do JDBC, espalhados pela aplicação. O outro problema presente, é o **alto acoplamento**, onde o código JDBC é fortemente acoplado ao banco de dados MySQL. Se fosse necessário trocar o banco para PostgreSQL, por exemplo, seria necessário ajustar a URL de conexão e possivelmente outras partes do código.

A seguir, é apresetado um exemplo usado JPA e muito do código repetitivo e do acoplamento é eliminado, tornando o código mais simples e eficiente. 
```java
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPersistenceUnit");

    public User getUserById(int id) {
        EntityManager em = emf.createEntityManager();
        User user = null;

        try {
            user = em.find(User.class, id);
        } finally {
            em.close();
        }

        return user;
    }
}
```
### Como a JPA resolve os problemas?

  **1. Redução do Código Verboso:** A JPA automatiza detalhes como abertura e fechamento de conexões e mapeamento de dados. No exemplo acima, o método *find()* simplifica a busca de dados, e o *EntityManager* lida com a persistência e recuperação de dados.
  
  **2. Redução do Acoplamento com o Banco de Dados:** A configuração do banco é feita externamente, em arquivos como *persistence.xml* (figura abaixo), permitindo trocar o banco de dados sem alterar o código Java. A JPA lida com diferentes bancos de dados por meio de dialetos (MySQL, PostgreSQL, etc.) que podem ser configurados no arquivo de persistência.
```xml
<!-- Exemplo de arquivo persistence.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.1"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="PostgresPU" transaction-type="RESOURCE_LOCAL">
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" /> <!-- DB Driver -->
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost/Pedidos" /> <!-- BD url -->
            <property name="javax.persistence.jdbc.user" value="postgres" /> <!-- DB User -->
            <property name="javax.persistence.jdbc.password" value="postgres" /> <!-- DB Password -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/> <!-- DB Dialect -->
            <property name="hibernate.hbm2ddl.auto" value="update" /> <!-- create / create-drop / update -->
            <property name="hibernate.show_sql" value="true" /> <!-- Show SQL in console -->
            <property name="hibernate.format_sql" value="true" /> <!-- Show SQL formatted -->
        </properties>
    </persistence-unit>
</persistence>
```

### Qual a diferença  entre JPA e Hibernate?
A JPA e o Hibernate são frequentemente mencionados juntos no contexto de persistência de dados em Java, mas é importante entender que eles têm papéis diferentes. Aqui estão as principais diferenças:

  • A **JPA** é uma especificação que estabelece um conjunto de regras e padrões para o Mapeamento Objeto-Relacional (ORM) e a persistência de dados em bancos de dados relacionais. Como uma especificação, a JPA não implementa nenhuma funcionalidade diretamente, mas define como um provedor de persistência deve ser implementado. Assim, a JPA atua como uma interface que abstrai as operações com o banco de dados, padronizando a persistência de dados e tornando o código independente de implementações específicas.
    
  • O **Hibernate** é uma implementação concreta da especificação JPA, é uma biblioteca. Em outras palavras, ele segue as regras e padrões definidos pela JPA, mas também oferece recursos adicionais que não fazem parte da especificação JPA. O Hibernate é uma das implementações mais populares de ORM para Java, amplamente utilizado devido à sua maturidade, estabilidade e comunidade ativa. 
     
## O que é Mapeamento Objeto-Relacional (ORM)?

O ORM é a técnica que mapeia objetos de uma aplicação Java para tabelas de um banco de dados. A JPA utiliza anotações para definir esse mapeamento de forma declarativa.

### Annotações JPA:

**@Entity:** Indica que uma classe representa uma entidade persistível, ou seja, um objeto que pode ser armazenado no banco de dados.

**@Id:** Identifica o atributo que será a chave primária da entidade.

**@Column:** Mapeia um atributo para uma coluna da tabela.

**@Table:** Mapeia a classe para uma tabela específica.

**@GeneratedValue:** Gera valores automaticamente para a chave primária.

**@OneToOne:** Define um relacionamento de um-para-um entre duas entidades. É usado para indicar que cada instância de uma entidade está associada a uma única instância de outra entidade.

**@OneToMany:** Define um relacionamento de um-para-muitos. Indica que uma entidade está relacionada a várias instâncias de outra entidade. Geralmente é usado com uma lista ou um conjunto (List, Set).

**@ManyToOne:** Define um relacionamento de muitos-para-um. Usado para indicar que várias instâncias de uma entidade podem estar relacionadas a uma única instância de outra entidade.

**@ManyToMany:** Define um relacionamento de muitos-para-muitos entre duas entidades. Geralmente envolve o uso de uma tabela de junção.

**@JoinColumn:** Especifica a coluna de junção para um relacionamento. Usado em conjunto com anotações como @OneToOne, @OneToMany, ou @ManyToOne.

**@JoinTable:** Define a tabela de junção em um relacionamento @ManyToMany. Especifica o nome da tabela de junção e as colunas que fazem o mapeamento entre as entidades.

**@Transient:** Indica que um atributo não deve ser persistido no banco de dados, ou seja, não será mapeado para uma coluna.

### EntityManager

É a interface principal da JPA para interagir com o banco de dados. Ele permite realizar operações de persistência, como criar, ler, atualizar e excluir objetos.

### JPQL

JPQL (Java Persistence Query Language) é uma linguagem de consulta orientada a objetos usada para fazer queries em bancos de dados relacionais no contexto da JPA. Assim como SQL, ela permite realizar operações de busca, inserção, atualização e exclusão, mas foi projetada para trabalhar com entidades Java em vez de tabelas relacionais. A grande vantagem da JPQL é que ela permite consultas com base no modelo de objetos, abstraindo a estrutura relacional do banco de dados. 

**Principais Características:**

- **Consulta a Entidades:** JPQL utiliza as classes de entidade em vez de tabelas, e seus atributos em vez de colunas.
- **Sintaxe Similar ao SQL:** Embora seja uma linguagem orientada a objetos, JPQL tem uma sintaxe que lembra a do SQL.
- **Portabilidade:** Como é abstraída da implementação específica do banco, JPQL é independente do sistema de gerenciamento de banco de dados.

**Exemplos:**

Consulta Simples: ```SELECT p FROM Produto p``` onde, ```Produto``` é uma classe de entidade, e ```p``` é um alias.

Consulta com Filtro: ```SELECT p FROM Produto p WHERE p.preco > 100.0```, seleciona produtos com preço acima de 100.

Consulta com JOIN: ```SELECT p FROM Pedido p JOIN p.produtos pe WHERE p.id = :pedidoId```, usa um ```JOIN``` para acessar uma lista de produtos relacionados a um pedido.

Ordenação e Agrupamento: ```SELECT c.nome, SUM(p.valor) FROM Cliente c JOIN c.pedidos p GROUP BY c.nome ORDER BY SUM(p.valor) DESC```


## O Padrão MVC

O MVC (Model-View-Controller) é um padrão arquitetural que organiza uma aplicação em três componentes principais: Model, View e Controller. Cada um desses componentes é responsável por uma função específica, o que facilita a manutenção e o desenvolvimento da aplicação, além de promover a separação de responsabilidades:

### Model (Modelo):

Representa a lógica de negócios e os dados da aplicação. É responsável por gerenciar as regras de negócio, consultar, criar, atualizar e excluir dados. Em uma aplicação que utiliza um banco de dados, o Model frequentemente interage com o banco, mapeando as tabelas e entidades de dados. No Java, o Model pode incluir classes representando entidades (com anotações do JPA, por exemplo) e pacotes dao (Data Access Object) que manipulam a persistência de dados. 

### View (Visão):

Refere-se à interface de usuário (UI), ou seja, à apresentação dos dados para o usuário. Exibe os dados do Model e permite que o usuário interaja com a aplicação. Em aplicações desktop, a view pode ser representada por classes que são criadas com base em componentes do tipo Swing, por exemplo. Já em aplicações web seriam representadas, por exemplo, por páginas do tipo JSP, que são exibidas em um navegador Web.

### Controller (Controlador):

Atua como um intermediário entre a View e o Model. Processa as requisições do usuário (normalmente vindas da View), realiza operações no Model e retorna uma resposta para a View. Garante que a lógica de apresentação e a lógica de negócios estejam separadas, melhorando a organização e a testabilidade do código.

**Fluxo no Padrão MVC:**

- O usuário interage com a View (por exemplo, clicando em um botão).
- A View envia uma requisição ao Controller, que processa essa ação.
- O Controller, então, interage com o Model para obter ou manipular dados conforme necessário.
- O Model realiza a operação e envia uma resposta de volta ao Controller.
- O Controller atualiza a View com as informações adequadas para que o usuário veja o resultado da ação.

## O Padrão DAO

O padrão DAO (Data Access Object) é um padrão de design estrutural que separa a lógica de acesso a dados das demais camadas de uma aplicação. Sua função principal é fornecer uma interface abstrata para realizar operações no banco de dados (como inserir, atualizar, deletar e buscar dados) sem que o restante da aplicação precise se preocupar com detalhes específicos da implementação, como o tipo de banco de dados ou a linguagem de consulta.

## O Padrão VO

O padrão VO (Value Object) é um padrão de projeto de software que representa dados simples e imutáveis, agrupando-os em um único objeto.

# Iniciando o Projeto

Abra o IDE IntelliJ e acesse a opção *New Project*. Em seguida, dê o nome de Vendas para o projeto, informe a localização onde você deseja salvar o projeto. Em *Build system*, marque a opção *Maven*. Em JDK selecione a versão 21 e clique no botão *Create*. Abaixo a imagem com as configurações necessárias para o projeto:

![image](https://github.com/user-attachments/assets/7b32c574-f8bf-43f0-9414-2d11e3c64718)


Maven é uma ferramenta de automação e gerenciamento de projetos Java, usada principalmente para gerenciar dependências, compilar código, rodar testes e empacotar a aplicação. Ele utiliza um arquivo XML chamado pom.xml (Project Object Model) para definir configurações do projeto e dependências externas. 

Após criar o projeto, abra o arquivo *pom.xml*. Ele deve estar como imagem abaixo:

![image](https://github.com/user-attachments/assets/1cdb4875-f6ae-4457-8548-96455f5dee74)


No arquivo *pom.xml*, logo abaixo da tag ```<properties>...</properties>```, vamos adicionar as dependências do Hibernate, do PostgreSQL e do JUnit, dentro da tag ```<dependencies></dependencies>```. Fazendo dessa forma, o Maven vai gerenciar essas dependências, trazendo automaticamente para o projeto as bibliotecas externas (.jar). A imagem abaixo mostra o arquivo com as dependências informadas (circuladas em vermelho):

![image](https://github.com/user-attachments/assets/ba28c521-dc48-484a-b650-352e720a8729)


Após inserir as dependências no arquivo, clique no botão do “m” azul, que está circulado de vermelhor na imagem acima, para atualizar as dependências e baixar as bibliotecas no projeto.

Essas dependências, e outras mais, podem ser encontradas no repositório Maven Repository disponível endereço https://mvnrepository.com/.

Na sequência, na pasta *resources* do projeto, vamos criar a pasta **META-INF** e dentro dela criar o arquivo *persistence.xml*. Nesse arquivo, vamos configurar as propriedades para conectar-se ao banco de dados PostgreSQL:

```xml
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.1" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    <persistence-unit name="PostgresPU" transaction-type="RESOURCE_LOCAL">
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver"/>
            <!--  DB Driver  -->
            <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost/Pedidos"/>
            <!--  BD url  -->
            <property name="javax.persistence.jdbc.user" value="postgres"/>
            <!--  DB User  -->
            <property name="javax.persistence.jdbc.password" value="postgres"/>
            <!--  DB Password  -->
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
            <!--  DB Dialect  -->
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <!--  create / create-drop / update  -->
            <property name="hibernate.show_sql" value="true"/>
            <!--  Show SQL in console  -->
            <property name="hibernate.format_sql" value="true"/>
            <!--  Show SQL formatted  -->
        </properties>
    </persistence-unit>
</persistence>
```

O arquivo *persistence.xml* é fundamental para configurar a persistência de dados em aplicações Java que utilizam JPA com um provedor ORM, como o Hibernate. Ele define as configurações da unidade de persistência e as propriedades relacionadas ao banco de dados:

   • ```<persistence version="2.1" ...>```, versão 2.1 da JPA.
   
   • ```persistence-unit```, define a unidade de persistência. Cada ```persistence-unit``` recebe um nome, no caso do projeto Vendas, é ```"PostgresPU"```. Esse nome é usado para identificar essa configuração dentro da aplicação. A ```transaction-type``` é o tipo de transação e é ```"RESOURCE_LOCAL"```, que significa que as transações serão gerenciadas pela própria aplicação e não por um servidor de aplicações. 
     
  • ```<properties> </properties>```, contém as configurações de conexão e outros detalhes importantes para o JPA/Hibernate, tais como: 
  
   ◦ Driver do banco de dados: ```<property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" /> ```
   
   ◦ Url de conexão: ```<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost/Pedidos" /> ```
   
   ◦ Usuário e senha: 
   
   ```<property name="javax.persistence.jdbc.user" value="postgres" /> ```
   
   ```<property name="javax.persistence.jdbc.password" value="postgres" /> ```
   
   ◦ Dialeto do Hibernate que define o dialeto SQL que o Hibernate deve usar, o que garante a geração correta das queries para o banco PostgreSQL: ```<property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/> ```
   
   ◦ Estratégia de gerenciamento de schema, que configura como o Hibernate deve lidar com o esquema de banco de dados. No caso de ```"update"```, ele atualiza as tabelas sem apagá-las. Outras opções incluem: ```“create"``` cria as tabelas, sobrescrevendo as existentes, ```"create-drop"``` cria as tabelas e as apaga ao final da sessão, e, ```"validate"``` apenas valida se o esquema está correto, sem modificá-lo.
   ```<property name="hibernate.hbm2ddl.auto" value="update" /> ```
   
   ◦ Exibição de SQL no console, define se as queries SQL geradas pelo Hibernate serão exibidas no console e se serão formatadas para melhor legibilidade. 
   
   ```<property name="hibernate.show_sql" value="true" /> ```
   
   ```<property name="hibernate.format_sql" value="true" /> ```

Antes de dar continuidade no projeto, será criado o banco de dados no PostgreSQL. Apenas o banco, pois as tabelas serão criadas automaticamente pela aplicação. Para isso, pode ser utilizado o PGAdmin, ou outro administrador de banco de dados que tenha conexão com o PostgreSQL. O nome do banco de dados deverá ser **Pedidos**.

Caso tenha dúvidas, ou dificuldades, com o processo de instalação do PostgreSQL, ou com o processo para criar a base de dados **Pedidos**, sugere-se consultar os links a seguir:

- Vídeo em https://www.youtube.com/watch?v=His77sqWfGU (duração 00:06:23) que ensina como instalar e configurar o PostgreSQL.

- Vídeo em https://www.youtube.com/watch?v=bA2nW6PFlio (duração 00:07:56) que ensina a criar um BD usando o PGAdmin.


## Estrutura do Projeto

O projeto será organizado nos seguintes pacotes:

   • **model:** Contém as classes de entidade.
    
   • **dao:** Contém as classes de acesso a dados.
    
   • **service:** Contém as classes com a lógica do negócio
    
   • **vo:** Contém classes que representam resumos e relatórios de dados
    
   • **util:** Contém a classe de utilitário para JPA.
    
   • **test:** Contém os testes unitários.
    
    
Após criar os pacotes indicados acima, a estrutura do projeto deverá estar como na imagem abaixo: 

![image](https://github.com/user-attachments/assets/2615c9d0-a958-4577-80e8-18e62680ccbc)

A estrutura usada neste projeto, não segue a arquitetura clássica do MVC. Porém, a ideia da separação das responsabilidades está presente. 

Por ser uma aplicação por linha de comando, classes de camada View não estão presentes. A função Main "desempenha" o papel de Controller, ela recebe as ações do usuário, processa e envia para as classes services. As classes services possuem as regras do negócio e interagem com as classes DAO. Já as classes DAO possuem acesso aos dados e interagem com as classes de entidades. As classes service, DAO e entidades são representaçãoes de classes de camada Model.

Agora, será dado o início da crição das classes do projeto. Primeiramente, serão criadas as classes entidades (Entity). As classes do tipo entidades possuem atributos e relacionamentos que serão persistidos no banco de dados. Elas devem ser criadas no pacote **model**:

### Classe Categoria:

```java
package br.com.model;

// Importação das anotações necessárias do JPA (Java Persistence API).
import jakarta.persistence.*;

@Entity // Define que a classe é uma entidade JPA, ou seja, será mapeada para uma tabela no banco de dados.
@Table(name = "categorias") // Define o nome da tabela no banco de dados que essa entidade irá representar. 
// Neste caso, a tabela 'categorias'.
public class Categoria {

    // Atributo que será usado como chave primária na tabela do banco de dados.
    @Id // Indica que o campo 'id' é a chave primária da tabela.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define a estratégia de geração do valor da chave primária. 
                                                        // 'IDENTITY' significa que o valor será gerado automaticamente 
                                                        // pelo banco de dados.
    private Long id;

    // Atributo que armazena o nome da categoria. Este será um campo na tabela 'categorias'.
    private String nome;

    // Construtor padrão sem parâmetros. Necessário para que o JPA possa instanciar a classe ao recuperar dados do BD.
    public Categoria() {}

    // Construtor com o parâmetro 'nome'. Permite criar uma instância de 'Categoria' com um nome específico.
    public Categoria(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    // Sobrescrita do metodo 'toString', usado para retornar uma representação em String da instância da classe.
    // Isso é útil para depuração e exibição de dados da entidade de forma legível.
    @Override
    public String toString() {
        // StringBuilder é utilizado para construir a string de forma eficiente.
        StringBuilder sb = new StringBuilder();
        sb.append("Categoria{") 
                .append("id=").append(id) 
                .append(", nome=").append(nome) 
                .append('}'); 
        return sb.toString(); 
    }
}
```
### Classe Produto:

```java
package br.com.model;

import jakarta.persistence.*;
import java.math.BigDecimal; // Tipo de dado para manipulação de valores monetários com precisão.
import java.time.LocalDate; // Tipo de dado para trabalhar com datas.

@Entity // Indica que esta classe é uma entidade JPA e será mapeada para uma tabela no banco de dados.
@Table(name = "produtos") // Define o nome da tabela que esta entidade representa, no caso 'produtos'.
public class Produto {

	@Id // Define o campo 'id' como a chave primária da entidade.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Configura a geração automática do valor do 'id' pelo banco de dados.
	private Long id;

	// Campos simples que representam colunas da tabela no banco de dados.
	private String nome; // Nome do produto.
	private String descricao; // Breve descrição do produto.
	private BigDecimal preco; // Preço do produto, utilizando BigDecimal para precisão em cálculos financeiros.

	@ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com a entidade Categoria. 'fetch = FetchType.LAZY' indica carregamento tardio.
	@JoinColumn(name = "categoria_id") // Define a chave estrangeira que associa o produto à sua categoria.
	private Categoria categoria; // Referência à entidade Categoria.

	public Produto() {
	}

	public Produto(String nome, String descricao, BigDecimal preco, Categoria categoria) {
		this.nome = nome;
		this.descricao = descricao;
		this.preco = preco;
		this.categoria = categoria;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getPreco() {
		return preco;
	}

	public void setPreco(BigDecimal preco) {
		this.preco = preco;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Produto{")
		  .append("id=").append(id) 
		  .append(", nome=").append(nome) 
		  .append(", descricao=").append(descricao) 
		  .append(", preco=").append(preco) 
		  .append(", categoria=").append(categoria) 
		  .append('}');
		return sb.toString(); 
	}
}
```

Comentários sobre a classe Produto:

**Relacionamento @ManyToOne:** O atributo categoria estabelece uma relação muitos-para-um com a classe Categoria. Isso significa que vários produtos podem pertencer a uma mesma categoria. A anotação ```@JoinColumn``` especifica o nome da coluna no banco que representa essa relação (categoria_id).

**Lazy Loading:** A configuração ```fetch = FetchType.LAZY``` no relacionamento indica que os dados da categoria serão carregados apenas quando forem acessados explicitamente. Isso otimiza o uso de recursos, especialmente quando as relações têm muitos dados.

**BigDecimal:** Usado para valores monetários e cálculos financeiros devido à sua alta precisão, essencial em sistemas que lidam com preços.

### Classe Cliente:

```java
package br.com.model;

import jakarta.persistence.*;

@Entity 
@Table(name = "clientes")
public class Cliente {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    private String nome;
    private String cpf;

    public Cliente() {
    }
    
    public Cliente(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente{")
                .append("id=").append(id)
                .append("nome=").append(nome)
                .append("CPF=").append(cpf)
                .append('}');
        return sb.toString();
    }
}
```

### Classe Pedido:

```java
package br.com.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_total") // Define o nome da coluna no banco como 'valor_total'.
    private BigDecimal valorTotal = BigDecimal.ZERO; // Armazena o valor total do pedido, iniciando com zero.

    private LocalDate data = LocalDate.now(); // Armazena a data do pedido, inicializando com a data atual.

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com a entidade Cliente.
    private Cliente cliente; // Cliente associado ao pedido.

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL) // Relacionamento um-para-muitos com a entidade PedidoItem.
    private List<PedidoItem> itens = new ArrayList<>(); // Lista de itens do pedido.

    public Pedido() {
    }

    // Construtor que inicializa o pedido com um cliente.
    public Pedido(Cliente cliente) {
        this.cliente = cliente;
    }

    // Metodo para adicionar um item ao pedido.
    public void adicionarItem(PedidoItem item) {
        item.setPedido(this); // Define a referência do pedido no item.
        this.getItens().add(item); // Adiciona o item à lista.
        processaValorTotal(); // Atualiza o valor total do pedido.
    }

    // Metodo para remover um item do pedido.
    public void removerItem(PedidoItem item) {
        this.getItens().remove(item); // Remove o item da lista.
        processaValorTotal(); // Atualiza o valor total do pedido.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<PedidoItem> getItens() {
        return itens;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido{")
                .append("id=").append(id)
                .append(", valorTotal=").append(valorTotal)
                .append(", data=").append(data)
                .append(", cliente=").append(cliente.getNome())
                .append(", itens=[");

        for (int i = 0; i < itens.size(); i++) {
            PedidoItem item = itens.get(i);
            sb.append("{")
                    .append("produto=").append(item.getProduto().getNome())
                    .append(", quantidade=").append(item.getQuantidade())
                    .append(", valor=").append(item.getValor())
                    .append("}");
            if (i < itens.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    // Metodo privado para calcular e atualizar o valor total do pedido com base nos itens.
    private void processaValorTotal() {
        BigDecimal total = itens.stream() // Processa a lista de itens.
                .map(PedidoItem::getValor) // Mapeia os valores dos itens.
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os valores.
        this.valorTotal = total; // Atualiza o valor total do pedido.
    }
}
```

Comentários sobre a classe Pedido:

**Relacionamento @ManyToOne com Cliente:** Indica que vários pedidos podem pertencer a um único cliente. Utiliza carregamento lazy para otimizar o uso de memória, carregando o cliente apenas quando necessário.

**Relacionamento @OneToMany com PedidoItem:** Define que um pedido pode conter vários itens. A anotação ```cascade = CascadeType.ALL``` garante que operações no pedido (como salvar ou deletar) serão propagadas para seus itens.

**Cálculo do valor total:** Feito no método ```processaValorTotal``` com Streams, que iteram sobre os itens e somam seus valores.

### Classe PedidoItem:

```java
package br.com.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity 
@Table(name = "pedido_itens") 
public class PedidoItem {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(name = "preco_unitario") // Mapeia o atributo para a coluna 'preco_unitario' no banco.
    private BigDecimal precoUnitario; 

    private int quantidade; 

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com a entidade Pedido.
    private Pedido pedido; // Pedido ao qual este item pertence.

    @ManyToOne(fetch = FetchType.LAZY) // Relacionamento muitos-para-um com a entidade Produto.
    private Produto produto; // Produto relacionado a este item.

    public PedidoItem() {
    }

    public PedidoItem(int quantidade, Pedido pedido, Produto produto) {
        this.quantidade = quantidade; 
        this.pedido = pedido; 
        this.precoUnitario = produto.getPreco(); 
        this.produto = produto; 
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    // Calcula e retorna o valor total do item (preço unitário * quantidade).
    public BigDecimal getValor() {
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }
}
```

Comentários sobre a classe PedidoItem:

**Relacionamento com Pedido e Produto:** O ```@ManyToOne``` indica que vários itens podem estar associados ao mesmo pedido ou produto.
O carregamento lazy (```fetch = FetchType.LAZY```) evita carregar automaticamente os dados de Pedido e Produto até que sejam acessados.

**Cálculo do valor total:** O método ```getValor()``` retorna o valor total do item, calculado como precoUnitario * quantidade.

Com as classes definidas e mapeadas para Entity, agora serão definidas as classes para acesso aos dados, as classes DAO. Elas devem ser criadas no pacote **dao**.

Antes de criar as classes DAO, criaremos uma classe utilitária para gerenciar a criação de EntityManager, usada para operações com o banco de dados. Essa classe será criada dentro de um novo pacote chamado util.
```java
package br.com.util;

// Importação das classes necessárias para gerenciar entidades no JPA.
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

// Classe utilitária para gerenciar a criação de EntityManager, usada para operações com o banco de dados.
public class JPAUtil {

	// Criação de uma única instância de EntityManagerFactory para gerenciar a conexão com o banco.
	// O "PostgresPU" deve corresponder ao nome da unidade de persistência definida no arquivo persistence.xml.
	private static final EntityManagerFactory FACTORY = Persistence
			.createEntityManagerFactory("PostgresPU");

	// Metodo para obter um EntityManager, cada chamada cria uma nova instância de EntityManager
	// a partir do EntityManagerFactory.
	public static EntityManager getEntityManager() {

		return FACTORY.createEntityManager();
	}
	
}
```

Como já descrito anteriormente, as classes DAO são dedicadas a gerenciar a interação entre a aplicação e o banco de dados. Elas encapsulam toda a lógica de acesso aos dados, como consultas, inserções, atualizações e exclusões, promovendo a separação de responsabilidades no código. Ao centralizar essas operações, as classes DAO tornam o sistema mais organizado, facilitam a manutenção e permitem que outras camadas da aplicação (como a de negócio ou a de apresentação) permaneçam desacopladas da lógica de persistência, garantindo maior modularidade e flexibilidade no desenvolvimento. 

Para entender os métodos que serão utilizados pelas classes DAO, vamos antes entender sobre a EntityManager e seus estados.

## EntityManager

A EntityManager é a principal interface do JPA usada para gerenciar entidades e realizar operações com o banco de dados. Os principais métodos do EntityManager e suas finalidades são:

### 1. Gerenciamento de Entidades
- persist(Object entity): Persiste (insere) uma nova entidade no banco de dados. A entidade deve estar no estado transiente (não gerenciada pelo contexto de persistência).
- merge(Object entity): Atualiza os dados de uma entidade no banco de dados. Usado para entidades no estado desanexado (detached) para sincronizá-las novamente com o banco.
- remove(Object entity): Remove uma entidade gerenciada do banco de dados. A entidade deve estar no estado gerenciado (managed).
- find(Class<T> entityClass, Object primaryKey): Busca uma entidade no banco de dados pelo seu identificador (chave primária). Retorna a entidade no estado gerenciado, ou null se não for encontrada.
- contains(Object entity): Verifica se uma entidade está no estado gerenciado pelo contexto de persistência.

### 2. Gerenciamento de Transações
- getTransaction(): Retorna o objeto EntityTransaction, permitindo gerenciar transações explicitamente em contextos não gerenciados pelo contêiner (Java SE).
- flush(): Sincroniza o estado do contexto de persistência com o banco de dados (envia as alterações pendentes).
- clear(): Remove todas as entidades do contexto de persistência, deixando-as no estado desanexado.
- close(): Fecha o EntityManager, liberando todos os recursos associados.
- lock(Object entity, LockModeType lockMode): Aplica um bloqueio a uma entidade gerenciada para garantir consistência em cenários de concorrência.
  
### 3. Consultas
- createQuery(String jpql, Class<T> resultClass): Cria uma consulta usando JPQL (Java Persistence Query Language) e especifica o tipo do resultado esperado.
- createNamedQuery(String name, Class<T> resultClass): Cria uma consulta predefinida usando o nome registrado em uma entidade.
- createNativeQuery(String sql): Cria uma consulta usando SQL nativo, permitindo maior controle e flexibilidade para casos específicos.
- getReference(Class<T> entityClass, Object primaryKey): Retorna uma instância proxy de uma entidade pelo seu identificador, sem carregar os dados imediatamente (carregamento lazy).

Esses métodos permitem gerenciar o ciclo de vida das entidades, realizar operações CRUD e interagir com o banco de dados de forma eficiente e controlada. A seguir, são apresentados os estados que as entidades passam durante o ciclo de vida.

### Os Estados das Entidades na JPA

Na JPA, as entidades passam por diferentes estados durante seu ciclo de vida.A seguir, serão explorados os estados: transiente, gerenciado, desanexado (detached) e removido:

### Estado Transiente
Uma entidade está no estado transiente quando foi criada na memória, mas ainda não foi associada ao banco de dados nem ao contexto de persistência do JPA.
Uma entidade recém-criada, que não possui um identificador ou chave primária, ou seja, não está em sincronia com o banco de dados. Exemplo:
```java
Categoria categoria = new Categoria(); // Entidade no estado transiente.
categoria.setNome("Tecnologia"); // Modificação em memória, sem relação com o banco.
```

### Estado Gerenciado
Uma entidade está no estado gerenciado quando está sendo acompanhada pelo EntityManager. Nesse estado, o JPA sincroniza automaticamente as alterações realizadas na entidade com o banco de dados durante a transação. Isso ocorre quando a entidade em memória está associada a um registro específico da tabela no banco (identificada por um ID). Enquanto permanece nesse estado, a entidade é controlada pelo Contexto de Persistência, permitindo que qualquer modificação feita nela seja detectada pelo Hibernate, que então traduz e sincroniza as mudanças com o banco de dados. 
Uma entidade se torna gerenciada quando:
- Ao ser persistida com o método persist().
- Ao ser recuperada do banco de dados com find() ou createQuery().
- Ao ser associada novamente com merge().

Exemplo:
```java
EntityManager em = JPAUtil.getEntityManager();
Categoria categoria = new Categoria();
categoria.setNome("Tecnologia");

em.getTransaction().begin();
em.persist(categoria); // Agora a entidade está gerenciada.
em.getTransaction().commit(); // Alterações são salvas no banco.
```

### Estado Detached (desanexado)
Uma entidade está no estado detached quando foi gerenciada anteriormente, mas agora não está mais associada ao contexto de persistência. É quando a entidade é removida do Contexto de Persistência, então a entidade não tem suas alterações em memoria propagadas ao banco de dados. Isso pode acontecer, por exemplo, quando o EntityManager é fechado ou a entidade é explicitamente desanexada. 
Uma entidade se torna desanexada quando:

- Quando o método detach() é chamado.
- Quando o contexto de persistência é limpo com clear() ou fechado com close().
- Quando a entidade foi recuperada, mas o EntityManager já foi encerrado.

Exemplo:
```java
EntityManager em = JPAUtil.getEntityManager();
Categoria categoria = em.find(Categoria.class, 1L); // Entidade gerenciada.

em.close(); // O EntityManager é fechado.
categoria.setNome("Atualizado"); // Modificação em memória, mas não será salva no banco.
```
### Estado Removido
Uma entidade está no estado removido quando foi marcada para exclusão no banco de dados, mas a exclusão ainda não foi confirmada (geralmente até o commit da transação). Isso ocorre quando o método remove() é chamado em uma entidade gerenciada. Nesse estado:
- A entidade continua vinculada ao contexto de persistência.
- Não será mais considerada nas operações de sincronização, exceto para ser excluída no banco de dados.
- Após a exclusão, a entidade pode se tornar transiente.

Exemplo:
```java
EntityManager em = JPAUtil.getEntityManager();
Categoria categoria = em.find(Categoria.class, 1L); // Entidade gerenciada.
em.getTransaction().begin();
em.remove(categoria); // Agora a entidade está no estado removido.
em.getTransaction().commit(); // Exclusão confirmada no banco de dados.
```

### Comparação dos Estados:

A tabela abaixo resume os principais estados e suas características:

| **Estado**    | **Persistido no Banco?** | **Monitorado pelo JPA?** | **Alterações Sincronizadas?** | **Exemplo de Transição**                    |
|---------------|---------------------------|---------------------------|-------------------------------|---------------------------------------------|
| **Transiente** | Não                      | Não                      | Não                          | Nova entidade criada com `new`.            |
| **Gerenciado** | Sim                      | Sim                      | Sim                          | Após `persist()`, `merg()` ou `find()`.    |
| **Desanexado** | Sim                      | Não                      | Não                          | Após `detach()`, `clear()` ou `close()`.   |
| **Removido**   | Sim (até commit)         | Sim                      | Não                          | Após `remove()` em uma entidade gerenciada.|

### Resumo dos Estados

- **Transiente**: A entidade existe apenas na memória e não está associada ao banco de dados nem ao contexto de persistência.
- **Gerenciado**: A entidade está associada ao contexto de persistência, e suas alterações são sincronizadas automaticamente com o banco de dados.
- **Desanexado**: A entidade foi removida do contexto de persistência, mas ainda existe no banco. Alterações nela não serão sincronizadas.
- **Removido**: A entidade está marcada para exclusão no banco, mas a exclusão só será efetivada após o `commit` da transação.

A Figura abaixo mostra um diagrama que ilustra como uma entidade transita de um estado para outro e quais operações do EntityManager são propagadas:

![image](https://github.com/user-attachments/assets/6cf31c4b-b6b5-4ffe-b8a0-2dded85c1d7e)

**Fonte:** [Vlad Mihalcea](https://x.com/vlad_mihalcea) em [Dev.to](https://dev.to/jordihofc/3-dicas-para-uso-eficiente-de-jpahibernate-42f9)


## Classes DAO

A seguir, serão apresentadas as classes DAO do projeto Vendas, iniciando pela CategoriaDao (que deverá ser criada dentro do pacote dao):
```java
package br.com.dao;

import br.com.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

// Classe DAO (Data Access Object) para gerenciar as operações de persistência da entidade Categoria.
public class CategoriaDao {

	// Instância de EntityManager, usada para realizar as operações no banco de dados.
	private EntityManager em;

	// Construtor que recebe um EntityManager como parâmetro.
	public CategoriaDao(EntityManager em) {
		this.em = em;
	}

	// Metodo para cadastrar uma nova categoria no banco de dados.
	public void cadastrar(Categoria categoria) {
		this.em.getTransaction().begin(); // Inicia uma transação.
		this.em.persist(categoria); // Persiste a entidade no banco de dados.
		this.em.getTransaction().commit(); // Finaliza a transação e salva as alterações.
	}

	// Metodo para atualizar uma categoria existente no banco de dados.
	public void atualizar(Categoria categoria) {
		this.em.getTransaction().begin();// Inicia uma transação.
		this.em.merge(categoria);// Atualiza a entidade no banco de dados.
		this.em.getTransaction().commit();// Finaliza a transação e salva as alterações.
	}

	// Metodo para remover uma categoria do banco de dados.
	public void remover(Categoria categoria) {
		this.em.getTransaction().begin();// Inicia uma transação.
		this.em.remove(categoria);// Remove a entidade do banco de dados.
		this.em.getTransaction().commit();// Finaliza a transação e salva as alterações.
	}

	// Metodo para buscar uma categoria pelo seu ID.
	public Categoria buscarPorId(Long id) {
		return em.find(Categoria.class, id);// Busca a entidade Categoria pelo ID.
	}

	// Metodo para buscar todas as categorias no banco de dados.
	public List<Categoria> buscarTodos() {
		String jpql = "SELECT c FROM Categoria c"; // Consulta JPQL para retornar todas as categorias.
		return em.createQuery(jpql, Categoria.class).getResultList(); // Executa a consulta e retorna os resultados.
	}

	// Metodo para buscar categorias pelo nome.
	public List<Categoria> buscarPorNome(String nome) {
		String jpql = "SELECT c FROM Categoria c WHERE c.nome = :nome"; // Consulta JPQL para buscar categorias por nome.
		return em.createQuery(jpql, Categoria.class)
				.setParameter("nome", nome) // Define o parâmetro "nome" na consulta.
				.getResultList(); // Executa a consulta e retorna os resultados.
	}
}
```
### Classe ClienteDao
A classe ClienteDao é semelhante a CategoriaDao, a lógica para implementar os métodos atualizar e remover, é a mesma.
```java
package br.com.dao;

import br.com.model.Cliente;
import jakarta.persistence.EntityManager;

import java.util.List;

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

	public List<Cliente> buscarTodos() {
		String jpql = "SELECT c FROM Cliente c";
		return em.createQuery(jpql, Cliente.class).getResultList();
	}
}
```

### Classe ProdutoDao

```java
public class ProdutoDao {

	private EntityManager em;

	public ProdutoDao(EntityManager em) {
		this.em = em;
	}

	public void cadastrar(Produto produto) {
		this.em.getTransaction().begin();
		this.em.persist(produto);
		this.em.getTransaction().commit();
	}

	public void atualizar(Produto produto) {
		this.em.getTransaction().begin();
		this.em.merge(produto);
		this.em.getTransaction().commit();
	}

	public void remover(Produto produto) {
		this.em.getTransaction().begin();
		this.em.remove(produto);
		this.em.getTransaction().commit();
	}
	
	public Produto buscarPorId(Long id) {
		return em.find(Produto.class, id);
	}
	
	public List<Produto> buscarTodos() {
		String jpql = "SELECT p FROM Produto p";
		return em.createQuery(jpql, Produto.class).getResultList();
	}
	
	public List<Produto> buscarPorNome(String nome) {
		String jpql = "SELECT p FROM Produto p WHERE p.nome = :nome";
		return em.createQuery(jpql, Produto.class)
				.setParameter("nome", nome)
				.getResultList();
	}

	public List<Produto> buscarPorCategoria(long idCategoria) {
		String jpql = "SELECT p FROM Produto p WHERE p.categoria.id = :id";
		//String jpql = "SELECT p FROM Produto p JOIN p.categoria c WHERE c.id = :id"; //ou
		return em.createQuery(jpql, Produto.class)
				.setParameter("id", idCategoria)
				.getResultList();
	}
}
```

### Classe PedidoDao

```java
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
```
 As classes DAO interagem com o banco de dados e para interagir com as classes Dao, iremos criar as classes de serviço no pacote service. 
 O objetivo dessas classes é abstrair a complexidade de acesso aos dadose melhorar a organização do código.

### Classe CategoriaService
```java
package br.com.service;

import br.com.dao.CategoriaDao;
import br.com.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaService {
    private CategoriaDao categoriaDao;

    public CategoriaService(EntityManager em){
        categoriaDao = new CategoriaDao(em);
    }

    public void inserir(Categoria categoria){
        categoriaDao.cadastrar(categoria);
    }

    public void alterar(Categoria categoria){
        categoriaDao.atualizar(categoria);
    }

    public void excluir(Categoria categoria){
        categoriaDao.remover(categoria);
    }

    public Categoria buscarCategoriaPorId(long id){
        return categoriaDao.buscarPorId(id);
    }

    public List<Categoria> buscarTodosAsCategorias(){
        return categoriaDao.buscarTodos();
    }
}
```

### Classe ProdutoService
```java
public class ProdutoService {
    private ProdutoDao produtoDao;

    public ProdutoService(EntityManager em){
        produtoDao = new ProdutoDao(em);
    }

    public void inserir(Produto produto){
        produtoDao.cadastrar(produto);
    }

    public void alterar(Produto produto){
        produtoDao.atualizar(produto);
    }

    public void excluir(Produto produto){
        produtoDao.remover(produto);
    }

    public Produto buscarProdutoPorId(long id){
        return produtoDao.buscarPorId(id);
    }

    public List<Produto> buscarTodosOsProdutos(){
        return produtoDao.buscarTodos();
    }

    public List<Produto> buscarProdutoPorNome(String nome){
        return produtoDao.buscarPorNome(nome);
    }

    public List<Produto> buscarProdutosDaCategoria(long idCategoria){
        return produtoDao.buscarPorCategoria(idCategoria);
    }
}
```

### Classe PedidoService
```java
public class PedidoService {
    private PedidoDao pedidoDao;

    public PedidoService(EntityManager em){
        this.pedidoDao = new PedidoDao(em);
    }

    public void inserir(Pedido pedido){
        pedidoDao.cadastrar(pedido);
    }

    public void alterar(Pedido pedido){
        pedidoDao.atualizar(pedido);
    }

    public void excluir(Pedido pedido){
        pedidoDao.remover(pedido);
    }

    public void excluirItem(PedidoItem pedidoItem){pedidoDao.removerItem(pedidoItem); }

    public Pedido buscarPedidoPorId(long id){
        return pedidoDao.buscarPedidoPoriD(id);
    }

    public List<Pedido> buscarPedidoPorPeriodo(LocalDate dataIni, LocalDate dataFim){
        return pedidoDao.buscarPedidosPorPeriodo(dataIni, dataFim);
    }

    public List<Pedido> buscarPedidoDeUmCliente(long id){
        return pedidoDao.buscarPedidosDeUmCliente(id);
    }
}
```

### Funcionalidades de Relatórios

Neste projeto, existem 3 funcionalidades sobre relatórios:
- Consultar Valor Total Vendido
- Consultar Relatório de Vendas
- Consultar Relatório Financeiro

Para implementar estas funcionalidades, serão usadas classes do tipo VO. Elas serão usadas para agrupar os dados para os relatórios. Elas devem ser criadas em um pacote chamado vo.

### Classe RelatorioDeVendasVo
```java
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
```

### Classe RelatorioFinanceiroVo
```java
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
```

Alémm das classes vo, para implementar os relatóios se fazem necessárias outras classes, tais como a seguir:

### Classe VendaDao
```java
public class VendaDAO {

    private EntityManager em;

    public VendaDAO(EntityManager em) {
        this.em = em;
    }

    public BigDecimal retornaValorTotalVendidoEmUmPeriodo(LocalDate dataIni, LocalDate dataFim) {
        String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.data BETWEEN :dataIni AND :dataFim";
        BigDecimal total = em.createQuery(jpql, BigDecimal.class)
                .setParameter("dataIni", dataIni)
                .setParameter("dataFim", dataFim)
                .getSingleResult();
        if (total == null) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    /* SELECT NEW em JPQL é indicado em situações onde se quer apenas uma parte dos dados das entidades e
       se quer encapsulá-los num objeto específico, como um VO (Value Object), por exemplo. É muito utilizado
       para gerar relatórios ou resumos que utilizam funções de agregação tais: como sum, max, min, count.
       Resumindo: utiliza-se o select new quando o resultado da consulta não é uma entidade mapeada,
       desta forma, é necessário indicar a classe que será retornada. */
    public List<RelatorioDeVendasVo> relatorioDeVendas() {
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
    }

    public List<RelatorioFinanceiroVo> relatorioFinanceiro() {
        String jpql = "SELECT new br.com.vo.RelatorioFinanceiroVo("
                + "cliente.nome, "
                + "SUM(pedido.valorTotal)) "
                + "FROM Pedido pedido "
                + "JOIN pedido.cliente cliente "
                + "GROUP BY cliente.nome "
                + "ORDER BY SUM(pedido.valorTotal) DESC";
        return em.createQuery(jpql, RelatorioFinanceiroVo.class)
                .getResultList();
    }
}
```

### Classe VendaService
```java
public class VendaService {
    private VendaDAO vendasDAO;

    public VendaService(EntityManager em){
        this.vendasDAO = new VendaDAO(em);
    }

    public BigDecimal retornaValorTotalVendido(LocalDate dataIni, LocalDate dataFim){
        return this.vendasDAO.retornaValorTotalVendidoEmUmPeriodo(dataIni, dataFim);
    }

    public List<RelatorioDeVendasVo> retornaRelatorioDeVendas(){
        return this.vendasDAO.relatorioDeVendas();
    }

    public List<RelatorioFinanceiroVo> retornaRelatorioFinanceiro(){
        return this.vendasDAO.relatorioFinanceiro();
    }
}
```
## Leitura sugerida
Sobre consultas avançadas com JPQL, é sugerido a leitura sobre:
- [NamedQuery](https://blog.triadworks.com.br/como-organizar-consultas-jpql-named-queries-ou-queries-dinamicas#:~:text=Uma%20Named%20Query%20nada%20mais,entidades%20atrav%C3%A9s%20da%20anota%C3%A7%C3%A3o%20%40NamedQuery)
- [Criteria](https://vanderloureiro.medium.com/filtros-avan%C3%A7ados-com-jpa-criteria-58fbe92f5171#:~:text=O%20que%20%C3%A9%20o%20JPA,de%20buscas%20atrav%C3%A9s%20de%20concatena%C3%A7%C3%A3o)



## Classe Main
Para testar as funcionalidades implementadas, iremos fazer as chamadas na classe Main:
```java
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static EntityManager em;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU");
        em = emf.createEntityManager();

        CategoriaService categoriaService = new CategoriaService(em);
        ProdutoService produtoService = new ProdutoService(em);
        PedidoService pedidoService = new PedidoService(em);
        VendaService vendaService =  new VendaService(em);

        boolean continuar = true;

        while (continuar) {
            System.out.println("----- MENU -----");
            System.out.println("1. Cadastrar Categoria");
            System.out.println("2. Alterar Categoria");
            System.out.println("3. Excluir Categoria");
            System.out.println("4. Consultar Categoria por ID");
            System.out.println("5. Listar todas as Categorias");
            System.out.println("6. Cadastrar Produto");
            System.out.println("7. Alterar Produto");
            System.out.println("8. Excluir Produto");
            System.out.println("9. Consultar Produto por ID");
            System.out.println("10. Consultar Produtos por Nome");
            System.out.println("11. Consultar Produtos por Categoria");
            System.out.println("12. Cadastrar Pedido");
            System.out.println("13. Consultar Pedido por ID");
            System.out.println("14. Consultar Pedidos por Período");
            System.out.println("15. Consultar Pedidos de um Cliente");
            System.out.println("16. Alterar a quantidade de um item do pedido");
            System.out.println("17. Excluir um item do pedido");
            System.out.println("18. Consultar Valor Total vendido");
            System.out.println("19. Consultar Relatório de Vendas");
            System.out.println("20. Consultar Relatório Financeiro");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (opcao) {
                case 1 -> cadastrarCategoria(categoriaService);
                case 2 -> alterarCategoria(categoriaService);
                case 3 -> excluirCategoria(categoriaService);
                case 4 -> consultarCategoriaPorId(categoriaService);
                case 5 -> listarCategorias(categoriaService);
                case 6 -> cadastrarProduto(produtoService, categoriaService);
                case 7 -> alterarProduto(produtoService);
                case 8 -> excluirProduto(produtoService);
                case 9 -> consultarProdutoPorId(produtoService);
                case 10 -> consultarProdutosPorNome(produtoService);
                case 11 -> consultarProdutosPorCategoria(produtoService, categoriaService);
                case 12 -> cadastrarPedido(pedidoService, produtoService);
                case 13 -> consultarPedidoPorId(pedidoService);
                case 14 -> consultarPedidosPorPeriodo(pedidoService);
                case 15 -> consultarPedidosDeCliente(pedidoService);
                case 16 -> alterarQuantidadeItemPedido(pedidoService);
                case 17 -> excluirItemPedido(pedidoService);
                case 18 -> consultarValorTotalVendido(vendaService);
                case 19 -> consultarRelatorioVendas(vendaService);
                case 20 -> consultarRelatorioFinanceiro(vendaService);
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida!");
            }
        }

        em.close();
        emf.close();
        System.out.println("Programa encerrado.");

    }

    private static void cadastrarCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o nome da categoria: ");
        String nome = scanner.nextLine();
        Categoria categoria = new Categoria(nome);
        categoriaService.inserir(categoria);
        System.out.println("Categoria cadastrada com sucesso!");
    }

    private static void alterarCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria a ser alterada: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        if (categoria != null) {
            System.out.print("Digite o novo nome da categoria: ");
            categoria.setNome(scanner.nextLine());
            categoriaService.alterar(categoria);
            System.out.println("Categoria alterada com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void excluirCategoria(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria a ser excluída: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        if (categoria != null) {
            categoriaService.excluir(categoria);
            System.out.println("Categoria excluída com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void consultarCategoriaPorId(CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(id);
        System.out.println(categoria != null ? categoria.toString() : "Categoria não encontrada.");
    }

    private static void listarCategorias(CategoriaService categoriaService) {
        List<Categoria> categorias = categoriaService.buscarTodosAsCategorias();
        categorias.forEach(System.out::println);
    }

    private static void cadastrarProduto(ProdutoService produtoService, CategoriaService categoriaService) {
        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine();
        System.out.print("Digite a descrição do produto: ");
        String descricao = scanner.nextLine();
        System.out.print("Digite o preço do produto: ");
        BigDecimal preco = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Digite o ID da categoria: ");
        Long categoriaId = scanner.nextLong();
        scanner.nextLine();
        Categoria categoria = categoriaService.buscarCategoriaPorId(categoriaId);

        if (categoria != null) {
            Produto produto = new Produto(nome, descricao, preco, categoria);
            produtoService.inserir(produto);
            System.out.println("Produto cadastrado com sucesso!");
        } else {
            System.out.println("Categoria não encontrada.");
        }
    }

    private static void alterarProduto(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto a ser alterado: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.print("Digite o novo nome do produto: ");
            produto.setNome(scanner.nextLine());
            System.out.print("Digite a nova descrição do produto: ");
            produto.setDescricao(scanner.nextLine());
            System.out.print("Digite o novo preço do produto: ");
            produto.setPreco(scanner.nextBigDecimal());
            scanner.nextLine(); // consumir nova linha
            produtoService.alterar(produto);
            System.out.println("Produto alterado com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void excluirProduto(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto a ser alterado: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            produtoService.excluir(produto);
            System.out.println("Produto excluído com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void consultarProdutoPorId(ProdutoService produtoService) {
        System.out.print("Digite o ID do produto: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Produto produto = produtoService.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.println("Produto encontrado: " + produto);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void consultarProdutosPorNome(ProdutoService produtoService) {
        System.out.print("Digite o nome do produto: ");
        String nome = scanner.nextLine();
        List<Produto> produtos = produtoService.buscarProdutoPorNome(nome);
        if (!produtos.isEmpty()) {
            System.out.println("Produtos encontrados:");
            produtos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum produto encontrado com esse nome.");
        }
    }

    private static void consultarProdutosPorCategoria(ProdutoService produtoService, CategoriaService categoriaService) {
        System.out.print("Digite o ID da categoria: ");
        Long categoriaId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha
        List<Produto> produtos = produtoService.buscarProdutosDaCategoria(categoriaId);
        if (!produtos.isEmpty()) {
            System.out.println("Produtos da categoria:");
            produtos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum produto encontrado nessa categoria.");
        }
    }

    private static void cadastrarPedido(PedidoService pedidoService, ProdutoService produtoService) {
        System.out.print("Digite o ID do cliente: ");
        Long clienteId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        Cliente cliente = em.find(Cliente.class, clienteId);
        if (cliente != null) {
            Pedido pedido = new Pedido(cliente);

            boolean adicionarProduto;
            do {
                System.out.print("Digite o ID do produto: ");
                Long produtoId = scanner.nextLong();
                scanner.nextLine(); // consumir nova linha

                Produto produto = produtoService.buscarProdutoPorId(produtoId);
                if (produto != null) {
                    System.out.print("Digite a quantidade do produto: ");
                    int quantidade = scanner.nextInt();
                    scanner.nextLine(); // consumir nova linha

                    PedidoItem pedidoItem = new PedidoItem(quantidade, pedido, produto);

                    pedido.adicionarItem(pedidoItem);
                } else {
                    System.out.println("Produto não encontrado.");
                }

                System.out.print("Deseja adicionar outro produto? (s/n): ");
                adicionarProduto = scanner.nextLine().equalsIgnoreCase("s");
            } while (adicionarProduto);

            pedidoService.inserir(pedido);
            System.out.println("Pedido cadastrado com sucesso!");
        }else{
            System.out.println("Cliente não encontrado.");
        }
    }

    private static void consultarPedidoPorId(PedidoService pedidoService) {
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.println("Pedido encontrado: " + pedido);
        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void consultarPedidosPorPeriodo(PedidoService pedidoService) {
        System.out.print("Digite a data de início (yyyy-MM-dd): ");
        LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.print("Digite a data de fim (yyyy-MM-dd): ");
        LocalDate dataFim = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

        List<Pedido> pedidos = pedidoService.buscarPedidoPorPeriodo(dataInicio, dataFim);
        if (!pedidos.isEmpty()) {
            System.out.println("Pedidos no período selecionado:");
            pedidos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum pedido encontrado no período informado.");
        }
    }

    private static void consultarPedidosDeCliente(PedidoService pedidoService) {
        System.out.print("Digite o ID do cliente: ");
        Long clienteId = scanner.nextLong();
        scanner.nextLine(); // consumir nova linha

        List<Pedido> pedidos = pedidoService.buscarPedidoDeUmCliente(clienteId);
        if (!pedidos.isEmpty()) {
            System.out.println("Pedidos do cliente selecionado:");
            pedidos.forEach(System.out::println);
        } else {
            System.out.println("Nenhum pedido encontrado para esse cliente.");
        }
    }

    private static void alterarQuantidadeItemPedido(PedidoService pedidoService){
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine();

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.print("Digite o ID do item do pedido que deseja alterar a quantidade: ");
            Long itemPedidoId = scanner.nextLong();
            scanner.nextLine();

            List<PedidoItem> itensPedido = pedido.getItens();
            boolean encontrou = false;
            for(PedidoItem item: itensPedido){
                if(item.getId().equals(itemPedidoId)){
                    System.out.print("Digite a nova quantidade: ");
                    int quantidade = scanner.nextInt();
                    scanner.nextLine();

                    pedido.removerItem(item);
                    item.setQuantidade(quantidade);
                    pedido.adicionarItem(item);
                    pedidoService.alterar(pedido);
                    System.out.println("Quantidade alterada com sucesso! ");
                    encontrou = true;
                    break;
                }
            }
            if(!encontrou){
                System.out.println("Item do pedido não encontrado.");
            }

        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void excluirItemPedido(PedidoService pedidoService){
        System.out.print("Digite o ID do pedido: ");
        Long pedidoId = scanner.nextLong();
        scanner.nextLine();

        Pedido pedido = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedido != null) {
            System.out.print("Digite o ID do item do pedido que deseja excluir: ");
            Long itemPedidoId = scanner.nextLong();
            scanner.nextLine();

            List<PedidoItem> itensPedido = pedido.getItens();
            boolean encontrou = false;
            for(PedidoItem item: itensPedido){
                if(item.getId().equals(itemPedidoId)){
                    pedido.removerItem(item);
                    pedidoService.alterar(pedido);
                    pedidoService.excluirItem(item);
                    System.out.println("Item excluido com sucesso! ");
                    encontrou = true;
                    break;
                }
            }
            if(!encontrou){
                System.out.println("Item do pedido não encontrado.");
            }

        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void consultarValorTotalVendido(VendaService vendaService){
        System.out.print("Digite a data de início (yyyy-MM-dd) do período de consulta: ");
        LocalDate dataInicio = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        System.out.print("Digite a data de fim (yyyy-MM-dd)  do período de consulta: ");
        LocalDate dataFim = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);

        BigDecimal valorTotal = vendaService.retornaValorTotalVendido(dataInicio, dataFim);
        System.out.println("Valor total vendido no período: " + valorTotal);
    }

    private static void  consultarRelatorioVendas(VendaService vendaService){
        List<RelatorioDeVendasVo> relatorioVendas = vendaService.retornaRelatorioDeVendas();
        System.out.println("Relatório de Vendas:");
        relatorioVendas.forEach(System.out::println);
    }

    private static void  consultarRelatorioFinanceiro(VendaService vendaService){
        List<RelatorioFinanceiroVo> relatorioFinanceiro = vendaService.retornaRelatorioFinanceiro();
        System.out.println("Relatório Financeiro:");
        relatorioFinanceiro.forEach(System.out::println);
    }
}
```


# Teste Unitário com JUnit
O framework JUnit5 utiliza anotações para a identificação de métodos de teste. Por se tratar de testes unitários, eles não devem depender de outros testes para o seu funcionamento. Além das anotações, são usados métodos de asserções para validar as informações e verificar se o teste falhou ou se está ok. A seguir, as principais anotações do JUnit5:

    • @Test: é usado para anotar os métodos para serem executados como um teste;
    • @BeforeEach: método anotado com essa anotação será executado uma vez antes de cada método de teste anotado com @Test;
    • @AfterEach: método anotado com essa anotação será executado uma vez após cada método de teste anotado com @Test;
    • @BeforeAll: é executado antes de todos os testes;
    • @AfterAll: é executado após todos os testes;

Os Asserts mais comuns são:

    • assertEquals (expected, actual): Afirma que os valores esperados e reais são iguais.
    • assertNotEquals(expected, actual): Afirma que os valores esperados e os valores reais não são iguais.
    • assertTrue(condition): Isto afirma se a condição dada é verdadeira. O caso de teste passa se for verdadeira e falha se não for.
    • assertFalse(condition): Isto afirma se a condição dada é falsa. O caso de teste passa se for falsa e falha se não for.
    • assertNull(value): Isso afirma se o valor fornecido é nulo. O caso de teste passa se for nulo e falha se não for.
    • assertNotNull(value): Isso afirma se o valor fornecido não é nulo. O caso de teste passa se não for nulo e falha se não for.
    

## Classes de Teste do Projeto

### Classe CategoriaServiceTest
```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriaServiceTest {
    private EntityManager em;
    private CategoriaService categoriaService;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();
        categoriaService = new CategoriaService(em);
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();
        em.createQuery("delete from Categoria c").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void cadastrarCategoria() {
        Categoria celulares = new Categoria("CELULARES");

        categoriaService.inserir(celulares);
        Categoria categ = em.find(Categoria.class, celulares.getId());
        assertNotNull(categ);
    }

    @Test
    public void alterarCategoria(){
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        celulares.setNome("smartphones");
        categoriaService.alterar(celulares);
        Categoria prod = em.find(Categoria.class, celulares.getId());
        assertEquals("smartphones", prod.getNome());
    }

    @Test
    public void excluirCategoria(){
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        categoriaService.excluir(celulares);
        Categoria prod = em.find(Categoria.class, celulares.getId());
        assertNull(prod);
    }

    @Test
    public void consultarCategoriaPorId() {
        Categoria celulares = new Categoria("CELULARES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();

        Categoria categoria = categoriaService.buscarCategoriaPorId(celulares.getId());
        assertNotNull(celulares);
        assertEquals("CELULARES", categoria.getNome());
    }

    @Test
    public void buscarTodasAsCategorias() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria teclados = new Categoria("TECLADOS");
        Categoria mouses = new Categoria("MOUSES");
        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(teclados);
        em.persist(mouses);
        em.getTransaction().commit();


        List<Categoria> categoria = categoriaService.buscarTodosAsCategorias();
        assertFalse(categoria.isEmpty());
        assertEquals(3, categoria.size());
    }
}
```
Você pode executar toda a classe de teste numa só vez clicando em Run Test (Ctrl+Shift+F10).
Ou clicar como indicado na Figura:
![image](https://github.com/user-attachments/assets/27e37238-57fe-42ef-a319-0e078ab0bc19)

Ou, executar cada método de teste separadamente, a medida em que eles forem sendo implementados.

![image](https://github.com/user-attachments/assets/25ef9b8f-96f2-4e96-979a-386ca49bb029)

Ao executar o(s) teste(s) de uma classe, a IDE irá mostrar se os casos passaram ou não. Abaixo um exemplo onde todos os casos de teste passaram.

![image](https://github.com/user-attachments/assets/0137b11d-d9ff-42ad-a901-9c7f644a851e)



### Classe ProdutoServiceTest
```java
public class ProdutoServiceTest {
    private EntityManager em;
    private ProdutoService produtoService;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        produtoService = new ProdutoService(em);
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void cadastrarProduto() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.getTransaction().commit();


        produtoService.inserir(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertNotNull(prod);
    }

    @Test
    public void alterarProduto(){
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        celular.setNome("Xiaomi Mi 9 SE");
        produtoService.alterar(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertEquals("Xiaomi Mi 9 SE", prod.getNome());
    }

    @Test
    public void excluirProduto(){
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        produtoService.excluir(celular);
        Produto prod = em.find(Produto.class, celular.getId());
        assertNull(prod);
    }

    @Test
    public void consultarProdutoPorId() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        Produto produtoCadastrado = produtoService.buscarProdutoPorId(celular.getId());
        assertNotNull(produtoCadastrado);
    }

    @Test
    public void buscarProdutosPorNome() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        List<Produto> produtos = produtoService.buscarProdutoPorNome("Xiaomi Redmi");
        assertFalse(produtos.isEmpty());
    }

    @Test
    public void buscarProdutosPorCategoria() {
        Categoria celulares = new Categoria("CELULARES");
        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);

        em.getTransaction().begin();
        em.persist(celulares);
        em.persist(celular);
        em.getTransaction().commit();

        List<Produto> produtos = produtoService.buscarProdutosDaCategoria(celulares.getId());
        assertFalse(produtos.isEmpty());
    }
}
```
### Classe PedidoServiceTest
```java
public class PedidoServiceTest {
    private EntityManager em;
    private PedidoService pedidoService;
    private ProdutoDao produtoDao;
    private ClienteDao clienteDao;

    @BeforeEach
    public void setup() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU");
        em = emf.createEntityManager();

        pedidoService = new PedidoService(em);

        popularBancoDeDados();
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from PedidoItem ip").executeUpdate();
        em.createQuery("delete from Pedido pd").executeUpdate();
        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();
        //em.createQuery("delete from Cliente c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void cadastrarPedidos() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        pedido.adicionarItem(new PedidoItem(40, pedido, produto2));

        pedidoService.inserir(pedido);

        assertNotNull(em.find(Pedido.class, pedido.getId()));
    }

    @Test
    public void cadastrarProdutoComPrecoZero() {
        Categoria categoria = new Categoria("BRINDES");
        Produto produtoGratuito = new Produto("Caneta", "Caneta promocional", BigDecimal.ZERO, categoria);
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        em.getTransaction().begin();
        em.persist(categoria);
        em.persist(produtoGratuito);
        em.getTransaction().commit();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(5, pedido, produtoGratuito));
        pedidoService.inserir(pedido);

        assertEquals(BigDecimal.ZERO, pedido.getValorTotal());
    }

    @Test
    public void alterarQuantidadeDeItemPedido() {
        Produto produto = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        PedidoItem item = new PedidoItem(5, pedido, produto);
        pedido.adicionarItem(item);
        pedidoService.inserir(pedido);

        pedido.removerItem(item);
        item.setQuantidade(10);
        pedido.adicionarItem(item);
        pedidoService.alterar(pedido);

        Pedido pedidoAtualizado = em.find(Pedido.class, pedido.getId());
        PedidoItem itemAtualizado = pedidoAtualizado.getItens().stream()
                .filter(i -> i.getId().equals(item.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(itemAtualizado);
        assertEquals(10, itemAtualizado.getQuantidade());
    }

    @Test
    public void excluirItemPedidoERetornarValorTotalDoPedidoAtualizado() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        PedidoItem item1 = new PedidoItem(5, pedido, produto1);
        PedidoItem item2 = new PedidoItem(1, pedido, produto2);
        pedido.adicionarItem(item1);
        pedido.adicionarItem(item2);
        pedidoService.inserir(pedido);

        pedido.removerItem(item2);
        pedidoService.alterar(pedido);

        Pedido pedidoAtualizado = em.find(Pedido.class, pedido.getId());
        PedidoItem itemRemovido = pedidoAtualizado.getItens().stream()
                .filter(i -> i.getProduto().equals(item2))
                .findFirst()
                .orElse(null);

        assertNull(itemRemovido);
        assertEquals(new BigDecimal("4000"), pedidoAtualizado.getValorTotal());
    }

    @Test
    public void excluirPedido() {
        Produto produto1 = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        pedidoService.inserir(pedido);

        pedidoService.excluir(pedido);

        Pedido pedidoExcluido = em.find(Pedido.class, pedido.getId());
        assertNull(pedidoExcluido);
    }

    @Test
    public void consultarPedidoPorId() {
        Produto produto1 = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        em.getTransaction().begin();

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, produto1));
        em.persist(pedido);

        em.getTransaction().commit();

        Pedido pedidoCadastrado = pedidoService.buscarPedidoPorId(pedido.getId());
        assertNotNull(pedidoCadastrado);
    }


    @Test
    public void consultarPedidosFiltradosPorPeriodo() {
        Produto produto = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1).getSingleResult();
        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();


        Pedido pedidoOntem = new Pedido(cliente);
        pedidoOntem.adicionarItem(new PedidoItem(1, pedidoOntem, produto));
        LocalDate ontem = LocalDate.now().minusDays(1);
        pedidoOntem.setData(ontem);
        pedidoService.inserir(pedidoOntem);

        Pedido pedidoHoje = new Pedido(cliente);
        pedidoHoje.adicionarItem(new PedidoItem(1, pedidoHoje, produto));
        pedidoHoje.setData(LocalDate.now());
        pedidoService.inserir(pedidoHoje);

        Pedido pedidoAmanha = new Pedido(cliente);
        pedidoAmanha.adicionarItem(new PedidoItem(1, pedidoAmanha, produto));
        LocalDate amanha = LocalDate.now().plusDays(1);
        pedidoAmanha.setData(amanha);
        pedidoService.inserir(pedidoAmanha);


        List<Pedido> pedidosPeriodo = pedidoService.buscarPedidoPorPeriodo(ontem, amanha);
        assertEquals(3, pedidosPeriodo.size());
        assertEquals(pedidoOntem, pedidosPeriodo.getFirst());
    }

    @Test
    public void consultarPedidosDeUmCliente() {
        List<Produto> produtos = em.createQuery("SELECT p FROM Produto p", Produto.class).getResultList();
        Produto produto1 = produtos.get(0);
        Produto produto2 = produtos.get(1);

        Cliente cliente = em.createQuery("SELECT c FROM Cliente c", Cliente.class).setMaxResults(1).getSingleResult();

        Pedido pedido1 = new Pedido(cliente);
        PedidoItem item1 = new PedidoItem(5, pedido1, produto1);
        PedidoItem item2 = new PedidoItem(1, pedido1, produto2);
        pedido1.adicionarItem(item1);
        pedido1.adicionarItem(item2);
        pedidoService.inserir(pedido1);

        Pedido pedido2 = new Pedido(cliente);
        PedidoItem item3 = new PedidoItem(2, pedido2, produto2);
        pedido2.adicionarItem(item3);
        pedidoService.inserir(pedido2);

        List<Pedido> pedidosCliente = pedidoService.buscarPedidoDeUmCliente(cliente.getId());
        assertEquals(2, pedidosCliente.size());
    }

    private void popularBancoDeDados() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");
        Categoria utilitarios = new Categoria("UTILITARIOS");

        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook pro", new BigDecimal("14000"), informatica);
        Produto mouse = new Produto("Mouse", "Mouse de computador", new BigDecimal("45"), utilitarios);
        Produto teclado = new Produto("Teclado", "Teclado de computador", new BigDecimal("130"), utilitarios);

        Cliente cliente = new Cliente("Fran", "123456");
        Cliente cliente2 = new Cliente("Celso", "987654");

        em.getTransaction().begin();

        em.persist(celulares);
        em.persist(videogames);
        em.persist(informatica);
        em.persist(utilitarios);
        em.persist(celular);
        em.persist(videogame);
        em.persist(macbook);
        em.persist(mouse);
        em.persist(teclado);
        em.persist(cliente);
        em.persist(cliente2);

        em.getTransaction().commit();
    }
}
```

### Classe VendaServiceTest
```java
public class VendaServiceTest {
    private EntityManager em;
    private VendaDAO vendaDAO;

    @BeforeEach
    public void setup() {
        // Configura a conexão com o banco de dados PostgreSQL
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PostgresPU"); // Verifique seu persistence.xml
        em = emf.createEntityManager();

        vendaDAO = new VendaDAO(em);

        // Popula o banco de dados com os dados necessários para os testes
        popularBancoDeDados();
    }

    @AfterEach
    public void limparBanco() {
        em.getTransaction().begin();

        em.createQuery("delete from PedidoItem ip").executeUpdate();
        em.createQuery("delete from Pedido pd").executeUpdate();
        em.createQuery("delete from Produto p").executeUpdate();
        em.createQuery("delete from Categoria c").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void retornarValorTotalVendido() {
        BigDecimal totalVendido = vendaDAO.retornaValorTotalVendidoEmUmPeriodo(LocalDate.now(), LocalDate.now());
        assertEquals(new BigDecimal("356000.00"), totalVendido);
    }

    @Test
    public void retornarRelatorioDeVendas() {

        List<RelatorioDeVendasVo> relatorio = vendaDAO.relatorioDeVendas();
        assertNotNull(relatorio);
        assertFalse(relatorio.isEmpty());
    }

    @Test
    public void retornarRelatorioFinanceiro() {
        List<RelatorioFinanceiroVo> relatorio = vendaDAO.relatorioFinanceiro();
        assertNotNull(relatorio);
        assertFalse(relatorio.isEmpty());
    }

    private void popularBancoDeDados() {
        // Popula o banco de dados real com dados para o teste
        Categoria celulares = new Categoria("CELULARES");
        Categoria videogames = new Categoria("VIDEOGAMES");
        Categoria informatica = new Categoria("INFORMATICA");

        Produto celular = new Produto("Xiaomi Redmi", "O preferido", new BigDecimal("800"), celulares);
        Produto videogame = new Produto("PS5", "Playstation 5", new BigDecimal("8000"), videogames);
        Produto macbook = new Produto("Macbook", "Macbook Pro", new BigDecimal("14000"), informatica);

        Cliente cliente = new Cliente("Franciele", "123456");

        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(new PedidoItem(10, pedido, celular));
        pedido.adicionarItem(new PedidoItem(40, pedido, videogame));

        Pedido pedido2 = new Pedido(cliente);
        pedido2.adicionarItem(new PedidoItem(2, pedido2, macbook));

        em.getTransaction().begin();

        em.persist(celulares);
        em.persist(videogames);
        em.persist(informatica);

        em.persist(celular);
        em.persist(videogame);
        em.persist(macbook);

        em.persist(cliente);

        em.persist(pedido);
        em.persist(pedido2);

        em.getTransaction().commit();
    }
}
```
## Realizando melhorias no código

### Tratamento de Erros

O tratamento de erros pode ser melhorado com exceções personalizadas e uso de blocos try-catch. Isso ajuda a capturar exceções específicas. Neste projeto, iremos criar uma classe personalizada de exceção para lidar com erros de acesso a dados. Para isso, iremos criar um pacote chamado exception com a classe DataAccessException, conforme abaixo:

```java
// Classe personalizada de exceção para lidar com erros de acesso a dados.
public class DataAccessException extends RuntimeException {

    // Construtor que recebe uma mensagem e uma causa (exceção original).
    public DataAccessException(String message, Throwable cause) {
        // Passa a mensagem e a causa para o construtor da classe RuntimeException.
        super(message, cause);
    }
}
```

A classe de exceção personalizada agora pode ser usada nas classes de acesso a dados, porém, antes será realizada uma melhoria no código dessas classes.

### Criação de uma classe genérica DAO

As operações básicas de persistência, tais como cadastrar, atualizar, remover, buscarPorId e buscarTodos, são repetitivas. Entretanto, isso pode ser abstraído em uma classe genérica, reduzindo duplicação e facilitando a manutenção.

```java
import br.com.exception.DataAccessException;// Exceção personalizada
import jakarta.persistence.EntityManager;
import java.util.List;

// Classe genérica para operações CRUD no banco de dados.
public abstract class GenericDao<T> {

    protected EntityManager em; // Gerenciador de entidades (JPA).
    private Class<T> entityClass; // Classe da entidade genérica.

    public GenericDao(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    // Metodo para cadastrar uma nova entidade.
    public void cadastrar(T entity) {
        try {
            em.getTransaction().begin();// Inicia a transação.
            em.persist(entity);// Persiste a entidade no banco de dados.
            em.getTransaction().commit();// Confirma a transação.
        } catch (Exception e) {
            em.getTransaction().rollback();// Reverte a transação em caso de erro.

            // Lança uma exceção personalizada com detalhes do erro.
            throw new DataAccessException("Erro ao cadastrar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para atualizar uma entidade existente.
    public void atualizar(T entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DataAccessException("Erro ao atualizar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para remover uma entidade do banco de dados.
    public void remover(T entity) {
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new DataAccessException("Erro ao atualizar a entidade: " + entity.getClass().getSimpleName(), e);
        }
    }

    // Metodo para buscar uma entidade pelo ID.
    public T buscarPorId(Long id) {
        try {
            return em.find(entityClass, id);// Busca a entidade pelo ID.
        } catch (Exception e) {
            throw new DataAccessException("Erro ao buscar o id: "+ id +" da entidade: "+ entityClass.getSimpleName(), e);
        }
    }

    // Metodo para buscar todas as entidades de um tipo.
    public List<T> buscarTodos() {
        try {
            // Monta uma consulta JPQL para buscar todas as entidades.
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";

            return em.createQuery(jpql, entityClass).getResultList();
            
        } catch (Exception e) {
            throw new DataAccessException("Erro ao buscar todos de: "+ entityClass.getSimpleName(), e);
        }
    }
}
```

Agora, podemos fazer com que as classes DAO específicas herdem as funcionalidades básicas da classe DAO genérica:

### Classe CategoriaDAO
```java
import br.com.exception.DataAccessException;
import br.com.model.Categoria;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CategoriaDao extends GenericDao<Categoria>{

	public CategoriaDao(EntityManager em) {
		super(em, Categoria.class);
	}


	// Metodo para buscar categorias pelo nome.
	public List<Categoria> buscarPorNome(String nome) {
		try{
			// Consulta JPQL para buscar categorias por nome.
			String jpql = "SELECT c FROM Categoria c WHERE c.nome = :nome";

			return em.createQuery(jpql, Categoria.class)
				.setParameter("nome", nome) // Define o parâmetro "nome" na consulta.
				.getResultList(); // Executa a consulta e retorna os resultados.

		} catch (Exception e) {
			throw new DataAccessException("Erro ao buscar categorias por nome: " + nome, e);
		}
	}
}
```

### Classe ClienteDAO
```java
import br.com.model.Cliente;
import jakarta.persistence.EntityManager;

public class ClienteDao extends GenericDao<Cliente>{

	private EntityManager em;

	public ClienteDao(EntityManager em) {
		super(em, Cliente.class);
	}
}
```

### Classe ProdutoDAO
```java
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
```

### Classe PedidoDAO
```java
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
```

A classe VendaDAO não herda da GenericDAO, pois não realiza operações de CRUD, porém, ela apresenta melhorias relacionadas ao tratamento de erros:
```java
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
```


