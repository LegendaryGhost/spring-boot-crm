package site.easy.to.build.crm.util.csv.parameter;

import site.easy.to.build.crm.util.csv.LineValue;

@FunctionalInterface
public interface SetterCSV<T> {
    public T get(LineValue value);
}