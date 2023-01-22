package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.constant.CarType;
import softuni.exam.models.dto.FirstNameDto;
import softuni.exam.models.dto.TaskImportDto;
import softuni.exam.models.dto.TaskWrapperDto;
import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.models.entity.Part;
import softuni.exam.models.entity.Task;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.MechanicRepository;
import softuni.exam.repository.PartRepository;
import softuni.exam.repository.TaskRepository;
import softuni.exam.service.TaskService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    public final String PRINT_FORMAT = "Car %s %s with %dkm\n"+
            "\t-Mechanic: %s %s - task №%d\n"+
            "\t--Engine:%.1f\n "+
            "\t---Price: %.2f$\n";
    public static final Path TASK_FILE_PATH = Path.of("src/main/resources/files/xml/tasks.xml");
    private final TaskRepository taskRepository;
    private final CarRepository carRepository ;
    private final PartRepository partRepository ;
    private final MechanicRepository mechanicRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    public TaskServiceImpl(TaskRepository taskRepository, CarRepository carRepository, PartRepository partRepository, MechanicRepository mechanicRepository, ValidationUtils validationUtils, ModelMapper modelMapper, XmlParser xmlParser) {
        this.taskRepository = taskRepository;
        this.carRepository = carRepository;
        this.partRepository = partRepository;
        this.mechanicRepository = mechanicRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {

        return this.taskRepository.count() > 0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString(TASK_FILE_PATH);
    }

    @Override
    public String importTasks() throws IOException, JAXBException {

        final StringBuilder stringBuilder = new StringBuilder();

        final File file = TASK_FILE_PATH.toFile();

        final TaskWrapperDto taskWrapperDto =
                xmlParser.fromFile(file, TaskWrapperDto.class);

        final List<TaskImportDto> tasks = taskWrapperDto.getTasks();

        for (TaskImportDto task : tasks) {
            boolean isValid = validationUtils.isValid(task);

            if (isValid) {
              if (!mechanicRepository.findByFirstName (task.getMechanic().getFirstName()).isPresent()) {
                 stringBuilder.append("Invalid task").append(System.lineSeparator());
                 continue;
             }
                if (carRepository.findById(task.getCar().getId()).isPresent() &&
                        partRepository.findById(task.getPart().getId()).isPresent()) {

                    final Car refCar = carRepository.findById(task.getCar().getId()).get();
                    final Part refPart = partRepository.findById(task.getPart().getId()).get();
                    final Mechanic refMechanic = mechanicRepository.findByFirstName (task.getMechanic().getFirstName()).get();

                    final Task taskToSave = this.modelMapper.map(task, Task.class);

                    taskToSave.setCar(refCar);
                    taskToSave.setPart(refPart);
                    taskToSave.setMechanic(refMechanic);

                    this.taskRepository.saveAndFlush(taskToSave);


                }

                stringBuilder.append(String.format("Successfully imported task  %.2f", task.getPrice())).append(System.lineSeparator());
                continue;
            }
            stringBuilder.append("Invalid task").append(System.lineSeparator());

        }

        return stringBuilder.toString();
    }


    @Override
    public String getCoupeCarTasksOrderByPrice() {
        final Set<Task> tasks = this.taskRepository
                .findAllByCar_CarTypeOrderByPriceDesc(CarType.coupe)
                .orElseThrow(NoSuchElementException::new);


        return tasks.stream()
                .map(task -> String.format(PRINT_FORMAT,
                        task.getCar().getCarModel(),
                        task.getCar().getCarMake(),
                        task.getCar().getKilometers(),
                        task.getMechanic().getFirstName(),
                        task.getMechanic().getLastName(),
                task.getId(),
                task.getCar().getEngine(),
                        task.getPrice()))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
