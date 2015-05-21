package com.example.jsonresttest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

	EditText etResponse;
	TextView tvIsConnected;

	static long elapsedMilliSeconds = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get reference to the views
		etResponse = (EditText) findViewById(R.id.etResponse);
		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

		// check if you are connected or not
		if (isConnected()) {
			tvIsConnected.setText("Internet enabled");
		} else {
			tvIsConnected.setText("Internet disabled");
		}

		// call AsynTask to perform network operation on separate thread
		new HttpAsyncTask()
				.execute("http://www.filltext.com/?rows=10&id=%7Bindex%7D&email=email%7D&username=%7Busername%7D&password=%7BrandomString%7C5%7D&pretty=true");
	}

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {

			elapsedMilliSeconds = System.currentTimeMillis();
			
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Failed to get response.";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return GET(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			
			elapsedMilliSeconds = System.currentTimeMillis() - elapsedMilliSeconds;
			Toast.makeText(getBaseContext(),
					"Responded in " + elapsedMilliSeconds + " ms",
					Toast.LENGTH_LONG).show();
			
			try {
				JSONArray results = new JSONArray(result);

				String str = "";

				for (int i = 0; i < results.length(); i++) {
					str += "\n--------\n";
					str += "id: " + results.getJSONObject(i).getString("id");
					str += "\n--------\n";
					str += "username: "
							+ results.getJSONObject(i).getString("username");
					str += "\n--------\n";
					str += "password: "
							+ results.getJSONObject(i).getString("password");
					str += "\n--------\n";
					str += "email: "
							+ results.getJSONObject(i).getString("email");
					str += "\n--------\n";
				}
				etResponse.setText(str);
				// etResponse.setText(json.toString(1));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
