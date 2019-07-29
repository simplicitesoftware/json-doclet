package com.simplicite.tests.doclets;

/**
 * Test 1
 * @author AZOULAY
 */
public class Test1
{
	/** Sub<br>class 1.1 */
	public class Sub1 {
	}

	/** Sub
	 * <br/>class 1.2
	 */
	public class Sub2 {
	}

	/**
	 * Default constructor 1
	 */
	public Test1() {
	}

	/**
	 * Constructor 2
	 * <br>This one has a long description
	 * <br />And even more
	 * @param value Constructor string<br>value
	 */
	public Test1(String value) {
	}

	/**
	 * Constructor 3
	 * @param value Constructor integer value
	 * @deprecated
	 */
	@Deprecated
	public Test1(int value) {
	}

	/**
	 * Constant 1
	 */
	public static final String CONSTANT1 = "Constant";

	/**
	 * Constant 2
	 */
	public static final int CONSTANT2 = 2;

	/**
	 * Constant 3
	 * @deprecated
	 */
	@Deprecated
	public static final boolean CONSTANT3 = true;

	/**
	 * Variable 1
	 */
	public String variable1 = "Variable";

	/**
	 * Variable 2
	 */
	public int variable2 = 1;

	/**
	 * Variable 3
	 * @deprecated
	 */
	@Deprecated
	public boolean variable3 = false;

	/**
	 * Method 1
	 */
	public static void method1() {
	}

	/**
	 * Method 2:
	 * - Case 1
	 * - Case 2
	 * @param label   	Label for method 2
	 * @param number  	Number for method 2
	 * @param values  	Values array for method 2
	 * @return Method 2 result
	 */
	public String method2(String label, String[] values, int number, boolean flag) {
		return label + " " + number;
	}

	/**
	 * Method 3
	 * <br>Details
	 * @return Method 2 result
	 */
	public byte[] method3() {
		return null;
	}

	/**
	 * Method 4
	 * @deprecated
	 */
	@Deprecated
	public void method4() {
	}
}
