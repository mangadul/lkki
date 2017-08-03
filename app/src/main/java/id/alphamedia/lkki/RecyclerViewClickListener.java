package id.alphamedia.lkki;

import android.view.View;

import id.alphamedia.lkki.adapter.MyListDataRecyclerViewAdapter;
import id.alphamedia.lkki.models.DataProspek;
import io.realm.OrderedRealmCollection;

public interface RecyclerViewClickListener
{
    public void recyclerViewListClicked(View v, int position);
    public void holderClicked(MyListDataRecyclerViewAdapter.ViewHolder v, int position, OrderedRealmCollection<DataProspek> data);
}