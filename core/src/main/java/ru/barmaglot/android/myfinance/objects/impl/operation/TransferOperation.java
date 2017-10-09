package ru.barmaglot.android.myfinance.objects.impl.operation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.operation.IOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


public class TransferOperation extends AbstractOperation implements IOperation {


    public TransferOperation() {
        super(OperationType.TRANSFER);
    }

    private IStorage fromStorage;// откуда переводим
    private IStorage toStorage; // куда переводим
    private BigDecimal fromAmount;// сумма перевода
    private Currency fromCurrency;// в какой валюте получили деньги

    public TransferOperation(Calendar dateTime,
                             OperationType operationType,
                             String description,
                             IStorage fromStorage,
                             Currency fromCurrency,
                             BigDecimal fromAmount,
                             IStorage toStorage
                             ) {

        super(dateTime, operationType, description);
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.fromAmount = fromAmount;
        this.fromCurrency = fromCurrency;
    }

    public IStorage getFromStorage() {
        return fromStorage;
    }

    public void setFromStorage(IStorage fromStorage) {
        this.fromStorage = fromStorage;
    }

    public IStorage getToStorage() {
        return toStorage;
    }

    public void setToStorage(IStorage toStorage) {
        this.toStorage = toStorage;
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
