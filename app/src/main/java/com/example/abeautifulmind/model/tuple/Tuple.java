package com.example.abeautifulmind.model.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Tuple<T> {
    final private TupleItemSet<T> first;

    public Tuple(TupleItemSet<T> first) {
        this.first = first;
    }

    protected TupleItemSet<T> getFirst(){ return  first;}

    public List<Map<Integer, T>> generateTuples(Supplier<? extends Map<Integer, T>> tupleNewInstance) {
        List<Map<Integer, T>> tuples = new ArrayList<>();
        if (first != null && !first.isEmpty()) {
            first.generateTuples(tupleNewInstance.get(), tupleNewInstance, tuples);
        }
        return tuples;
    }

    public Map<Integer, T> finds(List<T> values, List<Map<Integer, T>> tuples) {
        if (values == null || tuples == null || values.isEmpty() || tuples.isEmpty()) {
            return null;
        }
        int size;
        for (Map<Integer, T> tuple : tuples) {
            size = 0;
            try {
                for (Map.Entry<Integer, T> entry : tuple.entrySet()) {
                    if (!Objects.equals(entry.getValue(), values.get(entry.getKey()))) {
                        break;
                    }
                    size++;
                }
                if (size == values.size()) {
                    return tuple;
                }
            } catch (IndexOutOfBoundsException e) {
                return null;
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    public List<Map<Integer, T>> haveAtIndex(T action, int index, List<Map<Integer, T>> tuples) {
        if (action == null || tuples == null || tuples.isEmpty()) {
            return null;
        }
        return tuples.stream().filter(m -> action.equals(m.get(index))).collect(Collectors.toList());
    }

    /*
    public Map<Integer,T> nashEquilibrium(List<Map<Integer,T>> tuples, Function<Map<Integer,T>, Integer> utilityExtractor){
        return null;
    }
    */
}