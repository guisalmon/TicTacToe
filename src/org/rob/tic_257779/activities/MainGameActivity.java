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
import android.content.SharedPreferences;
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

/**
 * This activity displays the main game and handles the device moves against the
 * player
 * 
 * @author Guillaume Salmon
 * 
 */
public class MainGameActivity extends Activity {

	public static final String GAME_SAVE = "GameSave";

	private List<Button> slots = new ArrayList<Button>();
	private Button aiBegins;
	private boolean withMusic;
	private Player human;
	private ArtificialIntelligence artInt;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_game);

		// Dynamically creates the grid
		create_board();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Initialize the ai and player's informations
		initGame();

		// Restore previous game
		settings = getPreferences(MODE_PRIVATE);
		restoreGame(settings.getInt(GAME_SAVE, 0));

		// Start the background music if necessary
		withMusic = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean("bg_music", true);
		if (withMusic) {
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

	@Override
	protected void onPause() {
		// Stop music if it's running
		if (withMusic) {
			Intent intent = new Intent(this, MusicService.class);
			stopService(intent);
		}

		// Save current game in preferences
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.clear();
		prefEditor.putInt(GAME_SAVE, createSave());
		prefEditor.commit();
		super.onPause();
	}

	/**
	 * Creates a int where each digit indicates who own the button of the
	 * corresponding index in slots
	 * 
	 * For instance the following game configuration with X as the human player
	 * X O X OO will give the following int : 102010220
	 * 
	 * @return a int where each digit indicates who own the button of the
	 *         corresponding index in slots
	 */
	private int createSave() {
		int save = 0;
		for (Button b : slots) {
			save = save * 10;
			if (human.hasButton(b)) {
				save = save + 1;
			}
			if (artInt.hasButton(b)) {
				save = save + 2;
			}
		}
		return save;
	}

	/**
	 * Restores the game according to the save given in parameter
	 * 
	 * @param save
	 *            to restore
	 */
	private void restoreGame(int save) {
		for (int i = 8; i >= 0; i--) {
			switch (save % 10) {
			case 2:
				artInt.move(slots.get(i));
				break;
			case 1:
				human.move(slots.get(i));
			default:
				break;
			}
			save = save / 10;
		}
	}

	/**
	 * Creates dynamically the game layout
	 */
	private void create_board() {

		int count = 0;
		TableLayout table = (TableLayout) findViewById(R.id.board);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, (float) 1.0);
		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0,
				LayoutParams.MATCH_PARENT, (float) 1.0);
		rowParams.setMargins(10, 10, 10, 10);

		for (int p = 0; p < 3; p++) {
			TableRow row = new TableRow(this);
			row.setLayoutParams(params);
			for (int i = 0; i < 3; i++) {
				Button button;
				button = new Button(this);
				button.setTextColor(Color.DKGRAY);
				button.setLayoutParams(rowParams);
				button.setId(count);
				row.addView(button);
				slots.add(button);
				count++;
			}
			table.addView(row);
		}

		// Adds the button allowing the AI to begin
		TableRow row = new TableRow(this);
		row.setLayoutParams(params);
		aiBegins = new Button(this);
		aiBegins.setLayoutParams(rowParams);
		aiBegins.setText("Let AI begin");
		aiBegins.setTextColor(Color.DKGRAY);
		aiBegins.setBackgroundColor(Color.LTGRAY);
		aiBegins.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!artInt.hasMoved()) {
					artInt.play(slots, human);
					aiBegins.setClickable(false);
				}
			}
		});
		row.addView(aiBegins);

		// Adds the button allowing the AI to begin
		Button newGame = new Button(this);
		newGame.setLayoutParams(rowParams);
		newGame.setText("Begin new game");
		newGame.setTextColor(Color.DKGRAY);
		newGame.setBackgroundColor(Color.LTGRAY);
		newGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initGame();
			}
		});
		row.addView(newGame);
		table.addView(row);

		// Adds handling of the grid buttons
		for (Button b : slots) {
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handleMove((Button) v);
					if (aiBegins.isClickable())
						aiBegins.setClickable(false);
				}
			});
		}

		((Button) findViewById(R.id.message))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						initGame();
					}
				});
	}

	/**
	 * Determinates if further moves are possible
	 * 
	 * @return true if no moves are possible
	 */
	private boolean isStalemate() {
		boolean stalemate = true;
		for (Button b : slots) {
			stalemate = stalemate && !b.isClickable();
		}
		return stalemate;
	}

	/**
	 * Initiates a new game
	 */
	private void initGame() {
		findViewById(R.id.board).setVisibility(View.VISIBLE);
		int humanColor = Color.parseColor(PreferenceManager
				.getDefaultSharedPreferences(this).getString("bg_human",
						"#55cc55"));
		int aiColor = Color.parseColor(PreferenceManager
				.getDefaultSharedPreferences(this)
				.getString("bg_ai", "#cc5555"));
		String humanName = PreferenceManager.getDefaultSharedPreferences(this)
				.getString("player_symbol", "X");
		String aiName;
		if (humanName.equals("O"))
			aiName = "X";
		else if (humanName.equals(""))
			aiName = "";
		else
			aiName = "O";
		human = new Player(humanName, humanColor);
		artInt = new ArtificialIntelligence(aiName, aiColor);
		for (Button b : slots) {
			b.setText("");
			b.setClickable(true);
			b.setBackgroundColor(Color.LTGRAY);
		}
		aiBegins.setClickable(true);
		findViewById(R.id.message_frame).setVisibility(View.GONE);
	}

	/**
	 * Handles the player's move
	 * 
	 * @param b
	 *            Pressed button
	 */
	private void handleMove(Button b) {
		b.setClickable(false);
		b.setText("X");
		if (human.isVictory(b)) {
			endGame();
			displayVictory(true);
		} else {
			human.move(b);
			if (!isStalemate()) {
				artInt.play(slots, human);
				if (artInt.wins) {
					endGame();
					displayVictory(false);
				}
			}
		}
	}

	/**
	 * Ends the game by preventing any further move
	 */
	private void endGame() {
		for (Button b : slots) {
			b.setClickable(false);
		}
	}

	/**
	 * Displays message of victory or defeat
	 */
	private void displayVictory(boolean isVictory) {
		findViewById(R.id.message_frame).setBackgroundColor(Color.LTGRAY);
		if (isVictory) {
			findViewById(R.id.message_frame).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.message)).setText("Victory !");
			((Button) findViewById(R.id.message))
					.setBackgroundColor(human.color);
			((Button) findViewById(R.id.message)).setTextColor(Color.DKGRAY);
		} else {
			findViewById(R.id.message_frame).setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.message)).setText("You lose !");
			((Button) findViewById(R.id.message))
					.setBackgroundColor(artInt.color);
			((Button) findViewById(R.id.message)).setTextColor(Color.DKGRAY);
		}
		findViewById(R.id.board).setVisibility(View.INVISIBLE);
	}
}
