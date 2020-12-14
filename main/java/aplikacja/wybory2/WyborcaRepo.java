package aplikacja.wybory2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WyborcaRepo extends JpaRepository <Wyborca, Integer> {
    List<Wyborca> findAllBynazwisko(String wyszukaj);
}
