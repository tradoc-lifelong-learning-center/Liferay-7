package mil.tjaglcs.mlrselector.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Average {
	private int mean;
	private List<Integer> mode;
	
	
	
	public Average(int[] numbers) {
		this.mode = setMode(numbers);
	}

	public List<Integer> getMode() {
		return mode;
	}

	public static List<Integer> setMode(final int[] numbers) {
		//https://stackoverflow.com/questions/4191687/how-to-calculate-mean-median-mode-and-range-from-a-set-of-numbers
	    final List<Integer> modes = new ArrayList<Integer>();
	    final Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();

	    int max = -1;

	    for (final int n : numbers) {
	        int count = 0;

	        if (countMap.containsKey(n)) {
	            count = countMap.get(n) + 1;
	        } else {
	            count = 1;
	        }

	        countMap.put(n, count);

	        if (count > max) {
	            max = count;
	        }
	    }

	    for (final Map.Entry<Integer, Integer> tuple : countMap.entrySet()) {
	        if (tuple.getValue() == max) {
	            modes.add(tuple.getKey());
	        }
	    }

	    return modes;
	}
}