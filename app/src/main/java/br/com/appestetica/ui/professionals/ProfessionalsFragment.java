package br.com.appestetica.ui.professionals;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.com.appestetica.R;
import br.com.appestetica.databinding.FragmentClientsBinding;
import br.com.appestetica.databinding.FragmentProfessionalsBinding;
import br.com.appestetica.ui.clients.ClientFormActivity;
import br.com.appestetica.ui.clients.ClientsViewModel;
import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.clients.repository.ClientDao;
import br.com.appestetica.ui.professionals.model.Professional;
import br.com.appestetica.ui.professionals.repository.ProfessionalDao;

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
            int idClient = listOfProfessionals.get(position).getId();
            intent.putExtra("action", "update");
            intent.putExtra("idClient", idClient);
            startActivity(intent);
        });

        lvProfessionals.setOnItemLongClickListener((adapterView, view1, position, l) ->{
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
            ProfessionalDao.delete(getContext(), professional.getId());
            loadProfessionals(lvProfessionals);
        });
        alert.show();
    }

    private void loadProfessionals(ListView lvProfessionals) {
        listOfProfessionals = ProfessionalDao.getProfessionals(getContext());

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