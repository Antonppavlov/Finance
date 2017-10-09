package ru.barmaglot.android.myfinance.objects.impl.operation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


// расход
public class OutcomeOperation extends AbstractOperation {

    public OutcomeOperation() {
        super(OperationType.OUTCOME);
    }

    private IStorage fromStorage; // откуда потратили
    private ISource toSource; // на что потратили
    private BigDecimal fromAmount; // сумму, которую потратили
    private Currency fromCurrency; // в какой валюте потратили



    public OutcomeOperation(Calendar dateTime,
                            OperationType operationType,
                            String description,
                            IStorage fromStorage,
                            Currency fromCurrency,
                            BigDecimal fromAmount,
                            ISource toSource) {

        super(dateTime, operationType, description);
        this.fromStorage = fromStorage;
        this.toSource = toSource;
        this.fromAmount = fromAmount;
        this.fromCurrency = fromCurrency;
    }

    public IStorage getFromStorage() {
        return fromStorage;
    }

    public void setFromStorage(IStorage fromStorage) {
        this.fromStorage = fromStorage;
    }

    public ISource getToSource() {
        return toSource;
    }

    public void setToSource(ISource toSource) {
        this.toSource = toSource;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }
}
