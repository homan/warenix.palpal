package org.dyndns.warenix.lab.compat1.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView.Tokenizer;

/**
 * trigger autocompletion when "indicator" is entered
 * 
 * @author warenix
 * 
 */
public class IndicatorTokenizer implements Tokenizer {

	char mIndicator = ' ';

	public IndicatorTokenizer(char indicator) {
		mIndicator = indicator;
	}

	public int findTokenStart(CharSequence text, int cursor) {
		int i = cursor;

		while (i > 0 && text.charAt(i - 1) != mIndicator) {
			i--;
		}
		while (i < cursor && text.charAt(i) == mIndicator) {
			i++;
		}

		return i;
	}

	public int findTokenEnd(CharSequence text, int cursor) {
		int i = cursor;
		int len = text.length();

		while (i < len) {
			if (text.charAt(i) == mIndicator) {
				return i;
			} else {
				i++;
			}
		}

		return len;
	}

	public CharSequence terminateToken(CharSequence text) {
		int i = text.length();

		while (i > 0 && text.charAt(i - 1) == mIndicator) {
			i--;
		}

		if (i > 0 && text.charAt(i - 1) == mIndicator) {
			return text;
		} else {
			if (text instanceof Spanned) {
				SpannableString sp = new SpannableString(text + " ");
				TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
						Object.class, sp, 0);
				return sp;
			} else {
				return text + " ";
			}
		}
	}
}
