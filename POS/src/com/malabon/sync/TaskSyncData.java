package com.malabon.sync;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import com.malabon.database.HistorySyncDB;
import com.malabon.object.Sync;
import com.malabon.object.SyncHistory;
import com.malabon.pos.MainActivity;

public class TaskSyncData extends AsyncTask<String, String, String> {

	private ProgressDialog pDialog;
	private MainActivity activity;
	private Context context;
	private boolean ismanual;
//	private boolean isnewsession;
	private int type;
	
	JSONParser jsonParser = new JSONParser();

	List<String> listTablePosToServer;
	List<String> listTableServerToPos;
	List<String> listTableBoth;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";

//	public TaskSyncData(MainActivity activity, boolean isnewsession) {
//		this.activity = activity;
//		context = activity;
//		this.isnewsession = isnewsession;
//		if (this.isnewsession)
//			this.ismanual = false;
//		else
//			this.ismanual = Sync.posSettings.is_manual;
//		pDialog = new ProgressDialog(context);
//	}
	
	public TaskSyncData(MainActivity activity, boolean showProgress, int type) {
		this.activity = activity;
		context = activity;
//		this.isnewsession = isnewsession;
		this.ismanual = showProgress;
		this.type = type;
		pDialog = new ProgressDialog(context);
	}

	//Before starting background thread Show Progress Dialog if IsManual
	@Override
	protected void onPreExecute() {
		super.onPreExecute(); 
		if (ismanual) {
			pDialog.setTitle("Ongoing Sync");
			pDialog.setMessage("Please do not turn off or unplug this device.\nMaintain a good internet connection.");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		} else if(!ismanual && type == MainActivity.DATA_PUSH_PULL){
			//disable switch button
			activity.setCloseEnabled(false);
		}
		
		
	}

	protected String doInBackground(String... args) {
		//if (!isnewsession)
		if(type == MainActivity.DATA_PUSH_PULL) {
			PushData();
		}
		
		PullData();
		return null;
	}

	protected void onPostExecute(String file_url) {
 		if (pDialog.isShowing()) {
			pDialog.dismiss();
		}
		 
		if(type != MainActivity.DATA_PULL_USER) {
			if(type == MainActivity.DATA_PUSH_PULL) {
				String time = DateFormat.format("yyyy-MM-dd hh:mm:ss", 
						new java.util.Date()).toString();
				
				SyncHistory history = new SyncHistory();
				history.IsManual = ismanual;
				history.StrSyncDate = time;
				history.UserId = activity.currentUser.user_id;
				
				HistorySyncDB historySyncDB = new HistorySyncDB(context);
				historySyncDB.open();
				historySyncDB.addHistorySync(history);
				
				//enable switch button
				activity.setCloseEnabled(true);
			}
			activity.refreshUI();
		}
 	}
 
	private void PushData() {
		listTablePosToServer = new ArrayList<String>();
		listTablePosToServer = TableToSync.GetTablePosToServer();

		for (String table : listTablePosToServer) {
			JSONObject jObj = new JSONObject();
			jObj = Data.GetDataForPush(this.context, table);
 
			if (jObj != null) {
				try {
					URL url = new URL(Data.GetUrlForPush(table));

					String data = jObj.toString();

					JSONObject json = jsonParser.PushData(url, data);
 
					try {
						int success = json.getInt(TAG_SUCCESS);

						if (success == 1) {
							Log.d("pos_sync", json.toString());
							Data.UpdateIsSynced(this.context, table);
						} else {
							Log.e("pos_error", json.getString(TAG_MESSAGE));
						}
					} catch (JSONException e) {
						Log.e("pos_error", "PushData: " + e.toString());
						continue;
					}

				} catch (Exception ex) {
					Log.e("pos_error", "PushData: " + ex.toString());
					continue;
				}
			}
		}
	}

	private void PullData() {
		listTableServerToPos = new ArrayList<String>();
		
		if(type == MainActivity.DATA_PULL_USER) {
			listTableServerToPos.add("user");
		} else {
			listTableServerToPos = TableToSync.GetTableServerToPos();
		}

		for (String table : listTableServerToPos) {
			try {
				JSONArray jArray = null;

				List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);
				nvp = Data.GetDataForPull(context, table);

				JSONObject json = jsonParser.PullData(
						Data.GetUrlForPull(table), nvp);
				Log.d("pos_sync", json.toString());

				try {
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						jArray = json.getJSONArray(TAG_MESSAGE);
						Data.AddUpdatePosData(context, jArray, table);
					} else {
						Log.e("pos_error", json.getString(TAG_MESSAGE));
					}
				} catch (JSONException e) {
					Log.e("pos_error", "PullData: " + e.toString());
					continue;
				}
			} catch (Throwable t) {
				Log.e("pos_error", "PullData: " + t.toString());
				continue;
			} 
		}
	}
}