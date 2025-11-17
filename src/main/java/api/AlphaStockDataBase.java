package api;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * AlphaDataBase class.
 */

public class AlphaStockDataBase implements StockDataBase{

    // define constants
    private static final String API_URL = "https://www.alphavantage.co/query?";

    private static final String FUNCTION = "function";
    private static final String SYMBOL = "symbol";
    private static final String INTERVAL = "interval";

    private static String API_KEY;
    private static final String STATUS_CODE = "status_code";
    private static final int SUCCESS_CODE = 200;

    // load token from env variable.
    public static String getAPIKey() {
        return System.getenv("token");
    }

    @Override
    public void getStockPrices(String stockSymbol) throws JSONException {
        // Build the request to get the grade.
        final OkHttpClient client = new OkHttpClient().newBuilder().build();

        final Request request = new Request.Builder()
                .url(String.format("%sfunction=TIME_SERIES_INTRADAY&symbol=%s&interval=5min&apikey=%s",
                        API_URL, stockSymbol, API_KEY))
                //.addHeader(TOKEN, getAPIToken())
                //.addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        try {
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getInt(STATUS_CODE) == SUCCESS_CODE) {
                File stockFile = new File(stockSymbol + ".txt"); // make File object

                if (stockFile.createNewFile()) {           // attempt making the file
                    System.out.println("File created: " + stockFile.getName());
                    FileWriter stockWriter = new FileWriter(stockSymbol + ".txt");
                    stockWriter.write(responseBody.toString());
                    stockWriter.close();  // must close manually

                } else {
                    System.out.println("File already exists.");
                }

            }
        }
        catch (IOException | JSONException event) {
                throw new RuntimeException(event);
            }
    }
}
