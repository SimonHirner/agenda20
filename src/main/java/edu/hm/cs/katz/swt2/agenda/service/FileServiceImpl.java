package edu.hm.cs.katz.swt2.agenda.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

  private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

  private final Path rootLocation = Paths.get("uploads");

  @Override
  @PreAuthorize("#login == authentication.name or hasRole('ROLE_ADMIN')")
  public void store(MultipartFile file, String login) {
    LOG.info("Datei {} wird gespeichert.", file.getName());
    LOG.debug("Datei wird von von {} gespeichert.", login);
    try {
      Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
    } catch (Exception e) {
      LOG.error("Datei {} konnte nicht gespeichert werden.", file.getName());
      throw new RuntimeException("Datei konnte nicht gespeichert werden! " + e.getMessage());
    }
  }

  @Override
  public Resource loadFile(String filename) {
    LOG.info("Datei {} wird geladen.", filename);
    try {
      Path file = rootLocation.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        LOG.error("Datei {} konnte nicht geladen werden.", filename);
        throw new RuntimeException("Datei konnte nicht geladen werden!");
      }
    } catch (MalformedURLException e) {
      LOG.error("Datei {} konnte nicht geladen werden.", filename);
      throw new RuntimeException("Fehler beim Laden der Datei! " + e.getMessage());
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void init() {
    try {
      Files.createDirectory(rootLocation);
    } catch (IOException e) {
      throw new RuntimeException("Speicherplatz konnte nicht initialisiert werden!");
    }
  }

  @Override
  public Stream<Path> loadFiles() {
    try {
      return Files.walk(this.rootLocation, 1)
                       .filter(path -> !path.equals(this.rootLocation))
                       .map(this.rootLocation::relativize);
    } catch (IOException e) {
      throw new RuntimeException("\"Fehler beim Lesen der gespeichterten Dateien!");
    }
  }
}