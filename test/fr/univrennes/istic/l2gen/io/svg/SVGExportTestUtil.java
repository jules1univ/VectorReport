package fr.univrennes.istic.l2gen.io.svg;

import java.io.File;
import java.util.List;

import fr.univrennes.istic.l2gen.geometry.IShape;
import fr.univrennes.istic.l2gen.geometry.base.Line;
import fr.univrennes.istic.l2gen.geometry.base.Rectangle;
import fr.univrennes.istic.l2gen.svg.color.Color;

public final class SVGExportTestUtil {
        public static void export(IShape shape) {

                File output = new File(
                                String.format("output/test_%s.svg", shape.getClass().getSimpleName().toLowerCase()));
                if (output.exists()) {
                        output.delete();
                }
                output.getParentFile().mkdirs();

                IShape background = new Rectangle(0, 0, 1000, 1000);
                background.getStyle().fillColor(Color.WHITE);

                IShape crossLine1 = new Line(500, 0, 500, 1000);
                crossLine1.getStyle()
                                .strokeWidth(2)
                                .strokeColor(Color.BLACK);

                IShape crossLine2 = new Line(0, 500, 1000, 500);
                crossLine2.getStyle()
                                .strokeWidth(2)
                                .strokeColor(Color.BLACK);

                IShape box = new Rectangle(
                                shape.getCenter().getX() - shape.getWidth() / 2,
                                shape.getCenter().getY() - shape.getHeight() / 2,
                                shape.getWidth(),
                                shape.getHeight());
                box.getStyle()
                                .fillColor(Color.TRANSPARENT)
                                .strokeWidth(2)
                                .strokeColor(Color.RED)
                                .strokeDashArray(5, 5, 5, 5);

                assert SVGExport.export(List.of(background, shape, crossLine1, crossLine2, box),
                                output.getAbsolutePath(),
                                1000,
                                1000);

        }
}
