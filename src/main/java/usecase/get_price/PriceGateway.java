package usecase.get_price;

import java.io.IOException;

public interface PriceGateway {
    /**
     * L.
     * @param symbol .
     * @return .
     * @throws IOException .
     */
    double getPrice(String symbol) throws IOException;
}
