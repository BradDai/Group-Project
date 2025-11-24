package data_access;

import use_case.get_price.PriceGateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TwelveDataPriceGateway implements PriceGateway {

    private final String apiKey;

    public TwelveDataPriceGateway(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public double getPrice(String symbol) throws IOException {
        String encoded = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
        String urlString = "https://api.twelvedata.com/price?symbol="
                + encoded + "&apikey=" + apiKey;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        connection.disconnect();
        String body = response.toString();
        int priceIndex = body.indexOf("\"price\"");
        if (priceIndex == -1) {
            throw new IOException("Price not found: " + body);
        }
        int colon = body.indexOf(':', priceIndex);
        int q1 = body.indexOf('"', colon + 1);
        int q2 = body.indexOf('"', q1 + 1);
        String priceStr = body.substring(q1 + 1, q2);
        return Double.parseDouble(priceStr);
    }
}
