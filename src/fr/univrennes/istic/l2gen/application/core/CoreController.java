package fr.univrennes.istic.l2gen.application.core;

import java.util.Optional;

import fr.univrennes.istic.l2gen.application.core.services.chart.ChartService;
import fr.univrennes.istic.l2gen.application.core.services.converter.ConverterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.FilterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.LoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.ReportService;
import fr.univrennes.istic.l2gen.application.core.services.stats.StatsService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public abstract class CoreController {

    private CSVTable mainTable;
    private CSVTable currentTable;

    private final ChartService chart;
    private final ConverterService converter;
    private final FilterService filter;
    private final StatsService stats;
    private final LoaderService loader;
    private final ReportService report;

    protected CoreController() {
        this.chart = new ChartService();
        this.converter = new ConverterService();
        this.filter = new FilterService();
        this.loader = new LoaderService();
        this.report = new ReportService();
        this.stats = new StatsService();
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

    public StatsService getStats() {
        return this.stats;
    }

    public ChartService getChart() {
        return this.chart;
    }

    public ReportService getReport() {
        return this.report;
    }

    public final Optional<CSVTable> getMainTable() {
        return Optional.ofNullable(mainTable);
    }

    public final void setMainTable(CSVTable table) {
        this.mainTable = table;
    }

    public final Optional<CSVTable> getCurrentTable() {
        return Optional.ofNullable(currentTable);
    }

    public void setCurrentTable(CSVTable table) {
        this.currentTable = table;
    }
}
