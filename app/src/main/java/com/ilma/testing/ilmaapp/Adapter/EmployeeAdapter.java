package com.ilma.testing.ilmaapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.ilma.testing.ilmaapp.Models.User;
import com.ilma.testing.ilmaapp.R;

import java.util.List;

/**
 * Created by fazal on 11/14/2017.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    List<User> listuser;

    public EmployeeAdapter(List<User> listuser) {
        this.listuser = listuser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.employee_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(listuser==null) return;

        if(listuser.size()==0) return;

        holder.user_name.setText(listuser.get(position).getFullname());
        holder.user_id.setText(listuser.get(position).getEmail());
        holder.gender_tv.setText(listuser.get(position).getGender());
    }

    @Override
    public int getItemCount() {
        return listuser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView user_name;
        TextView user_id;
        TextView gender_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.name_tv);
            user_id = itemView.findViewById(R.id.email_tv);
            gender_tv = itemView.findViewById(R.id.gender_tv);
        }
    }
}
