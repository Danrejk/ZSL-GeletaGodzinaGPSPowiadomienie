package com.example.apiandroidjava;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Handler handler = new Handler();
    private EditText imieTextBox, nazwiskoTextBox;
    private Button getButton, setButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String CHANNEL_ID = "LocationNotificationChannel";

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

        // Request notification permission for Android 13 and above
        requestNotificationPermission();

        textView = findViewById(R.id.godzina);
        imieTextBox = findViewById(R.id.imieTextBox);
        nazwiskoTextBox = findViewById(R.id.nazwiskoTextBox);
        getButton = findViewById(R.id.getButton);
        setButton = findViewById(R.id.setButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create Notification Channel for Android 8.0+ devices
        createNotificationChannel();

        handler.post(updateTimeRunnable);

        getButton.setOnClickListener(v -> {
            String imie = imieTextBox.getText().toString();
            String nazwisko = nazwiskoTextBox.getText().toString();

            SimpleDateFormat formatDaty = new SimpleDateFormat("dd-MM-yyyy");
            String data = formatDaty.format(Calendar.getInstance().getTime());

            String toastText = imie + " " + nazwisko + " " + data;
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_LONG).show();
        });

        setButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Create notification with current location
                            showLocationNotification(latitude, longitude);
                        } else {
                            // Handle case when location is null (if location is unavailable)
                            showLocationNotification(0.0, 0.0);
                        }
                    });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showLocationNotification(double latitude, double longitude) {
        // Create notification content
        String locationText = "Latitude: " + latitude + "\nLongitude: " + longitude;

        Notification powiadomienie = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.krzysiu) // You can set your own icon here
                .setContentTitle("Lokalizacja GPS")
                .setContentText(locationText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        // Get NotificationManager system service and show the notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(1, powiadomienie);
    }

    // cholera wie co to jest, ale potrzebne
    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Location Notifications";
            String description = "Channel for location-based notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the notification channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // pytanie o permy do powiadomień jak nie ma
    private void requestNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
    }

    // pytanie o permy do GPS
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setButton.performClick();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for notifications, you can show notifications
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
