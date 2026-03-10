package fr.univrennes.istic.l2gen.application.core.services;

import java.util.Map;

import fr.univrennes.istic.l2gen.application.core.services.chart.ChartService;
import fr.univrennes.istic.l2gen.application.core.services.chart.IChartService;
import fr.univrennes.istic.l2gen.application.core.services.converter.ConverterService;
import fr.univrennes.istic.l2gen.application.core.services.converter.IConverterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.FilterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.IFilterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.ILoaderService;
import fr.univrennes.istic.l2gen.application.core.services.loader.LoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.IReportService;
import fr.univrennes.istic.l2gen.application.core.services.report.ReportService;

public final class CoreServices {

    private final Map<Class<?>, Object> services;

    public CoreServices(Map<Class<?>, Object> services) {
        this.services = Map.copyOf(services);
    }

    public static ServiceBuilder builder() {
        return new ServiceBuilder();
    }

    public static CoreServices defaultServices() {
        return builder()
                .register(ILoaderService.class, new LoaderService())
                .register(IFilterService.class, new FilterService())
                .register(IConverterService.class, new ConverterService())
                .register(IChartService.class, new ChartService())
                .register(IReportService.class, new ReportService())
                .build();
    }

    public ILoaderService loader() {
        return get(ILoaderService.class);
    }

    public IFilterService filter() {
        return get(IFilterService.class);
    }

    public IConverterService converter() {
        return get(IConverterService.class);
    }

    public IChartService chart() {
        return get(IChartService.class);
    }

    public IReportService report() {
        return get(IReportService.class);
    }

    public <T> T get(Class<T> contract) {
        Object service = this.services.get(contract);
        if (service == null) {
            throw new IllegalStateException("No service registered for contract: " + contract.getName());
        }
        return contract.cast(service);
    }
}