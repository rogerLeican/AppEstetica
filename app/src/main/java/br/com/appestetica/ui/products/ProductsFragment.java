package br.com.appestetica.ui.products;

import static br.com.appestetica.commons.UtilityNamesDataBase.PRODUCTS_COLLECTION_NAME;
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
import br.com.appestetica.databinding.FragmentProductsBinding;
import br.com.appestetica.ui.products.adapter.AdapterProducts;
import br.com.appestetica.ui.products.model.Product;

public class ProductsFragment extends Fragment {

    private ProductsViewModel productsViewModel;
    private FragmentProductsBinding binding;
    private List<Product> listOfProducts;
    private AdapterProducts adapter;
    private RecyclerView rvProducts;

//    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener eventListener;
    private Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listOfProducts = new ArrayList<>();
        productsViewModel =
                new ViewModelProvider(this).get(ProductsViewModel.class);

        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewDataProducts();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvProducts);

        return root;
    }

    Product deleteProducts = null;
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
                    deleteProducts = listOfProducts.get(position);
                    delete(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(rvProducts, deleteProducts.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Undo", view -> {
                                listOfProducts.add(position, deleteProducts);
                                reference.child(PRODUCTS_COLLECTION_NAME).push().setValue(deleteProducts);
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

    private void viewDataProducts() {

        rvProducts = binding.rvProducts;
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProducts.setHasFixedSize(true);
        adapter = new AdapterProducts(getContext(), listOfProducts);
        rvProducts.setAdapter(adapter);
    }

    private void delete(int position) {
        Product product = listOfProducts.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(R.drawable.ic_baseline_cancel_24);
        alert.setMessage("Confirm " + product.getName() + " product deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {

            reference.child(PRODUCTS_COLLECTION_NAME).child(product.getId())
                    .removeValue();
        });
        alert.show();
    }

    private void loadProducts() {
        FirebaseDatabase database;
        listOfProducts.clear();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);
        query = reference.child(PRODUCTS_COLLECTION_NAME).orderByChild("name");
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = new Product();
                product.setId(snapshot.getKey());
                product.setName(snapshot.child("name").getValue(String.class));
                product.setPrice(snapshot.child("price").getValue(Float.class));

                listOfProducts.add(product);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idProduct = snapshot.getKey();
                for (Product product : listOfProducts) {
                    if (product.getId().equals(idProduct)) {
                        product.setName(snapshot.child("name").getValue(String.class));
                        product.setPrice(snapshot.child("price").getValue(Float.class));
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String idProduct = snapshot.getKey();
                listOfProducts.removeIf(product -> product.getId().equals(idProduct));
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
        loadProducts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}