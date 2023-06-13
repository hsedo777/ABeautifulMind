package com.example.abeautifulmind.model;

import com.example.abeautifulmind.model.tuple.TupleItemSet;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class MindActionSet extends TupleItemSet<MindActionWrapper> {

    public MindActionSet(MindActionSet previous) {
        super(EnumSet.allOf(MindAction.class).stream().map(MindActionWrapper::new).collect(Collectors.toSet()), previous);
    }
}
