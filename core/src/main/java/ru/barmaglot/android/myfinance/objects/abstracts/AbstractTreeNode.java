package ru.barmaglot.android.myfinance.objects.abstracts;

import java.util.ArrayList;
import java.util.List;

import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;


public abstract class AbstractTreeNode implements ITreeNode {

    private long id;
    private List<ITreeNode> childs = new ArrayList<>();
    private ITreeNode parent;
    private String name;
    private long parentId;

    public AbstractTreeNode() {
    }

    @Override
    public void addChild(ITreeNode child) {
        child.setParent(this);
        this.childs.add(child);
    }

    @Override
    public void removeChild(ITreeNode child) {
        child.setParent(null);
        this.childs.remove(child);
    }

    @Override
    public ITreeNode getChild(long id) {
        ITreeNode child = null;
        for (ITreeNode treeNode : childs) {
            if (treeNode.getId() == id) {
                child = treeNode;
                break;
            }
        }
        return child;
    }


    @Override
    public boolean hasChild() {
        //если коллекция не пустая возвращает true
        return !childs.isEmpty();
    }

    @Override
    public boolean hasParent() {
        if (parent == null) {
            return false;
        }
        return true;
    }

    @Override
    public long getId() {
        return id;
    }


    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public List<ITreeNode> getListChild() {
        return childs;
    }

    public void setChilds(List<ITreeNode> childs) {
        this.childs = childs;
    }

    @Override
    public ITreeNode getParent() {
        return parent;
    }

    @Override
    public void setParent(ITreeNode parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractTreeNode that = (AbstractTreeNode) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
