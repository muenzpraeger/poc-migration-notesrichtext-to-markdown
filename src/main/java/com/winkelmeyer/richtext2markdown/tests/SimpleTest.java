/**
 * 
 */

package com.winkelmeyer.richtext2markdown.tests;

import com.winkelmeyer.richtext2markdown.RichTextConverter;

public class SimpleTest {

	public static void main(String[] args) {
		RichTextConverter converter = new RichTextConverter();
		try {
			converter.runConversion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
