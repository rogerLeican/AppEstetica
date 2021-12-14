package br.com.appestetica.ui.professionals;

import static br.com.appestetica.commons.UtilityNamesDataBase.PROFESSIONALS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

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

        lvProfessionals = binding.lvProfessionals;

        View view = binding.getRoot();

        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, listOfProfessionals);
        lvProfessionals.setAdapter(adapter);

        lvProfessionals.setOnItemClickListener((adapterView, view12, position, l) -> {
            Intent intent = new Intent(getContext(), ProfessionalFormActivity.class);
            Professional professional = listOfProfessionals.get(position);
            intent.putExtra("action", "update");
            intent.putExtra("idProfessional", professional.getId());
            intent.putExtra("name", professional.getName());
            intent.putExtra("telephone", professional.getTelephone());
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