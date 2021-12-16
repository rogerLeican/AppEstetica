package br.com.appestetica.ui.events;

import static br.com.appestetica.commons.UtilityNamesDataBase.CLIENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.EVENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.appestetica.databinding.ActivityEventsFormBinding;
import br.com.appestetica.ui.events.model.Event;

public class EventsFormActivity extends AppCompatActivity {


    private EditText editTextName;
    private EditText editTexTelephone;
    private EditText editTextEmail;
    private Event event;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    ActivityEventsFormBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventsFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);

        Button btnSave = binding.btnSaveClient;

        editTextName = binding.etClientName;
        editTexTelephone = binding.etClientPhone;
        editTextEmail = binding.etClientEmail;
        String action = getIntent().getStringExtra("action");

        if (action.equals("update")) {
            loadForm();
        }
        btnSave.setOnClickListener(view -> {
            if (action.equals("insert")) {
                save();
            } else if (action.equals("update")) {
                update();
            }
        });
    }

    private void update() {
        String eventId = getIntent().getStringExtra("idClient");
        String clientId = editTextName.getText().toString();
        String productId = editTexTelephone.getText().toString();
        String professionalI = editTextEmail.getText().toString();
        event.setEventId(eventId);
        event.setClientId(clientId);
        event.setProductId(productId);
        event.setProfessionalId(professionalI);

        reference.child(CLIENTS_COLLECTION_NAME).child(event.getEventId())
                .setValue(event);
        finish();
    }

    private void save() {
//        todo refatorar o txtView
        String clientId = editTextName.getText().toString();
        String productId = editTexTelephone.getText().toString();
        String professionalI = editTextEmail.getText().toString();
        if (clientId.isEmpty() || productId.isEmpty() || professionalI.isEmpty()) {
            Toast.makeText(this, "You must fill in all fields!", Toast.LENGTH_LONG).show();
        } else {

            event = new Event();
            event.setClientId(clientId);
            event.setProductId(productId);
            event.setProfessionalId(professionalI);
            reference.child(EVENTS_COLLECTION_NAME).push().setValue(event);
            Toast.makeText(this,
                    "The event was successfully registered",
                    Toast.LENGTH_LONG)
                    .show();
            editTextName.setText("");
            editTexTelephone.setText("");
            editTextEmail.setText("");
        }
    }

    //        todo refatorar o txtView
    private void loadForm() {
        event = Event.builder()
                .eventId(getIntent().getStringExtra("idEvent"))
                .clientId(getIntent().getStringExtra("idClient"))
                .productId(getIntent().getStringExtra("idProduct"))
                .professionalId(getIntent().getStringExtra("idProfessional"))
                .build();
        editTextName.setText(event.getEventId());
        editTexTelephone.setText(event.getClientId());
        editTextEmail.setText(event.getProductId());
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }
}