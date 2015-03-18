package niclabs.lolhowdoibgn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	public ProgressBar pBar;

	public class ProgressReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(BGNEncryption.start)){
				Log.d("STEP", "Start!");
				int prog = intent.getIntExtra("Progress", 0);
				pBar.setProgress(prog);
			}
			else if (intent.getAction().equals(BGNEncryption.end)){
				Log.d("STEP", "Finish!");
				pBar.setProgress(100);
				Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
				Bundle b = intent.getExtras();
				Intent runtent = new Intent(MainActivity.this, Response.class);
				runtent.putExtras(b);
				startActivity(runtent);
			}
			else{
				int prog = intent.getIntExtra("Progress", 50);
				pBar.setProgress(prog);
			}
		}
	}

	public ProgressReceiver rcv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BGNEncryption.start);
		filter.addAction(BGNEncryption.end);
		rcv = new ProgressReceiver();
		registerReceiver(rcv, filter);
		
		final EditText txtMsg = (EditText)findViewById(R.id.TxtMessage);
		final EditText txtBits = (EditText)findViewById(R.id.TxtBits);
		final Button btnSend = (Button)findViewById(R.id.BtnSend);
		pBar = (ProgressBar)findViewById(R.id.pBar);
		
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				int nbits = 32;
				Intent intent = new Intent(MainActivity.this, BGNEncryption.class);
				Bundle b = new Bundle();
				b.putString("message", txtMsg.getText().toString());
				if (txtBits.length()>0){
					nbits = Integer.valueOf(txtBits.getText().toString());
				}
				b.putInt("bits", nbits);
				intent.putExtras(b);
				startService(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void finish(){
		unregisterReceiver(rcv);
		super.finish();
	}
}
