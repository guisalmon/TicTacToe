package org.rob.tic_257779;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class MainGame extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        create_board();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_game, menu);
        return true;
    }
    
    private void create_board(){
    	int count=0;
    	List<Button> slots = new ArrayList<Button>();
    	TableLayout table = (TableLayout)findViewById(R.id.board);
    	TableLayout.LayoutParams params = new TableLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, (float) 1.0);
    	TableRow.LayoutParams rowParams = new TableRow.LayoutParams(0, LayoutParams.MATCH_PARENT, (float) 1.0);
    	
    	for (int p=0; p<3; p++){
    		TableRow row = new TableRow(this);
    		row.setLayoutParams(params);
	    	for (int i=0; i<3; i++){
	    		Button button;
	    		button = new Button(this);
	    		button.setTextColor(Color.GRAY);
	    		button.setLayoutParams(rowParams);
	    		button.setTag(count);
	    		row.addView(button);
	    		slots.add(button);
	    		Toast.makeText(this, button.toString(), Toast.LENGTH_SHORT).show();
	    		count++;
	    	}
	    	table.addView(row);
    	}
    	for(Button b: slots){
    		b.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
    	}
    }
    
}
