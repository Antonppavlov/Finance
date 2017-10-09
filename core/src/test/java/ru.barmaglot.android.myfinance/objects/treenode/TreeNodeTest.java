package ru.barmaglot.android.myfinance.objects.treenode;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractTreeNode;
import ru.barmaglot.android.myfinance.objects.impl.source.DefaultSource;
import ru.barmaglot.android.myfinance.objects.impl.storage.DefaultStorage;

@RunWith(Parameterized.class)
public class TreeNodeTest {

    @Parameterized.Parameter
    public AbstractTreeNode abstractTreeNode;
    @Parameterized.Parameter(value = 1)
    public AbstractTreeNode defaultSource;

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {new DefaultSource(), new DefaultSource()},
                {new DefaultStorage(), new DefaultStorage()},
        });
    }


    @Test
    public void checkAddChild() {
//добавляю дочерний объект
        abstractTreeNode.addChild(defaultSource);

//проверяю что дочернему объекту добавился родитель
        Assert.assertEquals(defaultSource.getParent(), abstractTreeNode);
        Assert.assertTrue(defaultSource.hasParent());

//проверяю что родителю добавился потомок
        Assert.assertTrue(abstractTreeNode.getListChild().contains(defaultSource));
        Assert.assertTrue(abstractTreeNode.hasChild());
    }

    @Test
    public void checkRemoveChild() {
//добавляю дочерний объект
        abstractTreeNode.addChild(defaultSource);
//удаляю дочерний объект
        abstractTreeNode.removeChild(defaultSource);

//проверяю что родитель не содержит в себе потомка
        Assert.assertFalse(abstractTreeNode.getListChild().contains(defaultSource));
        Assert.assertFalse(abstractTreeNode.hasChild());
//проверяю что у потомка нет родителя
        Assert.assertFalse(defaultSource.hasParent());
    }

}
