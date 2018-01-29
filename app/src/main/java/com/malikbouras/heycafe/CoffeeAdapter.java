package com.malikbouras.heycafe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malikbouras.heycafe.model.User;

import java.util.List;

/**
 *
 */

public class CoffeeAdapter extends RecyclerView.Adapter<CoffeeAdapter.MyViewHolder> {

    private List<User> userList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, coffeesNumber;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.userName);
            coffeesNumber = (TextView) view.findViewById(R.id.coffees);
        }
    }


    public CoffeeAdapter(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = userList.get(position);
        if (user != null) {
            holder.name.setText(user.getName());
            holder.coffeesNumber.setText(String.valueOf(user.getCoffees()));
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

