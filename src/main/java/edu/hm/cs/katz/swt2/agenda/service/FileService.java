package edu.hm.cs.katz.swt2.agenda.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public void store(MultipartFile file, String login);

    public Resource loadFile(String filename);

    public void deleteAll();

    public void init();

    public Stream<Path> loadFiles();
}