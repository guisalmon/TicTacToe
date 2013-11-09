package org.rob.tic_257779.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class PreferencesActivity extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PreferencesFragment()).commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
