package fr.univrennes.istic.l2gen.svg.attributes.path.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public final class ParseCommands {

    public static List<IPathCommand> parse(String raw) {
        Pattern tokenPattern = Pattern.compile(
                "[MmLlHhVvCcSsQqTtAaZz]|[+-]?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?");

        Matcher matcher = tokenPattern.matcher(raw);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        List<IPathCommand> commands = new ArrayList<>();

        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);

            if (!token.matches("[MmLlHhVvCcSsQqTtAaZz]")) {
                i++;
                continue;
            }

            char cmd = token.charAt(0);
            i++;

            char implicitCmd = cmd;
            if (cmd == 'M') {
                implicitCmd = 'L';

            }
            if (cmd == 'm') {
                implicitCmd = 'l';
            }

            boolean firstOcc = true;
            do {
                char currentCmd = firstOcc ? cmd : implicitCmd;
                firstOcc = false;

                switch (currentCmd) {
                    case 'M': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, y, MoveCommandType.ABSOLUTE));
                        break;
                    }
                    case 'm': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, y, MoveCommandType.RELATIVE));
                        break;
                    }
                    case 'L': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, y, MoveCommandType.LINE));
                        break;
                    }
                    case 'l': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, y, MoveCommandType.LINE_RELATIVE));
                        break;
                    }
                    case 'H': {
                        double x = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, null, MoveCommandType.HORIZONTAL));
                        break;
                    }
                    case 'h': {
                        double x = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(x, null, MoveCommandType.HORIZONTAL_RELATIVE));
                        break;
                    }
                    case 'V': {
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(null, y, MoveCommandType.VERTICAL));
                        break;
                    }
                    case 'v': {
                        double y = parseDouble(tokens, i++);
                        commands.add(new MoveCommand(null, y, MoveCommandType.VERTICAL_RELATIVE));
                        break;
                    }
                    case 'C': {
                        double x1 = parseDouble(tokens, i++);
                        double y1 = parseDouble(tokens, i++);
                        double x2 = parseDouble(tokens, i++);
                        double y2 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new CubicBezierCommand(x1, y1, x2, y2, x, y,
                                CubicBezierCommandType.ABSOLUTE));
                        break;
                    }
                    case 'c': {
                        double x1 = parseDouble(tokens, i++);
                        double y1 = parseDouble(tokens, i++);
                        double x2 = parseDouble(tokens, i++);
                        double y2 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new CubicBezierCommand(x1, y1, x2, y2, x, y,
                                CubicBezierCommandType.RELATIVE));
                        break;
                    }
                    case 'S': {
                        double x2 = parseDouble(tokens, i++);
                        double y2 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new CubicBezierCommand(null, null, x2, y2, x, y,
                                CubicBezierCommandType.SMOOTH));
                        break;
                    }
                    case 's': {
                        double x2 = parseDouble(tokens, i++);
                        double y2 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new CubicBezierCommand(null, null, x2, y2, x, y,
                                CubicBezierCommandType.SMOOTH_RELATIVE));
                        break;
                    }
                    case 'Q': {
                        double x1 = parseDouble(tokens, i++);
                        double y1 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new QuadBezierCommand(x1, y1, x, y,
                                QuadBezierCommandType.ABSOLUTE));
                        break;
                    }
                    case 'q': {
                        double x1 = parseDouble(tokens, i++);
                        double y1 = parseDouble(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new QuadBezierCommand(x1, y1, x, y,
                                QuadBezierCommandType.RELATIVE));
                        break;
                    }
                    case 'T': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new QuadBezierCommand(null, null, x, y,
                                QuadBezierCommandType.SMOOTH));
                        break;
                    }
                    case 't': {
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new QuadBezierCommand(null, null, x, y,
                                QuadBezierCommandType.SMOOTH_RELATIVE));
                        break;
                    }
                    case 'A': {
                        double rx = parseDouble(tokens, i++);
                        double ry = parseDouble(tokens, i++);
                        double xAxisRot = parseDouble(tokens, i++);
                        boolean largeArcFlag = parseFlag(tokens, i++);
                        boolean sweepFlag = parseFlag(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new ArcCommand(rx, ry, xAxisRot, largeArcFlag, sweepFlag,
                                x, y, ArcCommandType.ABSOLUTE));
                        break;
                    }
                    case 'a': {
                        double rx = parseDouble(tokens, i++);
                        double ry = parseDouble(tokens, i++);
                        double xAxisRot = parseDouble(tokens, i++);
                        boolean largeArcFlag = parseFlag(tokens, i++);
                        boolean sweepFlag = parseFlag(tokens, i++);
                        double x = parseDouble(tokens, i++);
                        double y = parseDouble(tokens, i++);
                        commands.add(new ArcCommand(rx, ry, xAxisRot, largeArcFlag, sweepFlag,
                                x, y, ArcCommandType.RELATIVE));
                        break;
                    }
                    case 'Z':
                    case 'z': {
                        commands.add(new CloseCommand());
                        break;
                    }
                    default:
                        break;
                }

            } while (i < tokens.size() && isNumber(tokens.get(i)));
        }

        return commands;
    }

    private static double parseDouble(List<String> tokens, int index) {
        if (index >= tokens.size())
            return 0.0;
        try {
            return Double.parseDouble(tokens.get(index));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static boolean parseFlag(List<String> tokens, int index) {
        return parseDouble(tokens, index) != 0.0;
    }

    private static boolean isNumber(String token) {
        return token.matches("[+-]?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?");
    }
}
