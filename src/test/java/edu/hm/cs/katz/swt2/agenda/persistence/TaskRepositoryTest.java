package edu.hm.cs.katz.swt2.agenda.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.hm.cs.katz.swt2.agenda.common.VisibilityEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

@DataJpaTest
public class TaskRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    TaskRepository taskRepository;

    private static final String UUID_BASE = "12345678901234567890123456789012345";

    @Test
    public void taskRepositoryDeliversTasksByTopicOdered() {
        User user = new User("tiffy", "Tiffy", "#Tiffy2020", false);
        userRepository.save(user);

        Topic topic = new Topic(UUID_BASE + "1", "Ttttttttttt", VisibilityEnum.PUBLIC, "Beschreibung", "Beschreibung", user);
        topicRepository.save(topic);

        Task taskA = new Task(topicRepository.getOne(UUID_BASE + "1"), "Ttttttttttt", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskA);

        Task taskB = new Task(topicRepository.getOne(UUID_BASE + "1"), "Xxxxxxxxxxxxx", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskB);

        Task taskC = new Task(topicRepository.getOne(UUID_BASE + "1"), "Uuuuuuuuuuuuu", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskC);

        List<Task> tasks = (List<Task>) taskRepository.findAllByTopic(topic,
                Sort.by(Sort.Order.asc("title").ignoreCase()));

        assertEquals(3, tasks.size());
        assertEquals(taskA, tasks.get(0));
        assertEquals(taskC, tasks.get(1));
        assertEquals(taskB, tasks.get(2));
    }

    @Test
    public void taskRepositoryDeliversTasksByTopicsOdered() {
        User user = new User("tiffy", "Tiffy", "#Tiffy2020", false);
        userRepository.save(user);

        Collection<Topic> topics = new ArrayList<Topic>();

        Topic topicA = new Topic(UUID_BASE + "1", "Ttttttttttt", VisibilityEnum.PUBLIC, "Beschreibung", "Beschreibung", user);
        topicRepository.save(topicA);
        topics.add(topicA);

        Topic topicB = new Topic(UUID_BASE + "2", "Xxxxxxxxxxxx", VisibilityEnum.PUBLIC, "Beschreibung", "Beschreibung", user);
        topicRepository.save(topicB);
        topics.add(topicB);

        Task taskA = new Task(topicRepository.getOne(UUID_BASE + "1"), "Ttttttttttt", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskA);

        Task taskB = new Task(topicRepository.getOne(UUID_BASE + "1"), "Xxxxxxxxxxxxx", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskB);

        Task taskC = new Task(topicRepository.getOne(UUID_BASE + "2"), "Uuuuuuuuuuuuu", "Beschreibung",
                "Beschreibung");
        taskRepository.save(taskC);

        List<Task> tasks = (List<Task>) taskRepository.findAllByTopicIn(topics,
                Sort.by(Sort.Order.asc("title").ignoreCase()));

        assertEquals(3, tasks.size());
        assertEquals(taskA, tasks.get(0));
        assertEquals(taskC, tasks.get(1));
        assertEquals(taskB, tasks.get(2));
    }

}
