package fr.univrennes.istic.l2gen.visustats.view.datagroup;

import fr.univrennes.istic.l2gen.geometry.Point;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;
import fr.univrennes.istic.l2gen.visustats.data.DataGroup;
import fr.univrennes.istic.l2gen.visustats.view.dataset.IDataSetView;
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

    @Override
    protected double getTotalHeight() {
        return this.radius * 2;
    }

    @Override
    protected double getElementWidth() {
        return this.radius * 2;
    }

    @Override
    protected IDataSetView createElement(Point position) {
        return new PieDataSetView(position, this.radius);
    }
}
