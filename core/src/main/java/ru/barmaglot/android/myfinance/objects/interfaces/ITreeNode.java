package ru.barmaglot.android.myfinance.objects.interfaces;


import java.util.List;

/**
 * Позволяет создать древовидную структуру из любого набора объектов, которые реализуют интерфейс ITreeNode
 * Паттерн "Компановщик" - вольная реализация
 */
public interface ITreeNode {

    long getId(); //каждый элемент дерева должен иметь свой униальный индетификатор

    void setId(long id);

    long getParentId();

    String getName(); //каждый элемент должен иметь свое имя

    void addChild(ITreeNode child); // добавить дочерный элемент

    void removeChild(ITreeNode child); //удалить дочерний элемент

    List<ITreeNode> getListChild(); //получить список дочерних элеметонв

    ITreeNode getChild(long id); //получение дочернего элемента по id

    ITreeNode getParent(); //получение родительсного элемента

    void setParent(ITreeNode parent); //установка родительского элемента

    boolean hasChild(); //проверяет есть ли дочерние элементы

    boolean hasParent(); //проверяет есть ли дочерние элементы


}
