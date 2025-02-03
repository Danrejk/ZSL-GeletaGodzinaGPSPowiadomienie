package com.example.apiandroidjava;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Handler handler = new Handler();
    private EditText imieTextBox, nazwiskoTextBox;
    private Button getButton;

    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            int godzina = calendar.get(Calendar.HOUR_OF_DAY);
            int minuta = calendar.get(Calendar.MINUTE);
            int sekunda = calendar.get(Calendar.SECOND);

            // Format the time as HH:mm:ss
            String godzinaText = String.format("%02d:%02d:%02d", godzina, minuta, sekunda);

            textView.setText(godzinaText);
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.godzina);
        imieTextBox = findViewById(R.id.imieTextBox);
        nazwiskoTextBox = findViewById(R.id.nazwiskoTextBox);
        getButton = findViewById(R.id.getButton);

        handler.post(updateTimeRunnable);

        getButton.setOnClickListener(v -> {
            String imie = imieTextBox.getText().toString();
            String nazwisko = nazwiskoTextBox.getText().toString();

            SimpleDateFormat formatDaty = new SimpleDateFormat("dd-MM-yyyy");
            String data = formatDaty.format(Calendar.getInstance().getTime());

            String toastText = imie + " " + nazwisko+ " " + data;
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_LONG).show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }
}
