package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.common.SecurityHelper;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.UserService;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.UserDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/**
 * Abstrakte Basisklasse für alle Controller, sorgt dafür, dass einige Verwaltungsattribute immer an
 * die Views übertragen werden.
 *
 * @author Bastian Katz (mailto: bastian.katz@hm.edu)
 */
public abstract class AbstractController {

  @Autowired
  private UserService userService;

  @Autowired
  private TaskService taskService;

  @ModelAttribute("administration")
  private boolean isAdministrator(Authentication auth) {
    return SecurityHelper.isAdmin(auth);
  }

  @ModelAttribute("user")
  private UserDisplayDto user(Authentication auth) {
    if (auth != null) {
      UserDisplayDto anwenderInfo = userService.getUserInfo(auth.getName());
      return anwenderInfo;
    }
    return null;
  }

  @ModelAttribute("search")
  private Search search(Authentication auth) {
    if (auth != null) {
      Search s = new Search();
      return s;
    }
    return null;
  }

  @ModelAttribute("numberOfOpenTasks")
  private Integer openTasks(Authentication auth){
    try {
      List<SubscriberTaskDto> openTasks = taskService.getAllTasksForStatus(auth.getName(), StatusEnum.OFFEN, "");
      openTasks.addAll(taskService.getAllTasksForStatus(auth.getName(), StatusEnum.NEU, ""));
      return openTasks.size();
    } catch (Exception e){
      return 0;
    }
  }
}