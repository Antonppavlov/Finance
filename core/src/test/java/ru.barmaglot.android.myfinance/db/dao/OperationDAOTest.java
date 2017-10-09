package ru.barmaglot.android.myfinance.db.dao;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import ru.barmaglot.android.myfinance.dao.decotation.SourceSynchronizer;
import ru.barmaglot.android.myfinance.dao.decotation.StorageSynchronizer;
import ru.barmaglot.android.myfinance.dao.impls.OperationDAO;
import ru.barmaglot.android.myfinance.dao.impls.SourceDAO;
import ru.barmaglot.android.myfinance.dao.impls.StorageDAO;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.operation.IncomeOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.operation.IOperation;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

public class OperationDAOTest {

    private final OperationDAO operationDAO = new OperationDAO(
            new SourceSynchronizer(new SourceDAO()).getIdentityMap(),
            new StorageSynchronizer(new StorageDAO()).getIdentityMap());


    private final long id = 1;
    private final IncomeOperation incomeOperation = new IncomeOperation(
            Calendar.getInstance(),
            OperationType.INCOME,
            "купил продуктов",
            operationDAO.getSourceIdentityMap().get(id),
            operationDAO.getStorageIdentityMap().get(id).getAvailableCurrencies().get(0),
            BigDecimal.valueOf(10),
            operationDAO.getStorageIdentityMap().get(id)
    );


    @Test
    public void getListOperationType() {
        OperationType operationType = OperationType.INCOME;

        List<IOperation> list = operationDAO.getList(operationType);
        for (IOperation iOperation : list) {
            Assert.assertEquals(iOperation.getOperationType(), operationType);
        }
    }

    @Test
    public void getAll() {
        Assert.assertTrue(operationDAO.getAll().size() > 0);
    }

    @Test
    public void get() {
        long idObject = operationDAO.getAll().get(0).getId();
        IOperation iOperation = operationDAO.get(idObject);

        Assert.assertNotNull(iOperation);
    }

    @Test
    public void add() throws CurrencyException {
        operationDAO.add(incomeOperation);

        IOperation iOperation = operationDAO.get(incomeOperation.getId());

        Assert.assertEquals(incomeOperation.getId(), iOperation.getId());

    }

    @Test
    public void update() {
        operationDAO.add(incomeOperation);
        Assert.assertNotNull(operationDAO.get(incomeOperation.getId()));
        String name = "Test Description";
        incomeOperation.setDescription(name);
        Assert.assertTrue(operationDAO.update(incomeOperation));

        Assert.assertEquals(operationDAO.get(incomeOperation.getId()).getDescription(), name);
    }

    @Test
    public void delete() {
        operationDAO.add(incomeOperation);
        Assert.assertNotNull(operationDAO.get(incomeOperation.getId()));

        operationDAO.delete(incomeOperation);
        Assert.assertNull(operationDAO.get(incomeOperation.getId()));
    }
}