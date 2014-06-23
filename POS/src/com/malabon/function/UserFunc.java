package com.malabon.function;

import android.content.Context;

import com.malabon.database.LogUserTimeSheetDB;
import com.malabon.database.UserDB;

public class UserFunc {
	public boolean isValidUser(Context context, String user_name,
			String user_password) {
		Boolean isvalid = false;

		UserDB userDB = new UserDB(context);
		userDB.open();
		isvalid = userDB.validateLogin(user_name, user_password);

		return isvalid;
	}

	public void addTime(Context context, boolean is_timein, int user_id,
			byte[] timein_image) {
		LogUserTimeSheetDB logUserTimeSheetDB = new LogUserTimeSheetDB(context);
		logUserTimeSheetDB.open();

		if (is_timein)
			logUserTimeSheetDB.addTimein(user_id, timein_image); // TODO: image
		else
			logUserTimeSheetDB.addTimeout(user_id, timein_image); // TODO: image
	}

	public boolean isAdmin(Context context, String user_name,
			String user_password) {
		Boolean isvalid = false;

		UserDB userDB = new UserDB(context);
		userDB.open();
		isvalid = userDB.validateAdmin(user_name, user_password);

		return isvalid;
	}
}
