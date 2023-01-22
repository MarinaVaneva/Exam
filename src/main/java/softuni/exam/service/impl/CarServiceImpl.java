package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarImportDto;
import softuni.exam.models.dto.CarWrapperDto;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.CarRepository;
import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    public static final Path CAR_FILE_PATH = Path.of("src/main/resources/files/xml/cars.xml");
    private final CarRepository carRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    public CarServiceImpl(CarRepository carRepository, ValidationUtils validationUtils, ModelMapper modelMapper, XmlParser xmlParser) {
        this.carRepository = carRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString(CAR_FILE_PATH);
    }

    @Override
    public String importCars() throws IOException, JAXBException {
        final StringBuilder stringBuilder = new StringBuilder();

        final File file = CAR_FILE_PATH.toFile();

        final CarWrapperDto carWrapperDto =
                xmlParser.fromFile(file, CarWrapperDto.class);

        final List<CarImportDto> cars = carWrapperDto.getCars();

        for (CarImportDto car : cars) {
            boolean isValid = validationUtils.isValid(car);


            if (isValid) {
                if (carRepository.findFirstByPlateNumber(car.getPlateNumber()).isPresent()) {
                    stringBuilder.append("Invalid car").append(System.lineSeparator());
                    continue;
                }
                this.carRepository.saveAndFlush(this.modelMapper.map(car, Car.class));
                stringBuilder.append(String.format("Successfully imported car %s - %s",car.getCarMake(),car.getCarModel())).append(System.lineSeparator());


            }else {
                stringBuilder.append("Invalid car").append(System.lineSeparator());
            }
        }

        return stringBuilder.toString();
    }
}