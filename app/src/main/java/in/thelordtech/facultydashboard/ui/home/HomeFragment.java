package in.thelordtech.facultydashboard.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import in.thelordtech.facultydashboard.NotesListActivity;
import in.thelordtech.facultydashboard.R;
import in.thelordtech.facultydashboard.ViewSchedule;

public class HomeFragment extends Fragment {

    private Button myProfile,myTimeTable,myNotes,myAppointments;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView welcome = root.findViewById(R.id.WelcomeText);
        myProfile = root.findViewById(R.id.myProfie);
        myAppointments = root.findViewById(R.id.myAppointments);
        myTimeTable = root.findViewById(R.id.myTimeTable);
        myNotes = root.findViewById(R.id.myNotes);
        welcome.setText(String.format("Welcome %s", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName()+"!"));


        myNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NotesListActivity.class);
                startActivity(i);
            }
        });

        myAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),ViewSchedule.class);
                startActivity(i);
            }
        });


        return root;
    }
}