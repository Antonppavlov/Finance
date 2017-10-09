package ru.barmaglot.android.myfinance.dao.impls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.barmaglot.android.myfinance.dao.interfaces.ISourceDAO;
import ru.barmaglot.android.myfinance.database.SQLiteConnection;
import ru.barmaglot.android.myfinance.objects.impl.source.DefaultSource;
import ru.barmaglot.android.myfinance.objects.interfaces.source.ISource;
import ru.barmaglot.android.myfinance.objects.type.OperationType;

// TODO: 20.12.16 можно общие метод для дао слоев вынести в отдельный класс

public class SourceDAO implements ISourceDAO {

    private static final String SOURCE_TABLE = "source";
    private List<ISource> sourceList = new ArrayList<>();


    @Override
    public List<ISource> getAll() {
        sourceList.clear();

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "SELECT * FROM " + SOURCE_TABLE + " order by parent_id");) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                DefaultSource source = new DefaultSource();
                source.setId(resultSet.getLong("id"));
                source.setName(resultSet.getString("name"));
                source.setOperationType(OperationType.getType(resultSet.getInt("operation_type_id")));
                source.setParentId(resultSet.getInt("parent_id"));
                sourceList.add(source);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sourceList;
    }

    @Override
    public ISource get(long id) {
        DefaultSource source = null;

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement("SELECT * FROM " + SOURCE_TABLE + " where id=?");) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                source = new DefaultSource();
                source.setId(resultSet.getLong("id"));
                source.setName(resultSet.getString("name"));
                source.setOperationType(OperationType.getType(resultSet.getInt("operation_type_id")));
                source.setParentId(resultSet.getInt("parent_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return source;
    }

    @Override
    public List<ISource> getListSource(OperationType operationType) {
        List<ISource> sourceList = new ArrayList<>();

        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement("SELECT * FROM " + SOURCE_TABLE + " where operation_type_id=?");) {

            preparedStatement.setLong(1, operationType.getId());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DefaultSource source = new DefaultSource();
                source.setId(resultSet.getLong("id"));
                source.setName(resultSet.getString("name"));
                source.setOperationType(OperationType.getType(resultSet.getInt("operation_type_id")));
                source.setParentId(resultSet.getInt("parent_id"));


                sourceList.add(source);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return sourceList;
    }

    @Override
    public boolean add(ISource object) {
        try (PreparedStatement preparedStatement =
                     SQLiteConnection.getConnection().prepareStatement(
                             "insert into " + SOURCE_TABLE + "(name, parent_id, operation_type_id) values(?,?,?)",
                             Statement.RETURN_GENERATED_KEYS);) {

            preparedStatement.setString(1, object.getName());
            if (object.hasParent()) {
                preparedStatement.setLong(2, object.getParent().getId());
            } else {
               // preparedStatement.setLong(2, );
            }

            preparedStatement.setLong(3, object.getOperationType().getId());

            if (preparedStatement.executeUpdate() == 1) { //если обновлена одна запить то выбрасываем тру
                try(ResultSet rs= preparedStatement.getGeneratedKeys()){
                   while (rs.next()){
                       object.setId(rs.getLong(1));
                   }
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(ISource object) {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "UPDATE " + SOURCE_TABLE + " set name=? where id=?")
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
    public boolean delete(ISource object) {
        try (PreparedStatement preparedStatement = SQLiteConnection.getConnection()
                .prepareStatement(
                        "DELETE FROM " + SOURCE_TABLE + " where id=?")
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
