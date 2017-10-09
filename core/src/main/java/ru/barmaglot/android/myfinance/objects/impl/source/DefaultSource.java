package ru.barmaglot.android.myfinance.objects.impl.source;

import ru.barmaglot.android.myfinance.objects.abstracts.AbstractTreeNode;
import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


public class DefaultSource extends AbstractTreeNode implements ISource {

    private OperationType operationType;

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }


    @Override
    public void addChild(ITreeNode child) {
        // TODO: 03.12.16 применить патерн
        // TODO: 03.12.16 для дочернего элемента устанавливаем тип операции родительского элемента
        if (child instanceof DefaultSource) {
            DefaultSource childSource = (DefaultSource) child;
            childSource.setOperationType(this.operationType);
        }
        super.addChild(child);
    }


    @Override
    public void setParent(ITreeNode parent) {
         if(parent instanceof DefaultSource){
            operationType = ((DefaultSource) parent).getOperationType();
        }
        super.setParent(parent);
    }
}
