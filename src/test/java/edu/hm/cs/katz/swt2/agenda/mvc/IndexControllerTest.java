package edu.hm.cs.katz.swt2.agenda.mvc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class IndexControllerTest {
  
  @Autowired
  MockMvc mvc;
  
  @MockBean
  TopicService topicService;
  
  @MockBean
  TaskService taskService;
  
  @MockBean
  UserService userService;
  
  @Test
  public void testGetIndex() throws Exception {
    MvcResult indexPage = mvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  
    assertTrue(indexPage.getResponse().getContentAsString().contains("Agenda"));
  }  
}
