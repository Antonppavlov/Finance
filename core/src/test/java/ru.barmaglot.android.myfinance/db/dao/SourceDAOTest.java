package ru.barmaglot.android.myfinance.db.dao;

// TODO: 20.12.16 можно общие метод для дао слоев вынести в отдельный класс

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ru.barmaglot.android.myfinance.dao.impls.SourceDAO;
import ru.barmaglot.android.myfinance.objects.impl.source.DefaultSource;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

@RunWith(Parameterized.class)
public class SourceDAOTest {

    private final SourceDAO sourceDAO = new SourceDAO();

    @Parameterized.Parameter
    public OperationType operationType;

    @Parameterized.Parameters
    public static Collection<OperationType> getParameters() {
        return Arrays.asList(
                OperationType.values()
        );
    }


    @Test
    public void getAll() {
        List<ISource> allSource = sourceDAO.getAll();
        System.out.println(allSource);
        Assert.assertTrue(allSource.size() > 1);
    }

    @Test
    public void getId() {
        ISource iSourceOneInTable = sourceDAO.getAll().get(0);
        long id = sourceDAO.getAll().get(0).getId();

        ISource iSource = sourceDAO.get(id);

        Assert.assertEquals(iSourceOneInTable, iSource);
    }

    @Test
    public void getListSource() {

        List<ISource> listSource = sourceDAO.getListSource(operationType);

        for (ISource source : listSource) {
            Assert.assertTrue(source.getOperationType().equals(operationType));
        }
    }

    @Test
    public void addSource() {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);
        sourceDAO.add(defaultSource);
        ISource iSource = sourceDAO.get(defaultSource.getId());

        Assert.assertEquals(defaultSource, iSource);
    }

    @Test
    public void update() {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);
        sourceDAO.add(defaultSource);

        String newName="New Name";
        defaultSource.setName(newName);
        sourceDAO.update(defaultSource);

        ISource source = sourceDAO.get(defaultSource.getId());

        Assert.assertEquals(defaultSource.getName(), source.getName());
    }


    @Test
    public void delete() {
        DefaultSource defaultSource = new DefaultSource();
        defaultSource.setName("Test Source");
        defaultSource.setOperationType(operationType);
        sourceDAO.add(defaultSource);

        Assert.assertTrue(sourceDAO.delete(defaultSource));

        //ISource source = sourceDAO.get(defaultSource.getId());
        //Assert.assertNull(defaultSource);
    }
}