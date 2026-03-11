package fr.univrennes.istic.l2gen.io.xml.parser;

/**
 * Exception levée lors d'une erreur d'analyse XML.
 * Contient un message d'erreur et optionnellement la cause initiale.
 */
public final class XMLParseException extends Exception {
    /**
     * Constructeur avec un message d'erreur.
     * 
     * @param message le message d'erreur décrivant le problème d'analyse
     */
    public XMLParseException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause.
     * 
     * @param message le message d'erreur
     * @param cause   la cause de l'erreur
     */
    public XMLParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
