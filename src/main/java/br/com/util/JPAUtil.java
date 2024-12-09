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
