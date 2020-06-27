package edu.hm.cs.katz.swt2.agenda.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.hm.cs.katz.swt2.agenda.service.FileService;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class TaskControllerTest {
  
  @Autowired
  MockMvc mvc;
  
  @MockBean
  TopicService topicService;
  
  @MockBean
  TaskService taskService;
  
  @MockBean
  UserService userService;

  @MockBean
  FileService fileService;
  
  @Test
  @WithMockUser(username = "finn", password = "user", roles = "USER")
  public void testGetTasks() throws Exception {
    
    UserDisplayDto finn = new UserDisplayDto();
    finn.setLogin("finn");
    
    Mockito.when(userService.getUserInfo("finn")).thenReturn(finn);
    
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/tasks"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  
    UserDisplayDto user = (UserDisplayDto) result.getModelAndView().getModel().get("user");
    
    assertEquals("finn", user.getLogin());
  }
  
}
