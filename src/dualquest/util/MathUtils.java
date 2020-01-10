package dualquest.util;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

/**
 * Performs mathematical, logical and other number operations
 */
public class MathUtils {

	/**
	 * Returns true or false based on random chance in percents. If the chance is larger or equal to 100 it will always return true; same thing with numbers smaller or equal to 0
	 * @param chance chance in percents between 0 and 100
	 */
	public static boolean chance(double chance) {
		if(chance <= 0) return false;
		if(chance >= 100) return true;
		return chance / 100 > Math.random();
	}

	/**
	 * Gets an integer number between (or equal to) the given minimum and maximum values
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return The number between (or equal to) the given minimum and maximum values
	 */
	public static int randomRange(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	/**
	 * Gets a double value between the given minimum and maximum values
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return The value between the given minimum and maximum values
	 */
	public static double randomRangeDouble(double min, double max) {
		return min + (max - min) * Math.random();
	}

	/**
	 * Clamps the given double value between the given minimum and maximum values
	 * @param num The number to clamp
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Given number, if it is between the minimum and maximum values; Minimum value if the number is smaller than it; Maximum value if the number is larger than it
	 */
	public static double clamp(double num, double min, double max) {
		if(num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	/**
	 * Clamps the given integer number between the given minimum and maximum values
	 * @param num The number to clamp
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Given number, if it is between the minimum and maximum values; Minimum value if the number is smaller than it; Maximum value if the number is larger than it
	 */
	public static int clamp(int num, int min, int max) {
		if(num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	/**
	 * Chooses a random element from the given array
	 * @param values Values to choose
	 * @return Random element from the given array
	 */
	public static <T> T choose(T... values) {
		if(values.length == 0) throw new IllegalArgumentException("Cannot choose an element from an empty array");
		return values[randomRange(0, values.length - 1)];
	}

	/**
	 * Chooses a random element from the given collection
	 * @param values Values to choose
	 * @return Random element from the given collection
	 */
	public static <T> T choose(Collection<T> values) {
		List<T> list = Lists.newArrayList(values);
		if(list.size() == 0) throw new IllegalArgumentException("Cannot choose an element from an empty collection");
		return list.get(randomRange(0, values.size() - 1));
	}


}
