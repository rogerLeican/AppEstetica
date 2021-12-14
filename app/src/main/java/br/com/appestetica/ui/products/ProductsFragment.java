package br.com.appestetica.ui.products;

import static br.com.appestetica.commons.UtilityNamesDataBase.PRODUCTS_COLLECTION_NAME;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.appestetica.databinding.FragmentProductsBinding;
import br.com.appestetica.ui.products.model.Product;

public class ProductsFragment extends Fragment {

    private ProductsViewModel productsViewModel;
    private FragmentProductsBinding binding;

    private List<Product> listOfProducts;
    private ArrayAdapter adapter;
    private ListView lvProducts;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener eventListener;
    private Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listOfProducts = new ArrayList<>();

        productsViewModel =
                new ViewModelProvider(this).get(ProductsViewModel.class);

        binding = FragmentProductsBinding.inflate(inflater, container, false);

        lvProducts = binding.lvProducts;

        View root = binding.getRoot();

        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, listOfProducts);
        lvProducts.setAdapter(adapter);

        lvProducts.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(getContext(), ProductsFormActivity.class);
            Product productSelected = listOfProducts.get(position);
            intent.putExtra("action", "update");
            intent.putExtra("idProduct", productSelected.getId());
            intent.putExtra("name", productSelected.getName());
            intent.putExtra("price", productSelected.getPrice());
            startActivity(intent);
        });

        lvProducts.setOnItemLongClickListener((adapterView, view, position, l) -> {
            delete(position);
            return false;
        });

        return root;
    }

    private void delete(int position) {
        Product product = listOfProducts.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Delete...");
        alert.setIcon(android.R.drawable.ic_delete);
        alert.setMessage("confirm " + product.getName() + " product deletion ?");
        alert.setNeutralButton("CANCEL", null);
        alert.setPositiveButton("YES", (dialogInterface, i) -> {

            reference.child(PRODUCTS_COLLECTION_NAME).child(product.getId())
                    .removeValue();
        });
        alert.show();
    }

    private void loadProducts() {
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