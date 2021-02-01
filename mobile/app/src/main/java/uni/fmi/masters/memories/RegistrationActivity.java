package uni.fmi.masters.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uni.fmi.masters.memories.entities.User;
import uni.fmi.masters.memories.services.local.DBHelper;

public class RegistrationActivity extends AppCompatActivity {

    DBHelper dbHelper;

    EditText usernameET;
    EditText passwordET;
    EditText repeatPasswordET;
    Button registerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new DBHelper(this);

        usernameET = findViewById(R.id.usernameRegistrationEditText);
        passwordET = findViewById(R.id.passwordRegistrationEditText);
        repeatPasswordET = findViewById(R.id.repeatPasswordRegistrationEditText);
        registerB = findViewById(R.id.registerRegistrationButton);

        registerB.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.registerRegistrationButton) {
                if (usernameET.getText().length() > 0
                        && passwordET.getText().length() > 0
                        && passwordET.getText().toString().equals(
                        repeatPasswordET.getText().toString())
                ) {
                    String username = usernameET.getText().toString();
                    String password = passwordET.getText().toString();

                    User user = new User();

                    user.setUsername(username);
                    user.setPassword(password);

                    if (!dbHelper.registerUser(user)) {
                        Toast.makeText(RegistrationActivity.this, "Грешка", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            "Моля проверете полетата отново",
                            Toast.LENGTH_LONG);
                    return;
                }
            }

            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };
}