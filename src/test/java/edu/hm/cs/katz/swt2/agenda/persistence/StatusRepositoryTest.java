package edu.hm.cs.katz.swt2.agenda.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class StatusRepositoryTest {
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  TopicRepository topicRepository;
  
  @Autowired
  StatusRepository statusRepository;
  
  @Autowired
  TaskRepository taskRepository;
  
  private static final String UUID_BASE = "12345678901234567890123456789012345";
  
  @Test
  public void statusRepositoryDeliversStatusByUserAndTask() {
    User user = new User("tiffy", "Tiffy", "#Tiffy2020", false);
    userRepository.save(user);
    
    Topic topic = new Topic(UUID_BASE + "1", "Ttttttttttt", "Beschreibung", "Beschreibung", user);
    topicRepository.save(topic);
    
    Task task = new Task(topicRepository.getOne(UUID_BASE + "1"), "Ttttttttttt", "Beschreibung",
        "Beschreibung");
    taskRepository.save(task);
    
    Status status = new Status(taskRepository.findAll().get(0), userRepository.getOne("tiffy"));
    statusRepository.save(status);
    
    Status result = statusRepository.findByUserAndTask(user, task);
    
    assertEquals(status, result);
  }  
}
