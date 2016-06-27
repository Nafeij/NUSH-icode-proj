package com.jiefanproj.android.embutton_master2.calculator;

public interface Calculator {

	enum Button {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, ZERO,
		EQUALS, PLUS, MINUS, MULTIPLY, DIVIDE
	}

	/** Handles a button press and returns a string to be displayed. */
	String handleButtonPress(Button button);
}
