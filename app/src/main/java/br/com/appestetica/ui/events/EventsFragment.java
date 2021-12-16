package br.com.appestetica.ui.events;

import static br.com.appestetica.commons.UtilityNamesDataBase.EVENTS_COLLECTION_NAME;
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
import br.com.appestetica.databinding.FragmentEventsBinding;
import br.com.appestetica.ui.events.adapter.AdapterEvents;
import br.com.appestetica.ui.events.model.Event;


public class EventsFragment extends Fragment {


    private FragmentEventsBinding binding;
    private List<Event> listOfEvents;
    private RecyclerView rvEvent;
    private AdapterEvents adapter;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener eventListener;
    private Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listOfEvents = new ArrayList<>();
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewData();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvEvent);

        return view;
    }

    Event deleteEvents = null;
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
                    deleteEvents = listOfEvents.get(position);
                    delete(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(rvEvent, deleteEvents.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                listOfEvents.add(position, deleteEvents);
                                reference.child(EVENTS_COLLECTION_NAME).push().setValue(deleteEvents);
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
        rvEvent = binding.rvEvents;
        rvEvent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEvent.setHasFixedSize(true);
        adapter = new AdapterEvents(getContext(), listOfEvents);
        rvEvent.setAdapter(adapter);
    }

    private void delete(int position) {
        Event event = listOfEvents.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(R.drawable.ic_baseline_cancel_24);
        alert.setMessage("Confirm event deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {

            reference.child(EVENTS_COLLECTION_NAME).child(event.getEventId()).removeValue();
        });
        alert.show();
    }

    private void loadEvents() {

        listOfEvents.clear();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);
        query = reference.child(EVENTS_COLLECTION_NAME).orderByChild("name");

        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Event event = new Event();
                event.setEventId(snapshot.getKey());
                event.setClientId(snapshot.child("idEvent").getValue(String.class));
                event.setProfessionalId(snapshot.child("idProfessional").getValue(String.class));
                event.setProductId(snapshot.child("idProduct").getValue(String.class));

                listOfEvents.add(event);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idEvent = snapshot.getKey();
                for (Event event : listOfEvents) {
                    if (event.getClientId().equals(idEvent)) {
                        event.setClientId(snapshot.child("clientId").getValue(String.class));
                        event.setProductId(snapshot.child("productId").getValue(String.class));
                        event.setProfessionalId(snapshot.child("professionalId").getValue(String.class));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idEvent = snapshot.getKey();
                listOfEvents.removeIf(event -> event.getEventId().equals(idEvent));
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
        loadEvents();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}