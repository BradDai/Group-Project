package dataaccess;

public final class Constants {

    private Constants() {

    }

    // View
    public static final String VIEW_NAME = "transfer";

    // Property names
    public static final String STATE_PROPERTY = "state";
    public static final String ERROR_PROPERTY = "error";

    // Transfer types
    public static final String TRANSFER_STOCK = "Stock";
    public static final String TRANSFER_CURRENCY = "Currency";

    // Default currency
    public static final String DEFAULT_CURRENCY = "USD";

    // Labels
    public static final String LABEL_STOCK_PANEL = "Stock Transfer Details";
    public static final String LABEL_CURRENCY_PANEL = "Currency Transfer Details";
    public static final String LABEL_SYMBOL = "Symbol:";
    public static final String LABEL_QUANTITY = "Quantity:";
    public static final String LABEL_CURRENCY = "Currency:";
    public static final String LABEL_AMOUNT = "Amount:";
    public static final String LABEL_SENDER_BALANCE = "Sender Balance: -";
    public static final String LABEL_RECEIVER_BALANCE = "Receiver Balance: -";
    public static final String LABEL_SENDER = "Sender ";
    public static final String LABEL_RECEIVER = "Receiver ";
    public static final String LABEL_BALANCE = "Balance";
    public static final String LABEL_QUANTITY_ONLY = "Quantity";

    // Buttons
    public static final String BUTTON_CONFIRM = "Confirm Transfer";
    public static final String BUTTON_CANCEL = "Cancel";

    // Error messages
    public static final String ERROR_INVALID_AMOUNT = "Invalid currency amount";
    public static final String ERROR_AMOUNT_POSITIVE = "Amount must be positive";
    public static final String ERROR_AMOUNT_FORMAT = "Invalid amount format";

    // Magic numbers – layout & component sizes
    public static final int SPINNER_MIN = 1;
    public static final int SPINNER_MAX = 1000;
    public static final int SPINNER_STEP = 1;
    public static final int TEXTFIELD_COLUMNS = 15;

    public static final int VERTICAL_SPACING_SMALL = 3;
    public static final int RIGID_AREA_SMALL = 10;
    public static final int RIGID_AREA_MEDIUM = 20;

    // Indices for dropdown initialization
    public static final int INDEX_FIRST = 0;
    public static final int INDEX_SECOND = 1;
}
