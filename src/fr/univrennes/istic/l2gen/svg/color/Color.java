package fr.univrennes.istic.l2gen.svg.color;

/**
 * Représente une couleur au format hexadécimal.
 * Fournit des constantes de couleurs prédéfinies et des méthodes pour créer des
 * couleurs
 * à partir de différents formats (hex, RGB, RGBA).
 */
public final class Color {
    public static final Color TRANSPARENT = new Color("#00000000");

    public static final Color BLACK = new Color("#000000");
    public static final Color WHITE = new Color("#ffffff");

    public static final Color RED = new Color("#ff0000");
    public static final Color GREEN = new Color("#00ff00");
    public static final Color BLUE = new Color("#0000ff");

    public static final Color ORANGE = new Color("#ffa500");
    public static final Color PURPLE = new Color("#800080");
    public static final Color CYAN = new Color("#00ffff");
    public static final Color MAGENTA = new Color("#ff00ff");
    public static final Color YELLOW = new Color("#ffff00");
    public static final Color GRAY = new Color("#808080");

    private final String hex;

    /**
     * Crée une couleur à partir d'une chaîne hexadécimale.
     * 
     * @param hex la valeur hexadécimale (ex: "#ff0000" pour rouge)
     * @return une couleur basée sur la valeur hex
     */
    public static Color hex(String hex) {
        return new Color(hex);
    }

    /**
     * Crée une couleur à partir de composantes RGB.
     * 
     * @param r la composante rouge (0-255)
     * @param g la composante verte (0-255)
     * @param b la composante bleue (0-255)
     * @return une couleur opaque basée sur les valeurs RGB
     */
    public static Color rgb(int r, int g, int b) {
        return new Color(r, g, b, 255);
    }

    /**
     * Crée une couleur à partir de composantes RGBA.
     * 
     * @param r la composante rouge (0-255)
     * @param g la composante verte (0-255)
     * @param b la composante bleue (0-255)
     * @param a la composante alpha (0-255)
     * @return une couleur basée sur les valeurs RGBA
     */
    public static Color rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a);
    }

    /**
     * Crée une couleur aléatoire.
     * 
     * @return une couleur générée aléatoirement
     */
    public static Color random() {
        int r = (int) (Math.random() * 256);
        int g = (int) (Math.random() * 256);
        int b = (int) (Math.random() * 256);
        return new Color(r, g, b, 255);
    }

    /**
     * Crée une couleur à partir d'une chaîne au format rgb() ou rgba() ou
     * hexadécimal.
     * 
     * @param raw la chaîne de format SVG (ex: "rgb(255,0,0)", "rgba(255,0,0,128)",
     *            "#ff0000")
     * @return une couleur parsée ou null si le format est invalide
     */
    public static Color raw(String raw) {
        if (raw.startsWith("rgb(") && raw.endsWith(")")) {
            String[] parts = raw.substring(4, raw.length() - 1).split(",");
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return new Color(r, g, b, 255);
        } else if (raw.startsWith("rgba(") && raw.endsWith(")")) {
            String[] parts = raw.substring(5, raw.length() - 1).split(",");
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            int a = Integer.parseInt(parts[3].trim());
            return new Color(r, g, b, a);
        } else if (raw.startsWith("#")) {
            return new Color(raw);
        } else {
            return null;
        }
    }

    private Color(int r, int g, int b, int a) {
        this.hex = String.format("#%02x%02x%02x%02x", r, g, b, a);
    }

    private Color(String hex) {
        if (!hex.matches("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$")) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        this.hex = hex;
    }

    /**
     * Retourne la représentation hexadécimale de la couleur.
     * 
     * @return la chaîne hexadécimale de la couleur
     */
    @Override
    public String toString() {
        return hex;
    }
}
