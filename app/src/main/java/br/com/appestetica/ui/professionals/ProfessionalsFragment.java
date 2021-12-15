package br.com.appestetica.ui.professionals;

import static br.com.appestetica.commons.UtilityNamesDataBase.CLIENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.PROFESSIONALS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import br.com.appestetica.databinding.FragmentProfessionalsBinding;
import br.com.appestetica.ui.clients.adapter.AdapterClients;
import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.professionals.adapter.AdapterProfessionals;
import br.com.appestetica.ui.professionals.model.Professional;

public class ProfessionalsFragment extends Fragment {

    private ProfessionalsViewModel professionalsViewModel;
    private FragmentProfessionalsBinding binding;
    private List<Professional> listOfProfessionals;
    private AdapterProfessionals adapter;
    private RecyclerView rvProfessionals;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener eventListener;
    private Query query;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        listOfProfessionals = new ArrayList<>();
        professionalsViewModel =
                new ViewModelProvider(this).get(ProfessionalsViewModel.class);

        binding = FragmentProfessionalsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewDataProfessional();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvProfessionals);

        return view;
    }

    Professional deleteProfessionals = null;
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
                    deleteProfessionals = listOfProfessionals.get(position);
                    delete(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(rvProfessionals, deleteProfessionals.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                listOfProfessionals.add(position, deleteProfessionals);
                                reference.child(PROFESSIONALS_COLLECTION_NAME).push().setValue(deleteProfessionals);
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
            ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.colorAccentVariant, getActivity().getTheme()));

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

    private void viewDataProfessional() {

        rvProfessionals = binding.rvProfessionals;
        rvProfessionals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProfessionals.setHasFixedSize(true);
        adapter = new AdapterProfessionals(getContext(), listOfProfessionals);
        rvProfessionals.setAdapter(adapter);
    }

    private void delete(int position) {
        Professional professional = listOfProfessionals.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(R.drawable.ic_baseline_cancel_24);
        alert.setMessage("Confirm " + professional.getName() + " professional deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {

            reference.child(PROFESSIONALS_COLLECTION_NAME).child(professional.getId()).removeValue();
        });
        alert.show();
    }

    private void loadProfessionals() {

        listOfProfessionals.clear();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);
        query = reference.child(PROFESSIONALS_COLLECTION_NAME).orderByChild("name");

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Professional professional = new Professional();
                professional.setId(snapshot.getKey());
                professional.setName(snapshot.child("name").getValue(String.class));
                professional.setTelephone(snapshot.child("telephone").getValue(String.class));

                listOfProfessionals.add(professional);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idProfessional = snapshot.getKey();
                for (Professional professional : listOfProfessionals) {
                    if (professional.getId().equals(idProfessional)) {
                        professional.setName(snapshot.child("name").getValue(String.class));
                        professional.setTelephone(snapshot.child("telephone").getValue(String.class));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idProfessional = snapshot.getKey();
                listOfProfessionals.removeIf(professional -> professional.getId().equals(idProfessional));
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
    public void onStart() {
        super.onStart();
        loadProfessionals();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}