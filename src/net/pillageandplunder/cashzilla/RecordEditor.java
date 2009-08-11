package net.pillageandplunder.cashzilla;

import net.pillageandplunder.cashzilla.Cashzilla.Records;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RecordEditor extends Activity {
    private static final String TAG = "Records";
    
    /**
     * Standard projection for the interesting columns of a normal record.
     */
    private static final String[] PROJECTION = new String[] {
            Records._ID, // 0
            Records.DESCRIPTION, // 1
            Records.CATEGORY, // 2
            Records.AMOUNT // 3
    };

    // The different distinct states the activity can be run in.
    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;
    private Uri mUri;
    private Cursor mCursor;
    private EditText mDescription, mCategory, mAmount;
    private Button mSubmit, mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
        	mState = STATE_EDIT;
        } else if (Intent.ACTION_INSERT.equals(action)) {
        	mState = STATE_INSERT;
        } else {
            // Whoops, unknown action!  Bail.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        } 
        
        // Set the layout for this activity.  You can find it in res/layout/record_editor.xml
        setContentView(R.layout.record_editor);

        mDescription = (EditText) findViewById(R.id.description);
        mCategory = (EditText) findViewById(R.id.category);
        mAmount = (EditText) findViewById(R.id.amount);
        
        mSubmit = (Button) findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch(mState) {
                case STATE_INSERT:
                	ContentValues data = new ContentValues();
                	data.put(Records.DESCRIPTION, mDescription.getText().toString());
                	data.put(Records.CATEGORY, mCategory.getText().toString());
                	
                	double amount = Math.floor(Double.parseDouble(mAmount.getText().toString()) * 100);
                	data.put(Records.AMOUNT, (new Double(amount)).intValue());
                	
                	getContentResolver().insert(intent.getData(), data);
                	setResult(RESULT_OK, (new Intent()).setAction(Cashzilla.Records.CONTENT_URI.toString()));
                	finish();
                	break;
                case STATE_EDIT:
                	break;
                }
            }
        });
        
        mCancel = (Button) findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	setResult(RESULT_CANCELED);
            }
        });

        // Do some setup based on the action being performed.
        switch(mState) {
        case STATE_EDIT:
            // Requested to edit: set that state, and the data being edited.
            mState = STATE_EDIT;
            mUri = intent.getData();
            mCursor = managedQuery(mUri, PROJECTION, null, null, null);
            mCursor.moveToFirst();
            
            mDescription.setText(mCursor.getString(1));
            mCategory.setText(mCursor.getString(2));
            break;
        case STATE_INSERT:
        	break;
        }
    }
}
