package uni.fmi.masters.memories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import uni.fmi.masters.memories.services.local.DBHelper;

public class LoginActivity extends AppCompatActivity {

    DBHelper dbHelper;

    EditText usernameET;
    EditText passwordET;
    Button loginB;
    TextView registerTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        usernameET = findViewById(R.id.usernameEditText);
        passwordET = findViewById(R.id.passwordEditText);
        loginB = findViewById(R.id.loginButton);
        registerTV = findViewById(R.id.registerTextView);

        loginB.setOnClickListener(onClick);
        registerTV.setOnClickListener(onClick);
    }

    View.OnClickListener onClick = v -> {
        if (v.getId() == R.id.loginButton) {
            String usernameInput = usernameET.getText().toString();
            String passwordInput = passwordET.getText().toString();

            if (dbHelper.login(usernameInput, passwordInput)) {
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                intent.putExtra("user", usernameInput);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Добър опит", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.registerTextView) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }
    };
}