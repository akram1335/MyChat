package com.example.myapplication2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class BlogViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public BlogViewHolder(View itemView) {
        super(itemView);

        mView = itemView;

    }

    public void setDisplayName(String name){

        TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);

    }
}
