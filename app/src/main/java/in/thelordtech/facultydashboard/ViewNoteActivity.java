package in.thelordtech.facultydashboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewNoteActivity extends AppCompatActivity {

    private EditText title;
    private EditText content;
    private Button updateNote, deleteNote, closeNote;
    private  String noteID;
    private DatabaseReference fnotesDataBaseReference;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Objects.requireNonNull(getSupportActionBar()).hide();

        noteID = getIntent().getStringExtra("noteID");

        title = (EditText)findViewById(R.id.view_video_title_edit_text);
        title.setText(getIntent().getStringExtra("title"));
        content = (EditText) findViewById(R.id.view_description_edit_text);
        content.setText(getIntent().getStringExtra("content"));
        updateNote = (Button)findViewById(R.id.updatebtn);
        deleteNote =(Button)findViewById(R.id.deletebtn);
        closeNote = (Button)findViewById(R.id.closebtn);

        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fnotesDataBaseReference = database.getReference("Notes").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());



        updateNote.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                String tTitle=title.getText().toString() ;
                String tContent = content.getText().toString();

                if((!TextUtils.isEmpty(tTitle)) && (!TextUtils.isEmpty(tContent))){

                    if (isOnline()){
                        updateNoteinFirebase(tTitle,tContent);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateNoteinFirebase(String tTitle, String tContent) {


        Map updatemap = new HashMap();
        updatemap.put("Title",tTitle);
        updatemap.put("Content",tContent);
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
