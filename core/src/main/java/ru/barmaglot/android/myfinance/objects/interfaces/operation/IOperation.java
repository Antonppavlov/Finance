package ru.barmaglot.android.myfinance.objects.interfaces.operation;

import java.util.Calendar;

import ru.barmaglot.android.myfinance.objects.type.OperationType;


public interface IOperation {

    long getId();

    void setId(long id);

    OperationType getOperationType();

    Calendar getDateTime();

    String getDescription();

}
