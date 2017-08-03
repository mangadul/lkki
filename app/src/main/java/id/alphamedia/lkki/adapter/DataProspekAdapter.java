package id.alphamedia.lkki.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import id.alphamedia.lkki.R;
import id.alphamedia.lkki.models.DataProspek;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

class DataProspekAdapter extends RealmRecyclerViewAdapter<DataProspek, DataProspekAdapter.MyViewHolder> {

    private boolean inDeletionMode = false;
    private Set<Long> countersToDelete = new HashSet<Long>();
    private Set<Long> idProspekToDelete = new HashSet<Long>();

    DataProspekAdapter(OrderedRealmCollection<DataProspek> data) {
        super(data, true);
        setHasStableIds(true);
    }

    void enableDeletionMode(boolean enabled) {
        inDeletionMode = enabled;
        if (!enabled) {
            countersToDelete.clear();
        }
        notifyDataSetChanged();
    }

    Set<Long> getIdProspekToDelete(){
        return idProspekToDelete;
    }

    Set<Long> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DataProspek obj = getItem(position);
        holder.data = obj;
        //noinspection ConstantConditions
        holder.title.setText(obj.getNama());
        holder.deletedCheckBox.setChecked(countersToDelete.contains(obj.getId_prospek()));
        if (inDeletionMode) {
            holder.deletedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        countersToDelete.add(obj.getId_prospek());
                    } else {
                        countersToDelete.remove(obj.getId_prospek());
                    }
                }
            });
        } else {
            holder.deletedCheckBox.setOnCheckedChangeListener(null);
        }
        holder.deletedCheckBox.setVisibility(inDeletionMode ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId_prospek();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CheckBox deletedCheckBox;
        public DataProspek data;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.textview);
            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}