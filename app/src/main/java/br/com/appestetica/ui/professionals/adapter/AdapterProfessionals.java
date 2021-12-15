package br.com.appestetica.ui.professionals.adapter;

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
import br.com.appestetica.ui.professionals.ProfessionalFormActivity;
import br.com.appestetica.ui.professionals.model.Professional;

public class AdapterProfessionals extends RecyclerView.Adapter<AdapterProfessionals.ViewHolder> {

    List<Professional> listOfProfessionals;
    Context context;

    public AdapterProfessionals(Context context, List<Professional> listOfProfessionals) {
        this.context = context;
        this.listOfProfessionals = listOfProfessionals;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public AdapterProfessionals.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View mActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_professional_item, parent, false);

        return new ViewHolder(mActivity);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterProfessionals.ViewHolder holder, int position) {
        ViewHolder viewHolder = holder;
        Professional professionalSelected = listOfProfessionals.get(position);
        viewHolder.etProfessionalName.setText(professionalSelected.getName());
        viewHolder.etProfessionalPhone.setText(professionalSelected.getTelephone());

        holder.professionalLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfessionalFormActivity.class);
            intent.putExtra("action", "update");
            intent.putExtra("idProfessional", professionalSelected.getId());
            intent.putExtra("name", professionalSelected.getName());
            intent.putExtra("telephone", professionalSelected.getTelephone());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listOfProfessionals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView etProfessionalName;
        TextView etProfessionalPhone;
        RelativeLayout professionalLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etProfessionalName = itemView.findViewById(R.id.textProfessionalName);
            etProfessionalPhone = itemView.findViewById(R.id.textProfessionalNumber);
            professionalLayout = itemView.findViewById(R.id.professionalLayout);
        }
    }
}
