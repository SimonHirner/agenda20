package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.TaskDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TaskController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  /**
   * Ertellt das Formular zur Erfassung eines neuen Tasks.
   */
  @GetMapping("/topics/{uuid}/createTask")
  public String getTaskCreationView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("newTask", new TaskDto(null, "", "", "", topic));
    return "task-creation";
  }

  /**
   * Verarbeitet die Erstellung eines Tasks.
   */
  @PostMapping("/topics/{uuid}/createTask")
  public String handleTaskCreation(Model model, Authentication auth,
      @PathVariable("uuid") String uuid, @ModelAttribute("newTask") TaskDto newTask,
      RedirectAttributes redirectAttributes) {
    try {
      taskService.createTask(uuid, newTask.getTitle(), newTask.getShortDescription(), 
          newTask.getLongDescription(), auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + uuid + "/createTask";
    }
    redirectAttributes.addFlashAttribute("success",
        "Task \"" + newTask.getTitle() + "\" erstellt.");
    return "redirect:/topics/" + uuid + "/manage";
  }

  /**
   * Erstellt die Taskansicht für Abonnenten.
   */
  @GetMapping("tasks/{id}")
  public String getSubscriberTaskView(Model model, Authentication auth,
      @PathVariable("id") Long taskId) {
    TaskDto task = taskService.getTask(taskId, auth.getName());
    StatusDto status = taskService.getStatus(taskId, auth.getName());
    model.addAttribute("task", task);
    model.addAttribute("status", status);
    return "task";
  }
  
  /**
   * Verarbeitet das Kommentieren eines Tasks.
   */
  @PostMapping("tasks/{id}")
  public String handleComment(@ModelAttribute("status") StatusDto status, Authentication auth,
      @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.updateComment(id, auth.getName(), status.getComment());
    return "redirect:" + referer;
  }

  /**
   * Erstellt die Taskansicht für den Verwalter/Ersteller eines Topics.
   */
  @GetMapping("tasks/{id}/manage")
  public String getManagerTaskView(Model model, Authentication auth, @PathVariable("id") Long id) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    List<StatusDto> statusesWithComment = task.getStatusesWithComment();
    model.addAttribute("task", task);
    model.addAttribute("statusesWithComment", statusesWithComment);
    return "task-management";
  }
  
  /**
   * Verarbeitet die Aktualisierung eines Tasks.
   */
  @PostMapping("tasks/{id}/manage")
  public String handleUpdate(@ModelAttribute("task") TaskDto task, Authentication auth,
      @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer) {
    taskService.updateTask(id, auth.getName(), task.getShortDescription(), 
        task.getLongDescription());
    
    return "redirect:" + referer;
  }

  /**
   * Verarbeitet die Löschung eines Tasks.
   */
  @PostMapping("tasks/{id}/delete")
  public String handleDeletion(Authentication auth, @PathVariable("id") Long id,
      RedirectAttributes redirectAttributes) {
    TaskDto task = taskService.getManagedTask(id, auth.getName());
    
    try {
      taskService.deleteTask(id, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + task.getTopic().getUuid() + "/manage";
    }
    redirectAttributes.addFlashAttribute("success",
        "Task \"" + task.getTitle() + "\" gelöscht.");
    return "redirect:/topics/" + task.getTopic().getUuid() + "/manage";
  }
  
  /**
   * Verarbeitet die Markierung eines Tasks als "Done".
   */
  @PostMapping("tasks/{id}/check")
  public String handleTaskChecking(Model model, Authentication auth, @PathVariable("id") Long id) {
    taskService.checkTask(id, auth.getName());
    return "redirect:/tasks";
  }
  
  /**
   * Verarbeitet das Zurücksetzen des Status eines Tasks.
   */
  @PostMapping("tasks/{id}/reset")
  public String handleTaskReset(Model model, Authentication auth, @PathVariable("id") Long id) {
    taskService.resetTask(id, auth.getName());
    return "redirect:/tasks";
  }

  /**
   * Erstellt die Übersicht aller Tasks abonnierter Topics für einen Anwender.
   */
  @GetMapping("tasks")
  public String getSubscriberTaskListView(Model model, Authentication auth, 
      @RequestParam(name = "search", required = false, defaultValue = "") String search) {
    List<SubscriberTaskDto> openTasks = taskService.getAllTasksForStatus(auth.getName(),
        StatusEnum.OFFEN, search);
    openTasks.addAll(taskService.getAllTasksForStatus(auth.getName(), StatusEnum.NEU, search));
    List<SubscriberTaskDto> finishedTasks = taskService.getAllTasksForStatus(auth.getName(),
        StatusEnum.FERTIG, search);
    model.addAttribute("search", new Search());
    model.addAttribute("openTasks", openTasks);
    model.addAttribute("finishedTasks", finishedTasks);
    return "task-listview";
  }
}
