package in.thelordtech.facultydashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ViewSchedule extends AppCompatActivity {
    ArrayList<String> NotesTitle = new ArrayList<>();
    SimpleAdapter adapter;
    ListView notesList;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference fnotesDataBaseReference;
    private ProgressDialog progressDialog;
    private TextView content;
    ArrayList<Map<String,String>> item = new ArrayList<Map<String,String>>();
    Map<String,String> inp = new HashMap<String,String>();
    private String arr[]=new String[500];
    String noteID;
    String t,h,mi,d,m,y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...\nLoading your Schedule...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        notesList = (ListView) findViewById(R.id.notes_list_view);
        //noteTime = (TextView)findViewById(R.id.noteDATE);
        // content = (TextView)findViewById(R.id.adapterid1);
        database = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Toast.makeText(this, "User: "+ Objects.requireNonNull(fAuth.getCurrentUser()).getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        //String key = database.getReference("Notes").getKey();
        //fnotesDataBaseReference = database.getReference("Notes").child(key);
        fnotesDataBaseReference = database.getReference("Schedule").child(fAuth.getCurrentUser().getUid());//goes upto
        //final String keyy =  fnotesDataBaseReference.getKey();

        fnotesDataBaseReference.addValueEventListener(new ValueEventListener() { //error line
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String count = String.valueOf(dataSnapshot.getChildrenCount());
                System.out.println("qwerty count: "+count);
                int count1 = Integer.parseInt(count);

                if (count1 == 0){

                    Intent intent = new Intent(ViewSchedule.this,RequestAppointmentActivity.class);
                    startActivity(intent);
                    finish();
//                    progressDialog.dismiss();

                }else{
                    Load(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("ERROR!");
                Toast.makeText(getApplicationContext(), "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    public void Load(final DataSnapshot dataSnapshot){
        Calendar calobj = Calendar.getInstance();
        Date da = new Date();
        y = calobj.get(Calendar.YEAR)+"";
        int tem;
        Long tor;
        tem = calobj.get(Calendar.MONTH);
        tem = (tem+1)%12;
        tor = Long.parseLong(y.trim())*100000000+tem*1000000+calobj.get(Calendar.DAY_OF_MONTH)*10000+(calobj.get(Calendar.HOUR_OF_DAY)%24)*100+calobj.get(Calendar.MINUTE);
        Log.d("NowT",tor+"");
        if(tem<9){
            m = "0"+tem+"";
        }
        else{
            m = tem+"";
        }
        d = (calobj.get(Calendar.DAY_OF_MONTH)<10)?'0'+calobj.get(Calendar.DAY_OF_MONTH)+"":calobj.get(Calendar.DAY_OF_MONTH)+"";
        h = ((calobj.get(Calendar.HOUR_OF_DAY)%24)<10)?'0'+calobj.get(calobj.get(Calendar.HOUR_OF_DAY)%24)+"":calobj.get(Calendar.HOUR_OF_DAY)%24+"";
        mi = (calobj.get(Calendar.MINUTE)<10)?'0'+calobj.get(Calendar.MINUTE)+"":calobj.get(Calendar.MINUTE)+"";
        t = y+m+d+h+mi;
        Log.d("Timegh",y+" "+m+' '+d);
        item.clear();
        adapter = new SimpleAdapter(this,item,android.R.layout.simple_list_item_2,new String[]{"Title","Date"},new int[]{android.R.id.text1,android.R.id.text2});
       try {
            int i = 0;
            Map<Long,DataSnapshot>input = new HashMap<Long,DataSnapshot>();

            List<Long> inpu = new ArrayList<Long>();
            for (DataSnapshot ds: dataSnapshot.getChildren()){
                if(ds!=null){
                Long temp = Long.parseLong(String.valueOf(ds.child("Sort").getValue()));
                Log.d("sorto",temp+"");
                input.put(temp,ds);
                inpu.add(temp);}
            }
            Collections.sort(inpu);
            for (Long lo:inpu) {
                DataSnapshot ds = input.get(lo);

//                Log.d("elei",ob+"");
//                DataSnapshot ds = input.get(ob);
                inp = new HashMap<String, String>();
//               int sor =Integer.parseInt(ds.child("Sort").getValue().toString());
//                Long tor = Integer.parseInt(t.trim());
//                Log.d("Sort",sor+"");
//

//              if(Integer.parseInt(ds.child("Sort").getValue().toString())>=Integer.parseInt(t)){
                String temp = ds.child("Date").getValue() + " " + ds.child("Time").getValue() + " - " + ds.child("EndTime").getValue();
                inp.put("Title", String.valueOf(ds.child("Content").getValue()));
                String so = String.valueOf(ds.child("Sort").getValue());
                inp.put("Date", temp);
                Long sor = Long.parseLong(so);
                Log.d("Sort", sor + "");
                if (sor >= tor) {
                    item.add(inp);

                    arr[i] = String.valueOf(ds.child("Noteid").getValue());
                    i++;
                }
            }

            progressDialog.dismiss();

        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "DataBase Error\nPlease Try Again After Sometime!", Toast.LENGTH_SHORT).show();
        }
        progressDialog.dismiss();
        notesList.setAdapter(adapter);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                noteID =arr[i];
                // noteID = idContainer.get(i);
                System.out.println("qwerty i: "+i);


                String d = String.valueOf(dataSnapshot.child(noteID).child("Date").getValue());
                String st= String.valueOf(dataSnapshot.child(noteID).child("Time").getValue());
                String et = String.valueOf(dataSnapshot.child(noteID).child("EndTime").getValue());
                String content = String.valueOf(dataSnapshot.child(noteID).child("Content").getValue());

                //Toast.makeText(DescriptionMaker.this, ""+noteID, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ExistSchedule.class);
                intent.putExtra("datet",d);
                intent.putExtra("st",st);
                intent.putExtra("et",et);
                intent.putExtra("conten",content);
                intent.putExtra("noteI",noteID);
                startActivity(intent);
            }
        });

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                noteID = arr[i];
                //noteID = idContainer.get(i);
                registerForContextMenu(adapterView);
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.schedul, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new:
                Intent noteintent = new Intent(getApplicationContext(),RequestAppointmentActivity.class);
                startActivity(noteintent);
                return true;
            case R.id.view_hist:
                Intent notintent = new Intent(getApplicationContext(),History.class);
                startActivity(notintent);
                return true;
            case R.id.refres:
                if(isOnline()){
                    Intent refreshintent = new Intent(getApplicationContext(),ViewSchedule.class);
                    startActivity(refreshintent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.add_new_notes, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//
//        switch (item.getItemId()){
//
//            case R.id.addnotes:
//                Intent noteintent = new Intent(getApplicationContext(),RequestAppointmentActivity.class);
//                startActivity(noteintent);
//                break;
//
//            case R.id.refreshh:
//                if(isOnline()){
//                    Intent refreshintent = new Intent(getApplicationContext(),ViewSchedule.class);
//                    startActivity(refreshintent);
//                    finish();
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
//                }
//
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//        return super.onOptionsItemSelected(item);
//
//    }
//
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.delete_note_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_menu:
                if (isOnline()){
                    deletenoteonMenuClick(noteID);
                }
                else {
                    Toast.makeText(getApplicationContext() , "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }

            default: return super.onContextItemSelected(item);
        }
    }

    private void deletenoteonMenuClick(String noteID) {
        fnotesDataBaseReference.child(noteID).removeValue();
        Toast.makeText(this, "Schedule Deleted Sucessfully", Toast.LENGTH_SHORT).show();
    }

    public boolean isOnline() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connManager != null;
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
