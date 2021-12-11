package br.com.appestetica.ui.clients.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Client {
    private String id;
    private String name;
    private String telephone;
    private String email;

    public Client(String name){
        this.name = name;
    }
}
