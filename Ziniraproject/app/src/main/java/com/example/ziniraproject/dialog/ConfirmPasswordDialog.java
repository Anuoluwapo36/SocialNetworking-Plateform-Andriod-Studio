package com.example.ziniraproject.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ziniraproject.R;

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";

    private Button confirmSave, cancelSave;
    //global variables

    EditText mPassword;


    public interface OnConfirmPasswordListener{
        void  onConfirmPassword(String password);

    }
    OnConfirmPasswordListener monConfirmPasswordListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_confrim_password,container, false);
        mPassword = view.findViewById(R.id.confirm_password);
        Log.d(TAG, "onCreateView: started");

        confirmSave = view.findViewById(R.id.dialog_confirm);
        confirmSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: capture password and confirm. ");
                String password = mPassword.getText().toString().trim();
                if (!password.isEmpty()) {
                    monConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You must enter a password", Toast.LENGTH_SHORT).show();
                }



            }
        });
        cancelSave= view.findViewById(R.id.dialog_cancel);
        cancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
        try {
            monConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (Exception e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
            throw new RuntimeException(e);
        }
    }
}
