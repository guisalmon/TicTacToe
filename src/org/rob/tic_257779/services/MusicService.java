package org.rob.tic_257779.services;

import org.rob.tic_257779.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service{
	private MediaPlayer player;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "Media Player", Toast.LENGTH_SHORT).show();
		player = MediaPlayer.create(this, R.raw.music);
		player.setLooping(true);
		player.start();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		player.stop();
		super.onDestroy();
	}


}
