package com.example.abeautifulmind.model.tuple;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

	/**
	 * Tries to find the tuple that matches the list <code>values</code> using item index in the list as tuple key
	 * in the supplied tuple list.
	 */
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
			} catch (Exception e) {}
		}
		return null;
	}

	/**
	* Gets the list of tuples which have value <code>action</code> mapped with key <code>index</code> in the supplied tuples list.
	 * @param <T> the type of the mapping action
	*/
	public static <T> List<Map<Integer, T>> haveAtIndex(T action, int index, List<Map<Integer, T>> tuples) {
		if (action == null || tuples == null || tuples.isEmpty()) {
			return null;
		}
		return tuples.stream().filter(m -> action.equals(m.get(index))).collect(Collectors.toList());
	}

	/**
	 * Tries to get get all tuples that can be used to test if the tuple <code>choice/code> is a Nash Equilibrium
	 * for player with index <code>index</code>. The returned list is not include the tuple <code>choice</code>.
	 * @param <T> the type of the mapping action.
	 */
	public static <T> List<Map<Integer, T>> nashCandidates(int index, Map<Integer, T> choice, List<Map<Integer, T>> tuples){
		if(choice == null || choice.isEmpty() || tuples == null || tuples.isEmpty()){
			return null;
		}
		try{
			T tampon = choice.remove(index);
			Set<Map.Entry<Integer, T>> set = choice.entrySet();
			List<Map<Integer, T>> out = tuples.stream().filter(m -> m.entrySet().containsAll(set) && choice != m).collect(Collectors.toList());
			choice.put(index, tampon);
			return out;
		}catch (Exception ignore){}
		return null;
	}

    /*
    public Map<Integer,T> nashEquilibrium(List<Map<Integer,T>> tuples, Function<Map<Integer,T>, Integer> utilityExtractor){
        return null;
    }
    */
}