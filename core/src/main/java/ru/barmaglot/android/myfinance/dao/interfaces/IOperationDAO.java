package ru.barmaglot.android.myfinance.dao.interfaces;

import java.util.List;

import ru.barmaglot.android.myfinance.objects.interfaces.operation.IOperation;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

/**
 * Created by antonpavlov on 22.12.16.
 */

public interface IOperationDAO extends ICommonDAO<IOperation> {

    // получить список операций определенного типа
    List<IOperation> getList(OperationType operationType);

}
