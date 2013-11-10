package org.rob.tic_257779.classes;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

public class Player {
	/**
	 * This class gives a position on the grid
	 * @author Guillaume Salmon
	 *
	 */
	protected class Position {
		public int x;
		public int y;
		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public Position(Button b){
			this.x = b.getId()%3;
			this.y = (b.getId()-(b.getId()%3))/3;
		}
		
		public int getId(){
			return 3*y+x;
		}
		
		public boolean isCorner() {
			return (x==0 || x==2)&&(y==0 || y==2);
		}
	}
	
	private List<Position> prevMoves;
	private String name;
	
	public Player(String name) {
		this.prevMoves = new ArrayList<Position>();
		this.name = name;
	}
	
	private Player(List<Position> l){
		this.prevMoves = l;
	}
	
	public boolean hasMoved() {
		return !prevMoves.isEmpty();
	}
	
	public boolean hasButton(Button b){
		for(Position p : prevMoves){
			if(b.getId()==p.getId()){
				return true;
			}
		}
		return false;
	}
	
	/**
     * Indicates whether a move allows victory or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return true if the position p allows victory
     */
    public boolean isVictory(Button b){
    	Position p = new Position(b);
    	int line = 1;
    	int col = 1;
    	int diag1 = 1;
    	int diag2 = 1;
    	for (Position pos : prevMoves){
    		if (pos.x == p.x) col++;
    		if (pos.y == p.y) line++;
    		if ((p.x == p.y) && (pos.x == pos.y)) diag1++;
    		if ((p.x+p.y == 2) && (pos.x+pos.y == 2)) diag2++;
    	}
    	return ((line == 3)||(col == 3)||(diag1 == 3)||(diag2 == 3));
    }
    
    public void move(Button b){
    	b.setClickable(false);
		b.setText(name);
    	prevMoves.add(new Position(b));
    }
    
    /**
     * Indicates whether a move creates two victory opportunities or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return True if this position creates two victory opportunities
     */
	public boolean isFork(Button newMove, List<Button> slots) {
		List<Position> hypMoves = new ArrayList<Position>(prevMoves);
		Position p = new Position(newMove);
		hypMoves.add(p);
		Player hypPlayer = new Player(hypMoves);
		
		Position p1 = null;
		int branch = 0;
		for (Button b : slots){
			p1 = new Position(b);
			if (b.isClickable() && hypPlayer.isVictory(b) && ((p.x!=p1.x)||(p1.y!=p.y))){
				branch++;
				break;
			}
		}
		for (Button b : slots){
			Position p2 = new Position(b);
			if (b.isClickable() && hypPlayer.isVictory(b) && p1!=null && ((p2.x!=p1.x)||(p1.y!=p2.y)) && ((p.x!=p2.x)||(p2.y!=p.y))){
				branch++;
				break;
			}
		}
		return branch >= 2;
	}
	
	/**
     * Indicates whether a move creates a victory opportunity or not regarding the previous positions owned by the player
     * @param p New position
     * @param l Previous positions owned by the player
     * @return True if this position creates a victory opportunity
     */
	public boolean isTwo(Button newMove, List<Button> slots) {
		List<Position> hypMoves = new ArrayList<Position>(prevMoves);
		Position p = new Position(newMove); 
		hypMoves.add(p);
		Player hypPlayer = new Player(hypMoves);
		for (Button b : slots){
			Position p1 = new Position(b);
			if (b.isClickable() && hypPlayer.isVictory(b) && ((p.x!=p1.x)||(p1.y!=p.y))){
				return true;
			}
		}
		return false;
	}

}
