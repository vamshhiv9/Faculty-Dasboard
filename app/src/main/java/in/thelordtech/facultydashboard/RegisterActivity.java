package in.thelordtech.facultydashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

import in.thelordtech.facultydashboard.helpers.Utils;

public class RegisterActivity extends AppCompatActivity {

    private EditText UserName, emailid, password,cpassword;
    private Button registerBtn;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserName = findViewById(R.id.userNAME);
        emailid = findViewById(R.id.userEMAIL);
        password = findViewById(R.id.userpassword);
        cpassword = findViewById(R.id.cpassword);
        registerBtn = findViewById(R.id.registerbtn);
        typeSpinner = findViewById(R.id.type);
        firebaseAuth = FirebaseAuth.getInstance();

        List<String> list = new ArrayList<>();
        list.add("Student");
        list.add("Faculty");
        list.add("Which kind are you?");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.addAll(list);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setSelection(adapter.getCount());

        progressDialog = new ProgressDialog(RegisterActivity.this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(RegisterActivity.this);
                if(UserName.getText().toString().isEmpty() ||emailid.getText().toString().isEmpty() || password.getText().toString().isEmpty() || cpassword.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Enter All Fields!", Toast.LENGTH_SHORT).show();
                }else if(UserName.getText().toString().length() < 5){
                    Toast.makeText(RegisterActivity.this, "Username Length should be greater than 5", Toast.LENGTH_SHORT).show();
                }else if(!(emailid.getText().toString().contains("@") || emailid.getText().toString().contains("."))){
                    Toast.makeText(RegisterActivity.this, "Email ID Invalid", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length() < 5){
                    Toast.makeText(RegisterActivity.this, "Password Length should be greater than 5", Toast.LENGTH_SHORT).show();
                }
                else if(!(password.getText().toString().equals(cpassword.getText().toString()))){
                    Toast.makeText(RegisterActivity.this, "Both passwords must be same!", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();
                    RegisterUser(emailid.getText().toString(), password.getText().toString());
                }

            }
        });
    }

    private void RegisterUser(String umail, String pass) {

        firebaseAuth.createUserWithEmailAndPassword(umail,pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();
                    profileBuilder.setDisplayName(UserName.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileBuilder.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    Utils.showToast(RegisterActivity.this,"User Registration Successful! Check your mail");
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Utils.showToast(RegisterActivity.this,e.getMessage());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Utils.showToast(RegisterActivity.this,e.getMessage());
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Utils.showToast(RegisterActivity.this,task.getException().getMessage());
                }
            }
        });
    }


}
