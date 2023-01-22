package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PartImportDto;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.PartRepository;
import softuni.exam.service.PartService;
import softuni.exam.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class PartServiceImpl implements PartService {

    public static final String PART_FILE_PATH = "src/main/resources/files/json/parts.json";
    private final PartRepository partRepository ;
    private final Gson gson;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;

    public PartServiceImpl(PartRepository partRepository, Gson gson, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.partRepository = partRepository;
        this.gson = gson;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.partRepository.count() > 0;
    }

    @Override
    public String readPartsFileContent() throws IOException {
        return Files.readString(Path.of(PART_FILE_PATH));
    }

    @Override
    public String importParts() throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(gson.fromJson(readPartsFileContent(), PartImportDto[].class))
                .forEach(partDto -> {
                    boolean isValid = this.validationUtils.isValid(partDto);

                    if (this.partRepository.findFirstByPartName(partDto.getPartName()).isPresent()) {
                        isValid = false;
                    }

                    if (isValid) {
                        stringBuilder.append(String.format("Successfully imported part %s - %.2f",
                                partDto.getPartName(),
                                partDto.getPrice())).append(System.lineSeparator());

                        this.partRepository.saveAndFlush(this.modelMapper.map(partDto, Part.class));
                    } else {
                        stringBuilder.append("Invalid part").append(System.lineSeparator());
                    }
                });

        return stringBuilder.toString();
    }

}

