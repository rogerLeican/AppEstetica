package br.com.appestetica.ui.professionals;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.appestetica.databinding.ActivityProfessionalFormBinding;
import br.com.appestetica.ui.professionals.model.Professional;

public class ProfessionalFormActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTexTelephone;
    private Professional professional;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private static final String PROFESSIONALS_COLLECTION_NAME = "professionals";

    ActivityProfessionalFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessionalFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/aesthetic");

        Button btnSave = binding.btnSaveProfessional;
        editTextName = binding.etProfessionalName;
        editTexTelephone = binding.etProfessionalPhone;
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
        professional = new Professional();
        professional.setName(name);
        professional.setTelephone(telephone);
        reference.child(PROFESSIONALS_COLLECTION_NAME).push().setValue(professional);
        editTextName.setText("");
        editTexTelephone.setText("");
    }

    private void update() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        professional.setName(name);
        professional.setTelephone(telephone);
        reference.child(PROFESSIONALS_COLLECTION_NAME).child(professional.getId())
                .setValue(professional);
        finish();
    }

    private void loadForm() {
        String idProfessional = getIntent().getStringExtra("idProfessional");
        editTextName.setText(professional.getName());
        editTexTelephone.setText(professional.getTelephone());
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

}