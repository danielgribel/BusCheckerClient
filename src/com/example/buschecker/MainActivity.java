package com.example.buschecker;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.example.buschecker.R;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends BusLoaderActivity implements LocationListener {

	private LocationManager locationManager;
	private String currentLocation = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listBusLines();
		
		// remover essas duas linhas se for testar no emulador
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 1, this);
	    
		getSpinnerBusLines().setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Spinner spinner = getSpinnerBusLines();
		    	//Log.e("spinner", (getBusDirection().get(spinner.getSelectedItem().toString())).toString());
		    	loadDirections(getBusDirection().get(spinner.getSelectedItem().toString()));
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // TODO
		    }
		});
	}

	private Spinner getSpinnerBusLines() {
		Spinner spinnerBusLines = (Spinner) findViewById(R.id.spinner_bus_lines);
		return spinnerBusLines;
	}
	
	private Spinner getSpinnerDirections() {
		Spinner spinnerDirections = (Spinner) findViewById(R.id.spinner_directions);
		return spinnerDirections;
	}
	
	public void searchOnClick(View view) {
		startActivity(new Intent(this, ListActivity.class));
	}
	
	public void getLocationOnClick(View view) {
		EditText locationText = (EditText) findViewById(R.id.et_location);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
	    Log.i("geo_location", location.toString());
	    String result = getAddress(latitude, longitude);
	    //position = result.replaceAll("[^a-zA-Z0-9]+","");
	    currentLocation = result;
	    locationText.setText(currentLocation);
	}

	public void sendOnClik(View view) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				//EditText busLine = (EditText) findViewById(R.id.et_line);
				Spinner busLine = getSpinnerBusLines();
				Spinner busDirection = getSpinnerDirections(); 
				//EditText busDirection = (EditText) findViewById(R.id.et_direction);
				try {
					final String res;
					//res = requestPutCheckin(busLine.getSelectedItem().toString(), busDirection.getText().toString());
					res = requestPutCheckin(busLine.getSelectedItem().toString(), busDirection.getSelectedItem().toString());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {
					Log.e("LogSendOnClink", "Error at sendOnClink: " + e.toString());
				}
			}
		};
		thread.start();
	}
	
	public String requestPutCheckin(String busLine, String busDirection) {
	    /*Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
	    Log.i("geo_location", location.toString());
	    String result = getAddress(latitude, longitude);
	    currentLocation = result;*/
	    if(!Util.isBlank(busLine) && !Util.isBlank(busDirection) && !Util.isBlank(currentLocation)) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(Util.SERVER_URL + Util.PUT_DATA_SCRIPT);
			HttpResponse response = null;
			Date date = new Date();
			Timestamp time = new Timestamp(date.getTime());
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>(3);
				params.add(new BasicNameValuePair("line", busLine));
				params.add(new BasicNameValuePair("timestamp", time.toString()));
				params.add(new BasicNameValuePair("address", currentLocation));
				params.add(new BasicNameValuePair("direction", busDirection.toUpperCase(Locale.getDefault())));
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				response = httpClient.execute(httpPost, localContext);
			} catch (Exception e) {
				Log.e("LogRequestPutCheckin", "Error at requestPutCheckin: " + e.toString());
			}
			return response.toString();
	    }
	    return null;
	}
	
	private String getAddress(double latitude, double longitude) {
		String result = "";
		try {
			Geocoder geo = new Geocoder(
					MainActivity.this.getApplicationContext(),
					Locale.getDefault());
			
			List<Address> addresses = geo.getFromLocation(latitude, longitude, 5);
			if (!addresses.isEmpty()) {
				if (addresses.size() > 0) {
					result = addresses.get(0).getAddressLine(0);
					/*result = addresses.get(0).getFeatureName() + ", "
							+ addresses.get(0).getSubThoroughfare() + ", "
							+ addresses.get(0).getLocality() + ", "
							+ addresses.get(0).getSubLocality() + ", "
							+ addresses.get(0).getAdminArea();*/
					
					for(int i = 0; i < addresses.size(); i++) {
						String r = "";
						r = addresses.get(i).getAddressLine(0);
						Log.i("Address " + i, r);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}
	
}
