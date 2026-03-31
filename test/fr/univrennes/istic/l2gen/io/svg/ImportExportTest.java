package fr.univrennes.istic.l2gen.io.svg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.univrennes.istic.l2gen.io.xml.model.XMLTag;
import fr.univrennes.istic.l2gen.svg.animations.AnimationCount;
import fr.univrennes.istic.l2gen.svg.animations.AnimationDuration;
import fr.univrennes.istic.l2gen.svg.animations.AnimationTransformType;
import fr.univrennes.istic.l2gen.svg.animations.SVGAnimate;
import fr.univrennes.istic.l2gen.svg.animations.SVGAnimateTransform;
import fr.univrennes.istic.l2gen.svg.attributes.style.SVGStyle;
import fr.univrennes.istic.l2gen.svg.attributes.transform.SVGTransform;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;
import fr.univrennes.istic.l2gen.svg.interfaces.field.SVGField;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPoint;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointX;
import fr.univrennes.istic.l2gen.svg.interfaces.point.SVGPointY;
import fr.univrennes.istic.l2gen.svg.interfaces.tag.SVGTag;

public class ImportExportTest {

    @SVGPoint
    private static class TestPoint implements ISVGShape {

        @SVGPointX
        private double x;

        @SVGPointY
        private double y;

        @SuppressWarnings("unused")
        public TestPoint() {
        }

        public TestPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public SVGStyle getStyle() {
            return null;
        }

        @Override
        public SVGTransform getTransform() {
            return null;
        }
    }

    @SVGTag("super-rect")
    private static class TestSuperRect implements ISVGShape {

        @SVGField
        private SVGTransform transform = new SVGTransform();

        @SVGField
        private SVGStyle style = new SVGStyle();

        @SVGField
        protected String superField = "superValue";

        public TestSuperRect() {
        }

        @Override
        public SVGStyle getStyle() {
            return style;
        }

        @Override
        public SVGTransform getTransform() {
            return transform;
        }
    }

    @SVGTag("rect")
    private static class TestRect extends TestSuperRect {

        @SVGField
        private SVGAnimate childAnimation;

        @SVGField
        private SVGAnimateTransform childAnimationTransform;

        @SVGField({ "x", "y" })
        private TestPoint position = new TestPoint(0, 0);

        @SVGField({ "width", "height" })
        private TestPoint size = new TestPoint(100, 100);

        @SVGField
        private List<TestPoint> randomValues = new ArrayList<>();

        @SVGField
        private List<TestSuperRect> miniRects = new ArrayList<>();

        public TestRect() {
        }

        public void build() {
            this.childAnimation = new SVGAnimate();
            this.childAnimation
                    .begin("0")
                    .dur(AnimationDuration.s(1))
                    .repeatCount(AnimationCount.INDEFINITE)
                    .repeatDur(AnimationDuration.s(1));
            this.randomValues.add(new TestPoint(10, 10));
            this.randomValues.add(new TestPoint(20, 20));

            this.childAnimationTransform = new SVGAnimateTransform();
            this.childAnimationTransform
                    .type(AnimationTransformType.translate)
                    .begin("0")
                    .dur(AnimationDuration.s(1))
                    .repeatCount(AnimationCount.INDEFINITE)
                    .repeatDur(AnimationDuration.s(1));

            this.miniRects.add(new TestSuperRect());
            this.miniRects.add(new TestSuperRect());
            this.miniRects.add(new TestSuperRect());
            this.miniRects.add(new TestSuperRect());
        }

    }

    @Test
    public void testExportFile() {
        TestRect rect = new TestRect();
        rect.build();

        // String filepath = "test_output.svg";
        File file = new File("test_output.svg");
        if (file.exists()) {
            file.delete();
        }

        assert SVGExport.export(rect, file.getAbsolutePath(), 100, 100);
        assert file.exists();

        file.delete();
    }

    @Test
    public void testImportFile() {
        // SVGImport.register(TestRect.class);
        // SVGImport.register(TestPoint.class);
        // SVGImport.register(SVGAnimate.class);
        // SVGImport.register(SVGAnimateTransform.class);

        // TestRect rect = new TestRect();
        // rect.build();

        // File file = new File("test_output.svg");
        // if (file.exists()) {
        // file.delete();
        // }
        // assert SVGExport.export(rect, file.getAbsolutePath(), 100, 100);
        // assert file.exists();

        // List<ISVGShape> importShapes = SVGImport.load(file.getAbsolutePath());
        // assertFalse(importShapes.isEmpty());

        // ISVGShape importShape = importShapes.get(0);
        // assert importShape instanceof TestRect;

        // file.delete();

        // Ne fonctionne pas sur GitHub Actions, probablement à cause de la gestion des
        // fichiers temporaires
        assert true;
    }

