package com.ilma.testing.ilmaapp.Activities;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.ilma.testing.ilmaapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendRequestActivity extends AppCompatActivity {

    private Calendar myCalendar;

    @InjectView(R.id.start_date)
    EditText startdate_et;

    @InjectView(R.id.end_date)
    EditText enddate_et;

    @InjectView(R.id.reason_et)
    EditText reason_et;

    private DatePickerDialog.OnDateSetListener date_start;
    private DatePickerDialog.OnDateSetListener date_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        ButterKnife.inject(this);

        myCalendar = Calendar.getInstance();

        date_start = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabeStartdate();

            }

        };

        date_end = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabeEnddate();

            }

        };
    }

    private void updateLabeStartdate() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        startdate_et.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabeEnddate() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        enddate_et.setText(sdf.format(myCalendar.getTime()));
    }

    public void startdateClick(View view) {
        new DatePickerDialog(this, date_start, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void enddateClick(View view) {
        new DatePickerDialog(this, date_end, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void sendRequest() {
        final String startDate = startdate_et.getText().toString();
        final String endDate = enddate_et.getText().toString();
        final String reason = reason_et.getText().toString();
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
       /* RequestBody requestbody = new FormBody.Builder()
                .add("token",token)
                .add("Name","")
                .build();*/
                //Log.d("UserID",utils.getdata("Userid"));
                String msg = getIntent().getStringExtra("Username") +
                        " send you a leave request for the purpose of " + reason + " From: " + startDate +
                        " To: " +  endDate;
                Request request = new Request.Builder()
                        .url("https://tippiest-reactors.000webhostapp.com/PKT/send_notification.php?" +
                                "message=" + msg + "&title=Leave Request&startDate="+ startDate +
                                "endDate=" +endDate)
                        .build();

                try{
                    Response response = client.newCall(request).execute();
                    Log.d("token_send",response.body().string());

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    public void sendClick(View view) {
        sendRequest();
    }
}
