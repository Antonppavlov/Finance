package ru.barmaglot.android.myfinance.objects.impl.operation;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


// доход
public class IncomeOperation extends AbstractOperation {

    public IncomeOperation() {
        super(OperationType.INCOME);
    }

    public IncomeOperation(Calendar dateTime, OperationType operationType, String description,  ISource fromSource,Currency fromCurrency, BigDecimal fromAmount, IStorage toStorage) {
        super(dateTime, operationType, description);
        this.fromSource = fromSource;
        this.toStorage = toStorage;
        this.fromAmount = fromAmount;
        this.fromCurrency = fromCurrency;
    }

    private ISource fromSource; // откула пришли деньги
    private IStorage toStorage; // куда положили деньги
    private BigDecimal fromAmount; // сумма получения
    private Currency fromCurrency; // в какой валюте получили деньги

    public ISource getFromSource() {
        return fromSource;
    }

    public void setFromSource(ISource fromSource) {
        this.fromSource = fromSource;
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
