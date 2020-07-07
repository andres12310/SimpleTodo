package com.andres.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//adapter is responsible for taking the data at a particular position and putting it into a viewholder.
//displays data from the model into a row int the recycler view
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public interface OnClickListener{
        void onItemClicked(int position);
    }
    public interface OnLongClickListener{
        //we nee to pass position so that the class implementing this interface method, which in our case is MainActivity, it needs to know the position of where we did the long press, so it can notify the adapter at which the we should delete the item.
        void onItemLongClicked(int position);
    }

    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener;

    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener, OnClickListener clickListener){
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    //responsible for creating each view
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //use layout inflator to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        //wrap it inside a view holder and return it
        return new ViewHolder(todoView);
    }

    @Override
    //takes data at a particular position and putting it into a viewholder
    //responsible for binding data to a particular view holder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //grab the item at the position
        String item = items.get(position);
        //bind the item into the specified view holder
        holder.bind(item);
    }

    @Override
    //the number of items available in the data
    //tells the recrycler view how many items are int the list
    public int getItemCount() {
        return items.size();
    }

    //container to provide easy access to views that represent each row of the list
     class ViewHolder extends RecyclerView.ViewHolder{

        //we need to get the view that we made and we use a built in view called
        // android.R.layout.simple_list_item_1 which consists of a single TextView
        // elelment with the ID "text1". We will use this ID in the ViewHolder
        // constructor to assign the view to the variable tvItem.
        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //because the view we used is a built in android view, we have to say "android.R..." instead of just "R...."
            tvItem = itemView.findViewById(android.R.id.text1);
        }

        //update the view inside of the view holder with this data
        public void bind(String item) {
            tvItem.setText(item);
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //remove the item from the cycler view. The problem is that we can't actually talk to the adapter that's behind the recycler view at this point. What we have to do is communciate that this particular item was click and we need to communciate that back to MainActivity
                    //we need to pass info from MainActivity into ItemsAdapter
                    //we do that by defining an interface in ItemsAdapter that the MainActivity will implement.

                    //this method isn't actually removing the item from the recycler view. what we do now is that when android notifies us that this item was long pressed, we are notifying the listener which position was long pressed.
                    //notify the listener which position was long pressed.
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
