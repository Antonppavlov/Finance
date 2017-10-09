package ru.barmaglot.android.myfinance.db.synchronizer;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Currency;

import ru.barmaglot.android.myfinance.dao.decotation.StorageSynchronizer;
import ru.barmaglot.android.myfinance.dao.impls.StorageDAO;
import ru.barmaglot.android.myfinance.dao.interfaces.IStorageDAO;
import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.storage.DefaultStorage;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;

public class StorageSynchronizerTest {

    private final StorageSynchronizer storageSynchronizer = new StorageSynchronizer(new StorageDAO());

    @Test
    public void getAll() {
        Assert.assertTrue(storageSynchronizer.getAll().size() > 1);
    }

    @Test
    public void get() {
        IStorage lastStorageInCollection = storageSynchronizer.getAll().get(storageSynchronizer.getAll().size() - 1);
        IStorage iStorage = storageSynchronizer.get(lastStorageInCollection.getId());
        Assert.assertEquals(lastStorageInCollection, iStorage);
    }

    @Test
    public void add() throws CurrencyException, AmountException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage StorageSynchronizerTest");


        Assert.assertTrue(storageSynchronizer.add(defaultStorage));

        IStorage iStorage = storageSynchronizer.get(defaultStorage.getId());

        Assert.assertEquals(defaultStorage, iStorage);

    }

    @Test
    public void update() throws CurrencyException, AmountException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage StorageSynchronizerTest");
        Assert.assertTrue(storageSynchronizer.add(defaultStorage));
        defaultStorage.setName("New Stotage StorageSynchronizerTest");

        Assert.assertTrue(storageSynchronizer.update(defaultStorage));

        Assert.assertEquals(storageSynchronizer.get(defaultStorage.getId()).getName(),
                            defaultStorage.getName());
    }

    @Test
    public void delete() throws CurrencyException, AmountException {
        DefaultStorage defaultStorage = new DefaultStorage();
        defaultStorage.setName("Test Storage StorageSynchronizerTest");


        Assert.assertTrue(storageSynchronizer.add(defaultStorage));

        Assert.assertTrue(storageSynchronizer.delete(defaultStorage));

        IStorage iStorage = storageSynchronizer.get(defaultStorage.getId());

        Assert.assertNull(iStorage);
    }

    @Test
    public void getiStorageDAO() {
        IStorageDAO storageDAO = storageSynchronizer.getiStorageDAO();
        Assert.assertNotNull(storageDAO);
        Assert.assertTrue(storageDAO instanceof StorageDAO);
    }

    @Test
    public void addCurrency() throws CurrencyException, SQLException {
        IStorage iStorage = storageSynchronizer.getAll().get(1);
        Currency kzt = Currency.getInstance("KZT");

        Assert.assertTrue(storageSynchronizer.deleteCurrency(iStorage, kzt));
        Assert.assertTrue(storageSynchronizer.addCurrency(iStorage, kzt, BigDecimal.ONE));

        storageSynchronizer.deleteCurrency(iStorage, kzt);
    }

    @Test
    public void deleteCurrency() throws CurrencyException, SQLException {
        IStorage iStorage = storageSynchronizer.getAll().get(1);
        Currency kzt = Currency.getInstance("KZT");

       // Assert.assertTrue(storageSynchronizer.deleteCurrency(iStorage, kzt));


        Assert.assertTrue(storageSynchronizer.addCurrency(iStorage, kzt, BigDecimal.ONE));
        Assert.assertTrue(storageSynchronizer.deleteCurrency(iStorage, kzt));

    }

    @Test
    public void updateAmount() throws CurrencyException, SQLException, AmountException {
        IStorage iStorage = storageSynchronizer.getAll().get(1);
        Currency kzt = Currency.getInstance("KZT");
        BigDecimal sumAmount = BigDecimal.valueOf(123);
        Assert.assertTrue(storageSynchronizer.addCurrency(iStorage, kzt, BigDecimal.ONE));
        Assert.assertTrue(storageSynchronizer.updateAmount(iStorage, kzt, sumAmount));

        int equalAmount = storageSynchronizer.getAllCurrency(iStorage).get(kzt).compareTo(sumAmount);

        Assert.assertTrue(equalAmount == 0);

    }
}