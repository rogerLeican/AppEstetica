package br.com.appestetica.ui.clients;

import static br.com.appestetica.commons.UtilityNamesDataBase.CLIENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.R;
import br.com.appestetica.databinding.FragmentClientsBinding;
import br.com.appestetica.ui.clients.adapter.AdapterClients;
import br.com.appestetica.ui.clients.model.Client;

public class ClientsFragment extends Fragment {

    private ClientsViewModel clientsViewModel;
    private FragmentClientsBinding binding;
    private List<Client> listOfClients;
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvClients);

        return root;
    }

    Client deleteClients = null;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    deleteClients = listOfClients.get(position);
                    delete(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(rvClients, deleteClients.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                listOfClients.add(position, deleteClients);
                                reference.child(CLIENTS_COLLECTION_NAME).push().setValue(deleteClients);
                            }).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            Drawable icon = ContextCompat.getDrawable(adapter.getContext(),
                    R.drawable.ic_baseline_delete_sweep_40);
            ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorAccent, getActivity().getTheme()));

            View itemView = viewHolder.itemView;
            int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconBottom = iconTop + icon.getIntrinsicHeight();

            if (dX > 0) { // Swiping to the right
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + icon.getIntrinsicWidth();
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            } else if (dX < 0) { // Swiping to the left
                int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0);
            }

            background.draw(c);
            icon.draw(c);
        }
    };

    private void viewData() {

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
        alert.setIcon(R.drawable.ic_baseline_cancel_24);
        alert.setMessage("Confirm " + client.getName() + " client deletion ?");
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
                Client client = new Client();
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
                for (Client client : listOfClients) {
                    if (client.getId().equals(idClient)) {
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