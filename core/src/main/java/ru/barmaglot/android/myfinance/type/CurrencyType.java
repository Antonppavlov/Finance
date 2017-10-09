package ru.barmaglot.android.myfinance.type;

/**
 * Created by ap_pavlov on 11.01.2017.
 */

public enum  CurrencyType {
    RUB("RUB"),
    USD("USD"),
    EUR("EUR");

    private final String code;

    CurrencyType(String code) {
        this.code=code;
    }

    public String getCode() {
        return code;
    }
}
