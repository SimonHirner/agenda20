package edu.hm.cs.katz.swt2.agenda.persistence;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository zum Zugriff auf gespeicherte Topics. Repostory-Interfaces erben eine unglaubliche
 * Menge hilfreicher Methoden. Weitere Methoden kann man einfach durch Benennung definierern. Spring
 * Data ergänzt die Implementierungen zur Laufzeit.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {

    /**
     * Findet alle Topics zu einem gegebenen Anwender.
     *
     * @param creator Anwender
     * @return
     */
    List<Topic> findByCreator(User creator);

    /**
     * Zähle alle Topics, die von einem bestimmten User erstellt wurden.
     *
     * @param user Creator
     * @return
     */
    int countByCreator(User user);

    /**
     * Finde alle Topics, die von einem bestimmten User erstellt wurden und sortiert diese
     * entsprechend.
     *
     * @param creator Topic Ersteller
     * @param sort    Gewünschte Sortierung
     * @return
     */
    List<Topic> findAllByCreator(User creator, Sort sort);

    /**
     * Findet alle abonnierten Topics und sortiert diese entsprechend.
     *
     * @param subscriber Abonnent
     * @param sort       Gewünschte Sortierung
     * @return
     */
    List<Topic> findAllBySubscribers(User subscriber, Sort sort);

    /**
     * Findet Topic zu einer UUID-Endung.
     *
     * @param key UUID-Endung
     * @return
     */
    Topic findByUuidEndingWith(String key);

    /**
     * Findet alle öffentlichen Topics.
     */
    @Query(value = "SELECT * FROM Topic WHERE Visibility = 1", nativeQuery = true)
    List<Topic> findAllByVisibilityPublic();
}
