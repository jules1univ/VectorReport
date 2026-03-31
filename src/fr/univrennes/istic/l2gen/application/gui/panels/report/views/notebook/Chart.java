package fr.univrennes.istic.l2gen.application.gui.panels.report.views.notebook;

import javax.swing.JPanel;

import fr.univrennes.istic.l2gen.application.gui.GUIController;
import fr.univrennes.istic.l2gen.io.svg.SVGExport;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

import java.awt.*;
import java.io.StringReader;
import java.net.URI;

public class Chart extends JPanel {

    private SVGDiagram diagram;

    public Chart(GUIController controller) {
        setLayout(new BorderLayout());
    }

    public void load(ISVGShape shape) {
        String svg = SVGExport.convert(shape).toString();

        try {
            SVGUniverse universe = new SVGUniverse();
            URI uri = universe.loadSVG(new StringReader(svg), "chart");
            diagram = universe.getDiagram(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (diagram == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float scaleX = getWidth() / diagram.getWidth();
        float scaleY = getHeight() / diagram.getHeight();
        g2.scale(Math.min(scaleX, scaleY), Math.min(scaleX, scaleY));

        try {
            diagram.render(g2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}