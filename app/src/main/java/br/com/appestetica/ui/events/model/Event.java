package br.com.appestetica.ui.events.model;

import java.util.List;

import br.com.appestetica.ui.clients.model.Client;
import br.com.appestetica.ui.products.model.Product;
import br.com.appestetica.ui.professionals.model.Professional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event {

    private String eventId;
    private String clientId;
    private String productId;
    private String professionalId;
}
