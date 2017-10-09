package ru.barmaglot.android.myfinance.dao.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.dao.interfaces.IOperationDAO;
import ru.barmaglot.android.myfinance.database.SQLiteConnection;
import ru.barmaglot.android.myfinance.objects.abstracts.AbstractOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.ConvertOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.IncomeOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.OutcomeOperation;
import ru.barmaglot.android.myfinance.objects.impl.operation.TransferOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.operation.IOperation;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;
import ru.barmaglot.android.myfinance.objects.type.OperationType;


public class OperationDAO implements IOperationDAO {

    private static final String OPERATION_TABLE = "operation";
    private List<IOperation> operationList = new ArrayList<>();


    // чтобы эти объекты не получать еще раз из БД - передаем сюда
    private Map<Long, ISource> sourceIdentityMap;// чтобы получить по id нужный Source
    private Map<Long, IStorage> storageIdentityMap; // чтобы получить по id нужный Storage

    public OperationDAO(Map<Long, ISource> sourceIdentityMap, Map<Long, IStorage> storageIdentityMap) {
        // IdentityMap - распространенное понятие, это коллекция, где вместо ключа id, а значение - это сам объект
        this.sourceIdentityMap = sourceIdentityMap;
        this.storageIdentityMap = storageIdentityMap;
    }

