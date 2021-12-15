package br.com.appestetica.ui.clients;

import static br.com.appestetica.commons.UtilityNamesDataBase.CLIENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.databinding.FragmentClientsBinding;
import br.com.appestetica.ui.clients.adapter.AdapterClients;
import br.com.appestetica.ui.clients.adapter.SwipeToDeleteCallback;
import br.com.appestetica.ui.clients.model.Client;

public class ClientsFragment extends Fragment {

    private ClientsViewModel clientsViewModel;
    private FragmentClientsBinding binding;
    private List<Client> listOfClients;
//    private ArrayAdapter adapter;
//    private ListView lvClients;
    private RecyclerView rvClients;
    AdapterClients adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener eventListener;
    private Query query;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listOfClients = new ArrayList<>();
        clientsViewModel =
                new ViewModelProvider(this).get(ClientsViewModel.class);

        binding = FragmentClientsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewData();


//        lvClients.setOnItemClickListener((adapterView, view, position, l) -> {
//            Intent intent = new Intent(getContext(), ClientFormActivity.class);
//            Client clientSelected = listOfClients.get(position);
//            intent.putExtra("action", "update");
//            intent.putExtra("idClient", clientSelected.getId());
//            intent.putExtra("name", clientSelected.getName());
//            intent.putExtra("telephone", clientSelected.getTelephone());
//            intent.putExtra("email", clientSelected.getEmail());
//            startActivity(intent);
//        });

//        lvClients.setOnItemLongClickListener((adapterView, view, position, l) -> {
//            delete(position);
//            return false;
//        });

//        rvClients.setOnItemLongClickListener((adapterView, view, position, l) -> {
//            delete(position);
//            return false;
//        });


//        clientsViewModel.getClients().observe(getViewLifecycleOwner(), clients -> {
////            listView Todo revisar
//        });

        return root;
    }


    public void viewData() {

        rvClients = binding.rvClients;
        rvClients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvClients.setHasFixedSize(true);
        adapter = new AdapterClients(getContext(), listOfClients);
        rvClients.setAdapter(adapter);
    }

    private void delete(int position) {
        Client client = listOfClients.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(android.R.drawable.ic_delete);
        alert.setMessage("confirm " + client.getName() + " client deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {

            reference.child(CLIENTS_COLLECTION_NAME).child(client.getId()).removeValue();
        });
        alert.show();
    }

    private void loadClients() {

        listOfClients.clear();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);
        query = reference.child(CLIENTS_COLLECTION_NAME).orderByChild("name");

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Client client= new Client();
                client.setId(snapshot.getKey());
                client.setName(snapshot.child("name").getValue(String.class));
                client.setTelephone(snapshot.child("telephone").getValue(String.class));
                client.setEmail(snapshot.child("email").getValue(String.class));

                listOfClients.add(client);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idClient = snapshot.getKey();
                for (Client client : listOfClients){
                    if(client.getId().equals(idClient)){
                        client.setName(snapshot.child("name").getValue(String.class));
                        client.setTelephone(snapshot.child("telephone").getValue(String.class));
                        client.setEmail(snapshot.child("email").getValue(String.class));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idClient = snapshot.getKey();
                listOfClients.removeIf(client -> client.getId().equals(idClient));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addChildEventListener(eventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        query.removeEventListener(eventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadClients();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}