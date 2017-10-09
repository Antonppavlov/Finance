package ru.barmaglot.android.myfinance.objects.source;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import ru.barmaglot.android.myfinance.objects.impl.source.DefaultSource;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


@RunWith(Parameterized.class)
public class DefaultSourceTest {

   private final DefaultSource defaultSource = new DefaultSource();

    @Parameterized.Parameter
    public OperationType operationType;


    @Parameterized.Parameters
    public static Collection<OperationType> getParameters() {
        return Arrays.asList(
                OperationType.values()
        );
    }

    @Test
    public void checkSetOperation() {
        defaultSource.setOperationType(operationType);
        Assert.assertEquals(defaultSource.getOperationType(),operationType);
    }

}
