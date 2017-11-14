package com.ilma.testing.ilmaapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ilma.testing.ilmaapp.Adapter.EmployeeAdapter;
import com.ilma.testing.ilmaapp.Models.User;
import com.ilma.testing.ilmaapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by fazal on 11/14/2017.
 */

public class EmployeeList extends Fragment {

    @InjectView(R.id.rv_employeelist)
    RecyclerView rv_employeelist;

    @InjectView(R.id.progress_bar)
    ProgressBar mProgressbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        ButterKnife.inject(this, view);
        SetupRecyclerView();
        return view;
    }

    private void SetupRecyclerView() {



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_employeelist.setLayoutManager(layoutManager);

        //for divider in recyler view item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv_employeelist.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.divider_rv_post)
        );
        rv_employeelist.addItemDecoration(dividerItemDecoration);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        rv_employeelist.setHasFixedSize(true);

        getEmployeeList();
    }

    public static EmployeeList newInstance(){
        return new EmployeeList();
    }

    private void getEmployeeList() {
        new AsyncTask<Void, Void, List<User>>(){

            @Override
            protected List<User> doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://tippiest-reactors.000webhostapp.com/PKT/getAllUser.php")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    //Log.d("getlist", response.body().string());
                    List<User> list = new ArrayList<User>();
                    JSONArray jsonarray_root = new JSONArray(response.body().string());
                    for(int i=0;i<jsonarray_root.length();i++){
                        JSONObject jsonObject = jsonarray_root.getJSONObject(i);
                        String FullName = jsonObject.getString("FullName");
                        String Email = jsonObject.getString("Email");
                        String Gender = jsonObject.getString("Gender");
                        list.add(new User(FullName, Email, Gender));
                    }

                    return list;
                }catch (Exception e){

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                super.onPostExecute(users);
                mProgressbar.setVisibility(View.GONE);
                if(users!=null && users.size()>0){
                    EmployeeAdapter adapter = new EmployeeAdapter(users);
                    rv_employeelist.setAdapter(adapter);
                }
            }
        }.execute();
    }
}
