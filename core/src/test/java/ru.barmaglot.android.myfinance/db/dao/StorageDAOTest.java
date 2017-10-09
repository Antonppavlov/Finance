package ru.barmaglot.android.myfinance.db.dao;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import ru.barmaglot.android.myfinance.dao.impls.StorageDAO;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.storage.DefaultStorage;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.type.CurrencyType;

@RunWith(Parameterized.class)
public class StorageDAOTest {

    private final StorageDAO storageDAO = new StorageDAO();

    @Parameterized.Parameter
    public CurrencyType currencyType;

    @Parameterized.Parameters
    public static Collection<CurrencyType> getParameters() {
        return Arrays.asList(
                CurrencyType.values()
        );
    }

    @Test
    public void addCurrency() throws CurrencyException, SQLException {
        IStorage iStorage = storageDAO.getAll().get(0);
        Currency currency = Currency.getInstance(currencyType.getCode());

        // TODO: 11.01.2017 удаляю перед проведением теста// сам тест не очень
        storageDAO.deleteCurrency(iStorage, currency);

        storageDAO.addCurrency(iStorage, currency, BigDecimal.valueOf(312312));
    }

    @Test(expected = SQLException.class)
    public void addExistingCurrency() throws CurrencyException, SQLException {
        IStorage iStorage = storageDAO.getAll().get(0);
        Currency currency = Currency.getInstance(currencyType.getCode());

        // TODO: 11.01.2017 удаляю перед проведением теста// сам тест не очень
        storageDAO.deleteCurrency(iStorage, currency);

        storageDAO.addCurrency(iStorage, currency, BigDecimal.valueOf(312312));
        storageDAO.addCurrency(iStorage, currency, BigDecimal.valueOf(312312));

    }

    @Test
    public void deleteCurrency() throws SQLException {
        IStorage iStorage = storageDAO.getAll().get(0);
        Currency currency = Currency.getInstance(currencyType.getCode());

        storageDAO.deleteCurrency(iStorage, currency);
        // TODO: 11.01.2017 удаляю перед проведением теста// сам тест не очень

        storageDAO.addCurrency(iStorage, currency, BigDecimal.valueOf(312312));
        storageDAO.deleteCurrency(iStorage, currency);
    }

    @Test
    public void updateAmount() throws SQLException {
        IStorage iStorage = storageDAO.getAll().get(0);
        Currency currency = Currency.getInstance(currencyType.getCode());

        storageDAO.deleteCurrency(iStorage, currency);
        // TODO: 11.01.2017 удаляю перед проведением теста// сам тест не очень

        storageDAO.addCurrency(iStorage, currency, BigDecimal.valueOf(1));

        Assert.assertTrue(storageDAO.updateAmount(iStorage, currency, BigDecimal.valueOf(1)));
    }

    @Test
    public void getAll() {
        Assert.assertTrue(storageDAO.getAll().size() > 0);
    }

    @Test
    public void get() throws SQLException {
        List<IStorage> all = storageDAO.getAll();
        IStorage iStorage = all.get(all.size() - 1);
        Assert.assertNotNull(iStorage);
    }

    @Test
    public void add() throws CurrencyException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage");
        storageDAO.add(defaultStorage);
    }

    @Test
    public void update() throws CurrencyException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage51");
        storageDAO.add(defaultStorage);

        defaultStorage.setName("New Storage Name");

        storageDAO.update(defaultStorage);

        IStorage iStorage = storageDAO.get(defaultStorage.getId());

        Assert.assertEquals(iStorage, defaultStorage);
    }

    @Test
    public void delete() throws CurrencyException, InterruptedException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage51");
        storageDAO.add(defaultStorage);

        Assert.assertTrue(storageDAO.delete(defaultStorage));

    }
}