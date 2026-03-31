package fr.univrennes.istic.l2gen.io.xml.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import fr.univrennes.istic.l2gen.io.xml.model.XMLAttribute;
import fr.univrennes.istic.l2gen.io.xml.model.XMLTag;

/**
 * Analyseur XML pour analyser des chaînes ou fichiers XML et générer une
 * structure d'arbres {@link XMLTag}.
 * Supporte les commentaires XML, déclarations et éléments XM valides.
 */
public final class XMLParser {
    private BufferedReader br;
    private int current;
    private int index;

    /**
     * Constructeur avec un BufferedReader.
     * 
     * @param reader le lecteur pour lire le XML
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    public XMLParser(BufferedReader reader) throws IOException {
        this.br = reader;
        this.index = 0;
        this.current = reader.read();
    }

    /**
     * Constructeur avec une chaîne XML.
     * 
     * @param xml la chaîne contenant le XML
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    public XMLParser(String xml) throws IOException {
        this(new BufferedReader(new StringReader(xml)));
    }

    /**
     * Constructeur avec un Reader générique.
     * 
     * @param reader le lecteur pour lire le XML
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    public XMLParser(Reader reader) throws IOException {
        this(new BufferedReader(reader));
    }

    /**
     * Analyse le XML et retourne la structure d'arbre.
     * 
     * @return la balise racine du XML analysé
     * @throws XMLParseException si une erreur d'analyse se produit
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    public XMLTag parse() throws XMLParseException, IOException {
        this.skipWhitespace();
        return this.parseTag();
    }

    /**
     * Analyse le nom d'une balise XML.
     * 
     * @return le nom de la balise
     * @throws XMLParseException si le nom est vide ou invalide
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    /**
     * Analyse récursivement une balise XML et ses enfants.
     * Gère les commentaires et déclarations XML.
     * 
     * @return la balise analysée
     * @throws XMLParseException si une erreur d'analyse se produit
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private XMLTag parseTag() throws XMLParseException, IOException {
        if (!this.expect('<')) {
            throw new XMLParseException("Expected '<' at position " + index);
        }

        if (this.peek("!--")) {
            this.skipComment();
            return this.parseTag();
        }

        if (this.peek('?') || this.peek('!')) {
            this.skipDeclaration();
            return this.parseTag();
        }

        String tagName = parseTagName();
        XMLTag tag = new XMLTag(tagName);

        while (true) {
            this.skipWhitespace();
            if (this.peek('>') || this.peek('/')) {
                break;
            }
            XMLAttribute attr = parseAttribute();
            tag.addAttribute(attr);
        }

        this.skipWhitespace();

        if (this.peek('/')) {
            advance();
            if (!this.expect('>')) {
                throw new XMLParseException("Expected '>' at position " + index);
            }
            return tag;
        }

        if (!this.expect('>')) {
            throw new XMLParseException("Expected '>' at position " + index);
        }

        StringBuilder content = new StringBuilder();
        while (current != -1) {
            this.skipWhitespace();

            if (this.peek('<')) {
                if (this.peek("</")) {
                    break;
                }

                if (content.length() > 0) {
                    tag.setTextContent(content.toString().trim());
                    content.setLength(0);
                }

                XMLTag child = this.parseTag();
                tag.appendChild(child);
            } else {
                content.append(this.parseTextContent());
            }
        }

        if (content.length() > 0) {
            String contentStr = content.toString().trim();
            if (!contentStr.isEmpty()) {
                tag.setTextContent(contentStr);
            }
        }

        if (!this.expect('<')) {
            throw new XMLParseException("Expected '<' at position " + index);
        }
        if (!this.expect('/')) {
            throw new XMLParseException("Expected '/' at position " + index);
        }
        String closingTagName = parseTagName();
        if (!closingTagName.equals(tagName)) {
            throw new XMLParseException("Mismatched closing tag. Expected </" + tagName +
                    "> but found </" + closingTagName + "> at position " + index);
        }
        if (!this.expect('>')) {
            throw new XMLParseException("Expected '>' at position " + index);
        }

        return tag;
    }

    private String parseTagName() throws XMLParseException, IOException {
        StringBuilder name = new StringBuilder();
        while (current != -1) {
            char c = (char) current;
            if (Character.isWhitespace(c) || c == '>' || c == '/') {
                break;
            }
            name.append(c);
            advance();
        }
        if (name.length() == 0) {
            throw new XMLParseException("Expected tag name at position " + index);
        }
        return name.toString();
    }

    /**
     * Analyse un attribut XML complet (nom=valeur).
     * 
     * @return l'attribut analysé
     * @throws XMLParseException si le format de l'attribut est invalide
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private XMLAttribute parseAttribute() throws XMLParseException, IOException {
        String attrName = parseAttributeName();
        this.skipWhitespace();

        if (!this.expect('=')) {
            throw new XMLParseException("Expected '=' after attribute name at position " + index);
        }

        this.skipWhitespace();
        String attrValue = parseAttributeValue();

        return new XMLAttribute(attrName, attrValue);
    }

    /**
     * Analyse le nom d'un attribut XML.
     * 
     * @return le nom de l'attribut
     * @throws XMLParseException si le nom est vide ou invalide
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private String parseAttributeName() throws XMLParseException, IOException {
        StringBuilder name = new StringBuilder();
        while (current != -1) {
            char c = (char) current;
            if (Character.isWhitespace(c) || c == '=') {
                break;
            }
            name.append(c);
            advance();
        }
        if (name.length() == 0) {
            throw new XMLParseException("Expected attribute name at position " + index);
        }
        return name.toString();
    }

    /**
     * Analyse la valeur d'un attribut XML entre guillemets.
     * Gère les caractères échappés &lt;, &gt;, &amp;, &quot;, &apos;.
     * 
     * @return la valeur unéchappée de l'attribut
     * @throws XMLParseException si les guillemets sont mal formés
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private String parseAttributeValue() throws XMLParseException, IOException {
        if (current == -1) {
            throw new XMLParseException("Expected quote at position " + index);
        }
        char quote = (char) current;
        if (quote != '"' && quote != '\'') {
            throw new XMLParseException("Expected quote at position " + index);
        }
        advance();
        StringBuilder value = new StringBuilder();
        while (current != -1) {
            char c = (char) current;
            if (c == quote) {
                advance();
                return this.unescapeXML(value.toString());
            }
            value.append(c);
            advance();
        }
        throw new XMLParseException("Unclosed attribute value at position " + index);
    }

    /**
     * Analyse le contenu textuel d'une balise jusqu'à la prochaine balise
     * d'ouverture.
     * 
     * @return le contenu textuel unéchappé
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private String parseTextContent() throws IOException {
        StringBuilder content = new StringBuilder();
        while (current != -1) {
            char c = (char) current;
            if (c == '<') {
                break;
            }
            content.append(c);
            advance();
        }
        return this.unescapeXML(content.toString());
    }

    /**
     * Convertit les entités XML échappées en caractères valides.
     * Transforme &lt;, &gt;, &amp;, &quot;, &apos; en leurs équivalents.
     * 
     * @param text le texte contenant les entités échappées
     * @return le texte avec les entités converties
     */
    private String unescapeXML(String text) {
        return text.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }

