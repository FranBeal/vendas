package br.com.exception;

// Classe personalizada de exceção para lidar com erros de acesso a dados.
public class DataAccessException extends RuntimeException {

    // Construtor que recebe uma mensagem e uma causa (exceção original).
    public DataAccessException(String message, Throwable cause) {
        // Passa a mensagem e a causa para o construtor da classe RuntimeException.
        super(message, cause);
    }
}


