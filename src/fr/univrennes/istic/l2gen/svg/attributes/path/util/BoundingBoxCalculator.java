package fr.univrennes.istic.l2gen.svg.attributes.path.util;

import java.util.List;

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

public class BoundingBoxCalculator {

    private static double[] cubicExtrema(double p0, double p1, double p2, double p3,
            double min, double max) {
        double a = -3 * p0 + 9 * p1 - 9 * p2 + 3 * p3;
        double b = 6 * p0 - 12 * p1 + 6 * p2;
        double c = -3 * p0 + 3 * p1;

        if (Math.abs(a) < 1e-12) {
            if (Math.abs(b) > 1e-12) {
                double t = -c / b;
                if (t > 0 && t < 1) {
                    double v = cubicAt(p0, p1, p2, p3, t);
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
        } else {
            double disc = b * b - 4 * a * c;
            if (disc >= 0) {
                double sqrtDisc = Math.sqrt(disc);
                double t1 = (-b + sqrtDisc) / (2 * a);
                double t2 = (-b - sqrtDisc) / (2 * a);
                if (t1 > 0 && t1 < 1) {
                    double v = cubicAt(p0, p1, p2, p3, t1);
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
                if (t2 > 0 && t2 < 1) {
                    double v = cubicAt(p0, p1, p2, p3, t2);
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
        }
        return new double[] { min, max };
    }

    private static double cubicAt(double p0, double p1, double p2, double p3, double t) {
        double mt = 1 - t;
        return mt * mt * mt * p0
                + 3 * mt * mt * t * p1
                + 3 * mt * t * t * p2
                + t * t * t * p3;
    }

    private static double[] quadExtrema(double p0, double p1, double p2,
            double min, double max) {
        double denom = p0 - 2 * p1 + p2;
        if (Math.abs(denom) > 1e-12) {
            double t = (p0 - p1) / denom;
            if (t > 0 && t < 1) {
                double v = quadAt(p0, p1, p2, t);
                min = Math.min(min, v);
                max = Math.max(max, v);
            }
        }
        return new double[] { min, max };
    }

    private static double quadAt(double p0, double p1, double p2, double t) {
        double mt = 1 - t;
        return mt * mt * p0 + 2 * mt * t * p1 + t * t * p2;
    }

    private static double[] arcBounds(double x1, double y1,
            double rx, double ry, double xRot,
            boolean largeArc, boolean sweep,
            double x2, double y2,
            double minX, double minY,
            double maxX, double maxY) {
        if (rx == 0 || ry == 0) {
            minX = Math.min(minX, x2);
            maxX = Math.max(maxX, x2);
            minY = Math.min(minY, y2);
            maxY = Math.max(maxY, y2);
            return new double[] { minX, minY, maxX, maxY };
        }

        double phi = Math.toRadians(xRot);
        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);

        double dx2 = (x1 - x2) / 2.0;
        double dy2 = (y1 - y2) / 2.0;
        double x1p = cosPhi * dx2 + sinPhi * dy2;
        double y1p = -sinPhi * dx2 + cosPhi * dy2;

        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double x1pSq = x1p * x1p;
        double y1pSq = y1p * y1p;
        double rxSq = rx * rx;
        double rySq = ry * ry;
        double lambda = x1pSq / rxSq + y1pSq / rySq;
        if (lambda > 1) {
            double sqrtL = Math.sqrt(lambda);
            rx *= sqrtL;
            ry *= sqrtL;
            rxSq = rx * rx;
            rySq = ry * ry;
        }

        double num = rxSq * rySq - rxSq * y1pSq - rySq * x1pSq;
        double den = rxSq * y1pSq + rySq * x1pSq;
        double sq = (den == 0) ? 0 : Math.sqrt(Math.max(0, num / den));
        double sign = (largeArc == sweep) ? -1 : 1;
        double cxp = sign * sq * rx * y1p / ry;
        double cyp = -sign * sq * ry * x1p / rx;

        double cx = cosPhi * cxp - sinPhi * cyp + (x1 + x2) / 2.0;
        double cy = sinPhi * cxp + cosPhi * cyp + (y1 + y2) / 2.0;

        double ux = (x1p - cxp) / rx;
        double uy = (y1p - cyp) / ry;
        double vx = (-x1p - cxp) / rx;
        double vy = (-y1p - cyp) / ry;

        double theta1 = angle(1, 0, ux, uy);
        double dTheta = angle(ux, uy, vx, vy);

        if (!sweep && dTheta > 0)
            dTheta -= 2 * Math.PI;
        if (sweep && dTheta < 0)
            dTheta += 2 * Math.PI;

        double theta2 = theta1 + dTheta;

        double thetaXext = Math.atan2(-ry * sinPhi, rx * cosPhi);
        double thetaYext = Math.atan2(ry * cosPhi, rx * sinPhi);

        double[] candidates = {
                theta1, theta2,
                thetaXext, thetaXext + Math.PI,
                thetaYext, thetaYext + Math.PI
        };

        for (double theta : candidates) {
            if (isAngleInArc(theta, theta1, dTheta)) {
                double px = cx + rx * Math.cos(theta) * cosPhi - ry * Math.sin(theta) * sinPhi;
                double py = cy + rx * Math.cos(theta) * sinPhi + ry * Math.sin(theta) * cosPhi;
                minX = Math.min(minX, px);
                maxX = Math.max(maxX, px);
                minY = Math.min(minY, py);
                maxY = Math.max(maxY, py);
            }
        }

        minX = Math.min(minX, Math.min(x1, x2));
        maxX = Math.max(maxX, Math.max(x1, x2));
        minY = Math.min(minY, Math.min(y1, y2));
        maxY = Math.max(maxY, Math.max(y1, y2));

        return new double[] { minX, minY, maxX, maxY };
    }

    private static double angle(double ux, double uy, double vx, double vy) {
        double n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        if (n == 0)
            return 0;
        double c = Math.max(-1, Math.min(1, (ux * vx + uy * vy) / n));
        double a = Math.acos(c);
        return (ux * vy - uy * vx < 0) ? -a : a;
    }

    private static boolean isAngleInArc(double theta, double theta1, double dTheta) {
        double t = theta - theta1;
        if (dTheta > 0) {
            t = t % (2 * Math.PI);
            if (t < 0)
                t += 2 * Math.PI;
            return t <= dTheta;
        } else {
            t = t % (2 * Math.PI);
            if (t > 0)
                t -= 2 * Math.PI;
            return t >= dTheta;
        }
    }

    public static BoundingBox calculate(List<IPathCommand> commands) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        double currentX = 0.0;
        double currentY = 0.0;

        double subpathStartX = 0.0;
        double subpathStartY = 0.0;

        double lastCubicCPX = 0.0;
        double lastCubicCPY = 0.0;
        boolean lastWasCubic = false;

        double lastQuadCPX = 0.0;
        double lastQuadCPY = 0.0;
        boolean lastWasQuad = false;

        for (IPathCommand command : commands) {

            if (command instanceof MoveCommand move) {
                lastWasCubic = false;
                lastWasQuad = false;

                double x = move.x() != null ? move.x() : 0.0;
                double y = move.y() != null ? move.y() : 0.0;

                switch (move.type()) {
                    case ABSOLUTE, LINE -> {
                        currentX = x;
                        currentY = y;
                    }
                    case RELATIVE, LINE_RELATIVE -> {
                        currentX += x;
                        currentY += y;
                    }
                    case HORIZONTAL -> {
                        currentX = x;
                    }
                    case HORIZONTAL_RELATIVE -> {
                        currentX += x;
                    }
                    case VERTICAL -> {
                        currentY = y;
                    }
                    case VERTICAL_RELATIVE -> {
                        currentY += y;
                    }
                }

                if (move.type() == MoveCommandType.ABSOLUTE || move.type() == MoveCommandType.RELATIVE) {
                    subpathStartX = currentX;
                    subpathStartY = currentY;
                }

                minX = Math.min(minX, currentX);
                minY = Math.min(minY, currentY);
                maxX = Math.max(maxX, currentX);
                maxY = Math.max(maxY, currentY);

            } else if (command instanceof CubicBezierCommand cubic) {

                double ax0 = currentX;
                double ay0 = currentY;
                double ax1, ay1, ax2, ay2, ax3, ay3;

                boolean isRelative = cubic.type() == CubicBezierCommandType.RELATIVE
                        || cubic.type() == CubicBezierCommandType.SMOOTH_RELATIVE;

                if (cubic.type() == CubicBezierCommandType.SMOOTH
                        || cubic.type() == CubicBezierCommandType.SMOOTH_RELATIVE) {
                    if (lastWasCubic) {
                        ax1 = 2 * currentX - lastCubicCPX;
                        ay1 = 2 * currentY - lastCubicCPY;
                    } else {
                        ax1 = currentX;
                        ay1 = currentY;
                    }
                } else {
                    ax1 = isRelative ? currentX + cubic.x1() : cubic.x1();
                    ay1 = isRelative ? currentY + cubic.y1() : cubic.y1();
                }

                ax2 = isRelative ? currentX + cubic.x2() : cubic.x2();
                ay2 = isRelative ? currentY + cubic.y2() : cubic.y2();
                ax3 = isRelative ? currentX + cubic.x() : cubic.x();
                ay3 = isRelative ? currentY + cubic.y() : cubic.y();

                minX = Math.min(minX, Math.min(ax0, ax3));
                maxX = Math.max(maxX, Math.max(ax0, ax3));
                minY = Math.min(minY, Math.min(ay0, ay3));
                maxY = Math.max(maxY, Math.max(ay0, ay3));

                double[] xBounds = cubicExtrema(ax0, ax1, ax2, ax3, minX, maxX);
                double[] yBounds = cubicExtrema(ay0, ay1, ay2, ay3, minY, maxY);
                minX = xBounds[0];
                maxX = xBounds[1];
                minY = yBounds[0];
                maxY = yBounds[1];

                lastCubicCPX = ax2;
                lastCubicCPY = ay2;
                lastWasCubic = true;
                lastWasQuad = false;
                currentX = ax3;
                currentY = ay3;

            } else if (command instanceof QuadBezierCommand quad) {

                double ax0 = currentX;
                double ay0 = currentY;
                double ax1, ay1, ax2, ay2;

                boolean isRelative = quad.type() == QuadBezierCommandType.RELATIVE
                        || quad.type() == QuadBezierCommandType.SMOOTH_RELATIVE;

                if (quad.type() == QuadBezierCommandType.SMOOTH
                        || quad.type() == QuadBezierCommandType.SMOOTH_RELATIVE) {
                    if (lastWasQuad) {
                        ax1 = 2 * currentX - lastQuadCPX;
                        ay1 = 2 * currentY - lastQuadCPY;
                    } else {
                        ax1 = currentX;
                        ay1 = currentY;
                    }
                } else {
                    ax1 = isRelative ? currentX + quad.x1() : quad.x1();
                    ay1 = isRelative ? currentY + quad.y1() : quad.y1();
                }

                ax2 = isRelative ? currentX + quad.x() : quad.x();
                ay2 = isRelative ? currentY + quad.y() : quad.y();

                // Expand with endpoints first
                minX = Math.min(minX, Math.min(ax0, ax2));
                maxX = Math.max(maxX, Math.max(ax0, ax2));
                minY = Math.min(minY, Math.min(ay0, ay2));
                maxY = Math.max(maxY, Math.max(ay0, ay2));

                double[] xBounds = quadExtrema(ax0, ax1, ax2, minX, maxX);
                double[] yBounds = quadExtrema(ay0, ay1, ay2, minY, maxY);
                minX = xBounds[0];
                maxX = xBounds[1];
                minY = yBounds[0];
                maxY = yBounds[1];

                lastQuadCPX = ax1;
                lastQuadCPY = ay1;
                lastWasQuad = true;
                lastWasCubic = false;
                currentX = ax2;
                currentY = ay2;

            } else if (command instanceof ArcCommand arc) {
                lastWasCubic = false;
                lastWasQuad = false;

                double ex, ey;
                if (arc.type() == ArcCommandType.RELATIVE) {
                    ex = currentX + arc.x();
                    ey = currentY + arc.y();
                } else {
                    ex = arc.x();
                    ey = arc.y();
                }

                double[] b = arcBounds(
                        currentX, currentY,
                        arc.rx(), arc.ry(), arc.xAxisRotation(),
                        arc.largeArcFlag(), arc.sweepFlag(),
                        ex, ey,
                        minX, minY, maxX, maxY);
                minX = b[0];
                minY = b[1];
                maxX = b[2];
                maxY = b[3];

                currentX = ex;
                currentY = ey;

            } else if (command instanceof CloseCommand) {
                lastWasCubic = false;
                lastWasQuad = false;
                currentX = subpathStartX;
                currentY = subpathStartY;
            }
        }

        return new BoundingBox(minX, minY, maxX, maxY);
    }

}
