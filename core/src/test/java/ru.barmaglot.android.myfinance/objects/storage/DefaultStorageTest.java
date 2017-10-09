package ru.barmaglot.android.myfinance.objects.storage;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;

import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.storage.DefaultStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;
import ru.barmaglot.android.myfinance.type.CurrencyType;

@RunWith(Parameterized.class)
public class DefaultStorageTest {


    private final DefaultStorage defaultStorage = new DefaultStorage();

    @Parameterized.Parameter
    public CurrencyType currencyCode;

    private Currency currency;

    @Parameterized.Parameters
    public static Collection<CurrencyType> getParameters() {
        return Arrays.asList(
                CurrencyType.values()
        );
    }

    @Before
    public void setUp() {
        currency= Currency.getInstance(currencyCode.getCode());
    }

    @Test
    public void checkAddCurrency() throws CurrencyException {
        defaultStorage.addCurrency(currency);

        Assert.assertEquals(defaultStorage.getAvailableCurrencies().get(0), currency);
    }


    @Test(expected = CurrencyException.class)
    public void checkErrorWhenRepeatAddCurrency() throws CurrencyException {
        defaultStorage.addCurrency(currency);
        defaultStorage.addCurrency(currency);
    }


    @Test
    public void checkGetSumInCurrency() throws CurrencyException {
        defaultStorage.addCurrency(currency);

        BigDecimal sumCurrencyRub = defaultStorage.getCurrencyAmounts().get(currency);
        Assert.assertEquals(sumCurrencyRub, BigDecimal.ZERO);
    }


    @Test
    public void checkUpdateAmount() throws CurrencyException {
        defaultStorage.addCurrency(currency);

        BigDecimal money = BigDecimal.valueOf(31312);
        defaultStorage.updateAmount(money, currency);

        BigDecimal sumCurrencyRub = defaultStorage.getCurrencyAmounts().get(currency);

        Assert.assertEquals(sumCurrencyRub, money);
    }


    @Test(expected = CurrencyException.class)
    public void checkDeleteCurrency() throws CurrencyException {
        defaultStorage.addCurrency(currency);
        defaultStorage.deleteCurrency(currency);


        defaultStorage.getAmount(currency);
    }
}
