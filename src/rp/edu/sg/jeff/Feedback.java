package rp.edu.sg.jeff;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class Feedback extends Activity {
	String location;

	Button submit;
	Button clear;
	Button cancel;

	EditText editEmail;
	EditText editTelephone;
	EditText editFeedback;

	RatingBar ratings;

	TextView textTitle;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.feedback);
		
		
		textTitle = (TextView)findViewById(R.id.textTitle);
		submit = (Button) findViewById(R.id.btnSubmit);
		cancel = (Button) findViewById(R.id.btnCancel);
		clear = (Button) findViewById(R.id.btnClear);
		editEmail = (EditText) findViewById(R.id.editEmail);
		editTelephone = (EditText) findViewById(R.id.editTelephone);
		editFeedback = (EditText) findViewById(R.id.editFeedback);
		ratings = (RatingBar) findViewById(R.id.rating);
		
		location = getIntent().getStringExtra("location");
		textTitle.setText(location);
		/**
		 *  attach text change listener to all of the text views
		 *  disable the button as soon as either one of the text views is empty
		 */ 
		editEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				if((editTelephone.getText().toString().equals("")) || (editFeedback.getText().toString().equals("")) || (editEmail.getText().toString().equals(""))) {
					submit.setEnabled(false);
				} else {
					submit.setEnabled(true);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
		
		editTelephone.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if((editTelephone.getText().toString().equals("")) || (editFeedback.getText().toString().equals("")) || (editEmail.getText().toString().equals(""))) {
					submit.setEnabled(false);
				} else {
					submit.setEnabled(true);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
		
		editFeedback.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if((editTelephone.getText().toString().equals("")) || (editFeedback.getText().toString().equals("")) || (editEmail.getText().toString().equals(""))) {
					submit.setEnabled(false);
				} else {
					submit.setEnabled(true);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

		submit.setEnabled(false);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = editEmail.getText().toString();
				String telephone = editTelephone.getText().toString();
				String feedback = editFeedback.getText().toString();
				double ratingValue= ratings.getRating();
				Intent sender = new Intent(android.content.Intent.ACTION_SEND);
				sender.setType("plain/text");
				//Use default device email as Implicit Intent
				sender.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"yemyat@gmail.com"});
				sender.putExtra(android.content.Intent.EXTRA_SUBJECT,"Feedback");
				sender.putExtra(android.content.Intent.EXTRA_TEXT,email+"("+telephone+")'s response to "+location+"\n"+" ' "+feedback+" ' "+"\n"+"Rated this location as "+ratingValue);
				startActivity(Intent.createChooser(sender, "Send email"));

			}
		});

		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editEmail.setText("");
				editTelephone.setText("");
				editFeedback.setText("");
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}	
		});	
	}
	public void showAlertBox(String myMessage) {
		AlertDialog.Builder myAlertBoxBuilder =new AlertDialog.Builder(this);
		myAlertBoxBuilder.setMessage(myMessage).setCancelable(true);
		myAlertBoxBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alertBox = myAlertBoxBuilder.create();
		alertBox.show();
	}
}
