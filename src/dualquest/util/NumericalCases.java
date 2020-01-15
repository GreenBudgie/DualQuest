package dualquest.util;

/**
 * Allows to operate with numbers and nouns in Russian language.
 */
public class NumericalCases {

	/**
	 * 1, 21, 181, -61...
	 */
	private String one;
	/**
	 * 2, 22, -62...
	 */
	private String few;
	/**
	 * 0, 10, 15, 115, 89, -1057...
	 */
	private String many;

	public NumericalCases(String one, String few, String many) {
		this.one = one;
		this.few = few;
		this.many = many;
	}

	private boolean endsWithOneOf(String str, char[] chars) {
		for(char c : chars) {
			if(str.endsWith(String.valueOf(c))) return true;
		}
		return false;
	}

	public String byNumber(int n) {
		String str = String.valueOf(n);
		char prevChar = str.length() >= 2 ? str.charAt(str.length() - 2) : ' ';
		char[] one = {'1'};
		char[] few = {'2', '3', '4'};
		char[] many = {'5', '6', '7', '8', '9', '0'};
		if(endsWithOneOf(str, many)) return getMany();
		if(endsWithOneOf(str, few) && prevChar != '1') return getFew();
		if(endsWithOneOf(str, one) && prevChar != '1') return getOne();
		return getMany();
	}

	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getFew() {
		return few;
	}

	public void setFew(String few) {
		this.few = few;
	}

	public String getMany() {
		return many;
	}

	public void setMany(String many) {
		this.many = many;
	}

}
