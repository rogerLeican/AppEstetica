package br.com.appestetica.ui.products;

import static br.com.appestetica.commons.UtilityNamesDataBase.CLIENTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.PRODUCTS_COLLECTION_NAME;
import static br.com.appestetica.commons.UtilityNamesDataBase.URI_DATABASE_AESTHETIC;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;

import br.com.appestetica.databinding.ActivityProductsFormBinding;
import br.com.appestetica.databinding.ClientActivityFormBinding;
import br.com.appestetica.ui.products.model.Product;

public class ProductsFormActivity extends AppCompatActivity {


    private EditText etProductName;
    private EditText etProductPrice;
    private Product product;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    ActivityProductsFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(URI_DATABASE_AESTHETIC);

        Button btnSave = binding.btnProductSave;
        etProductName = binding.etProductName;
        etProductPrice = binding.etProductPrice;
        String action = getIntent().getStringExtra("action");

        if (action.equals("update")) {
            loadForm();
        }
        btnSave.setOnClickListener(view -> {
            if (action.equals("insert")) {
                save();
            } else if (action.equals("update")) {
                update();
            }
        });
    }

    private void update() {

        String idProduct = getIntent().getStringExtra("idProduct");
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        product.setId(idProduct);
        product.setName(name);
        product.setPrice(Float.parseFloat(price));
        reference.child(PRODUCTS_COLLECTION_NAME).child(product.getId())
                .setValue(product);
        finish();
    }

    private void save() {
        String name = etProductName.getText().toString();
        String price = etProductPrice.getText().toString();
        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "You must fill in all fields!", Toast.LENGTH_LONG).show();
        } else {
            float floatPrice = Float.parseFloat(price);
            product = new Product();
            product.setName(name);
            product.setPrice(floatPrice);
            reference.child(PRODUCTS_COLLECTION_NAME).push().setValue(product);
            Toast.makeText(this,
                    "The product " + product.getName() + " was successfully registered",
                    Toast.LENGTH_LONG)
                    .show();
            etProductName.setText("");
            etProductPrice.setText("");
        }
    }

    private void loadForm() {
        float price = getIntent().getFloatExtra("price", 0);
        product = Product.builder()
                .id(getIntent().getStringExtra("idProduct"))
                .name(getIntent().getStringExtra("name"))
                .price(price)
                .build();
        etProductName.setText(product.getName());
        etProductPrice.setText(String.valueOf(product.getPrice()));

    }
}