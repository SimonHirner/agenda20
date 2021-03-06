package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.common.DateUtilities;
import edu.hm.cs.katz.swt2.agenda.common.FileInfo;
import edu.hm.cs.katz.swt2.agenda.common.StatusEnum;
import edu.hm.cs.katz.swt2.agenda.service.FileService;
import edu.hm.cs.katz.swt2.agenda.service.TaskService;
import edu.hm.cs.katz.swt2.agenda.service.TopicService;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.OwnerTopicDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.StatusDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.SubscriberTaskDto;
import edu.hm.cs.katz.swt2.agenda.service.dto.TaskDto;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TaskController extends AbstractController {

  @Autowired
  private TopicService topicService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private FileService fileService;

  /**
   * Ertellt das Formular zur Erfassung eines neuen Tasks.
   */
  @GetMapping("/topics/{uuid}/createTask")
  public String getTaskCreationView(Model model, Authentication auth,
      @PathVariable("uuid") String uuid) {
    OwnerTopicDto topic = topicService.getManagedTopic(uuid, auth.getName());
    model.addAttribute("topic", topic);
    model.addAttribute("newTask", new TaskDto(null, "", "", "", null, topic));
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
          newTask.getLongDescription(), auth.getName(), newTask.getDeadline(),
          DateUtilities.getCurrentDate());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:/topics/" + uuid + "/createTask";
    }
    redirectAttributes.addFlashAttribute("success",
        "Task \"" + newTask.getTitle() + "\" erstellt.");
    return "redirect:/topics/" + uuid + "/manage";
  }

  /**
   * Erstellt die Taskansicht f??r Abonnenten.
   */
  @GetMapping("tasks/{id}")
  public String getSubscriberTaskView(Model model, Authentication auth,
      @PathVariable("id") Long taskId) {
    TaskDto task = taskService.getTask(taskId, auth.getName());
    StatusDto status = taskService.getStatus(taskId, auth.getName());
    List<FileInfo> fileInfos = fileService.loadFiles().map(
        path -> {
          String filename = path.getFileName().toString();
          String url = MvcUriComponentsBuilder.fromMethodName(FileController.class,
                  "downloadFile", path.getFileName().toString()).build().toString();
          return new FileInfo(filename, url);
        }
    )
                                       .collect(Collectors.toList());
    model.addAttribute("task", task);
    model.addAttribute("status", status);
    model.addAttribute("files", fileInfos);
    model.addAttribute("currentDate", DateUtilities.getCurrentDate());
    return "task";
  }
  
  /**
   * Verarbeitet das Kommentieren eines Tasks.
   */
  @PostMapping("tasks/{id}")
  public String handleComment(@ModelAttribute("status") StatusDto status, Authentication auth,
      @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer,
      RedirectAttributes redirectAttributes) {
    try {
      taskService.updateComment(id, auth.getName(), status.getComment());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Kommentar aktualisiert.");  
    return "redirect:" + referer;
  }

  /**
   * Erstellt die Taskansicht f??r den Verwalter/Ersteller eines Topics.
   */
  @GetMapping("tasks/{id}/manage")
  public String getManagerTaskView(Model model, Authentication auth, @PathVariable("id") Long id) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    List<StatusDto> statusesWithComment = task.getStatusesWithComment();
    List<FileInfo> fileInfos = fileService.loadFiles().map(
        path -> {
          String filename = path.getFileName().toString();
          String url = MvcUriComponentsBuilder.fromMethodName(FileController.class,
                  "downloadFile", path.getFileName().toString()).build().toString();
          return new FileInfo(filename, url);
        }
    )
                                       .collect(Collectors.toList());
    model.addAttribute("task", task);
    model.addAttribute("files", fileInfos);
    model.addAttribute("statusesWithComment", statusesWithComment);
    return "task-management";
  }

  /**
   * Verarbeitet die Aktualisierung eines Tasks.
   */
  @PostMapping("tasks/{id}/manage")
  public String handleUpdate(@ModelAttribute("task") TaskDto task, Authentication auth,
      @PathVariable("id") Long id,
      @RequestHeader(value = "referer", required = true) String referer,
      RedirectAttributes redirectAttributes) {
    
    try {
      taskService.updateTask(id, auth.getName(), task.getShortDescription(), 
          task.getLongDescription(), task.getDeadline(), DateUtilities.getCurrentDate());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Task aktualisiert.");  
    return "redirect:" + referer;
  }

  /**
   * Verarbeitet die L??schung eines Tasks.
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
        "Task \"" + task.getTitle() + "\" gel??scht.");
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
   * Verarbeitet das Zur??cksetzen des Status eines Tasks.
   */
  @PostMapping("tasks/{id}/reset")
  public String handleTaskReset(Model model, Authentication auth, @PathVariable("id") Long id) {
    taskService.resetTask(id, auth.getName());
    return "redirect:/tasks";
  }

  /**
   * Erstellt die ??bersicht aller Tasks abonnierter Topics f??r einen Anwender.
   */
  @GetMapping("tasks")
  public String getSubscriberTaskListView(Model model, Authentication auth, 
      @RequestParam(name = "search", required = false, defaultValue = "") String search) {
    List<SubscriberTaskDto> openTasks = taskService.getAllTasksForStatus(auth.getName(),
        StatusEnum.OFFEN, search);
    openTasks.addAll(taskService.getAllTasksForStatus(auth.getName(), StatusEnum.NEU, search));
    List<SubscriberTaskDto> finishedTasks = taskService.getAllTasksForStatus(auth.getName(),
        StatusEnum.FERTIG, search);
    List<SubscriberTaskDto> expiredTasks = taskService.getAllTasksForStatus(auth.getName(),
        StatusEnum.ABGELAUFEN, search);
    model.addAttribute("search", new Search());
    model.addAttribute("openTasks", openTasks);
    model.addAttribute("finishedTasks", finishedTasks);
    model.addAttribute("expiredTasks", expiredTasks);
    
    model.addAttribute("currentDate", DateUtilities.getCurrentDate());
    return "task-listview";
  }
  
  /**
   * Erstellt ??bersicht ??ber die Bewertungen eines Tasks.
   */
  @GetMapping("tasks/{id}/statuses")
  public String createRatingsListView(Model model, Authentication auth,
      @PathVariable("id") Long id) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    List<StatusDto> statuses = taskService.getStatuses(id, auth.getName());
    model.addAttribute("task", task);
    model.addAttribute("statuses", statuses);
    return "status-listview";
  }
  
  /**
   * Erstellt ??bersicht ??ber die Bewertungen eines Tasks.
   */
  @GetMapping("tasks/{id}/{login}/rating")
  public String createRatingView(Model model, Authentication auth,
      @PathVariable("id") Long id, @PathVariable("login") String login) {
    OwnerTaskDto task = taskService.getManagedTask(id, auth.getName());
    StatusDto status = taskService.getStatus(id, login);
    model.addAttribute("task", task);
    model.addAttribute("status", status);
    return "task-rating";
  }
  
  /**
   * Verarbeitet die Bewertung eines Tasks.
   */
  @PostMapping("tasks/{id}/{login}/rating")
  public String handleRating(Model model, Authentication auth, @PathVariable("id") Long id, 
      @ModelAttribute("status") StatusDto status, @PathVariable("login") String login,
      @RequestHeader(value = "referer", required = true) String referer,
      RedirectAttributes redirectAttributes) {    
    try {
      taskService.updateRating(id, auth.getName(), login, status.getRating());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return "redirect:" + referer;
    }
    redirectAttributes.addFlashAttribute("success", "Bewertung aktualisiert.");  
    return "redirect:" + referer; 
  }
}