package edu.hm.cs.katz.swt2.agenda.service;

import edu.hm.cs.katz.swt2.agenda.common.UuidProviderImpl;
import edu.hm.cs.katz.swt2.agenda.common.VisibilityEnum;
import edu.hm.cs.katz.swt2.agenda.persistence.Topic;
import edu.hm.cs.katz.swt2.agenda.persistence.TopicRepository;
import edu.hm.cs.katz.swt2.agenda.persistence.User;
import edu.hm.cs.katz.swt2.agenda.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TopicServiceTest {
  @Mock
  TopicRepository topicRepository;

  @Mock
  UuidProviderImpl uuidProvider;

  @Mock
  UserRepository userRepository;
    
  @InjectMocks
  TopicService topicService = new TopicServiceImpl();
    
  @Test
  void createTopicSuccess() {
    ArgumentCaptor<Topic> topicCaptor = ArgumentCaptor.forClass(Topic.class);
    topicService.createTopic("Topic12345", VisibilityEnum.PUBLIC, "This is the Short Description", "", "ernie");
    Mockito.verify(topicRepository).save(topicCaptor.capture());
    assertEquals("Topic12345", topicCaptor.getValue().getTitle());
  }

  @Test
  void getPublicTopicListSuccess() {
    ArgumentCaptor<Topic> topicCaptor = ArgumentCaptor.forClass(Topic.class);
    topicService.createTopic("Topic12345", VisibilityEnum.PUBLIC, "This is the Short Description", "", "ernie");
    topicService.getPublicTopics("ernie", "");
    Mockito.verify(topicRepository).findAllByVisibilityPublic();
  }
}
