package ru.barmaglot.android.myfinance.dao.interfaces;

import java.util.List;

import ru.barmaglot.android.myfinance.exception.AmountException;
import ru.barmaglot.android.myfinance.exception.CurrencyException;

//описывает общие действия в бд для всех объектов
public interface ICommonDAO<T> {

    List<T> getAll();

    T get(long id);

    boolean add(T object) throws CurrencyException, AmountException;

    boolean update(T object) throws CurrencyException, AmountException;

    boolean delete(T object) throws AmountException, CurrencyException;



}
