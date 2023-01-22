package softuni.exam.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import softuni.exam.models.constant.CarType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
public class CarImportDto {
    @NotNull
    @XmlElement
    @Size(min = 2, max = 30)
    private String carMake;

    @NotNull
    @XmlElement
    @Size(min = 2, max = 30)
    private String carModel;

    @NotNull
    @XmlElement
    @Positive
    private Integer year;

    @NotNull
    @XmlElement
    @Size(min = 2, max = 30)
    private String plateNumber;

    @NotNull
    @XmlElement
    @Positive
    private Integer kilometers;

    @NotNull
    @Min(value = 1)
    @XmlElement
    private Double engine;

    @NotNull
    @XmlElement
    @Enumerated(EnumType.STRING)
    private CarType carType;
}
