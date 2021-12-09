package br.com.appestetica.ui.professionals;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import br.com.appestetica.R;
import br.com.appestetica.databinding.FragmentClientsBinding;
import br.com.appestetica.databinding.FragmentProfessionalsBinding;
import br.com.appestetica.ui.clients.ClientsViewModel;
import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.professionals.model.Professional;

public class ProfessionalsFragment extends Fragment {

    private ProfessionalsViewModel professionalsViewModel;
    private FragmentProfessionalsBinding binding;
    private List<Professional> listOfProfessionals;
    private ArrayAdapter adapter;
    private ListView lvProfessionals;

    public static ProfessionalsFragment newInstance() {
        return new ProfessionalsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        professionalsViewModel =
                new ViewModelProvider(this).get(ProfessionalsViewModel.class);

        binding = FragmentProfessionalsBinding.inflate(inflater, container, false);
        lvProfessionals = binding.lvProfessionals;


        View root = binding.getRoot();
        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}