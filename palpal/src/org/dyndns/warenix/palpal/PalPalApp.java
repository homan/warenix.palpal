package org.dyndns.warenix.palpal;

import org.dyndns.warenix.palpaltwitter.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PalPalApp extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button startButton = (Button) findViewById(R.id.start);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// UpdateMessageService.start(PalPalApp.this);
			}

		});

		Button stopButton = (Button) findViewById(R.id.stop);
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// UpdateMessageService.stop(PalPalApp.this);
			}

		});

	}
}