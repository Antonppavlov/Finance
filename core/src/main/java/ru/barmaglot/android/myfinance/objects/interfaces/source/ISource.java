package ru.barmaglot.android.myfinance.objects.interfaces.source;


import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

public interface ISource extends ITreeNode {

    OperationType getOperationType();

}
