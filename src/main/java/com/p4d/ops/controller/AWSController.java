package com.p4d.ops.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.p4d.ops.models.Greeting;
import com.p4d.ops.service.AwsS3Service;
import com.p4d.ops.service.FilesStorageService;
import com.p4d.ops.service.impl.FilesStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class AWSController {

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    private final AwsS3Service awsS3Service;
   // @Qualifier("FilesStorageServiceImpl")
    @Autowired
    FilesStorageService filesStorageService;

    public AWSController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
      //  this.filesStorageService=filesStorageService;
    }

    @GetMapping("/aws")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "aws";
    }

    @PostMapping("/aws")
    public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
        List<S3ObjectSummary> lst=awsS3Service.getBucketObjects();

//        List<Bucket> buckets = awsS3Service.getS3Buckets();
//        for(Bucket bucket : buckets) {
//            System.out.println(bucket.getName());
//        }
        model.addAttribute("greeting", greeting);
        model.addAttribute("s3", lst);
        return "result";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {

        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // save the file on the local file system
        try {
           // Path path = Paths.get(UPLOAD_DIR + fileName);
            filesStorageService.save(file);
            //Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return success response
        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');
        attributes.addFlashAttribute("error", "You successfully uploaded " + fileName + '!');

        return "redirect:/";
    }
}
