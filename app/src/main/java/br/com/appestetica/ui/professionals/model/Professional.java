package br.com.appestetica.ui.professionals.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Professional {

    private int id;
    private String name;
    private String telephone;
    private List<LocalDateTime> schedule;

}
