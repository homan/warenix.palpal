package org.dyndns.warenix.palpal.service;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.message.TwitterMessage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendMessageService extends IntentService {
	public SendMessageService() {
		super("Send Message Service");
	}

	public SendMessageService(String name) {
		super(name);
	}

	static Context context;

	static boolean isRunning = false;

	static ArrayList<SendableMessage> messageBufferList = new ArrayList<SendableMessage>();
	static ArrayList<SendableMessage> messageSuccessList = new ArrayList<SendableMessage>();

	public boolean doInBackground(SendableMessage message) {
		message.send();
		return false;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TwitterMessage message = (TwitterMessage) intent
				.getSerializableExtra("message");
		doInBackground(message);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		context = this.getApplicationContext();
		Log.d("palpal", "sendMessageService onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	public interface SendableMessage {
		public boolean send();
	}
}
