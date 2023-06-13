package com.example.abeautifulmind.model.tuple;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Representation of one set in the cartesian product A_1 * A_2 * ... * A_n.
 * As a linked list, each instance of this class have the reference of the next or previous set, if exists.
 *
 * @param <T> the type of the wrapped set.
 * @author hovozounkou
 */
public class TupleItemSet<T> {
    final private Set<T> items;
    private TupleItemSet<T> next, previous;

    public TupleItemSet(Set<T> items, TupleItemSet<T> previous) {
        this.items = items;
        this.previous = previous;
    }

    public Set<T> getItems() {
        return items;
    }

    public TupleItemSet<T> getNext() {
        return next;
    }

    /**
     * Changes the next tuple item set only if it is not defined until now.
     */
    public void setNext(TupleItemSet<T> next) {
        if (this.next == null) {
            this.next = next;
            if (next != null && next.previous != this/* Prevent circular dependence*/) {
                next.previous = this;
            }
        }
    }

    public TupleItemSet<T> getPrevious() {
        return previous;
    }

    /**
     * Tests if there is a next set item.
     */
    public boolean hasNext() {
        return next != null;
    }

    /**
     * Tests if the wrapped set is empty.
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    /**
     * Gets the position of this item set. Item which doesn't have previous item has the position 0.
     * The position is increased, by one, switch item's deep.
     */
    public int getPosition() {
        return (previous == null ? 0 : previous.getPosition() + 1);
    }

    /**
     * Generates tuples and adds them to the collection of tuples. Each added tuple is represented by a Map where key is the
     * position of the TupleItemSet.
     *
     * @param from             must be a part of one final tuple, from position '0' to 'i-1' where 'i' is the position of the current.
     * @param tupleNewInstance a supplier of instance of Map to use to store a tuple. Must return a new instance at each invocation.
     * @param tuples           the collection which contains all final tuples.
     */
    public void generateTuples(Map<Integer, T> from, Supplier<? extends Map<Integer, T>> tupleNewInstance, Collection<Map<Integer, T>> tuples) {
        if (items != null && !items.isEmpty()) {
            int position = getPosition();
            Map<Integer, T> witness;
            for (T t : items) {
                witness = tupleNewInstance.get();
                witness.putAll(from);
                witness.put(position, t);
                if (hasNext()) {
                    next.generateTuples(witness, tupleNewInstance, tuples);
                } else {
                    //We are in the last tuple item set.
                    tuples.add(witness);
                }
            }
        } else {
            System.err.println("The tuple set item is empty.");
        }
    }
}
