package fr.univrennes.istic.l2gen.svg.attributes.path;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.univrennes.istic.l2gen.svg.attributes.path.commands.ArcCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.ArcCommandType;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.CloseCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.CubicBezierCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.CubicBezierCommandType;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.IPathCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.MoveCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.MoveCommandType;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.QuadBezierCommand;
import fr.univrennes.istic.l2gen.svg.attributes.path.commands.QuadBezierCommandType;
import fr.univrennes.istic.l2gen.svg.attributes.path.util.BoundingBox;
import fr.univrennes.istic.l2gen.svg.attributes.path.util.BoundingBoxCalculator;
import fr.univrennes.istic.l2gen.svg.attributes.path.util.ParseCommands;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGAttribute;

/**
 * Représente un chemin SVG implémentant ISVGAttribute.
 * Un chemin est composé d'une série de commandes de tracé (move, line, curve,
 * arc, etc.).
 * Maintient en cache la boîte englobante pour les performances.
 */
public class SVGPath implements ISVGAttribute {

    private List<IPathCommand> commands = new ArrayList<>();

    private boolean isDirty = true;
    private BoundingBox cachedBox = BoundingBox.empty();

    /**
     * Constructeur par défaut. Crée un chemin vide.
     */
    public SVGPath() {
    }

    /**
     * Constructeur avec chaîne de commandes SVG path.
     *
     * @param raw la chaîne de commandes (ex: "M 10 10 L 20 20 Z")
     */
    public SVGPath(String raw) {
        this.commands = ParseCommands.parse(raw);
        refreshBox();
    }

    public void translate(double dx, double dy) {
        this.commands = this.commands.stream().map(cmd -> cmd.translate(dx, dy)).collect(Collectors.toList());
        refreshBox();
    }

    /**
     * Ferme le chemin avec une commande de fermeture.
     */
    public SVGPath close() {
        this.commands.add(new CloseCommand());
        refreshBox();
        return this;
    }

    public SVGPath move(double x, double y, boolean relative) {
        this.commands.add(new MoveCommand(x, y, relative ? MoveCommandType.RELATIVE : MoveCommandType.ABSOLUTE));
        refreshBox();
        return this;
    }

    public SVGPath line(double x, double y, boolean relative) {
        this.commands.add(new MoveCommand(x, y, relative ? MoveCommandType.LINE_RELATIVE : MoveCommandType.LINE));
        refreshBox();
        return this;
    }

    public SVGPath horizontal(double value, boolean relative) {
        this.commands.add(
                new MoveCommand(value, relative ? MoveCommandType.HORIZONTAL_RELATIVE : MoveCommandType.HORIZONTAL));
        refreshBox();
        return this;
    }

    public SVGPath vertical(double value, boolean relative) {
        this.commands
                .add(new MoveCommand(value, relative ? MoveCommandType.VERTICAL_RELATIVE : MoveCommandType.VERTICAL));
        refreshBox();
        return this;
    }

    public SVGPath cubicBezier(double x1, double y1, double x2, double y2, double x, double y, boolean relative) {
        this.commands.add(new CubicBezierCommand(x1, y1, x2, y2, x, y,
                relative ? CubicBezierCommandType.RELATIVE : CubicBezierCommandType.ABSOLUTE));
        refreshBox();
        return this;
    }

    public SVGPath cubicBezierSmooth(double x2, double y2, double x, double y, boolean relative) {
        this.commands.add(new CubicBezierCommand(null, null, x2, y2, x, y,
                relative ? CubicBezierCommandType.SMOOTH_RELATIVE : CubicBezierCommandType.SMOOTH));
        refreshBox();
        return this;
    }

    public SVGPath quadBezier(double x1, double y1, double x, double y, boolean relative) {
        this.commands.add(new QuadBezierCommand(x1, y1, x, y,
                relative ? QuadBezierCommandType.RELATIVE : QuadBezierCommandType.ABSOLUTE));
        refreshBox();
        return this;
    }

    public SVGPath quadBezierSmooth(double x, double y, boolean relative) {
        this.commands.add(new QuadBezierCommand(null, null, x, y,
                relative ? QuadBezierCommandType.SMOOTH_RELATIVE : QuadBezierCommandType.SMOOTH));
        refreshBox();
        return this;
    }

    public SVGPath arc(double rx, double ry, double xAxisRotation, boolean largeArcFlag, boolean sweepFlag, double x,
            double y, boolean relative) {
        this.commands.add(new ArcCommand(rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y,
                relative ? ArcCommandType.RELATIVE : ArcCommandType.ABSOLUTE));
        refreshBox();
        return this;
    }

    public SVGPath reset() {
        this.commands.clear();
        refreshBox();
        return this;
    }

    private void refreshBox() {
        this.isDirty = true;
    }

    public BoundingBox getBoundingBox() {
        if (this.isDirty) {
            if (!this.commands.isEmpty()) {
                this.cachedBox = BoundingBoxCalculator.calculate(this.commands);

            } else {
                this.cachedBox = BoundingBox.empty();
            }
            this.isDirty = false;
        }
        return this.cachedBox;
    }

    public List<IPathCommand> getDrawCommands() {
        return this.commands;
    }

    @Override
    public boolean hasContent() {
        return !this.commands.isEmpty();
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        for (IPathCommand command : this.commands) {
            sb.append(command.getValue());
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}