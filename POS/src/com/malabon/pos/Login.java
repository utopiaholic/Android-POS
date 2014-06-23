package com.malabon.pos;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.malabon.function.UserFunc;
import com.malabon.object.Sync;

public class Login extends Activity {

	EditText txtUsername, txtPassword;
	String currentUsername;
	Boolean logout = false;
	Button cancelbutton;

	// requestType:
	// 1 = fresh log-in
	// 2 = switch
	// 3 = lock
	// 4 = log-out
	// 5 = validate admin
	int requestType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		Initialize();
	}

	private void Initialize() {
		cancelbutton = (Button) findViewById(R.id.txtCancelAuth);
		cancelbutton.setVisibility(View.INVISIBLE);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		SharedPreferences prefs = this.getSharedPreferences("com.malabon.pos",
				Context.MODE_PRIVATE);
		currentUsername = prefs.getString("CurrentUser", null);
		requestType = prefs.getInt("RequestType", 1);

		txtUsername.setText(currentUsername);
		txtUsername.setEnabled(requestType != 3);
		txtUsername.requestFocus();

		switch (requestType) {
		case 3:
			txtPassword.requestFocus();
			break;
		case 4:
			logout = true; // face capture on log-out
			dispatchTakePictureIntent();
			break;
		case 5:
			cancelbutton.setVisibility(View.VISIBLE);
			txtUsername.setText(null);
			break;
		default:
			break;
		}
	}

	public void validateLogin(View view) {
		String user_name = txtUsername.getText().toString();
		String user_password = txtPassword.getText().toString();

		if (user_name.length() > 0 && user_password.length() > 0) {
			switch (requestType) {
			case 1:
				if (new UserFunc().isValidUser(this, user_name, user_password)) {
					logout = false;
					dispatchTakePictureIntent();
				} else
					showToast("Incorrect username and/or password");
				break;
			case 3:
				if (new UserFunc().isValidUser(this, user_name, user_password)) {
					loginSuccess();
				} else
					showToast("Incorrect username and/or password");
				break;
			case 5:
				validateAdmin(user_name, user_password);
				break;
			default:
				break;
			}
		} else
			showToast("Complete all required fields.");
	}

	private void validateAdmin(String user_name, String user_password) {
		if (new UserFunc().isAdmin(this, user_name, user_password)) {
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		} else
			showToast("Incorrect username and/or password");
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	private void loginSuccess() {
		try {
			switch (requestType) {
			case 1:
				saveTime(true, Sync.CurrentUserBitmap);
				break;
			case 3:
				break;
			default:
				break;
			}
		} catch (Exception e) {
			Log.e("pos_error", "loginSuccess: " + e);
		}
		String user_name = txtUsername.getText().toString();
		Intent resultIntent = new Intent();
		SharedPreferences prefs = this.getSharedPreferences("com.malabon.pos",
				Context.MODE_PRIVATE);
		prefs.edit().putString("CurrentUser", user_name).commit();
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	// -----FACE CAPTURE-----//

	static final int REQUEST_TAKE_PHOTO = 1;
	String mCurrentPhotoPath;

	@SuppressLint("SimpleDateFormat")
	private void saveImageFile(Bitmap bmp) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "LoginUserMug_" + timeStamp + "_.jpg";

		FileOutputStream out = openFileOutput(imageFileName,
				Context.MODE_PRIVATE);
		bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		out.flush();
		out.close();

		mCurrentPhotoPath = getFileStreamPath(imageFileName).getAbsolutePath();
	}

	private void saveTime(boolean is_timein, Bitmap bmp) throws IOException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
			byte[] img = bos.toByteArray();

			new UserFunc().addTime(this, is_timein, Sync.user.user_id, img);
		} catch (Exception e) {
			Log.e("pos_error", "saveTime: " + e);
		}
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	}

	public void close(View v) {
		if (requestType == 5) {
			Intent resultIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, resultIntent);
			finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Sync.CurrentUserBitmap = (Bitmap) extras.get("data");
			try {
				saveImageFile(Sync.CurrentUserBitmap);
				if (logout) {
					saveTime(false, Sync.CurrentUserBitmap);
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					loginSuccess();
				}

			} catch (Exception e) {
				Log.e("pos_error", "onActivityResult Login: " + e);
			}
		}
	}

	@Override
	public void onBackPressed() {
		showToast("Please log-in.");
	}
}
