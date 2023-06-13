package com.example.abeautifulmind.model;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.abeautifulmind.model.tuple.Tuple;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MindTuple extends Tuple<MindActionWrapper> {

    private final static String DEEP = "deep";
    private final static String TUPLES = "tuples";
    private final static String TUPLES_SIZE = "tuples_size";

    private static final Supplier<? extends Map<Integer, MindActionWrapper>> TUPLE_SUPPLIER = HashMap::new;
    private int deep;
    public MindTuple(int deep) {
        super(new MindActionSet(null));
        MindActionSet previous = (MindActionSet) getFirst(), now;
        this.deep = Math.max(1, deep);
        deep = this.deep;
        while (deep > 1){
            now = new MindActionSet(previous);
            previous.setNext(now);
            previous = now;
            deep--;
        }
    }

    public List<Map<Integer, MindActionWrapper>> generateTuples() {
        return super.generateTuples(TUPLE_SUPPLIER);
    }

    /**
     * Converts the Map to a formatted String.
     */
    private static final String toString(Map<Integer, MindActionWrapper> tuple){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, MindActionWrapper> entry : tuple.entrySet()){
            sb.append(entry.getKey());
            sb.append(" ");
            sb.append(entry.getValue().toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    private static final Map<Integer, MindActionWrapper> fromString(String map){
        try{
            String[] indices = map.split("\n");
            Map<Integer, MindActionWrapper> tuple = TUPLE_SUPPLIER.get();
            int index;
            MindActionWrapper mindActionWrapper;
            for (String str : indices){
                if(!str.isEmpty()){
                    index = str.indexOf(" ");
                    mindActionWrapper = MindActionWrapper.parseString(str.substring(index + 1));
                    index = Integer.parseUnsignedInt(str.substring(0, index));
                    tuple.put(index, mindActionWrapper);
                }
            }
            return tuple;
        }catch (Exception e){}
        return null;
    }

    public Bundle toBundle(@NonNull Bundle bundle, List<Map<Integer, MindActionWrapper>> tuples){
        bundle.putInt(DEEP, deep);
        if (tuples != null && !tuples.isEmpty()){
            int size = tuples.size(), i = 0;
            for (Map<Integer, MindActionWrapper> tuple : tuples){
                bundle.putString(TUPLES + "_" + i++, toString(tuple));
            }
            bundle.putInt(TUPLES_SIZE, size);
        }
        return bundle;
    }

    public static final MindTuple fromBundle(@NonNull Bundle bundle, List<Map<Integer, MindActionWrapper>> tuplesReceiver){
        try {
            int deep = bundle.getInt(DEEP);
            MindTuple mindTuple = new MindTuple(deep);
            if (bundle.containsKey(TUPLES_SIZE) && tuplesReceiver != null){
                int size = bundle.getInt(TUPLES_SIZE);
                for (int j = 0; j < size; j++) {
                    tuplesReceiver.add(fromString(bundle.getString(TUPLES + "_" + j)));
                }
            }
            return mindTuple;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, MindActionWrapper> findByActions(List<MindAction> actions, List<Map<Integer, MindActionWrapper>> tuples) {
        return super.finds(actions == null ? null : actions.stream().map(MindActionWrapper::new).collect(Collectors.toList()), tuples);
    }

    public Map<Integer, MindActionWrapper> maxFor(int index, List<Map<Integer, MindActionWrapper>> tuples) {
        return Collections.max(tuples, Comparator.comparingInt(m -> m.get(index).getUtility()));
    }

    public Map<Integer, MindActionWrapper> minFor(int index, List<Map<Integer, MindActionWrapper>> tuples) {
        return Collections.min(tuples, Comparator.comparingInt(m -> m.get(index).getUtility()));
    }
}
