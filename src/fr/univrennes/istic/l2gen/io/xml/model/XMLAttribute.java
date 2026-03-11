package fr.univrennes.istic.l2gen.io.xml.model;

import java.util.Optional;

/**
 * Représente un attribut XML avec un nom et une valeur.
 * Record Java fournissant des méthodes pour convertir la valeur en différents
 * types.
 * 
 * @param name  le nom de l'attribut
 * @param value la valeur de l'attribut
 */
public record XMLAttribute(String name, String value) {

  /**
   * Retourne la valeur de l'attribut comme une chaîne.
   * 
   * @return la valeur
   */
  public String getValue() {
    return value;
  }

  /**
   * Retourne la valeur comme un entier.
   * 
   * @return un Optional contenant l'entier ou vide si la conversion échoue
   */
  public Optional<Integer> getIntValue() {
    try {
      return Optional.of(Integer.parseInt(value));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * Retourne la valeur comme un nombre décimal.
   * 
   * @return un Optional contenant le double ou vide si la conversion échoue
   */
  public Optional<Double> getDoubleValue() {
    try {
      return Optional.of(Double.parseDouble(value));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  /**
   * Retourne la valeur comme un booléen.
   * 
   * @return un Optional contenant le booléen ou vide si la conversion échoue
   */
  public Optional<Boolean> getBooleanValue() {
    try {
      return Optional.of(Boolean.parseBoolean(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Retourne la représentation en chaîne de l'attribut au format XML.
   * 
   * @return la représentation "name=\"value\"" ou juste "name" si la valeur est
   *         null
   */
  @Override
  public String toString() {
    if (value == null) {
      return name;
    }
    return name + "=\"" + value + "\"";
  }
}