    /**
     * Saute tous les caractères d'espacement blanc du flux.
     * 
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private void skipWhitespace() throws IOException {
        while (current != -1 && Character.isWhitespace((char) current)) {
            advance();
        }
    }

    /**
     * Saute un commentaire XML (<!-- ... -->).
     * 
     * @throws XMLParseException si le commentaire n'est pas fermé correctement
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private void skipComment() throws XMLParseException, IOException {
        if (!this.expect("!--")) {
            throw new XMLParseException("Expected '<!--' at position " + index);
        }
        int dashCount = 0;
        while (current != -1) {
            char c = (char) current;
            if (dashCount == 2 && c == '>') {
                advance();
                this.skipWhitespace();
                return;
            }
            if (c == '-') {
                dashCount++;
            } else {
                dashCount = 0;
            }
            advance();
        }
        throw new XMLParseException("Unclosed comment");
    }

    /**
     * Saute une déclaration XML (<? ... ?> ou <! ... >).
     * 
     * @throws XMLParseException si la déclaration n'est pas fermée
     * @throws IOException       si une erreur d'entrée/sortie se produit
     */
    private void skipDeclaration() throws XMLParseException, IOException {
        while (current != -1) {
            if ((char) current == '>') {
                advance();
                this.skipWhitespace();
                return;
            }
            advance();
        }
        throw new XMLParseException("Unclosed declaration");
    }

    /**
     * Vérifie si le caractère actuel correspond au caractère attendu sans
     * l'avancer.
     * 
     * @param expected le caractère attendu
     * @return true si le caractère actuel correspond, false sinon
     */
    private boolean peek(char expected) {
        return current != -1 && (char) current == expected;
    }

    /**
     * Vérifie si les caractères à venir correspondent à la chaîne attendue sans
     * l'avancer.
     * Utilise un marqueur de position pour examiner ahead.
     * 
     * @param expected la chaîne attendue
     * @return true si les caractères correspondent, false sinon
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private boolean peek(String expected) throws IOException {
        if (current == -1) {
            return false;
        }

        br.mark(expected.length());
        boolean matches = true;

        for (int i = 0; i < expected.length(); i++) {
            int ch = (i == 0) ? current : br.read();
            if (ch == -1 || (char) ch != expected.charAt(i)) {
                matches = false;
                break;
            }
        }

        br.reset();
        return matches;
    }

    /**
     * Vérifie et consomme le caractère attendu du flux.
     * 
     * @param expected le caractère attendu
     * @return true si le caractère correspond, false sinon
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private boolean expect(char expected) throws IOException {
        if (this.peek(expected)) {
            advance();
            return true;
        }
        return false;
    }

    /**
     * Vérifie et consomme la chaîne attendue du flux.
     * 
     * @param expected la chaîne attendue
     * @return true si la chaîne correspond, false sinon
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private boolean expect(String expected) throws IOException {
        if (this.peek(expected)) {
            for (int i = 0; i < expected.length(); i++) {
                advance();
            }
            return true;
        }
        return false;
    }

    /**
     * Avance d'un caractère dans le flux et met à jour la position.
     * 
     * @throws IOException si une erreur d'entrée/sortie se produit
     */
    private void advance() throws IOException {
        current = br.read();
        index++;
    }
}