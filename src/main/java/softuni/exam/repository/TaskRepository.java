package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.constant.CarType;
import softuni.exam.models.entity.Task;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional <Set<Task>> findAllByCar_CarTypeOrderByPriceDesc (CarType carType);

}
