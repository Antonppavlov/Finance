package ru.barmaglot.android.myfinance.exception;


public class AmountException extends Exception {

    public AmountException() {
        super();
    }

    public AmountException(String s) {
        super(s);
    }

    public AmountException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AmountException(Throwable throwable) {
        super(throwable);
    }
}
