package usecase.get_price;

import java.io.IOException;

public interface PriceGateway {
    double getPrice(String symbol) throws IOException;
}
