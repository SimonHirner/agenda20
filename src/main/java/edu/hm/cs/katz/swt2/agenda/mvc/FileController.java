package edu.hm.cs.katz.swt2.agenda.mvc;

import edu.hm.cs.katz.swt2.agenda.common.FileInfo;
import edu.hm.cs.katz.swt2.agenda.service.FileService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FileController extends AbstractController {

  @Autowired
  FileService fileService;

  @GetMapping("tasks/{id}/manage/upload")
  public String index() {
    return "upload-listview";
  }

  /**
   * Upload Files.
   */
  @PostMapping("tasks/{id}/manage/upload")
  public String uploadMultipartFile(@RequestParam("uploadfile") MultipartFile file, Model model,
      RedirectAttributes redirectAttributes, Authentication auth) {
    try {
      fileService.store(file, auth.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("failure", "Die Datei " + file.getName() 
          + " konnte nicht geladen werden!");
    }
    return "redirect:/tasks/{id}/manage/upload";
  }

  /**
   * Retrieve Files' Information.
   */
  @ModelAttribute("files")
  public List<FileInfo> getListFiles(Model model) {
    List<FileInfo> fileInfos = fileService.loadFiles().map(
        path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder.fromMethodName(FileController.class,
                    "downloadFile", path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }
    )
                                       .collect(Collectors.toList());
    return fileInfos;
  }

  /**
   * Download Files.
   */
  @GetMapping("/files/{filename}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
    Resource file = fileService.loadFile(filename);
    return ResponseEntity.ok()
                   .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                       + file.getFilename() + "\"")
                   .body(file);
  }

}