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

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.com.appestetica.R;
import br.com.appestetica.ui.clients.ClientFormActivity;
import br.com.appestetica.ui.clients.model.Client;

public class AdapterClients extends RecyclerView.Adapter<AdapterClients.ViewHolder> {

    List<Client> listOfClients;

    //    LayoutInflater inflater;
//    EditText etClientEmail;
//    EditText etClientName;
//    EditText etClientPhone;
    Context context;
    View mActivity;
    int mRecentlyDeletedItemPosition;
    Client mRecentlyDeletedItem;

    public AdapterClients(Context context, List<Client> listOfClients) {

//        this.context = context;
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
        ViewHolder viewHolder = holder;
        Client clientSelected = listOfClients.get(position);
        viewHolder.etClientName.setText(clientSelected.getName());
        viewHolder.etClientPhone.setText(clientSelected.getTelephone());
        viewHolder.etClientEmail.setText(clientSelected.getEmail());

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ClientFormActivity.class);
            intent.putExtra("action", "update");
            intent.putExtra("idClient", clientSelected.getId());
            intent.putExtra("name", clientSelected.getName());
            intent.putExtra("telephone", clientSelected.getTelephone());
            intent.putExtra("email", clientSelected.getEmail());
            context.startActivity(intent);
        });

//        holder.mainLayout.setOnLongClickListener(view -> {
//            Intent intent = new Intent(context, ClientFormActivity.class);
//            intent.putExtra("action", "update");
//            intent.putExtra("idClient", clientSelected.getId());
//            intent.putExtra("name", clientSelected.getName());
//            intent.putExtra("telephone", clientSelected.getTelephone());
//            intent.putExtra("email", clientSelected.getEmail());
//            context.startActivity(intent);
//        });

    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = listOfClients.get(position);
        Client clientSelected = listOfClients.get(position);
        mRecentlyDeletedItemPosition = position;
        listOfClients.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.mainLayout);
        Snackbar snackbar = Snackbar.make(view, R.string.snack_bar_text,
                Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snack_bar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        listOfClients.add(mRecentlyDeletedItemPosition,
                mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
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
