package ru.barmaglot.android.myfinance.db.synchronizer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.dao.decotation.SourceSynchronizer;
import ru.barmaglot.android.myfinance.dao.impls.SourceDAO;
import ru.barmaglot.android.myfinance.dao.interfaces.ISourceDAO;
import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.source.DefaultSource;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

@RunWith(Parameterized.class)
public class SourceSynchronizerTest {

    private final SourceSynchronizer sourceSynchronizer = new SourceSynchronizer(new SourceDAO());

    @Parameterized.Parameter
    public OperationType operationType;

    @Parameterized.Parameters
    public static Collection<OperationType> getParameters() {
        return Arrays.asList(
                OperationType.values()
        );
    }


    @Test
    public void getIdentityMap() {
        Map<Long, ISource> identityMap = sourceSynchronizer.getIdentityMap();

        Assert.assertNotNull(identityMap);
        Assert.assertTrue(identityMap instanceof Map);
    }

    @Test
    public void getSourceList() {
        List<ISource> listSource = sourceSynchronizer.getListSource(operationType);

        for (ISource iSource : listSource) {
            Assert.assertEquals(iSource.getOperationType(), operationType);
        }

    }

    @Test
    public void getAll() {
        Assert.assertTrue(sourceSynchronizer.getAll().size() > 0);
    }

    @Test
    public void get() {
        List<ISource> iSourceList = sourceSynchronizer.getAll();

        ISource iSource = iSourceList.get(iSourceList.size() - 1);

        Assert.assertNotNull(iSource);
    }

    @Test
    public void addSourceNotParent() throws CurrencyException, AmountException {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);

        Assert.assertTrue(sourceSynchronizer.add(defaultSource));

        Assert.assertTrue(sourceSynchronizer.getTreeList().contains(defaultSource));
        Assert.assertEquals(sourceSynchronizer.getIdentityMap().get(defaultSource.getId()), defaultSource);
        Assert.assertTrue(sourceSynchronizer.getListSource(defaultSource.getOperationType()).contains(defaultSource));


        sourceSynchronizer.delete(defaultSource);
    }

    @Test
    public void addSourceHaveParent() throws CurrencyException, AmountException {
        long parentId = 1;

        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Sourc");
        defaultSource.setOperationType(operationType);
        defaultSource.setParent(sourceSynchronizer.get(parentId));

        Assert.assertTrue(sourceSynchronizer.add(defaultSource));

        List<ISource> treeList = sourceSynchronizer.getTreeList();

        int treeListIdSource = 0;
        for (int i = 0; i < treeList.size(); i++) {
            if (treeList.get(i).getId() == parentId) {
                treeListIdSource = i;
            }
        }

        Assert.assertTrue(treeList.get(treeListIdSource).getListChild().contains(defaultSource));
        Assert.assertEquals(sourceSynchronizer.getIdentityMap().get(defaultSource.getId()), defaultSource);
        Assert.assertTrue(sourceSynchronizer.getListSource(defaultSource.getOperationType()).contains(defaultSource));


        sourceSynchronizer.delete(defaultSource);
    }

    @Test
    public void update() throws CurrencyException, AmountException {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);

        Assert.assertTrue(sourceSynchronizer.add(defaultSource));
        defaultSource.setName("New name Source");

        Assert.assertTrue(sourceSynchronizer.update(defaultSource));
        ISource source = sourceSynchronizer.get(defaultSource.getId());

        Assert.assertEquals(defaultSource.getName(), source.getName());

        sourceSynchronizer.delete(defaultSource);
    }

    @Test
    public void delete() throws CurrencyException, AmountException {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);

        Assert.assertTrue(sourceSynchronizer.add(defaultSource));

        sourceSynchronizer.delete(defaultSource);


        ISource source = sourceSynchronizer.get(defaultSource.getId());


        Assert.assertNull(source);
    }


    @Test
    public void getiSourceDAO() {
        ISourceDAO iSourceDAO = sourceSynchronizer.getiSourceDAO();
        Assert.assertNotNull(iSourceDAO);
        Assert.assertTrue(iSourceDAO instanceof ISourceDAO);
    }
}