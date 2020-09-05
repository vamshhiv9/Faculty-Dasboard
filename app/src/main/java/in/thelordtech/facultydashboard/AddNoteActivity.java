package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {

    private EditText notesTitle;
    private EditText notesDescription;
    private FirebaseAuth fAuth;
    private DatabaseReference fnotesDataBase;
    private Button savebtnn;
    private Button closeADDnote;
    private  int checkforclick;
    ProgressDialog progressDialog;

    String title;
    String notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        getSupportActionBar().hide();

        notesTitle = (EditText) findViewById(R.id.video_title_edit_text);
        notesDescription = (EditText) findViewById(R.id.description_edit_text);
        savebtnn = (Button)findViewById(R.id.saveee);
        closeADDnote = (Button) findViewById(R.id.close_addnote_activity);

        fAuth = FirebaseAuth.getInstance();
        fnotesDataBase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());


        savebtnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = notesTitle.getText().toString().trim();
                notes = notesDescription.getText().toString().trim();

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(notes)){

                    if(isOnline()){
                        createNotes();
                    }
                    else {
                        Snackbar.make(view,"Please Connect to Internet",Snackbar.LENGTH_SHORT).show();
                    }

                }
                else{
                    Snackbar.make(view,"Enter All Fields",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        closeADDnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                closeAddNote();

            }
        });
    }


    @Override
    public void onBackPressed(){
        closeAddNote();
    }

    private void createNotes() {
        checkforclick++;

        if (fAuth.getCurrentUser() != null) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait..Saving into DataBase..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            try {
                // FirebaseDatabase database = FirebaseDatabase.getInstance();
                //String id = fnotesDataBase.push().getKey();
                final DatabaseReference newNoteref = fnotesDataBase.push();

                final Map notemap = new HashMap();
                notemap.put("Title", title);
                notemap.put("Content", notes);
                notemap.put("Noteid",newNoteref.getKey());
                notemap.put("timestamp", ServerValue.TIMESTAMP);

                Thread makeThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteref.setValue(notemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    checkforclick++;
                                    progressDialog.dismiss();
                                    Toast.makeText(AddNoteActivity.this, "Notes Added Sucessfully", Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(AddNoteActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    //   Toast.makeText(add_note_activity.this, "Error : "+fAuth.getCurrentUser(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                makeThread.start();
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Not Signed in", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeAddNote(){

        title = notesTitle.getText().toString().trim();
        notes = notesDescription.getText().toString().trim();

        if (checkforclick == 0 && (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(notes))){

            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddNoteActivity.this);
            builder1.setTitle("Exit without saving?");
            builder1.setMessage("Notes will not be Saved.\nClick Save to save your work!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if(isOnline()){
                                createNotes();
                            }
                            else {
                                Toast.makeText(AddNoteActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        }
        else {

            finish();
        }


    }
    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

}
