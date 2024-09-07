package rs.ac.uns.ftn.informatika.jpa.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DateAndAdminDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateSlot;

    private Integer availableAdminId;


}
