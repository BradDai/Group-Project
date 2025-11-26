package dataaccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import usecase.get_price.PriceGateway;

public class TwelveDataPriceGateway implements PriceGateway {

    private final String apiKey;

    public TwelveDataPriceGateway(final String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public double getPrice(final String symbol) throws IOException {
        final String encoded = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
        final String urlString = "https://api.twelvedata.com/price?symbol="
            + encoded + "&apikey=" + apiKey;
        final URL url = new URL(urlString);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        final BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
        );
        final StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();
        connection.disconnect();
        final String body = response.toString();
        final int priceIndex = body.indexOf("\"price\"");
        if (priceIndex == -1) {
            throw new IOException("Price not found: " + body);
        }
        final int colon = body.indexOf(':', priceIndex);
        final int q1 = body.indexOf('"', colon + 1);
        final int q2 = body.indexOf('"', q1 + 1);
        final String priceStr = body.substring(q1 + 1, q2);
        return Double.parseDouble(priceStr);
    }
}
