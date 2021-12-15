package br.com.appestetica.ui.professionals;

import static br.com.appestetica.commons.UtilityNamesDataBase.PROFESSIONALS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    ActivityProfessionalFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessionalFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);

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

        if (name.isEmpty() || telephone.isEmpty()) {
            Toast.makeText(this, "You must fill in all fields!", Toast.LENGTH_LONG).show();
        } else {


            professional = new Professional();
            professional.setName(name);
            professional.setTelephone(telephone);
            reference.child(PROFESSIONALS_COLLECTION_NAME).push().setValue(professional);
            Toast.makeText(this,
                    "The professional " + professional.getName() + " was successfully registered",
                    Toast.LENGTH_LONG)
                    .show();
            editTextName.setText("");
            editTexTelephone.setText("");
        }
    }

    private void update() {
        String idProfessional = getIntent().getStringExtra("idProfessional");
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        professional.setId(idProfessional);
        professional.setName(name);
        professional.setTelephone(telephone);
        reference.child(PROFESSIONALS_COLLECTION_NAME).child(professional.getId())
                .setValue(professional);
        finish();
    }

    private void loadForm() {
        professional = Professional.builder()
                .id(getIntent().getStringExtra("idProfessional"))
                .name(getIntent().getStringExtra("name"))
                .telephone(getIntent().getStringExtra("telephone"))
                .build();
        editTextName.setText(professional.getName());
        editTexTelephone.setText(professional.getTelephone());
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

}