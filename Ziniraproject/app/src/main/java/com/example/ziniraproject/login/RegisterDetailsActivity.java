package com.example.ziniraproject.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ziniraproject.R;
import com.example.ziniraproject.until.ProfileManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class RegisterDetailsActivity extends AppCompatActivity {

    private EditText inputBirth;
    private RadioGroup radioGroup;
    private Button nextButton, skips;
    private ProgressBar progressBar;
    private TextView pleaseWait;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        init();
        setupDatePicker();
        setupNextButton();
        Auth();

//        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * initialization
     */
    private void init() {
        inputBirth = findViewById(R.id.inputBirth);
        radioGroup = findViewById(R.id.radioGroup);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.genderRequestLoadingProgressbar);
        pleaseWait = findViewById(R.id.pleaseWaitId);
        progressBar.setVisibility(View.GONE);
        pleaseWait.setVisibility(View.GONE);
    }


    /**
     * check if it current user and if not return back to login page
     */
    private void  Auth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(this, "You are not signed in. Please login first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    /**
     * calendar picker
     */
    private void setupDatePicker() {

        inputBirth.setOnClickListener(v -> showDatePicker());
    }

    /**
     * show date picker method
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18; // Default to 18 years ago
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            inputBirth.setText(date);
        }, year, month, day).show();
    }

    /**
     * next button
     */

    private void setupNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savePersonalDetails();


            }
        });
    }


    /**
     * save personal details date of brith
     * gender collection in firebase
     */

    private void savePersonalDetails() {
        // Get user input
        String dateOfBirth = inputBirth.getText().toString().trim();
        int selectedGenderId = radioGroup.getCheckedRadioButtonId();

        if (TextUtils.isEmpty(dateOfBirth)) {
            inputBirth.setError("Please enter your date of birth");
            return;
        }

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = ((RadioButton) findViewById(selectedGenderId)).getText().toString();


        progressBar.setVisibility(View.VISIBLE);
        pleaseWait.setVisibility(View.VISIBLE);

        new ProfileManager().savePersonalDetails(userId, dateOfBirth, gender, new ProfileManager.PersonalDetailsCallback() {

            // success and failure method
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
                pleaseWait.setVisibility(View.GONE);
                Intent intent = new Intent(RegisterDetailsActivity.this, WelcomeLanding.class);
                startActivity(intent);
//                startActivity(new Intent(RegisterDetailsActivity.this, WelcomeLanding.class));
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                pleaseWait.setVisibility(View.GONE);
                Toast.makeText(RegisterDetailsActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void escSkip(){
        skips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                pleaseWait.setVisibility(View.VISIBLE);

                startActivity(new Intent(RegisterDetailsActivity.this, WelcomeLanding.class));
                finish();
            }
        });
    }
}
