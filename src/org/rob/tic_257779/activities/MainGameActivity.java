/*
 * Guillaume Salmon
 * https://github.com/guisalmon/TicTacToe
 */
package org.rob.tic_257779.activities;

import java.util.ArrayList;
import java.util.List;

import org.rob.tic_257779.R;
import org.rob.tic_257779.classes.ArtificialIntelligence;
import org.rob.tic_257779.classes.Player;
import org.rob.tic_257779.preferences.PreferencesActivity;
import org.rob.tic_257779.services.MusicService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * This activity displays the main game and handles the device moves against the player
 * @author Guillaume Salmon
 *
 */
public class MainGameActivity extends Activity {
	
	private List<Button> slots = new ArrayList<Button>();
	private Button aiBegins;
	private boolean withMusic;
	private Player human;
	private ArtificialIntelligence artInt;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        
        //Dynamically creates the grid
        create_board();
        
        //Initialize the ai and player's informations
        initGame();
    }

    @Override
	protected void onResume() {
    	super.onResume();
		withMusic = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("bg_music", true);
		//Starts the background music if necessary
		if (withMusic){
			Intent intent = new Intent(this, MusicService.class);
	        startService(intent);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_game_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.preferences_entry:
			Intent pref = new Intent(this, PreferencesActivity.class);
			startActivity(pref);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
     * Creates dynamically the game layout
     */
    private void create_board(){
    	
    	int count=0;
    	TableLayout table = (TableLayout)findViewById(R.id.board);
    	TableLayout.LayoutParams params = new TableLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, (float) 1.0);
    	TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, (float) 1.0);
    	
    	for (int p=0; p<3; p++){
    		TableRow row = new TableRow(this);
    		row.setLayoutParams(params);
	    	for (int i=0; i<3; i++){
	    		Button button;
	    		button = new Button(this);
	    		//TODO handle color of buttons
	    		//if (p+i == 2) button.setBackgroundColor(Color.RED);
	    		button.setTextColor(Color.GRAY);
	    		button.setLayoutParams(rowParams);
	    		button.setId(count);
	    		row.addView(button);
	    		slots.add(button);
	    		count++;
	    	}
	    	table.addView(row);
    	}
    	
    	//Adds the button allowing the AI to begin
    	TableRow row = new TableRow(this);
		row.setLayoutParams(params);
		aiBegins = new Button(this);
		aiBegins.setTextColor(Color.GRAY);
		aiBegins.setLayoutParams(rowParams);
		aiBegins.setText("Let AI begin");
		aiBegins.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!artInt.hasMoved()){
					artInt.play(slots, human);
				}
			}
		});
		row.addView(aiBegins);
		table.addView(row);
		
		//Adds handling of the grid buttons
    	for(Button b: slots){
    		b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleMove((Button)v);
				}
			});
    	}
    }
    
    //Initiates a new game
    private void initGame(){
    	human = new Player("X");
    	artInt = new ArtificialIntelligence("O");
        for (Button b : slots){
        	b.setText("");
        	b.setClickable(true);
        }
        aiBegins.setClickable(true);
    }
    
    /**
     * Handles the player's move
     * @param b Pressed button
     */
    private void handleMove(Button b) {
    	b.setClickable(false);
    	b.setText("X");
    	if (human.isVictory(b)){
    		Toast.makeText(this, "Victory !", Toast.LENGTH_SHORT).show();
    		endGame();
    	}else{
    		human.move(b);
    		artInt.play(slots, human);
    	}
	}
    
    private void endGame() {
		for (Button b : slots){
			b.setClickable(false);
		}
	}

	@Override
	protected void onPause() {
		Intent intent = new Intent(this, MusicService.class);
		stopService(intent);
		super.onPause();
	}
	
	
}
