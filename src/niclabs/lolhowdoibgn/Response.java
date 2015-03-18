package niclabs.lolhowdoibgn;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Response extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);
		final TextView output = (TextView)findViewById(R.id.Output);
		/*final Button btnSave = (Button)findViewById(R.id.BtnSave);*/
		final Bundle bundle = this.getIntent().getExtras();
		output.setText(bundle.getString("message"));
		BGNTable usdbh = new BGNTable(this, "Keys", null, 1);
		final SQLiteDatabase db = usdbh.getWritableDatabase();
		
		/*btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				if (db!=null){
					String user = USER_SERVICE.toString();
					String a = bundle.getString("secret");
					db.execSQL("INSERT INTO Keys (secretKey, id) VALUES ('"+a+"', '"+user+"')");
				}
				db.close();
			}
		});	*/		
	}
}
