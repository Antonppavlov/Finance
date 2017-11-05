package ru.barmaglot.android.myfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.barmaglot.android.myfinance.R;
import ru.barmaglot.android.myfinance.fragments.SprListFragment;
import ru.barmaglot.android.myfinance.objects.interfaces.ITreeNode;

import java.util.List;


public class TreeNodeAdapter extends RecyclerView.Adapter<TreeNodeAdapter.ViewHolder> {

    private   List<? extends ITreeNode> iTreeNodes;
    private final SprListFragment.OnListFragmentInteractionListener clickListener;

    public TreeNodeAdapter(List<? extends ITreeNode> items, SprListFragment.OnListFragmentInteractionListener listener) {
        iTreeNodes = items;
        clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spt_node, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
      final   ITreeNode treeNode = iTreeNodes.get(position);

        holder.tvSprName.setText(treeNode.getName());
        holder.sprMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener) {
                    clickListener.onListFragmentInteraction(treeNode);
                }
                if(treeNode.hasChild()){
                    updateDate(treeNode.getListChild());
                }
            }
        });
    }

    private void updateDate(List<? extends ITreeNode> listChild) {
        this.iTreeNodes = listChild;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return iTreeNodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ViewGroup sprMainLayout;
        public final TextView tvSprName;

        public ViewHolder(View view) {
            super(view);
            sprMainLayout = (ViewGroup) view.findViewById(R.id.spr_main_layout);
            tvSprName = (TextView) view.findViewById(R.id.spr_name);
        }

    }
}