    @Override
    public List<IOperation> getList(OperationType operationType) {
        List<IOperation> operationList = new ArrayList<>();

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection().prepareStatement(
                "SELECT * FROM " + OPERATION_TABLE + " where type_id=?")) {
            preparedStatement.setLong(1, operationType.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                operationList.add(fillOperation(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return operationList;
    }

    @Override
    public List<IOperation> getAll() {
        List<IOperation> operationList = new ArrayList<>();
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection().prepareStatement(
                "SELECT * FROM " + OPERATION_TABLE)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                operationList.add(fillOperation(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operationList;
    }


    @Override
    public IOperation get(long id) {
        IOperation iOperation = null;
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection().prepareStatement(
                "SELECT * FROM " + OPERATION_TABLE + " where id=?");) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                iOperation = fillOperation(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return iOperation;
    }

    @Override
    public boolean add(IOperation object) {
        String sql = createSQL(object.getOperationType());

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, object.getDateTime().getTimeInMillis());
            preparedStatement.setLong(2, object.getOperationType().getId());
            preparedStatement.setString(3, object.getDescription());

            switch (object.getOperationType()) {
                case INCOME: {
                    IncomeOperation incomeOperation = (IncomeOperation) object;
                    preparedStatement.setLong(4, incomeOperation.getFromSource().getId());
                    preparedStatement.setString(5, incomeOperation.getFromCurrency().getCurrencyCode());
                    preparedStatement.setBigDecimal(6, incomeOperation.getFromAmount());
                    preparedStatement.setLong(7, incomeOperation.getToStorage().getId());
                    break;
                }
                case OUTCOME: {
                    OutcomeOperation outcomeOperation = (OutcomeOperation) object;
                    preparedStatement.setLong(4, outcomeOperation.getFromStorage().getId());
                    preparedStatement.setString(5, outcomeOperation.getFromCurrency().getCurrencyCode());
                    preparedStatement.setBigDecimal(6, outcomeOperation.getFromAmount());
                    preparedStatement.setLong(7, outcomeOperation.getToSource().getId());
                    break;

                }
                case TRANSFER: {
                    TransferOperation transferOperation = (TransferOperation) object;
                    preparedStatement.setLong(4, transferOperation.getFromStorage().getId());
                    preparedStatement.setString(5, transferOperation.getFromCurrency().getCurrencyCode());
                    preparedStatement.setBigDecimal(6, transferOperation.getFromAmount());
                    preparedStatement.setLong(7, transferOperation.getToStorage().getId());
                    break;
                }
                case CONVERT: {
                    ConvertOperation convertOperation = (ConvertOperation) object;
                    preparedStatement.setLong(4, convertOperation.getFromStorage().getId());
                    preparedStatement.setString(5, convertOperation.getFromCurrency().getCurrencyCode());
                    preparedStatement.setBigDecimal(6, convertOperation.getFromAmount());
                    preparedStatement.setLong(7, convertOperation.getToStorage().getId());
                    preparedStatement.setString(8, convertOperation.getToCurrency().getCurrencyCode());
                    preparedStatement.setBigDecimal(9, convertOperation.getToAmount());
                    break;
                }
            }
            if (preparedStatement.executeUpdate() == 1) { //если добавлена одна запить то выбрасываем тру
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    while (resultSet.next()) {
                        object.setId(resultSet.getLong(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String createSQL(OperationType operationType) {
        StringBuilder stringBuilder = new StringBuilder(
                "INSERT INTO " + OPERATION_TABLE + " (datetime, type_id, description, "
        );

        switch (operationType) {
            case INCOME: {
                //берем деньги из источника
                //в валюте такой-то
                //такое-то количество
                //вкладываем в такое-то хранилище
                stringBuilder.append("from_source_id, from_currency_code, from_amount, to_storage_id) " +
                        "values (?,?,?,?,?,?,?)");
                break;
            }
            case OUTCOME: {
                //из такого-то хранилища
                //в валюте такой-то
                //такое-то количество
                //тратим на такой-то источник
                stringBuilder.append("from_storage_id, from_currency_code, from_amount, to_source_id) " +
                        "values (?,?,?,?,?,?,?)");
                break;
            }
            case TRANSFER: {
                //берем из такого-то хранилища
                //в валюте такой-то
                //такое-то количество
                //перекладываем в такое-то хранилище
                stringBuilder.append("from_storage_id, from_currency_code, from_amount, to_storage_id) " +
                        "values (?,?,?,?,?,?,?)");
                break;
            }
            case CONVERT: {
                //берем из такого-то хранилища
                //в валюте такой-то
                //такое-то количество
                //перекладываем в такое-то хранилище
                //в такуе-то валюту
                //в такое-то хранилище
                stringBuilder.append(
                        "from_storage_id, from_currency_code, from_amount, to_storage_id, to_currency_code, to_amount) " +
                                "values (?,?,?,?,?,?,?,?,?)");
                break;
            }
        }
        System.out.println(stringBuilder);
        System.out.println();
        return String.valueOf(stringBuilder);
    }

    //при обновлении не даем менять тип операции - только данные самой операции (дата, суммы, источники, хранилища, описание)
    @Override
    public boolean update(IOperation operation) {
        // при обновлении - удаляем старую операцию, добавляем новую, т.к. могут поменяться хранилища, источники
        return (delete(operation) && add(operation));
    }

    @Override
    public boolean delete(IOperation object) {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection().prepareStatement(
                "DELETE FROM " + OPERATION_TABLE + " where id=?"
        );) {
            preparedStatement.setLong(1, object.getId());

            if (preparedStatement.executeUpdate() == 1) { //если удалена одна запить то выбрасываем тру
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    private IOperation fillOperation(ResultSet resultSet) throws SQLException {
        OperationType operationType = OperationType.getType(resultSet.getLong("type_id"));

        //создание уникальных параметров для операции
        AbstractOperation operation = createOperation(operationType, resultSet);

        operation.setId(resultSet.getLong("id"));
        operation.setOperationType(operationType);
        operation.setDescription(resultSet.getString("description"));

        Calendar datetime = Calendar.getInstance();
        datetime.setTimeInMillis(resultSet.getLong("datetime"));
        operation.setDateTime(datetime);

        return operation;
    }

    private AbstractOperation createOperation(OperationType operationType, ResultSet resultSet) throws SQLException {
        // список операций должен загружаться при запуске программы только один
        // - тогда можно использовать лямбда выражения для поиска объектов по id
        // для всех новых операций - поиск по id проводиться не будет
        switch (operationType) {
            case INCOME: { // доход
                IncomeOperation operation = new IncomeOperation();

                operation.setFromSource(sourceIdentityMap.get(resultSet.getLong("from_source_id"))); // откуда поступили деньги
                operation.setFromCurrency(Currency.getInstance(resultSet.getString("from_currency_code"))); // в какой валюте поступили
                operation.setFromAmount(resultSet.getBigDecimal("from_amount")); // сумма поступления
                operation.setToStorage(storageIdentityMap.get(resultSet.getLong("to_storage_id")));// куда положим эти деньги


                return operation;
            }
            case OUTCOME: { // расход
                OutcomeOperation operation = new OutcomeOperation();

                operation.setFromStorage(storageIdentityMap.get(resultSet.getLong("from_storage_id"))); // откуда взяли деньги
                operation.setFromCurrency(Currency.getInstance(resultSet.getString("from_currency_code")));// в какой валюте расход
                operation.setFromAmount(resultSet.getBigDecimal("from_amount")); // сумма расхода
                operation.setToSource(sourceIdentityMap.get(resultSet.getLong("to_source_id")));// на что потратили

                return operation;
            }

            case TRANSFER: { // перевод в одной валюте между хранилищами
                TransferOperation operation = new TransferOperation();

                operation.setFromStorage(storageIdentityMap.get(resultSet.getLong("from_storage_id")));// откуда переводим
                operation.setFromCurrency(Currency.getInstance(resultSet.getString("from_currency_code"))); // в какой валюте переводим
                operation.setFromAmount(resultSet.getBigDecimal("from_amount")); // какую сумму переводим
                operation.setToStorage(storageIdentityMap.get(resultSet.getLong("to_storage_id"))); // не создаем новый объект, используем ранее созданный объект источника

                return operation;
            }

            case CONVERT: { // конвертация из любой валюты в любую между хранилищами
                ConvertOperation operation = new ConvertOperation();

                operation.setFromStorage(storageIdentityMap.get(resultSet.getLong("from_storage_id"))); // откуда конвертируем
                operation.setFromCurrency(Currency.getInstance(resultSet.getString("from_currency_code"))); // в каой валюте
                operation.setFromAmount(resultSet.getBigDecimal("from_amount")); // какая сумма в исходной валюте

                operation.setToStorage(storageIdentityMap.get(resultSet.getLong("to_storage_id"))); // куда конвертируем
                operation.setToCurrency((Currency.getInstance(resultSet.getString("to_currency_code")))); // валюта поступления
                operation.setToAmount(resultSet.getBigDecimal("to_amount")); // какая итоговая сумма поступила в этой валюте

                return operation;
            }


        }

        return null;// если ни один из типов операция не подошел, можно также выбрасывать исключение

    }

    public Map<Long, ISource> getSourceIdentityMap() {
        return sourceIdentityMap;
    }


    public Map<Long, IStorage> getStorageIdentityMap() {
        return storageIdentityMap;
    }


}
