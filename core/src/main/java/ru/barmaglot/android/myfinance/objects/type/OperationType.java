package ru.barmaglot.android.myfinance.objects.type;


import java.util.HashMap;
import java.util.Map;

public enum OperationType {
    //доход
    INCOME(1),
    //расход
    OUTCOME(2),
    //перевод
    TRANSFER(3),
    //конвертация
    CONVERT(4),;

    private static Map<Long, OperationType> map = new HashMap<>();

    static {
        for (OperationType operationType : OperationType.values()) {
            map.put(operationType.getId(), operationType);
        }
    }

    private long id;

    private OperationType(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public static OperationType getType(long id) {
        return map.get(id);
    }


}
