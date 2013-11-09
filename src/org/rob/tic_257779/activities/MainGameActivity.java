/*
 * Guillaume Salmon
 * https://github.com/guisalmon/TicTacToe
 */
package org.rob.tic_257779.activities;

import java.util.ArrayList;
import java.util.List;

import org.rob.tic_257779.R;
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
	
	/**
	 * This class gives a position on the grid
	 * @author Guillaume Salmon
	 *
	 */
	private class Position {
		public int x;
		public int y;
		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public boolean isCorner() {
			return (x==0 || x==2)&&(y==0 || y==2);
		}
	}
	
	private List<Position> player;
	private List<Position> ai;
	private Button aiBegins;
	private boolean withMusic;
	
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
				if (ai.isEmpty()){
					playAi();
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
    	player = new ArrayList<Position>();
        ai = new ArrayList<Position>();
        for (Button b : slots){
        	b.setText("");
        	b.setClickable(true);
        }
        aiBegins.setClickable(true);
    }
    
    /**
     * Give the position of a given button
     * @param b 
     * @return Position of the button
     */
    private Position getPosition (Button b){
    	return new Position(b.getId()%3, (b.getId()-(b.getId()%3))/3);
    }
    
    /**
     * Handles the player's move
     * @param b Pressed button
     */
    private void handleMove(Button b) {
    	Position p = getPosition(b);
    	b.setClickable(false);
    	b.setText("X");
    	if (isVictory(p, player)){
    		Toast.makeText(this, "Victory !", Toast.LENGTH_SHORT).show();
    		initGame();
    	}else{
    		player.add(p);
    		playAi();
    	}
	}
    
    /**
     * Indicates whether a move allows victory or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return true if the position p allows victory
     */
    private boolean isVictory(Position p, List<Position> l){
    	int line = 1;
    	int col = 1;
    	int diag1 = 1;
    	int diag2 = 1;
    	for (Position pos : l){
    		if (pos.x == p.x) col++;
    		if (pos.y == p.y) line++;
    		if ((p.x == p.y) && (pos.x == pos.y)) diag1++;
    		if ((p.x+p.y == 2) && (pos.x+pos.y == 2)) diag2++;
    	}
    	return ((line == 3)||(col == 3)||(diag1 == 3)||(diag2 == 3));
    }
    
    /**
     * Makes the AI choose the best move to make if it's possible
     */
    private void playAi(){
    	//AI no longers have to begin
    	aiBegins.setClickable(false);
    	
    	//If the game is not already full and if a move allows victory, do this move
    	boolean full = true;
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && isVictory(p, ai)){
    			b.setClickable(false);
    			b.setText("O");
    			Toast.makeText(this, "You lose !", Toast.LENGTH_SHORT).show();
    			initGame();
    			return;
    		}
    		if (b.isClickable()){
    			full = false;
    		}
    	}
    	
    	//If the game is already full declare stalemate
    	//TODO And if it is after AI's turn, genius ?
    	if (full) {
    		Toast.makeText(this, "Stalemate...", Toast.LENGTH_SHORT).show();
    		initGame();
    		return;
    	}
    	
    	//Case where ai hasn't played any move
    	if (ai.isEmpty()){
    		//If the AI is the first to play, choose the upper left corner to play
    		if (player.isEmpty()){
    			Button b = slots.get(0);
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(getPosition(b));
    			return;
    		}else{
    			//Otherwise choose the best move to make regarding the player first move
    			for(Button b : slots){
    				if (b.isClickable() == false){
    					Button bAi;
    					switch(b.getId()){
    					//If the player payed in a corner, choose the opposite corner
    					case 0:
    						bAi = slots.get(8);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					case 2:
    						bAi = slots.get(6);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					case 6:
    						bAi = slots.get(2);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					case 8:
    						bAi = slots.get(0);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					//If the player played in the middle of the grid, play in the upper left corner
    					case 4:
    						bAi = slots.get(0);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					//Otherwise, play in the middle
    					default:
    						bAi = slots.get(4);
    						bAi.setClickable(false);
    						bAi.setText("O");
    						ai.add(getPosition(bAi));
    						return;
    					}
    				}
    			}
    		}
    	}
    	
    	//Block the victory opportunity of the player
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && isVictory(p, player)){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    	
    	//If possible, make two opportunities of victory
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && isFork(p, ai)){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    	
    	//Prevent the player of making two opportunities of victory
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && isFork(p, player)){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    	
    	//Play in a corner to make a victory opportunity
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && p.isCorner() && isTwo(p, ai)){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    	
    	//Play in a corner
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable() && p.isCorner()){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    	
    	//Play in the first available position
    	for (Button b : slots){
    		Position p = getPosition(b);
    		if (b.isClickable()){
    			b.setClickable(false);
    			b.setText("O");
    			ai.add(p);
    			return;
    		}
    	}
    }
    
    /**
     * Indicates whether a move creates a victory opportunity or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return True if this position creates a victory opportunity
     */
	private boolean isTwo(Position p, List<Position> l) {
		List<Position> hypAi = new ArrayList<Position>(l);
		hypAi.add(p);
		for (Button b : slots){
			Position p1 = getPosition(b);
			if (b.isClickable() && isVictory(p1, hypAi) && ((p.x!=p1.x)||(p1.y!=p.y))){
				return true;
			}
		}
		return false;
	}

	/**
     * Indicates whether a move creates two victory opportunities or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return True if this position creates two victory opportunities
     */
	private boolean isFork(Position p, List<Position> ai) {
		List<Position> hypAi = new ArrayList<Position>(ai);
		hypAi.add(p);
		Position p1 = null;
		int branch = 0;
		for (Button b : slots){
			p1 = getPosition(b);
			if (b.isClickable() && isVictory(p1, hypAi) && ((p.x!=p1.x)||(p1.y!=p.y))){
				branch++;
				break;
			}
		}
		for (Button b : slots){
			Position p2 = getPosition(b);
			if (b.isClickable() && isVictory(p2, hypAi) && p1!=null && ((p2.x!=p1.x)||(p1.y!=p2.y)) && ((p.x!=p2.x)||(p2.y!=p.y))){
				branch++;
				hypAi.add(p2);
				break;
			}
		}
		return branch == 2;
	}

	@Override
	protected void onPause() {
		Intent intent = new Intent(this, MusicService.class);
		stopService(intent);
		super.onPause();
	}
	
	
}
