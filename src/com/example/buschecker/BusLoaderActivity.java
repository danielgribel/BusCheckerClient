package com.example.buschecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BusLoaderActivity extends Activity {

	private Map<String, List<String>> busDirection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		busDirection = new HashMap<String, List<String>>();
	}

	public void listBusLines() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				final String result = requestGetBusLines();
				runOnUiThread(
					new Runnable() {
						@Override
						public void run() {
							loadBusLines(getJsonDataBusLines(result));
						}
					}
				);
			}		
		};
		thread.start();
	}
	
	public String requestGetBusLines() {
		HttpClient client = new DefaultHttpClient();
		HttpContext context = new BasicHttpContext();
		HttpGet httpGet = new HttpGet(Util.SERVER_URL + Util.GET_BUSLINES_SCRIPT);
		
		String result = null;
		try {
			HttpResponse response = client.execute(httpGet, context);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (Exception e) {
			Log.e("LogRequestGetBusLines", "Error at requestGetBusLines: " + e.toString());
		}
		return result;
	}
	
	public ArrayList<String> getJsonDataBusLines(String response) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			JSONArray json = new JSONArray(response);
			String text = "";
			String origin = "";
			String destiny = "";
			for (int i = 0; i < json.length(); i++) {
				text = json.getJSONObject(i).getString("idbus");
				origin = json.getJSONObject(i).getString("origin");
				destiny = json.getJSONObject(i).getString("destiny");
				list.add(text);
				List<String> directions = new ArrayList<String>();
				directions.add(origin);
				directions.add(destiny);
				busDirection.put(text, directions);
			}
		} catch (Exception e) {
			Log.e("LogGetJsonDataBusLines", "Error at getJsonDataBusLines: " + e.toString());
		}
		return list;
	}
	
	public void loadBusLines(List<String> data) {
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		Spinner dataList = (Spinner) findViewById(R.id.spinner_bus_lines);
		dataList.setAdapter(adapter);
	}
	
	public void loadDirections(List<String> data) {
		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
		Spinner dataList = (Spinner) findViewById(R.id.spinner_directions);
		dataList.setAdapter(adapter);
	}
	
	public Map<String, List<String>> getBusDirection() {
		return busDirection;
	}
	
}
