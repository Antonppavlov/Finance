package ru.barmaglot.android.myfinance.objects.impl.storage;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.abstracts.AbstractTreeNode;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;

public class DefaultStorage extends AbstractTreeNode implements IStorage {

    private Map<Currency, BigDecimal> currencyAmounts = new HashMap<>();
    private List<Currency> currencyList = new ArrayList<>();

    public DefaultStorage() {
    }

    @Override
    public List<Currency> getAvailableCurrencies() {
        return currencyList;
    }

    public void setAvailableCurrencies(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    @Override
    public Map<Currency, BigDecimal> getCurrencyAmounts() {
        return currencyAmounts;
    }

    public void setCurrencyAmounts(Map<Currency, BigDecimal> currencyAmounts) {
        this.currencyAmounts = currencyAmounts;
    }


    @Override
    public BigDecimal getAmount(Currency currency) throws CurrencyException {
        checkCurrencyExist(currency);
        return currencyAmounts.get(currency);
    }


    // ручное обновление баланса
    @Override
    public void updateAmount(BigDecimal amount, Currency currency) throws CurrencyException {
        checkCurrencyExist(currency);
        currencyAmounts.put(currency, amount);
    }



    @Override
    public void addCurrency(Currency currency) throws CurrencyException {
        if (currencyList.contains(currency)) {
            throw new CurrencyException("Currency already exist!");
        }
        currencyList.add(currency);
        currencyAmounts.put(currency, BigDecimal.ZERO);
    }

    @Override
    public void deleteCurrency(Currency currency) throws CurrencyException {
        checkCurrencyExist(currency);
        // TODO: 30.12.16 идея в том чтобы нельзя было уйти в минус/ пока закомментированно
//        if (!currencyAmounts.get(currency).equals(BigDecimal.ZERO)) {
//            throw new CurrencyException("Can't delete currency with amount!");
//        }
        currencyAmounts.remove(currency);
        currencyList.remove(currency);
    }


    @Override
    public BigDecimal getApproxAmount(Currency currency) {
        // TODO реализовать расчет остатка с приведением в одну валюту
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Currency getCurrency(String code) throws CurrencyException {
        // количество валют для каждого хранилища будет небольшим - поэтому можно провоить поиск через цикл
        // можно использовать библиотеку Apache Commons Collections
        for (Currency currency : currencyList) {
            if (currency.getCurrencyCode().equals(code)) {
                return currency;
            }
        }

        throw new CurrencyException();

    }

    private void checkAmount(BigDecimal amount) throws AmountException {
        int result = amount.compareTo(BigDecimal.ZERO);
        if (result <= 0) {
            throw new AmountException("Amount can't be <0");
        }
    }


    private void checkCurrencyExist(Currency currency) throws CurrencyException {
        if (!currencyList.contains(currency)) {
            throw new CurrencyException("Currency not exist for delete!");
        }
    }
}
