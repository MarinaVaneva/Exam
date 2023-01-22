package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.MechanicImportDto;
import softuni.exam.models.dto.PartImportDto;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.service.MechanicService;
import softuni.exam.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class MechanicServiceImpl implements MechanicService {
    public static final String MECHANIC_FILE_PATH = "src/main/resources/files/json/mechanics.json";
    private final MechanicRepository mechanicRepository;
    private final Gson gson;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;

    public MechanicServiceImpl(MechanicRepository mechanicRepository, Gson gson, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.mechanicRepository = mechanicRepository;
        this.gson = gson;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.mechanicRepository.count() > 0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        return Files.readString(Path.of(MECHANIC_FILE_PATH));
    }

    @Override
    public String importMechanics() throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(gson.fromJson(readMechanicsFromFile(), MechanicImportDto[].class))
                .forEach(mechanicDto -> {
                    boolean isValid = this.validationUtils.isValid(mechanicDto);

                    if (this.mechanicRepository.findFirstByEmail(mechanicDto.getEmail()).isPresent()) {
                        stringBuilder.append("Invalid mechanic").append(System.lineSeparator());
                    }

                    if (isValid) {
                        stringBuilder.append(String.format("Successfully imported mechanic %s %s",
                                mechanicDto.getFirstName(),
                                mechanicDto.getLastName())).append(System.lineSeparator());

                        this.mechanicRepository.saveAndFlush(this.modelMapper.map(mechanicDto, Mechanic.class));
                    } else {
                        stringBuilder.append("Invalid mechanic").append(System.lineSeparator());
                    }
                });

        return stringBuilder.toString();
    }

}


