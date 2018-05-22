package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.hs.emirim.uuuuri.ohdormitory.R;

public class ToggleSettingsActivity extends BaseActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mSettingsRef;
    private DatabaseReference mInputSettingsRef;

    private ValueEventListener mSettingsListener;


    Switch cleanSwitch;
    Switch laundrySwitch;
    Switch sleepOutSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_settings);
        cleanSwitch = findViewById(R.id.clean);
        laundrySwitch = findViewById(R.id.laundry);
        sleepOutSwitch = findViewById(R.id.sleep_out);

        mDatabase = FirebaseDatabase.getInstance();
        mInputSettingsRef = mDatabase.getReference();
        setBeforeSettings();
        cleanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
//                mInputSettingsRef.child("alarm").child(getUid()).child("clean").setValue(isChecked);
            }
        });

        laundrySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
//                mInputSettingsRef.child("alarm").child(getUid()).child("laundry").setValue(isChecked);
            }
        });

        sleepOutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    Toast.makeText(getApplicationContext(), "ON", Toast.LENGTH_SHORT).show();
//                mInputSettingsRef.child("alarm").child(getUid()).child("sleep_out").setValue(isChecked);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mSettingsListener != null) {
            mSettingsRef.removeEventListener(mSettingsListener);
        }
    }

    private void setBeforeSettings() {
//        showProgressDialog();
//        mSettingsRef = mDatabase.getReference("alarm/"+getUid()); // get database reference
//
//        mSettingsListener = mSettingsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot uid) {
//                boolean clean = true;
//                boolean laundry = true;
//                boolean sleepOut = true;
//                if(uid.child("clean").getValue()!=null)
//                    clean =(boolean) uid.child("clean").getValue();
//
//                if(uid.child("laundry").getValue()!= null)
//                       laundry = (boolean)uid.child("laundry").getValue() ;
//
//                if(uid.child("sleep_out").getValue()!= null)
//                    sleepOut = (boolean) uid.child("sleep_out").getValue();
//
//                cleanSwitch.setChecked(clean);
//                laundrySwitch.setChecked(laundry);
//                sleepOutSwitch.setChecked(sleepOut);
//
//                hideProgressDialog();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                cleanSwitch.setChecked(true);
//                laundrySwitch.setChecked(true);
//                sleepOutSwitch.setChecked(true);
//                hideProgressDialog();
//            }
//        });


    }
}
