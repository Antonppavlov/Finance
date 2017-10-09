package ru.barmaglot.android.myfinance.utils;

import java.util.List;

import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;

/**
 * Created by antonpavlov on 10.12.16.
 */

public class TreeUtils<T extends ITreeNode> {
    //встраивает новый  элемент в нужное место в днреве
    //суть в том что необходимо пройти по всем элементамб
    //найти родителя и добавить к нему
    public void addToTree(long parentId, T newNode, List<T> nodeList) {

        if (parentId != 0) {
            //искать во всех корневых
            for (T currencyNode : nodeList) {
                if (currencyNode.getId() == parentId) {
                    currencyNode.addChild(newNode);
                    return;
                }
                //если родитель не найден в корневых, то ищем родителя среди дочерних элементов
                else {
                    ITreeNode node = recursiveSearch(parentId, currencyNode);
                    //если нашли в дочерних
                    if (node != null) {
                        node.addChild(newNode);
                        return;
                    }
                }
            }
        }
        nodeList.add(newNode);
    }

    private ITreeNode recursiveSearch(long parentId, T currencyNode) {
        for (ITreeNode node : currencyNode.getListChild()) {
            if (node.getId() == parentId) {
                return node;
            } else if (node.hasChild()) {
                recursiveSearch(node.getId(), currencyNode);
            }
        }
        return null;
    }
}
