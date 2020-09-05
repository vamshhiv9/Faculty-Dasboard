package in.thelordtech.facultydashboard;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExistSchedule extends AppCompatActivity {

    private EditText dater, st,et;
    private EditText content;
    private Button updateNote, deleteNote, closeNote;
    private  String noteID;
    private DatabaseReference fnotesDataBaseReference;
    private Calendar fromDate = Calendar.getInstance();
    private Calendar fromTime = Calendar.getInstance();
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    String con;
    String t,h,mi,d,m,y;

    String dat, tim;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exist_schedule);
        Objects.requireNonNull(getSupportActionBar()).hide();
        noteID = getIntent().getStringExtra("noteI");
        dater = (EditText)findViewById(R.id.view_date);
        dater.setText(getIntent().getStringExtra("datet"));
        st = (EditText)findViewById(R.id.view_starttime);
        st.setText(getIntent().getStringExtra("st"));
        et = (EditText)findViewById(R.id.view_endtime);
        et.setText(getIntent().getStringExtra("et"));
        content = (EditText) findViewById(R.id.view_desc);
        content.setText(getIntent().getStringExtra("conten"));
        updateNote = (Button)findViewById(R.id.updatebtn);
        deleteNote =(Button)findViewById(R.id.deletebtn);
        closeNote = (Button)findViewById(R.id.closebtn);

        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        //String key = database.getReference("Notes").getKey();
        //fnotesDataBaseReference = database.getReference("Notes").child(key);
        fnotesDataBaseReference = database.getReference("Schedule").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
        updateNote.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String tTitle=dater.getText().toString() ;
                String tContent = content.getText().toString();
                String tst = st.getText().toString();
                String tet = et.getText().toString();
                if((!TextUtils.isEmpty(tTitle)) && (!TextUtils.isEmpty(tContent))&& (!TextUtils.isEmpty(tst))&& (!TextUtils.isEmpty(tet))){

                    if (isOnline()){
                        updateNoteinFirebase(tTitle,tContent,tst,tet);
                    }
                    else {
                        Snackbar.make(view,"Please Connect to Internet",Snackbar.LENGTH_SHORT).show();
                    }
                }
                else {
                    Snackbar.make(view,"Enter all fields",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOnline()){
                    deleteNotefromDatabase(noteID);
                }
                else {
                    Snackbar.make(view,"Please Connect to Internet",Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        closeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void setFromDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(ExistSchedule.this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (dayOfMonth<10)
                    d = 0+""+dayOfMonth+"";
                else
                    d = dayOfMonth+"";
                if(month<10)
                    m = 0+""+(month+1)+"";
                else
                    m = (month+1)+"";
                y = year+"";
                dater.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                fromDate.set(year, month, dayOfMonth);

            }
        }, fromDate.get(Calendar.YEAR),
                fromDate.get(Calendar.MONTH),
                fromDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void setToTime(View view) {

        int hour = fromTime.get(Calendar.HOUR_OF_DAY);
        int minute = fromTime.get(Calendar.MINUTE);
//        System.out.println(df.format(calobj.getTime()));
        TimePickerDialog timePickerDialog = new TimePickerDialog(ExistSchedule.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hour, int min) {
                fromTime.set(Calendar.HOUR_OF_DAY, hour);
                fromTime.set(Calendar.MINUTE, min);
                String timeSet = "";
                if(hour<10)
                    h = 0+""+hour;
                else
                    h = hour+"";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String minutes;
                if (min < 10)
                    minutes = "0" + min;
                else
                    minutes = String.valueOf(min);
                mi = minutes;
                et.setText(hour + ":" + minutes + " " + timeSet);
            }
        }, hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    public void setFromTime(View view) {
        int hour = fromTime.get(Calendar.HOUR_OF_DAY);
        int minute = fromTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ExistSchedule.this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hour, int min) {
                fromTime.set(Calendar.HOUR_OF_DAY, hour);
                fromTime.set(Calendar.MINUTE, min);
                String timeSet = "";
//                if(hour<10)
//                    h = 0+""+hour;
//                else
//                    h = hour+"";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12) {
                    timeSet = "PM";
                } else {
                    timeSet = "AM";
                }

                String minutes;
                if (min < 10)
                    minutes = "0" + min;
                else
                    minutes = String.valueOf(min);
//                mi = minutes;
                st.setText(hour + ":" + minutes + " " + timeSet);
            }
        }, hour,
                minute,
                false
        );
        timePickerDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateNoteinFirebase(String tTitle, String tContent, String tst, String tet) {

        dat = dater.getText().toString().trim();
        tim = st.getText().toString().trim();

        t = y+m+d+h+mi;
        Map updatemap = new HashMap();

        updatemap.put("Content", content.getText().toString());
        updatemap.put("Date", dat);
        updatemap.put("Time", tim);
        updatemap.put("EndTime",et.getText().toString());
        updatemap.put("timestamp", ServerValue.TIMESTAMP);

        fnotesDataBaseReference.child(noteID).updateChildren(updatemap);
        Toast.makeText(this, "Note Updated Sucessfully", Toast.LENGTH_SHORT).show();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void deleteNotefromDatabase(String noteID) {

        fnotesDataBaseReference.child(noteID).removeValue();
        Toast.makeText(this, "Note Deleted Sucessfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connManager != null) {
            networkInfo = connManager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }
}
