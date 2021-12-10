package br.com.appestetica.ui.professionals;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import br.com.appestetica.R;
import br.com.appestetica.databinding.ActivityProfessionalFormBinding;
import br.com.appestetica.ui.professionals.model.Professional;
import br.com.appestetica.ui.professionals.repository.ProfessionalDao;

public class ProfessionalFormActivity extends AppCompatActivity {

    Button btnSave;
    EditText editTextName;
    EditText editTexTelephone;
    private Professional professional;

    ActivityProfessionalFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessionalFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnSave = binding.btnSaveProfessional;
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

    private void update() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        professional.setName(name);
        professional.setTelephone(telephone);
        ProfessionalDao.update(this, professional);
        finish();
    }

    private void save() {
        String name = editTextName.getText().toString();
        String telephone = editTexTelephone.getText().toString();
        professional = new Professional();
        professional.setName(name);
        professional.setTelephone(telephone);
       ProfessionalDao.insert(this, professional);
        editTextName.setText("");
        editTexTelephone.setText("");
    }

    private void loadForm() {
        int idProfessional = getIntent().getIntExtra("idProfessional", 0);
        professional = ProfessionalDao.getProfessionalById(this, idProfessional);
        editTextName.setText(professional.getName());
        editTexTelephone.setText(professional.getTelephone());
    }
}