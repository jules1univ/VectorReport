package fr.univrennes.istic.l2gen.visustats.data;

import java.util.ArrayList;
import java.util.List;

public record DataGroup(List<DataSet> datasets, Label title, List<Label> legends) {
    public DataGroup(Label title) {
        this(new ArrayList<>(), title, new ArrayList<>());
    }

    public double sum() {
        return this.datasets.stream().flatMapToDouble(ds -> ds.values().stream().mapToDouble(Value::value)).sum();
    }

    public double max() {
        return this.datasets.stream().flatMapToDouble(ds -> ds.values().stream().mapToDouble(Value::value)).max()
                .orElse(0.0);
    }

    public double min() {
        return this.datasets.stream().flatMapToDouble(ds -> ds.values().stream().mapToDouble(Value::value)).min()
                .orElse(0.0);
    }

    public DataSet get(int index) {
        return this.datasets.get(index);
    }

    public void add(DataSet dataset) {
        this.datasets.add(dataset);
    }

    public void add(Label colorLabel) {
        this.legends.add(colorLabel);
    }

    public int maxSize() {
        return this.datasets.stream().mapToInt(ds -> ds.size()).max().orElse(0);
    }

    public int size() {
        return this.datasets.size();
    }
}
