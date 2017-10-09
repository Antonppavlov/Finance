package ru.barmaglot.android.myfinance.dao.interfaces;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Currency;
import java.util.Map;

import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;

//во время использование ICommonDAO<IStorage>
// передаем в объект IStorage и подставляем вместо Т
public interface IStorageDAO extends ICommonDAO<IStorage> {
    //добавление валюты в определенное хранилище
    boolean addCurrency(IStorage storage, Currency currency, BigDecimal amount) throws CurrencyException, SQLException;
    boolean deleteCurrency(IStorage storage, Currency currency) throws CurrencyException;//удаление
    boolean updateAmount(IStorage storage, Currency currency, BigDecimal amount) throws AmountException, CurrencyException; //обновить значение остатка
    Map<Currency, BigDecimal> getAllCurrency(IStorage storage);


}
