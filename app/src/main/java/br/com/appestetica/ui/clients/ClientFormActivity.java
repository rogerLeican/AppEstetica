package br.com.appestetica.ui.clients;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import br.com.appestetica.databinding.ClientActivityFormBinding;
import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.clients.repository.ClientDao;

public class ClientFormActivity extends AppCompatActivity {

    ClientActivityFormBinding binding;
    Button btnSave;
    EditText editTextName;
    EditText editTexTelephone;
    EditText editTextEmail;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ClientActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnSave = binding.btnSaveClient;
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
        ClientDao.insert(this, client);
        editTextName.setText("");
        editTexTelephone.setText("");
        editTextEmail.setText("");
    }

    private void update() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        client.setName(name);
        client.setTelephone(telephone);
        ClientDao.update(this, client);
        finish();
    }

    private void loadForm() {

        int idClient = getIntent().getIntExtra("idClient", 0);
        client = ClientDao.getClientById(this, idClient);
        editTextName.setText(client.getName());
        editTexTelephone.setText(client.getTelephone());
        editTextEmail.setText(client.getEmail());
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }
}