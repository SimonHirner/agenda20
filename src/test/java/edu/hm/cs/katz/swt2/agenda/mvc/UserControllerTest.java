package edu.hm.cs.katz.swt2.agenda.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import java.util.ArrayList;
import java.util.List;
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
public class UserControllerTest {
  
  @Autowired
  MockMvc mvc;
  
  @MockBean
  TopicService topicService;
  
  @MockBean
  TaskService taskService;
  
  @MockBean
  UserService userService;
  
  @Test
  @WithMockUser(username = "finn", password = "user", roles = "USER")
  public void testGetUsers() throws Exception {
    
    UserDisplayDto finn = new UserDisplayDto();
    finn.setLogin("finn");
    List<UserDisplayDto> list = new ArrayList<UserDisplayDto>();
    list.add(finn);
    
    Mockito.when(userService.getUserInfo("finn")).thenReturn(finn);
    Mockito.when(userService.getAllUsers()).thenReturn(list);
    
    MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users"))
        .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
  
    UserDisplayDto user = (UserDisplayDto) result.getModelAndView().getModel().get("user"); 
    @SuppressWarnings("unchecked")
    List<UserDisplayDto> users = (ArrayList<UserDisplayDto>) 
        result.getModelAndView().getModel().get("users"); 
    
    assertEquals("finn", user.getLogin());
    assertEquals("finn", users.get(0).getLogin());
  }
  
}
