package ru.barmaglot.android.myfinance.objects.impl.operation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


// конвертация - перевод из одного хранилища в другое в разной валюте
public class ConvertOperation extends AbstractOperation {

    public ConvertOperation() {
        super(OperationType.CONVERT);
    }

    private IStorage fromIStorage; // откуда конвертируем
    private IStorage toIStorage; // куда конвертируем
    private Currency fromCurrency;// в какой валюте оправили деньги
    private Currency toCurrency; // в какой валюте получили деньги
    private BigDecimal fromAmount; // сумма отправки в первой валюте
    private BigDecimal toAmount; // сумма получения во второй валюте

    public ConvertOperation(Calendar dateTime,
                            OperationType operationType,
                            String description,

                            IStorage fromIStorage,
                            Currency fromCurrency,
                            BigDecimal fromAmount,
                            IStorage toIStorage,
                            Currency toCurrency,
                            BigDecimal toAmount) {

        super(dateTime, operationType, description);
        this.fromIStorage = fromIStorage;
        this.toIStorage = toIStorage;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    public IStorage getFromStorage() {
        return fromIStorage;
    }

    public void setFromStorage(IStorage fromIStorage) {
        this.fromIStorage = fromIStorage;
    }

    public IStorage getToStorage() {
        return toIStorage;
    }

    public void setToStorage(IStorage toIStorage) {
        this.toIStorage = toIStorage;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }
}
