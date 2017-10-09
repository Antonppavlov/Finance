package ru.barmaglot.android.myfinance.exception;


public class CurrencyException extends Exception {

    public CurrencyException() {
        super();
    }

    public CurrencyException(String s) {
        super(s);
    }

    public CurrencyException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CurrencyException(Throwable throwable) {
        super(throwable);
    }

}
