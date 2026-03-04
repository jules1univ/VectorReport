package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.dataset.PieDataSetView;

/**
 * Vue groupe pour afficher plusieurs diagrammes camemberts côte à côte.
 * 
 * Chaque DataSet du DataGroup produit un camembert avec son titre associé.
 * Les camemberts sont alignés horizontalement avec espacement constant.
 * Une légende unique est affichée sous l'ensemble, correspondant aux
 * catégories colorées.
 * 
 * Pattern Observateur : la vue observe le DataGroup et se recalcule via
 * update() quand les données changent.
 */
@SVGTag("g")
public class PieDataGroupView extends AbstractDataGroupView {

    @SVGField("data-radius")
    protected double radius;

    /**
     * Constructeur.
     * 
     * @param data    le DataGroup contenant les datasets à visualiser
     * @param spacing espacement horizontal entre les camemberts (en pixels)
     * @param radius  le rayon de chaque camembert (en pixels)
     * @param centre  le point centre autour duquel positionner les camemberts
     */
    public PieDataGroupView(DataGroup data, Point center, double spacing, double radius) {
        super(data, center, spacing);
        this.radius = radius;
        this.update();
    }

    /**
     * Recalcule la visualisation quand les données changent.
     * 
     * Cette méthode est appelée automatiquement par le pattern Observateur
     * (setData, addData, addLegend, setTitle).
     * 
     * Parcourt tous les DataSet du DataGroup et crée un camembert pour chacun,
     * en les positionnant côte à côte horizontalement autour du centre.
     */
    @Override
    protected void update() {
        // Étape 1 : Effacer les anciens éléments SVG
        this.elements.clear();

        // Étape 2 : Vérifier qu'il y a des données
        int pieCount = this.data.size();
        if (pieCount == 0) {
            return;
        }

        // Étape 3 : Calculer les positions horizontales des camemberts
        double pieWidth = 2.0 * this.radius;
        double totalPiesWidth = pieCount * pieWidth + (pieCount - 1) * this.spacing;
        double startX = center.getX() - totalPiesWidth / 2.0;

        // Étape 4 : Pour chaque DataSet, créer un camembert et le positionner
        for (int i = 0; i < this.data.size(); i++) {
            // Position du centre du camembert courant
            double centerX = startX + i * (pieWidth + this.spacing) + this.radius;
            Point pieCenter = new Point(centerX, center.getY());

            // Créer une vue pie pour ce dataset
            PieDataSetView pieView = new PieDataSetView(pieCenter, this.radius);
            pieView.setData(this.data.get(i));

            // Ajouter cette vue au groupe principal
            this.elements.add(pieView);
        }

        Point titlePoint = new Point(center.getX(), center.getY() - this.radius * 1.2f);
        this.elements.add(this.data.title().createTitle(titlePoint));
    }
}
