package br.com.appestetica.ui.clients.adapter;

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
import br.com.appestetica.ui.clients.ClientFormActivity;
import br.com.appestetica.ui.clients.model.Client;

public class AdapterClients extends RecyclerView.Adapter<AdapterClients.ViewHolder> {

    List<Client> listOfClients;
    Context context;

    public AdapterClients(Context context, List<Client> listOfClients) {

        this.context = context;
        this.listOfClients = listOfClients;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public AdapterClients.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View mActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem, parent, false);

        return new ViewHolder(mActivity);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterClients.ViewHolder holder, int position) {

        Client clientSelected = listOfClients.get(position);
        holder.etClientName.setText(clientSelected.getName());
        holder.etClientPhone.setText(clientSelected.getTelephone());
        holder.etClientEmail.setText(clientSelected.getEmail());

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ClientFormActivity.class);
            intent.putExtra("action", "update");
            intent.putExtra("idClient", clientSelected.getId());
            intent.putExtra("name", clientSelected.getName());
            intent.putExtra("telephone", clientSelected.getTelephone());
            intent.putExtra("email", clientSelected.getEmail());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listOfClients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView etClientEmail;
        TextView etClientName;
        TextView etClientPhone;
        RelativeLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etClientName = itemView.findViewById(R.id.textName);
            etClientPhone = itemView.findViewById(R.id.textNumber);
            etClientEmail = itemView.findViewById(R.id.textEmail);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
