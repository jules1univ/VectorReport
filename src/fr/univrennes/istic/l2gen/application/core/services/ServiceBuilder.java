package fr.univrennes.istic.l2gen.application.core.services;

import java.util.HashMap;
import java.util.Map;

import fr.univrennes.istic.l2gen.application.core.services.chart.IChartService;
import fr.univrennes.istic.l2gen.application.core.services.converter.IConverterService;
import fr.univrennes.istic.l2gen.application.core.services.filter.IFilterService;
import fr.univrennes.istic.l2gen.application.core.services.loader.ILoaderService;
import fr.univrennes.istic.l2gen.application.core.services.report.IReportService;

public final class ServiceBuilder {

    private final Map<Class<?>, Object> services = new HashMap<>();

    public <T> ServiceBuilder register(Class<T> contract, T service) {
        this.services.put(contract, service);
        return this;
    }

    public CoreServices build() {
        this.require(ILoaderService.class);
        this.require(IFilterService.class);
        this.require(IConverterService.class);
        this.require(IChartService.class);
        this.require(IReportService.class);
        return new CoreServices(this.services);
    }

    private void require(Class<?> contract) {
        if (!this.services.containsKey(contract)) {
            throw new IllegalStateException("Missing required service registration: " + contract.getName());
        }
    }
}