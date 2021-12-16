package br.com.appestetica.ui.events.adapter;

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
import br.com.appestetica.ui.events.EventsFormActivity;
import br.com.appestetica.ui.events.model.Event;

public class AdapterEvents extends RecyclerView.Adapter<AdapterEvents.ViewHolder> {

    List<Event> listOfEvent;
    Context context;

    public AdapterEvents(Context context, List<Event> listOfEvent) {

        this.context = context;
        this.listOfEvent = listOfEvent;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public AdapterEvents.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View mActivity = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem, parent, false);

        return new ViewHolder(mActivity);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterEvents.ViewHolder holder, int position) {

        Event EventSelected = listOfEvent.get(position);
        holder.etEventName.setText(EventSelected.getEventId());
        holder.etEventPhone.setText(EventSelected.getClientId());
        holder.etEventPhone.setText(EventSelected.getProductId());
        holder.etEventEmail.setText(EventSelected.getProfessionalId());

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, EventsFormActivity.class);
            intent.putExtra("action", "update");
            intent.putExtra("idEvent", EventSelected.getEventId());
            intent.putExtra("idClient", EventSelected.getClientId());
            intent.putExtra("idProfessional", EventSelected.getProfessionalId());
            intent.putExtra("idProduct", EventSelected.getProductId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listOfEvent.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView etEventEmail;
        TextView etEventName;
        TextView etEventPhone;
        RelativeLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            etEventName = itemView.findViewById(R.id.textClientEventName);
            etEventPhone = itemView.findViewById(R.id.textProductName);
            etEventEmail = itemView.findViewById(R.id.textProfessionalEventName);
            mainLayout = itemView.findViewById(R.id.eventsLayout);
        }
    }
}
