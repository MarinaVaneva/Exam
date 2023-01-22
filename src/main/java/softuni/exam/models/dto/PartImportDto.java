package softuni.exam.models.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class PartImportDto {
    @Size(min = 2, max = 19)
    private String partName;

    @Min(value = 10)
    @Max(value = 2000)
    private Double price;

    @Positive
    private Integer quantity;
}
