package fr.univrennes.istic.l2gen.svg.interfaces;

/**
 * Interface pour les attributs SVG.
 * Définit le contrat pour les classes qui représentent des attributs SVG.
 */
public interface ISVGAttribute {

    /**
     * Vérifie si cet attribut a du contenu.
     * 
     * @return true si l'attribut a du contenu, false sinon
     */
    public boolean hasContent();

    /**
     * Retourne la représentation en chaîne du contenu de cet attribut.
     * 
     * @return le contenu formaté en chaîne
     */
    public String getContent();
}
