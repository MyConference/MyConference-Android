package es.ucm.myconference.util;

import android.text.SpannableString;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpeakersDescriptionHelper {
	
	@SuppressWarnings("deprecation")
	public static void tryFlowText(String text, View photoView, TextView messageView, Display display){
		
		// Get height and width of the image and height of the text line
		photoView.measure(display.getWidth(), display.getHeight());
		int height = photoView.getMeasuredHeight();
		int width = photoView.getMeasuredWidth();
		messageView.measure(width, height); //to allow getTotalPaddingTop
        int padding = messageView.getTotalPaddingTop();
		float textLineHeight = messageView.getPaint().getTextSize();
		
		// Set the span according to the number of lines and width of the image
		int lines =  (int)Math.round((height - padding) / textLineHeight);
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new ViewForSpeakersDescription(lines, width), 0, ss.length(), 0);
		messageView.setText(ss);
		
		// Align the text with the image by removing the rule that the text is to the right of the image
	    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)messageView.getLayoutParams();
	    int[]rules = params.getRules();
	    rules[RelativeLayout.RIGHT_OF] = 0;
	}
}
