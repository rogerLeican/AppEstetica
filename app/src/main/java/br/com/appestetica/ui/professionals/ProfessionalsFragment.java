package br.com.appestetica.ui.professionals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.databinding.FragmentProfessionalsBinding;
import br.com.appestetica.ui.professionals.model.Professional;

public class ProfessionalsFragment extends Fragment {

    private ProfessionalsViewModel professionalsViewModel;
    private FragmentProfessionalsBinding binding;
    private List<Professional> listOfProfessionals;
    private ArrayAdapter adapter;
    private ListView lvProfessionals;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        professionalsViewModel =
                new ViewModelProvider(this).get(ProfessionalsViewModel.class);

        binding = FragmentProfessionalsBinding.inflate(inflater, container, false);
        lvProfessionals = binding.lvProfessionals;

        View view = binding.getRoot();


        lvProfessionals.setOnItemClickListener((adapterView, view12, position, l) -> {
            Intent intent = new Intent(getContext(), ProfessionalFormActivity.class);
            String idClient = listOfProfessionals.get(position).getId();
            intent.putExtra("action", "update");
            intent.putExtra("idClient", idClient);
            startActivity(intent);
        });

        lvProfessionals.setOnItemLongClickListener((adapterView, view1, position, l) -> {
            delete(position);
            return false;
        });

        return view;
    }

    private void delete(int position) {
        Professional professional = listOfProfessionals.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(android.R.drawable.ic_delete);
        alert.setMessage("confirm " + professional.getName() + " professional deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {
            loadProfessionals(lvProfessionals);
        });
        alert.show();
    }

    private void loadProfessionals(ListView lvProfessionals) {
        listOfProfessionals = new ArrayList<>();
        if (listOfProfessionals.isEmpty()) {
            Professional fake = new Professional("Empty list...");
            listOfProfessionals.add(fake);
            lvProfessionals.setEnabled(false);
        }

        lvProfessionals.setEnabled(true);
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, listOfProfessionals);

        lvProfessionals.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfessionals(lvProfessionals);
    }
}