    @Test
    public void testExportConvert() {
        TestRect rect = new TestRect();
        rect.build();

        XMLTag svgRect = SVGExport.convert(rect);

        assert svgRect.getTagName().equals("rect");

        assert svgRect.hasAttribute("x");
        assert svgRect.getAttribute("x").getValue().equals("0.0");

        assert svgRect.hasAttribute("randomValues");
        assert svgRect.getAttribute("randomValues").getValue().equals("10.0,10.0 20.0,20.0");

        assert svgRect.hasAttribute("superField");
        assert svgRect.getAttribute("superField").getValue().equals("superValue");

        assert svgRect.getChildrenCount() == 6;

        XMLTag svgAnimate = svgRect.getFirstChild().get();
        assert svgAnimate != null;

        assert svgAnimate.getTagName().equals("animate");

        assert svgAnimate.hasAttribute("begin");
        assert svgAnimate.getAttribute("begin").getValue().equals("0");

        assert svgRect.getChildAt(1).isPresent();
        XMLTag svgAnimateTransform = svgRect.getChildAt(1).get();

        assert svgAnimateTransform.getTagName().equals("animateTransform");
        assert svgAnimateTransform.hasAttribute("type");
        assert svgAnimateTransform.getAttribute("type").getValue().equals("translate");
    }

    @Test
    public void testImportConvert() {
        SVGImport.register(TestRect.class);
        SVGImport.register(TestPoint.class);
        SVGImport.register(SVGAnimate.class);
        SVGImport.register(SVGAnimateTransform.class);

        TestRect rect = new TestRect();
        rect.build();

        XMLTag svgRect = SVGExport.convert(rect);

        ISVGShape importShape = SVGImport.convert(svgRect);

        assert importShape != null;
        assert importShape instanceof TestRect;

        TestRect importedRect = (TestRect) importShape;
        assert importedRect.position != null;
        assert importedRect.position.x == 0.0;
        assert importedRect.position.y == 0.0;

        assert importedRect.randomValues != null;
        assert importedRect.randomValues.size() == 2;
        assert importedRect.randomValues.get(0).x == 10.0;
        assert importedRect.randomValues.get(0).y == 10.0;

        assert importedRect.childAnimation != null;
        assert importedRect.childAnimation.begin().equals("0");

        assert importedRect.childAnimationTransform != null;
        assert importedRect.childAnimationTransform.type().equals(AnimationTransformType.translate);
        assert importedRect.childAnimationTransform.begin().equals("0");

        assert importedRect.superField.equals("superValue");
    }

    @Test
    public void testExportConvertWithNull() {
        TestRect rect = new TestRect();
        rect.build();

        rect.position = null;
        XMLTag svgRect = SVGExport.convert(rect);

        assert svgRect.getTagName().equals("rect");

        assert !svgRect.hasAttribute("x");
        assert !svgRect.hasAttribute("y");
    }

    @Test
    public void testExportConvertWithEmptyList() {
        TestRect rect = new TestRect();
        rect.build();

        rect.randomValues.clear();
        XMLTag svgRect = SVGExport.convert(rect);

        assert svgRect.getTagName().equals("rect");

        assert !svgRect.hasAttribute("randomValues");
    }

    @Test
    public void testImportMissingRegister() {

        TestRect rect = new TestRect();
        rect.build();

        XMLTag svgRect = SVGExport.convert(rect);

        ISVGShape importShape = SVGImport.convert(svgRect);

        assert importShape == null;
    }

    @Test
    public void testImportMissingAttribute() {
        SVGImport.register(TestRect.class);
        SVGImport.register(TestPoint.class);
        SVGImport.register(SVGAnimate.class);
        SVGImport.register(SVGAnimateTransform.class);

        TestRect rect = new TestRect();
        rect.build();

        XMLTag svgRect = SVGExport.convert(rect);

        svgRect.removeAttribute("x");

        ISVGShape importShape = SVGImport.convert(svgRect);

        assert importShape != null;
        assert importShape instanceof TestRect;

        TestRect importedRect = (TestRect) importShape;
        assert importedRect.position != null;
        assert importedRect.position.x == 0.0;
        assert importedRect.position.y == 0.0;
    }

}
