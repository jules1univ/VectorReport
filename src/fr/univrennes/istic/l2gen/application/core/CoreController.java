package fr.univrennes.istic.l2gen.application.core;

import fr.univrennes.istic.l2gen.application.core.services.CoreServices;
import fr.univrennes.istic.l2gen.application.core.services.chart.IChartService;
import fr.univrennes.istic.l2gen.application.core.services.converter.IConverterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.IFilterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.ILoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.IReportService;
import fr.univrennes.istic.l2gen.io.csv.model.CSVTable;

public abstract class CoreController {

    private final CoreServices services;
    private CSVTable currentTable;

    protected CoreController() {
        this(CoreServices.defaultServices());
    }

    protected CoreController(CoreServices services) {
        this.services = services;
    }

    public ILoaderService getLoader() {
        return this.services.loader();
    }

    public IFilterService getFilter() {
        return this.services.filter();
    }

    public IConverterService getConverter() {
        return this.services.converter();
    }

    public IChartService getChart() {
        return this.services.chart();
    }

    public IReportService getReport() {
        return this.services.report();
    }

    public final <T> T getService(Class<T> contract) {
        return this.services.get(contract);
    }

    public abstract boolean init();

    public final CSVTable getTable() {
        return this.currentTable;
    }

    public void setTable(CSVTable table) {
        this.currentTable = table;
    }
}
