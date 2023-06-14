package com.example.abeautifulmind.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class MindActionWrapper {
	final private MindAction action;
	private int utility;

	public MindActionWrapper(MindAction action) {
		this(action, 0);
	}

	public MindActionWrapper(MindAction action, int utility) {
		this.action = action;
		this.utility = utility;
	}

	public MindAction getAction() {
		return action;
	}

	public int getUtility() {
		return utility;
	}

	public void setUtility(int utility) {
		this.utility = utility;
	}

	@NonNull
	@Override
	public String toString() {
		return "(" + action + "," + utility + ')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MindActionWrapper wrapper = (MindActionWrapper) o;
		return action == wrapper.action;
	}

	@Override
	public int hashCode() {
		return Objects.hash(action);
	}

	/**
	 * Parses the input string and produces the appropriate MindActionWrapper.
	 * @param toParse must be non null and of type returned by method {@link #toString}
	 */
	public static MindActionWrapper parseString(String toParse){
		MindActionWrapper mindActionWrapper = null;
		try{
			toParse = toParse.substring(1, toParse.length() - 1);
			int i = toParse.lastIndexOf(',');
			MindAction action = MindAction.valueOf(toParse.substring(0, i));
			int utility = Integer.parseInt(toParse.substring(i + 1));
			mindActionWrapper = new MindActionWrapper(action, utility);
		}catch (Exception ignored){}
		return mindActionWrapper;
	}
}