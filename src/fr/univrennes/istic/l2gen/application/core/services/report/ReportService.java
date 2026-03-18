package fr.univrennes.istic.l2gen.application.core.services.report;

import java.util.Map;

import fr.univrennes.istic.l2gen.application.core.services.IService;
import fr.univrennes.istic.l2gen.io.svg.SVGExport;
import fr.univrennes.istic.l2gen.svg.interfaces.ISVGShape;

public class ReportService implements IService {

    public String export(String title, String conclusion, Map<ISVGShape, String> pairGraphDescription,
            String footer) {
        StringBuilder res = new StringBuilder();
        res.append("<html>");
        res.append(makeDefaultHead());
        res.append(makeTitle(title));
        res.append(makeReport(pairGraphDescription));
        res.append(makeConclusion(conclusion));
        res.append(makeFooter(footer));
        res.append("</body>");
        res.append("</html>");
        return res.toString();
    }

    /**
     * On crée un <head> HTML par défaut contenant le charset UTF-8, ainsi que la
     * meta viewport.
     */

    private String makeDefaultHead() {
        StringBuilder sb = new StringBuilder();
        sb.append("<head>");
        sb.append("<meta charset=\"utf-8\"/>");
        sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>");
        sb.append("<title>Report</title>");
        sb.append("<style>");
        sb.append("body{font-family:Arial,Helvetica,sans-serif;margin:0;padding:0;} .report{padding:1rem;}");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body><div class=\"report\">");
        return sb.toString();
    }

    private String makeTitle(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>").append(title).append("</h1>");
        return sb.toString();
    }

    private String makeConclusion(String conclusion) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h3>Conclusion</h3>");
        sb.append("<p>").append(conclusion).append("</p>");
        return sb.toString();
    }

    private String makeFooter(String footer) {
        StringBuilder sb = new StringBuilder();
        sb.append("<footer><p>").append(footer).append("</p></footer>");
        return sb.toString();
    }

    private String makeReport(Map<ISVGShape, String> pairGraphDescription) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<ISVGShape, String> entry : pairGraphDescription.entrySet()) {
            ISVGShape shape = entry.getKey();
            String SVGdata = SVGExport.convert(shape).toString();
            String description = entry.getValue();
            sb.append("<div class=\"report-item\">");
            sb.append("<div class=\"report-shape\">").append(SVGdata).append("</div>");
            sb.append("<div class=\"report-description\"><p>").append(description).append("</p></div>");
            sb.append("</div>");
        }
        return sb.toString();
    }

}
