package ru.barmaglot.android.myfinance.dao.impls;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.barmaglot.android.myfinance.dao.interfaces.IStorageDAO;
import ru.barmaglot.android.myfinance.database.SQLiteConnection;
import ru.barmaglot.android.myfinance.exception.CurrencyException;
import ru.barmaglot.android.myfinance.objects.impl.storage.DefaultStorage;
import ru.barmaglot.android.myfinance.objects.interfaces.storage.IStorage;

public class StorageDAO implements IStorageDAO {

    private static final String CURRENCY_AMOUNT_TABLE = "currency_amount";
    private static final String STORAGE_TABLE = "storage";

    private List<IStorage> storageList = new ArrayList<>();

    @Override
    public Map<Currency, BigDecimal> getAllCurrency(IStorage storage) {

        Map<Currency, BigDecimal> currencyMap = new HashMap<>();

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "SELECT * FROM " + CURRENCY_AMOUNT_TABLE + " where storage_id=?")
             ;) {
            preparedStatement.setLong(1, storage.getId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Currency currency_code = Currency.getInstance(resultSet.getString("currency_code"));
                BigDecimal amount = resultSet.getBigDecimal("amount");
                currencyMap.put(currency_code,amount);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return currencyMap;
    }


    @Override
    public boolean addCurrency(IStorage storage, Currency currency, BigDecimal amount) throws SQLException {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "INSERT INTO " + CURRENCY_AMOUNT_TABLE + " (storage_id,currency_code,amount) values(?,?,?)")
             ;) {
            preparedStatement.setLong(1, storage.getId());
            preparedStatement.setString(2, currency.getCurrencyCode());
            preparedStatement.setBigDecimal(3, amount);

            if (preparedStatement.executeUpdate() == 1) { //если добавлена одна запить то выбрасываем тру
                return true;
            }

        }
        return false;
        // TODO: 07.12.16 реализовать исключени и сообщать пользователю что такую операцию сделать нельзя т.к. валюта уже существует
    }

    @Override
    public boolean deleteCurrency(IStorage storage, Currency currency) {
        // TODO: 07.12.16 Необходимо проверять есть ли у сумма на счету и если есть выбрасывать ошибку
        // TODO: 07.12.16 или предлагать пользователю подтверждения удаления с суммой
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "DELETE FROM  " + CURRENCY_AMOUNT_TABLE + " where storage_id=? and currency_code=?")
             ;) {
            preparedStatement.setLong(1, storage.getId());
            preparedStatement.setString(2, currency.getCurrencyCode());

            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAmount(IStorage storage, Currency currency, BigDecimal amount) {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "UPDATE  " + CURRENCY_AMOUNT_TABLE + " set amount=? where storage_id=? and currency_code=?")
             ;) {
            preparedStatement.setLong(2, storage.getId());
            preparedStatement.setString(3, currency.getCurrencyCode());
            preparedStatement.setBigDecimal(1, amount);
            if (preparedStatement.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<IStorage> getAll() {
        storageList.clear();

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "SELECT * FROM " + STORAGE_TABLE + " order by parent_id")
             ;) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DefaultStorage defaultStorage = new DefaultStorage();
                defaultStorage.setId(resultSet.getLong("id"));
                defaultStorage.setName(resultSet.getString("name"));
                defaultStorage.setParentId(resultSet.getLong("parent_id"));
                Map<Currency, BigDecimal> allCurrencyInStorage = getAllCurrency(defaultStorage);
                defaultStorage.setCurrencyAmounts(allCurrencyInStorage);
                defaultStorage.setAvailableCurrencies(new ArrayList<Currency>(allCurrencyInStorage.keySet()));
                storageList.add(defaultStorage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storageList;//должен содержать только корневые элементы
    }

    @Override
    public IStorage get(long id) {
        DefaultStorage storage = null;
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "SELECT * FROM " + STORAGE_TABLE + " where id=?")
             ;) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                storage=new DefaultStorage();
                storage.setId(resultSet.getLong("id"));
                storage.setName(resultSet.getString("name"));
                storage.setParentId(resultSet.getLong("parent_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return storage;//должен содержать только корневые элементы
    }

    @Override
    public boolean add(IStorage object) throws CurrencyException {
        Connection connection = SQLiteConnection.getConnection();
        try {
            //отключаем автокоммит чтобы сделать все одной тразакцией если все условия успешно выполнятся
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection
                    .prepareStatement(
                            "insert into " + STORAGE_TABLE + " (name,parent_id) values(?,?)",
                            //получаем назад сгенерированный ключ
                            Statement.RETURN_GENERATED_KEYS)
                 ;) {
                preparedStatement.setString(1, object.getName());

                if (object.hasParent()) {
                    preparedStatement.setLong(2, object.getParent().getId());
                } else {
                    //preparedStatement.setLong(2, Types.BIGINT);
                }


                if (preparedStatement.executeUpdate() == 1) { //если обновлена одна запить то выбрасываем тру

                    try (ResultSet rs = preparedStatement.getGeneratedKeys()) {

                        while (rs.next()) {
                            //записываем сгенеррированый id к объекту
                            object.setId(rs.getLong(1));
                        }

                        for (Currency currency : object.getAvailableCurrencies()) {
                            if (!addCurrency(object, currency, object.getAmount(currency))) {
                                //если хоть одну валюту не удасца добавить то сделаем роллбек всей транзакции
                                connection.rollback();
                                return false;
                            }
                        }
                    }
                    connection.commit();
                    connection.setAutoCommit(true);
                    return true;
                }
            }
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean update(IStorage object) {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "UPDATE " + STORAGE_TABLE + " set name=? where id=?")
             ;) {
            preparedStatement.setString(1, object.getName());
            preparedStatement.setLong(2, object.getId());

            if (preparedStatement.executeUpdate() == 1) { //если обновлена одна запить то выбрасываем тру

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean delete(IStorage object) {
          // TODO: 07.12.16 проверять если есть заведенные валюты и операции по данному хранилищу то запрещать удаление

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "DELETE FROM " + STORAGE_TABLE + " where id=?")
             ;) {

            preparedStatement.setLong(1, object.getId());

            if (preparedStatement.executeUpdate() == 1) { //если удалена одна запить то выбрасываем тру

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
