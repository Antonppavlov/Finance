package ru.barmaglot.android.myfinance.objects.interfaces.storage;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;

// TODO изменить тип BigDecimal на готовый класс по работе с деньгами Money
public interface IStorage extends ITreeNode {


    // получение баланса (остатка)
    Map<Currency, BigDecimal> getCurrencyAmounts(); // остаток по каждой доступной валюте в хранилище

    BigDecimal getAmount(Currency currency) throws CurrencyException; // остаток по определенной валюте

    BigDecimal getApproxAmount(Currency currency);// примерный остаток в переводе всех денег в одну валюту


    // изменение баланса
    void updateAmount(BigDecimal amount, Currency currency) throws AmountException, CurrencyException; // изменение баланса по определенной валюте


    // работа с валютой
    void addCurrency(Currency currency) throws CurrencyException; // добавить новую валюту в хранилище

    void deleteCurrency(Currency currency) throws CurrencyException; // удалить валюту из хранилища

    Currency getCurrency(String code) throws CurrencyException; // получить валюту по коду

    List<Currency> getAvailableCurrencies(); // получить все доступные валюты хранилища в отдельной коллекции

}