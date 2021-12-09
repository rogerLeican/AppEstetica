package br.com.appestetica.ui.clients;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import br.com.appestetica.databinding.FragmentClientsBinding;
import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.clients.repository.ClientDao;

public class ClientsFragment extends Fragment {

    private ClientsViewModel clientsViewModel;
    private FragmentClientsBinding binding;
    private List<Client> listOfClients;
    private ArrayAdapter adapter;
    private ListView lvClients;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientsViewModel =
                new ViewModelProvider(this).get(ClientsViewModel.class);

        binding = FragmentClientsBinding.inflate(inflater, container, false);

        lvClients = binding.lvClients;
        loadClients(lvClients);

        View root = binding.getRoot();

        lvClients.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(getContext(), ClientFormActivity.class);
            int idClient = listOfClients.get(position).getId();
            intent.putExtra("action", "update");
            intent.putExtra("idClient", idClient);
            startActivity(intent);
        });

        lvClients.setOnItemLongClickListener((adapterView, view, position, l) -> {
            delete(position);
            return false;
        });


//        clientsViewModel.getClients().observe(getViewLifecycleOwner(), clients -> {
////            listView Todo revisar
//        });

        return root;
    }

    private void delete(int position) {
        Client client = listOfClients.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(android.R.drawable.ic_delete);
        alert.setMessage("confirm " + client.getName() + " client deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {
            ClientDao.delete(getContext(), client.getId());
            loadClients(lvClients);
        });
        alert.show();
    }

    private void loadClients(ListView lvAccounts) {
        listOfClients = ClientDao.getClients(getContext());

        if (listOfClients.isEmpty()) {
            Client fake = new Client("Empty list...");
            listOfClients.add(fake);
            lvAccounts.setEnabled(false);
        }

        lvAccounts.setEnabled(true);
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, listOfClients);

        lvAccounts.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClients(lvClients);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}