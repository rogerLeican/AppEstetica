package br.com.appestetica.ui.products.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.appestetica.R;
import br.com.appestetica.ui.products.ProductsFormActivity;
import br.com.appestetica.ui.products.model.Product;
import br.com.appestetica.ui.professionals.ProfessionalFormActivity;

public class AdapterProducts extends RecyclerView.Adapter<AdapterProducts.ViewHolder> {

    List<Product> listOfProducts;
    Context context;

    public AdapterProducts(Context context, List<Product> listOfProducts) {
        this.context = context;
        this.listOfProducts = listOfProducts;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public AdapterProducts.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View mActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_product_item, parent, false);

        return new ViewHolder(mActivity);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterProducts.ViewHolder viewHolder, int position) {
        Product productSelected = listOfProducts.get(position);
        viewHolder.etProductName.setText(productSelected.getName());
        viewHolder.etProductPrice.setText(String.valueOf(productSelected.getPrice()));

        viewHolder.productLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductsFormActivity.class);
            intent.putExtra("action", "update");
            intent.putExtra("idProfessional", productSelected.getId());
            intent.putExtra("name", productSelected.getName());
            intent.putExtra("price", productSelected.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listOfProducts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView etProductName;
        TextView etProductPrice;
        RelativeLayout productLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etProductName = itemView.findViewById(R.id.textProductName);
            etProductPrice = itemView.findViewById(R.id.textProductPrice);
            productLayout = itemView.findViewById(R.id.productLayout);
        }
    }
}
