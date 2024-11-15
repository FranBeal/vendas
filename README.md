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

//Colocar a imagem aqui

Maven é uma ferramenta de automação e gerenciamento de projetos Java, usada principalmente para gerenciar dependências, compilar código, rodar testes e empacotar a aplicação. Ele utiliza um arquivo XML chamado pom.xml (Project Object Model) para definir configurações do projeto e dependências externas. 

Após criar o projeto, abra o arquivo *pom.xml*. Ele deve estar como imagem abaixo:

//Colocar a imagem aqui

No arquivo *pom.xml*, logo abaixo da tag <properties>...</properties>, vamos adicionar as dependências do Hibernate, do PostgreSQL e do JUnit, dentro da tag <dependencies></dependencies>. Fazendo dessa forma, o Maven vai gerenciar essas dependências, trazendo automaticamente para o projeto as bibliotecas externas (.jar). A imagem abaixo mostra o arquivo com as dependências informadas (circuladas em vermelho):

//Colocar a imagem aqui

Após inserir as dependências no arquivo, clique no botão do “m” azul, que está circulado na imagem acima, para atualizar as dependências e baixar as bibliotecas no projeto.

Essas dependências, e outras mais, podem ser encontradas no repositório Maven Repository disponível endereço https://mvnrepository.com/.

Na sequência, na pasta *resources* do projeto, vamos criar a pasta **META-INF** e dentro dela criar o arquivo *persistence.xml*. Nesse arquivo, vamos configurar as propriedades para conectar-se ao banco de dados PostgreSQL.

//Colocar o código do aqui

O arquivo *persistence.xml* é fundamental para configurar a persistência de dados em aplicações Java que utilizam JPA com um provedor ORM, como o Hibernate. Ele define as configurações da unidade de persistência e as propriedades relacionadas ao banco de dados:

   • ```<persistence version="2.1" ...>```, versão 2.1 da JPA.
   
   • ```persistence-unit```, define a unidade de persistência. Cada ```persistence-unit``` recebe um nome, no caso do projeto Vendas, é ```"PostgresPU"```. Esse nome é usado para identificar essa configuração dentro da aplicação. A ```transaction-type``` é o tipo de transação e é ```"RESOURCE_LOCAL"```, que significa que as transações serão gerenciadas pela própria aplicação e não por um servidor de aplicações. 
     
  • ```<properties> </properties>```, contém as configurações de conexão e outros detalhes importantes para o JPA/Hibernate, tais como: 
  
   ◦ Driver do banco de dados: ```<property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" /> ```
   
   ◦ Url de conexão: ```<property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost/playlistdb" /> ```
   
   ◦ Usuário e senha: 
   
   ```<property name="javax.persistence.jdbc.user" value="postgres" /> ```
   
   ```<property name="javax.persistence.jdbc.password" value="postgres" /> ```
   
   ◦ Dialeto do Hibernate que define o dialeto SQL que o Hibernate deve usar, o que garante a geração correta das queries para o banco PostgreSQL: ```<property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/> ```
   
   ◦ Estratégia de gerenciamento de schema, que configura como o Hibernate deve lidar com o esquema de banco de dados. No caso de ```"update"```, ele atualiza as tabelas sem apagá-las. Outras opções incluem: ```“create"``` cria as tabelas, sobrescrevendo as existentes, ```"create-drop"``` cria as tabelas e as apaga ao final da sessão, e, ```"validate"``` apenas valida se o esquema está correto, sem modificá-lo.
   ```<property name="hibernate.hbm2ddl.auto" value="update" /> ```
   
   ◦ Exibição de SQL no console, define se as queries SQL geradas pelo Hibernate serão exibidas no console e se serão formatadas para melhor legibilidade. 
   
   ```<property name="hibernate.show_sql" value="true" /> ```
   
   ```<property name="hibernate.format_sql" value="true" /> ```

## Estrutura do Projeto

O projeto será organizado nos seguintes pacotes:

   • **model:** Contém as classes de entidade.
    
   • **dao:** Contém as classes de acesso a dados.
    
   • **service:** Contém as classes com a lógica do negócio
    
   • **vo:** Contém classes que representam resumos e relatórios de dados
    
   • **util:** Contém a classe de utilitário para JPA.
    
   • **test:** Contém os testes unitários.
    
    
Após criar os pacotes indicados acima, a estrutura do projeto deverá estar como na imagem abaixo: 

//Colocar a imagem aqui




Em construção...



