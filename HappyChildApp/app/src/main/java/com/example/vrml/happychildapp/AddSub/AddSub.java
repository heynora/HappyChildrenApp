package com.example.vrml.happychildapp.AddSub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vrml.happychildapp.Homonym.Homonym;
import com.example.vrml.happychildapp.Jennifer_Code.FireBaseDataBaseTool;
import com.example.vrml.happychildapp.R;
import com.example.vrml.happychildapp.StarGrading.StarGrading;
import com.example.vrml.happychildapp.menu_choose;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nora on 2017/11/6.
 */

public class AddSub extends AppCompatActivity {
    TextView num1, num2, ans;
    int n1, n2, answer;
    int bingo = 0;//答對題數
    int count = 0;//總題數
    Button add, sub;
    Bundle bundle;
    String temp2;
    DisplayMetrics metrics = new DisplayMetrics();
    AlertDialog isExit;
    List<String[]> content = new ArrayList<String[]>();
    private int index = 0;
    private long startTime, timeup, totaltime;
    Integer[] array = new Integer[]{0, 1, 2};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sub);

        getSupportActionBar().hide(); //隱藏標題
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        DialogSet();
        ImageView imageView = (ImageView) findViewById(R.id.equalsimage);

        num1 = (TextView) findViewById(R.id.Num1);
        num2 = (TextView) findViewById(R.id.Num2);
        ans = (TextView) findViewById(R.id.ansNum);

        add = (Button) findViewById(R.id.Add);
        sub = (Button) findViewById(R.id.Sub);
        add.setOnClickListener(click);
        sub.setOnClickListener(click);
        size();
        bundle = this.getIntent().getExtras();
        getdataFromFirebase();
        startTime = System.currentTimeMillis();

    }

    private void getdataFromFirebase() {

        DatabaseReference reference_contacts = FirebaseDatabase.getInstance().getReference("Teach");
        reference_contacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String Subject = bundle.getString("Subject");
                String Mode = bundle.getString("Mode");
                String Unit = bundle.getString("Unit");
                String Lesson = bundle.getString("Lesson");
                dataSnapshot = dataSnapshot.child(Subject).child(Mode).child(Unit).child(Lesson);
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    temp2 = (String) temp.getValue();
                    content.add(new String[]{temp2.split(",")[0], temp2.split(",")[1], temp2.split(",")[2]});
                    count++;
                }
                Log.e("DEBUG", "AddSub Line 76 count:" + count);
                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void size() {
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        num1.setTextSize(metrics.widthPixels / 30);
        num2.setTextSize(metrics.widthPixels / 30);
        ans.setTextSize(metrics.widthPixels / 30);
        add.setTextSize(metrics.widthPixels / 30);
        sub.setTextSize(metrics.widthPixels / 30);
    }

    private void setData() {
        num1.setText(content.get(index)[array[0]]);
        num2.setText(content.get(index)[array[1]]);
        ans.setText(content.get(index)[array[2]]);
    }

    private void toInteger() {
        n1 = Integer.parseInt(content.get(index)[array[0]]);
        n2 = Integer.parseInt(content.get(index)[array[1]]);
        answer = Integer.parseInt(content.get(index++)[array[2]]);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toInteger();
            Button button = (Button) v;
            String string = button.getText().toString();
            if (add.getText().equals(string)) {
                if (n1 + n2 == answer) {
                    bingo++;
                }
            } else if (sub.getText().equals(string)) {
                if (n1 - n2 == answer) {
                    bingo++;
                }
            }

            if (index < count) {
                setData();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                String User = sharedPreferences.getString("Name", "");
                timeup = System.currentTimeMillis();
                totaltime = (timeup - startTime) / 1000;
                ShowMessage("答對了" + bingo + "題\n" + "共花了" + totaltime + "秒");
                int star = StarGrading.getStar(bundle.getString("Unit"), count, bingo);
                FireBaseDataBaseTool.SendStudyRecord(bundle.getString("Unit"), User, "答對了" + bingo + "題," + "共花了" + totaltime + "秒,Star:" + star);
            }
        }
    };

    private void ShowMessage(String str) {
        new AlertDialog.Builder(this).setMessage(str)
                .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddSub.this.finish();
                    }
                }).setCancelable(false).show();

    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    startActivity(new Intent(AddSub.this, menu_choose.class));
                    AddSub.this.finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:

                    break;
                default:
                    break;
            }
        }
    };

    private void DialogSet() {
        isExit = new AlertDialog.Builder(this)
                .setTitle("離開")
                .setMessage("確定要退出嗎?")
                .setPositiveButton("Yes", listener)
                .setNegativeButton("No", listener)
                .setCancelable(false)
                .create();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isExit.show();
        }
        return true;
    }
}


