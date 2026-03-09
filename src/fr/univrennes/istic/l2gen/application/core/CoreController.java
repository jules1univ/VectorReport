package fr.univrennes.istic.l2gen.application.core;

import fr.univrennes.istic.l2gen.application.core.services.chart.ChartService;
import fr.univrennes.istic.l2gen.application.core.services.filter.FilterService;
import fr.univrennes.istic.l2gen.application.core.services.converter.ConverterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.LoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.ReportService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public abstract class CoreController {

    private final LoaderService loader = new LoaderService();

    private final FilterService filter = new FilterService();
    private final ConverterService convert = new ConverterService();

    private final ChartService chart = new ChartService();
    private final ReportService report = new ReportService();

    private CSVTable currentTable;

    public CoreController() {
    }

    public LoaderService getLoader() {
        return this.loader;
    }

    public FilterService getFilter() {
        return this.filter;
    }

    public ConverterService getConverter() {
        return this.convert;
    }

    public ChartService getChart() {
        return this.chart;
    }

    public ReportService getReport() {
        return this.report;
    }

    public abstract boolean init();

    public final CSVTable getTable() {
        return this.currentTable;
    }

    public final void setTable(CSVTable table) {
        this.currentTable = table;
    }
}
