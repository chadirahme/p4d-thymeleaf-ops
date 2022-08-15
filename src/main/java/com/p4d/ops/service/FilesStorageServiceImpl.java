package com.p4d.ops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
@Service
public class FilesStorageServiceImpl implements FilesStorageService{

    @Autowired
    AwsS3Service awsS3Service;

//    @Value("${uploads}")
//    private String uploads;
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final Path root = Paths.get(UPLOAD_DIR);

    @Override
    public void init() {

    }

    @Override
    public void save(MultipartFile file) {
        try {
            awsS3Service.putObject(file,"home");
           // Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()) , StandardCopyOption.REPLACE_EXISTING);
            //Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }
}
