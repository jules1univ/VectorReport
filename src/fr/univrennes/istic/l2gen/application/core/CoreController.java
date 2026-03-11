package fr.univrennes.istic.l2gen.application.core;

import fr.univrennes.istic.l2gen.application.core.services.chart.ChartService;
import fr.univrennes.istic.l2gen.application.core.services.converter.ConverterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.FilterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.LoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.ReportService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public abstract class CoreController {

    private CSVTable table;

    private final ChartService chart;
    private final ConverterService converter;
    private final FilterService filter;
    private final LoaderService loader;
    private final ReportService report;

    protected CoreController() {
        this.chart = new ChartService();
        this.converter = new ConverterService();
        this.filter = new FilterService();
        this.loader = new LoaderService();
        this.report = new ReportService();
    }

    public LoaderService getLoader() {
        return this.loader;
    }

    public ConverterService getConverter() {
        return this.converter;
    }

    public FilterService getFilter() {
        return this.filter;
    }

    public ChartService getChart() {
        return this.chart;
    }

    public ReportService getReport() {
        return this.report;
    }

    public abstract boolean init();

    public final CSVTable getTable() {
        return this.table;
    }

    public void setTable(CSVTable table) {
        this.table = table;
    }
}
