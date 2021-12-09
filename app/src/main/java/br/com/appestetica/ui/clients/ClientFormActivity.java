package br.com.appestetica.ui.clients;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import br.com.appestetica.R;
import br.com.appestetica.databinding.ClientActivityFormBinding;
import br.com.appestetica.ui.clients.model.Client;

public class ClientFormActivity extends AppCompatActivity {

    ClientActivityFormBinding binding;
    Button btnSave;
    EditText editTextName;
    EditText editTexTelephone;
    EditText editTextEmail;
    private String action;
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
        action = getIntent().getStringExtra("action");



    }

}