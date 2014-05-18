package com.example.buschecker;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.example.buschecker.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class ListActivity extends BusLoaderActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		listBusLines();
	}

	public void listCheckinsOnClick(View view) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String result = requestGetCheckins();
				runOnUiThread(
					new Runnable() {
						@Override
						public void run() {
							loadCheckins(getJsonDataCheckins(result));
						}
					}
				);
			}		
		};
		thread.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}
		
	public String requestGetCheckins() {
		String url = "";
		Spinner busList = (Spinner) findViewById(R.id.spinner_bus_lines);
		String line = busList.getSelectedItem().toString();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();

	    if(!Util.isBlank(line)) {
	    	if(!url.endsWith("?")) {
		        url += "?";
	    	}
	        params.add(new BasicNameValuePair("line", line));
	    }
	    String paramString = URLEncodedUtils.format(params, "utf-8");
	    url += paramString;
	    
		HttpClient client = new DefaultHttpClient();
		HttpContext context = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(Util.SERVER_URL + Util.GET_DATA_SCRIPT + url);
		
		String result = null;
		try {
			HttpResponse response = client.execute(httpGet, context);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			Log.e("LogRequestGetCheckins", "Error at requestGetCheckins: " + e.toString());
		}
		return result;
	}
	
	public void loadCheckins(ArrayList<String> data) {
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data);
		ListView dataList = (ListView) findViewById(R.id.listView1);
		dataList.setAdapter(adapter);
	}
	
	public ArrayList<String> getJsonDataCheckins(String response) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONArray json = new JSONArray(response);
			String text = "";
			for (int i = 0; i < json.length(); i++) {
				
			    //DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			    //Date date = new Date(format.parse(json.getJSONObject(i).getString("timestamp")).getTime());  
				//String time = date.getDate() + "/" + date.getMonth() + " " 
			    //+ date.getHours() + ":" + date.getMinutes();
						
				text = json.getJSONObject(i).getString("line") + " - " +
						json.getJSONObject(i).getString("timestamp") + " - " +
						json.getJSONObject(i).getString("address") + " - " +
						json.getJSONObject(i).getString("direction");
				list.add(text);
			}
		} catch (Exception e) {
			Log.e("LogGetJsonDataCheckins", "Error at getJsonDataCheckins: " + e.toString());
		}
		return list;
	}
	
	public void homeOnClick(View view) {
		goToHome();
		//startActivity(new Intent(this, MainActivity.class));
	}

	@Override
	public void onBackPressed() {
		goToHome();
	}
	
	private void goToHome() {
		Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	}
	
}