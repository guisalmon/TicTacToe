package org.rob.tic_257779.classes;

import java.util.List;

import android.widget.Button;

/**
 * This class handles the behavior of the artificial intelligence of the game
 * 
 * @author Guillaume Salmon
 * 
 */
public class ArtificialIntelligence extends Player {

	public boolean wins;

	public ArtificialIntelligence(String name, int color) {
		super(name, color);
		wins = false;
	}

	/**
	 * Makes the AI choose the best move to make if it's possible Based on this
	 * strategy : http://en.wikipedia.org/wiki/Tic-tac-toe#Strategy (but with
	 * variations)
	 */
	public void play(List<Button> slots, Player opponent) {

		// If a move allows victory, do this move
		for (Button b : slots) {
			if (b.isClickable() && isVictory(b)) {
				move(b);
				wins = true;
				return;
			}
		}

		// Case where ai hasn't played any move
		if (!hasMoved()) {
			// If the AI is the first to play, choose the upper left corner to
			// play
			if (!opponent.hasMoved()) {
				Button b = slots.get(0);
				move(b);
				return;
			} else {
				// Otherwise choose the best move to make regarding the player
				// first move
				for (Button b : slots) {
					if (b.isClickable() == false) {
						Button bAi;
						switch (b.getId()) {
						// If the player payed in a corner, choose the opposite
						// corner
						case 0:
							bAi = slots.get(8);
							move(bAi);
							return;
						case 2:
							bAi = slots.get(6);
							move(bAi);
							return;
						case 6:
							bAi = slots.get(2);
							move(bAi);
							return;
						case 8:
							bAi = slots.get(0);
							move(bAi);
							return;
							// If the player played in the middle of the grid,
							// play in the upper left corner
						case 4:
							bAi = slots.get(0);
							move(bAi);
							return;
							// Otherwise, play in the middle
						default:
							bAi = slots.get(4);
							move(bAi);
							return;
						}
					}
				}
			}
		}

		// Block the victory opportunity of the player
		for (Button b : slots) {
			if (b.isClickable() && opponent.isVictory(b)) {
				move(b);
				return;
			}
		}

		// If possible, make two opportunities of victory
		for (Button b : slots) {
			if (b.isClickable() && isFork(b, slots)) {
				move(b);
				return;
			}
		}

		// Prevent the player of making two opportunities of victory
		for (Button b : slots) {
			if (b.isClickable() && opponent.isFork(b, slots)) {
				move(b);
				return;
			}
		}

		// Play in a corner to make a victory opportunity
		for (Button b : slots) {
			Position p = new Position(b);
			if (b.isClickable() && p.isCorner() && isTwo(b, slots)) {
				move(b);
				return;
			}
		}

		// Play in a corner
		for (Button b : slots) {
			Position p = new Position(b);
			if (b.isClickable() && p.isCorner()) {
				move(b);
				return;
			}
		}

		// Play in the first available position
		for (Button b : slots) {
			if (b.isClickable()) {
				move(b);
				return;
			}
		}
	}

}
