package br.com.appestetica.ui.clients;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.appestetica.databinding.ClientActivityFormBinding;
import br.com.appestetica.ui.clients.model.Client;

public class ClientFormActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTexTelephone;
    private EditText editTextEmail;
    private Client client;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private static final String CLIENTS_COLLECTION_NAME = "clients";

    ClientActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ClientActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/aesthetic");

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

    private void save() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        String email = editTextEmail.getText().toString();
        client = new Client();
        client.setName(name);
        client.setTelephone(telephone);
        client.setEmail(email);
        reference.child(CLIENTS_COLLECTION_NAME).push().setValue(client);
        editTextName.setText("");
        editTexTelephone.setText("");
        editTextEmail.setText("");
    }

    private void update() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        client.setName(name);
        client.setTelephone(telephone);
        reference.child(CLIENTS_COLLECTION_NAME).child(client.getId())
                .setValue(client);
        finish();
    }

    private void loadForm() {
        String idClient = getIntent().getStringExtra("idClient");
        editTextName.setText(client.getName());
        editTexTelephone.setText(client.getTelephone());
        editTextEmail.setText(client.getEmail());
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }
}