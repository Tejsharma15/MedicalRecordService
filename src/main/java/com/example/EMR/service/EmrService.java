package com.example.EMR.service;

import com.example.EMR.Exception.ResourceNotFoundException;
import com.example.EMR.dto.CreateEmrDtoText;
import com.example.EMR.dto.UpdateEmrDto;
import com.example.EMR.dto.UpdateEmrDtoText;
import com.example.EMR.models.Document;
import com.example.EMR.models.Emr;
import com.example.EMR.property.DocumentStorageProperty;
import com.example.EMR.repository.DocumentRepository;
import com.example.EMR.repository.EmrRepository;
import jakarta.transaction.Transactional;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.awt.image.BufferedImage;
import org.springframework.data.util.Pair;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.imageio.ImageIO;

@Service
@Transactional
public class EmrService {
    private final EmrRepository emrRepository;
    private final DocumentRepository documentRepository;
    private final PublicPrivateService publicPrivateService;
    private final Path emrStorageLocation;

    @Autowired
    EmrService(EmrRepository emrRepository, DocumentRepository documentRepository,
            DocumentStorageProperty documentStorageProperty, PublicPrivateService publicPrivateService) {
        this.emrRepository = emrRepository;
        this.emrStorageLocation = Paths.get(documentStorageProperty.getUploadDirectory());
        this.documentRepository = documentRepository;
        this.publicPrivateService = publicPrivateService;
    }

    public ResponseEntity<?> getPrescriptionByEmrIdText(String publicEmrId) throws IOException {
        UUID emrId = publicPrivateService.privateIdByPublicId(publicEmrId);
        // Define the categories and corresponding file paths
        String[] categories = { "Prescriptions"};
        String basePath = this.emrStorageLocation.toString() + "/"; // Adjust the base path

        Map<String, List<ImageTimestamp>> fileImageMap = new HashMap<>();
        for (String category : categories) {
            String categoryPath = basePath + category + "/" + emrId.toString() + "/";
            try {
                List<ImageTimestamp> temp = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                fileImageMap.put(category, temp);
                
                Files.walk(Path.of(categoryPath))
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            try {
                                String fileName = filePath.getFileName().toString();
                                System.out.println(fileName + " " +filePath);
                                if(fileName.endsWith(".png")){
                                    BufferedImage img = ImageIO.read(filePath.toFile());
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    System.out.println(img + " " + filePath);
                                    ImageIO.write(img, "png", baos);
                                    byte[] imgBytes = baos.toByteArray();
                                    String timestamp = fileName.substring(0, fileName.lastIndexOf('.')).replace("_", ":").replace("$", ".");
                                    List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                    // Add the value to the list
                                    list.add(new ImageTimestamp(imgBytes, timestamp));
                                    // Put the list back into the map
                                    fileImageMap.put(category, list);
                                }
                                else{
                                    System.out.println("Text file");
                                    String textContent = Files.readString(filePath);
                                    String timestamp = fileName.substring(0, fileName.lastIndexOf('.')).replace("_", ":").replace("$", ".");
                                    List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                    // Add the value to the list
                                    list.add(new ImageTimestamp(textContent, timestamp));
                                    // Put the list back into the map
                                    fileImageMap.put(category, list);
                                }
                            } catch (IOException e) {
                                System.out.println("ekvjev j");
                                List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                // Add the value to the list
                                list.add(new ImageTimestamp( ""));
                                // Put the list back into the map
                                fileImageMap.put(category, list);
                                // fileImageMap.put(category, new ImageTimestamp(null, ""));
                            }
                        });
            } catch (IOException e) {
                throw new ResourceNotFoundException("Can't find records for the emr: " + publicEmrId);
            }
        }

        return ResponseEntity.ok().body(fileImageMap);
    }

    public ResponseEntity<?> getCommentsByEmrIdText(String publicEmrId) throws FileNotFoundException {
        UUID id = publicPrivateService.privateIdByPublicId(publicEmrId);
        UUID emrId = emrRepository.getEmrIdByPrivateEmrId(id);
        String categoryPath = this.emrStorageLocation + "/Comments/" + emrId.toString() + "/";
        System.out.println(categoryPath);
        Map<String, String> fileTextMap = new HashMap<>();
        try {
            Files.walk(Path.of(categoryPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String fileName = filePath.getFileName().toString();
                            String textContent = Files.readString(filePath);
                            fileTextMap.put("Comments", textContent);
                        } catch (IOException e) {
                            String fileName = filePath.getFileName().toString();
                            fileTextMap.put("Comments", "");
                        }
                    });
            // nestedMap.put(category, fileTextMap);
        } catch (IOException e) {
            System.out.println(e);
        }
        return ResponseEntity.ok().body(fileTextMap);
    }

    public ResponseEntity<?> getTestsByEmrIdText(UUID publicEmrId) throws FileNotFoundException {
        UUID id = publicPrivateService.privateIdByPublicId(publicEmrId.toString());
        UUID emrId = emrRepository.getEmrIdByPrivateEmrId(id);
        String basePath = "/doc-uploads/Tests/";
        String categoryPath = basePath + emrId.toString() + "/";
        Map<String, String> fileTextMap = new HashMap<>();
        try {
            Files.walk(Path.of(categoryPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            String fileName = filePath.getFileName().toString();
                            String textContent = Files.readString(filePath);
                            fileTextMap.put("Tests", textContent);
                        } catch (IOException e) {
                            String fileName = filePath.getFileName().toString();
                            fileTextMap.put("Tests", "");
                        }
                    });
            // nestedMap.put(category, fileTextMap);
        } catch (IOException e) {
            System.out.println("empty");
        }
        return ResponseEntity.ok().body(fileTextMap);
    }

    public String insertConsulationEmr(CreateEmrDtoText createEmrDtoText) throws IOException {
        Emr obj = new Emr();
        obj.setPatientId(createEmrDtoText.getPatientId());
        obj.setAccessDepartments(createEmrDtoText.getAccessDepartments());
        obj.setAccessList(createEmrDtoText.getAccessList());
        obj.setComments("");
        obj.setPrescription("");
        obj.setTests("");
        obj.setLastUpdate(System.currentTimeMillis() / 1000);
        System.out.println("Created EMR object, storing now.");
        UUID privateId = emrRepository.save(obj).getEmrId();
        // Files.createDirectories(this.emrStorageLocation);
        Path pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Prescriptions/" + privateId + "/");
        Files.createDirectories(pngFilePath);
        pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Comments/" + privateId + "/");
        Files.createDirectories(pngFilePath);
        pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Tests/" + privateId + "/");
        Files.createDirectories(pngFilePath);

        return publicPrivateService.savePublicPrivateId(privateId, "EMR");
    }

    public static void convertStringToFile(String content, Path filePath) throws IOException {
        System.out.println(filePath.toString());
        // Create parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath.toFile(), true);
            // LocalDateTime currentDate = LocalDateTime.now();
            // Format date and time
            // String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Write formatted date, content, and newline character to file
            writer.write("\n" + content + "\n");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ResponseEntity<?> updateEmrByIdText(UpdateEmrDtoText updateEmrDtoText) throws NoSuchAlgorithmException {

        UUID id = publicPrivateService.privateIdByPublicId(updateEmrDtoText.getPublicEmrId().toString());
        System.out.println("id: " + id);
        System.out.println("/Prescriptions/" + id + "/");
        if(updateEmrDtoText.getIsImage()!=0){
            if (updateEmrDtoText.getPrescription() != null && updateEmrDtoText.getPrescription().length > 0) {
                
                try {
                    // Define the path to the text file and the output image
                    Path pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Prescriptions/" + id + "/"
                    + Instant.now().toString().replace(":", "_").replace(".", "$") + ".png");
                    Files.createDirectories(pngFilePath.getParent());
    
                    // // Read the SVG string from the text file
                    // String svgString = new String(Files.readAllBytes(txtFilePath));
                    String[] svgString = updateEmrDtoText.getPrescription();
                    // System.out.println(Arrays.toString(svgString));
                    // Convert the SVG string to a PNG image
                    convertSvgPathsToSinglePng(svgString, pngFilePath);
                    
                } catch (Exception e) {
                    // logger.error("Error converting SVG to PNG", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in saving prescription " + e);
                }
                
            }
            if (updateEmrDtoText.getComments() != null && updateEmrDtoText.getComments().length > 0) {
                try {
                    // Define the path to the text file and the output image
                    Path pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Comments/" + id + "/"
                            + Instant.now().toString().replace(":", "_").replace(".", "$") + ".png");
                    Files.createDirectories(pngFilePath.getParent());
                    
                    // // Read the SVG string from the text file
                    // String svgString = new String(Files.readAllBytes(txtFilePath));
                    String[] svgString = updateEmrDtoText.getComments();
                    
                    // Convert the SVG string to a PNG image
                    convertSvgPathsToSinglePng(svgString, pngFilePath);
                } catch (Exception e) {
                    // logger.error("Error converting SVG to PNG", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in saving prescription " + e);
                }
            }
            if (updateEmrDtoText.getTests() != null && updateEmrDtoText.getTests().length > 0) {
                
                try {
                    // Define the path to the text file and the output image
                    Path pngFilePath = Paths.get(this.emrStorageLocation.toString() + "/Tests/" + id + "/"
                    + Instant.now().toString().replace(":", "_").replace(".", "$") + ".png");
                    Files.createDirectories(pngFilePath.getParent());
                    
                    // // Read the SVG string from the text file
                    // String svgString = new String(Files.readAllBytes(txtFilePath));
                    String svgString[] = updateEmrDtoText.getTests();
                    
                    // Convert the SVG string to a PNG image
                    convertSvgPathsToSinglePng(svgString, pngFilePath);
                } catch (Exception e) {
                    // logger.error("Error converting SVG to PNG", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in saving prescription " + e);
                }
            }
        }
        if(updateEmrDtoText.getIsText() != 0){
            try {
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" + id
                + "/" + id + "/");
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getPrescriptiont().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getPrescriptiont());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/"
                + id + "/" + Instant.now().toString().replace(":", "_").replace(".", "$") + ".txt");
                convertStringToFile(updateEmrDtoText.getPrescriptiont(), prescriptionLocation);
                emrRepository.setPrescriptionLocation(id, prescriptionLocation.toString());
            }
            catch(IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving prescription " + e);
            }
            try {
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Tests/" + id + "/" +
                id + "/");
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getTestst().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getTestst());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Tests/" + id +
                "/" + Instant.now().toString().replace(":", "_").replace(".", "$") + ".txt");
                convertStringToFile(updateEmrDtoText.getTestst(), prescriptionLocation);
                emrRepository.setTestLocation(id, prescriptionLocation.toString());
            } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving tests");
            }
            
            try {
                System.out.println("Trying to add comments:");
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Comments/" + id + "/"
                + id + "/");
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getCommentst().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getCommentst());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Comments/" + id
                + "/" + Instant.now().toString().replace(":", "_").replace(".", "$") + ".txt");
                convertStringToFile(updateEmrDtoText.getCommentst(), prescriptionLocation);
                emrRepository.setCommentLocation(id, prescriptionLocation.toString());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving comments");
            }
        }
        // if(updateEmrDtoText.getIsAudio() == true){

        // }
        emrRepository.updateLastUpdate(id, System.currentTimeMillis() / 1000);
        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
    }

    public ResponseEntity<Map<String, String>> getEmrByPatientIdText(String pId) throws IOException {
        UUID patientId = publicPrivateService.privateIdByPublicId(pId);
        // Map<String, Map<String, String>> nestedMap = new HashMap<>();
        // Define the categories and corresponding file paths
        String[] categories = { "Prescriptions", "Comments", "Tests" };
        String basePath = this.emrStorageLocation.toString() + "/"; // Adjust the base path
        List<UUID> EmrId = emrRepository.getEmrByPatientId(patientId);
        Map<String, String> fileTextMap = new HashMap<>();
        for (int i = 0; i < EmrId.size(); i++) {
            UUID emrId = EmrId.get(i);
            for (String category : categories) {
                String categoryPath = basePath + category + "/" + emrId.toString() + "/";
                try {
                    Files.walk(Path.of(categoryPath))
                            .filter(Files::isRegularFile)
                            .forEach(filePath -> {
                                try {
                                    // String fileName = filePath.getFileName().toString();
                                    String textContent = Files.readString(filePath);
                                    fileTextMap.put(category, textContent);
                                } catch (IOException e) {
                                    // String fileName = filePath.getFileName().toString();
                                    fileTextMap.put(category, "");
                                }
                            });

                } catch (IOException e) {
                    System.out.println("empty");
                }
            }
        }

        return ResponseEntity.ok().body(fileTextMap);
    }

    public static void convertSvgPathsToSinglePng(String[] svgPathDatas, Path outputPath) throws Exception {
        // String[] arr
        // = { "The", "quick", "brown", "fox", "jumps",
        // "over", "the", "lazy", "dog" };
        // System.out.println("dhv kejbvkewvj "+Arrays.toString(arr));
        // Initialize the SVG content with a suitable viewBox size
        StringBuilder svgContent = new StringBuilder(
                "<svg xmlns=\"http://www.w3.org/2000/svg\">");

        // Append each SVG path to the SVG content
        for (String svgPathData : svgPathDatas) {
            // System.out.println(svgContent);
            svgContent.append("<path d=\"").append(svgPathData).append("\" stroke=\"black\" fill=\"none\"/>");
        }

        // Close the SVG tag
        svgContent.append("</svg>");

        // Set up the transcoder with the desired image dimensions
        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1280f);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 800f);

        // System.out.println(svgContent);
        // Read the SVG content
        try (StringReader stringReader = new StringReader(svgContent.toString());
                OutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
            TranscoderInput input = new TranscoderInput(stringReader);
            TranscoderOutput output = new TranscoderOutput(outputStream);
            transcoder.transcode(input, output);
        }
    }
    /*
     * try {
     * // Define the path to the text file and the output image
     * Path txtFilePath = Paths.
     * get("/media/karanjitsaha/DATA1/Semester 8/HAD/MedicalRecordService/sample.txt"
     * );
     * Path pngFilePath = Paths.get(txtFilePath.toString().replace(".txt", ".png"));
     * 
     * // Read the SVG string from the text file
     * String svgString = new String(Files.readAllBytes(txtFilePath));
     * 
     * // Convert the SVG string to a PNG image
     * convertSvgStringToPng(svgString, pngFilePath);
     * } catch (Exception e) {
     * logger.error("Error converting SVG to PNG", e);
     * }
     */

    // public ResponseEntity<?> getEmrByEmrIdText(String publicEmrId) throws
    // IOException{
    // UUID emrId = publicPrivateService.privateIdByPublicId(publicEmrId);
    // // Define the categories and corresponding file paths
    // String[] categories = {"Prescriptions", "Comments", "Tests"};
    // String basePath = this.emrStorageLocation.toString() + "/"; // Adjust the
    // base path

    // Map<String, String> fileTextMap = new HashMap<>();
    // for (String category : categories) {
    // String categoryPath = basePath + category + "/" + emrId.toString() + "/";
    // try {
    // Files.walk(Path.of(categoryPath))
    // .filter(Files::isRegularFile)
    // .forEach(filePath -> {
    // try {
    // String fileName = filePath.getFileName().toString();
    // String textContent = Files.readString(filePath);
    // fileTextMap.put(category, textContent);
    // } catch (IOException e) {
    // String fileName = filePath.getFileName().toString();
    // fileTextMap.put(category, "");
    // }
    // });
    // // nestedMap.put(category, fileTextMap);
    // } catch (IOException e) {
    // throw new ResourceNotFoundException("Can't find records for the emr:
    // "+publicEmrId);
    // }
    // }

    // return ResponseEntity.ok().body(fileTextMap);
    // }

    public ResponseEntity<?> getEmrByEmrIdText(String publicEmrId) throws IOException {
        UUID emrId = publicPrivateService.privateIdByPublicId(publicEmrId);
        // Define the categories and corresponding file paths
        String[] categories = { "Prescriptions", "Comments", "Tests" };
        String basePath = this.emrStorageLocation.toString() + "/"; // Adjust the base path

        Map<String, List<ImageTimestamp>> fileImageMap = new HashMap<>();
        for (String category : categories) {
            String categoryPath = basePath + category + "/" + emrId.toString() + "/";
            try {
                List<ImageTimestamp> temp = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                fileImageMap.put(category, temp);
                    // File directory = new File(categoryPath);
                    // File[] files = directory.listFiles();

                    // if (files == null) {
                    //     return ResponseEntity.notFound().build();
                    // }

                    // // Sort the list of files based on their creation timestamp
                    // Arrays.sort(files, Comparator.comparingLong(File::lastModified));

                    // // Create a multi-part response
                    // MultipartBuilder builder = MultipartBuilder.create();
                    // for (File file : files) {
                    //     try {
                    //         // Read the content of each file
                    //         byte[] fileContent = Files.readAllBytes(file.toPath());
                    //         // Add the file content as a part in the response
                    //         Timestamp timestamp = file.getName();
                    //         builder.addPart(file.getName(), fileContent);
                    //     } catch (IOException e) {
                    //         // Handle error if unable to read file content
                    //         e.printStackTrace();
                    //     }
                    // }

                    // // Convert multi-part response to byte array
                    // byte[] responseBody = builder.build();
                    // fileImageMap.insert(category, responseBody);
    // }
                Files.walk(Path.of(categoryPath))
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            try {
                                String fileName = filePath.getFileName().toString();
                                System.out.println(fileName + " " +filePath);
                                if(fileName.endsWith(".png")){
                                    BufferedImage img = ImageIO.read(filePath.toFile());
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    System.out.println(img + " " + filePath);
                                    ImageIO.write(img, "png", baos);
                                    byte[] imgBytes = baos.toByteArray();
                                    String timestamp = fileName.substring(0, fileName.lastIndexOf('.')).replace("_", ":").replace("$", ".");
                                    List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                    // Add the value to the list
                                    list.add(new ImageTimestamp(imgBytes, timestamp));
                                    // Put the list back into the map
                                    fileImageMap.put(category, list);
                                }
                                else{
                                    System.out.println("Text file");
                                    String textContent = Files.readString(filePath);
                                    String timestamp = fileName.substring(0, fileName.lastIndexOf('.')).replace("_", ":").replace("$", ".");
                                    List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                    // Add the value to the list
                                    list.add(new ImageTimestamp(textContent, timestamp));
                                    // Put the list back into the map
                                    fileImageMap.put(category, list);
                                }
                            } catch (IOException e) {
                                System.out.println("ekvjev j");
                                List<ImageTimestamp> list = fileImageMap.getOrDefault(category, new ArrayList<ImageTimestamp>());
                                // Add the value to the list
                                list.add(new ImageTimestamp( ""));
                                // Put the list back into the map
                                fileImageMap.put(category, list);
                                // fileImageMap.put(category, new ImageTimestamp(null, ""));
                            }
                        });
            } catch (IOException e) {
                throw new ResourceNotFoundException("Can't find records for the emr: " + publicEmrId);
            }
        }

        return ResponseEntity.ok().body(fileImageMap);
        // return ResponseEntity.ok()
        // .header("Content-Disposition", "attachment; filename=files.zip")
        // .body(responseBody);
    }

    private static String readTextFromFile(String filePath) throws IOException {
        try {
            // Read text content from file
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            return new String(bytes);
        } catch (IOException e) {
            return ""; // Return empty string if file not found or cannot be read
        }
    }

    public ResponseEntity<String> deleteEmrByPatientId(String pId) {
        UUID patientId = publicPrivateService.privateIdByPublicId(pId);
        Path commentsPath = Paths.get(this.emrStorageLocation + "/Comments/" + patientId.toString());
        Path prescriptionsPath = Paths.get(this.emrStorageLocation + "/Prescriptions/" + patientId.toString());
        Path testsPath = Paths.get(this.emrStorageLocation + "/Tests/" + patientId.toString());
        try {
            if (Files.exists(commentsPath)) {
                Files.walkFileTree(commentsPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }

            if (Files.exists(prescriptionsPath)) {
                Files.walkFileTree(prescriptionsPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            if (Files.exists(testsPath)) {
                Files.walkFileTree(testsPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            // Now that directories are empty, delete them
            emrRepository.deleteByPatientId(patientId);
        } catch (IOException e) {
            System.err.println("Failed to delete directories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete directories");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Successfully removed all records for given Patient-ID");
    }

    public UUID getPatientIdByEmrId(String EmrId) {
        UUID emrId = publicPrivateService.privateIdByPublicId(EmrId);
        return emrRepository.getPatientIdByEmrId(emrId);
    }

    // public ResponseEntity<?> getEmrByPatientId(UUID patientId) throws
    // FileNotFoundException {
    // List<UUID> publicEmrId = emrRepository.getEmrByPatientId(patientId);
    // System.out.println("Calling the database for fetching emr by public id");
    //
    // HttpHeaders header = new HttpHeaders();
    // header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    //
    // List<InputStream> inputStreams = new ArrayList<>();
    //
    // try {
    // for (UUID emrId : publicEmrId) {
    // String prescriptionHash = emrRepository.findPrescriptionHash(emrId);
    // String commentHash = emrRepository.findCommentHash(emrId);
    // String testHash = emrRepository.findTestHash(emrId);
    //
    // File prescription = new
    // File(String.valueOf(emrStorageLocation.resolve("Prescriptions")) + "/" +
    // patientId.toString() + "/" + prescriptionHash);
    // File comment = new
    // File(String.valueOf(emrStorageLocation.resolve("Comments")) + "/" +
    // patientId.toString() + "/" + commentHash);
    // File test = new File(String.valueOf(emrStorageLocation.resolve("Tests")) +
    // "/" + patientId.toString() + "/" + testHash);
    //
    // InputStream prescriptionInputStream = new FileInputStream(prescription);
    // InputStream commentInputStream = new FileInputStream(comment);
    // InputStream testInputStream = new FileInputStream(test);
    //
    // inputStreams.add(prescriptionInputStream);
    // inputStreams.add(commentInputStream);
    // inputStreams.add(testInputStream);
    // }
    // } catch (FileNotFoundException e) {
    // // Handle file not found exception
    // throw new RuntimeException(e);
    // }
    //
    // // Combine all InputStreams into one byte array
    // byte[] combinedBytes = combineInputStreams(inputStreams);
    //
    // // Return ResponseEntity with byte array and headers
    // return new ResponseEntity<>(combinedBytes, header, HttpStatus.OK);
    // }
    //
    // private byte[] combineInputStreams(List<InputStream> inputStreams) {
    // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // byte[] buffer = new byte[8192];
    //
    // try {
    // for (InputStream inputStream : inputStreams) {
    // int bytesRead;
    // while ((bytesRead = inputStream.read(buffer)) != -1) {
    // outputStream.write(buffer, 0, bytesRead);
    // }
    // }
    // } catch (IOException e) {
    // throw new RuntimeException("Error combining InputStreams", e);
    // } finally {
    // try {
    // outputStream.close();
    // } catch (IOException e) {
    // // Ignore
    // }
    // for (InputStream inputStream : inputStreams) {
    // try {
    // inputStream.close();
    // } catch (IOException e) {
    // // Ignore
    // }
    // }
    // }
    // return outputStream.toByteArray();
    // }
    //
    // private void storeDocument(MultipartFile file, Path targetLocation, String
    // hash) throws IOException{
    // if (!Files.exists(targetLocation)) {
    // Files.createDirectories(targetLocation);
    // }
    // Files.copy(file.getInputStream(), targetLocation.resolve(hash));
    // }
    //
    // public ResponseEntity<String> insertNewEmr(EmrDto emrDto) {
    // System.out.println("Converting the EmrDto to storable entity");
    // if (emrDto != null && emrDto.getPrescription() != null &&
    // emrDto.getComments() != null) {
    // try {
    // MultipartFile prescriptions = emrDto.getPrescription();
    // Document document = new Document();
    // document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" +
    // emrDto.getPatientId().toString() + prescriptions.getOriginalFilename());
    // document.setMimeType(prescriptions.getContentType());
    // document.setSize(prescriptions.getSize());
    // document.setHash();
    // Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/"
    // + emrDto.getPatientId());
    // storeDocument(prescriptions, prescriptionLocation, document.getHash());
    //
    // MultipartFile comments = emrDto.getComments();
    // Document comment = new Document();
    // comment.setName(this.emrStorageLocation.toString() + "/Comments/" +
    // emrDto.getPatientId().toString() + comments.getOriginalFilename());
    // comment.setMimeType(comments.getContentType());
    // comment.setSize(comments.getSize());
    // comment.setHash();
    // Path commentLocation = this.emrStorageLocation.resolve("Comments/" +
    // emrDto.getPatientId());
    // storeDocument(comments, commentLocation, comment.getHash());
    //
    // MultipartFile tests = emrDto.getTests();
    // Document test = new Document();
    // test.setMimeType(tests.getContentType());
    // test.setName(this.emrStorageLocation.toString() + "/Tests/" +
    // emrDto.getPatientId().toString() + tests.getOriginalFilename());
    // test.setSize(tests.getSize());
    // test.setHash();
    // Path testLocation = this.emrStorageLocation.resolve("Tests/" +
    // emrDto.getPatientId());
    // storeDocument(tests, testLocation, test.getHash());
    //
    //
    // Emr obj = new Emr();
    // obj.setPatientId(emrDto.getPatientId());
    // obj.setAccessDepartments(emrDto.getAccessDepartments());
    // obj.setAccessList(emrDto.getAccessList());
    // obj.setComments(comment.getHash());
    // obj.setTests(test.getHash());
    // obj.setPrescription(document.getHash());
    // obj.setPublicEmrId(UUID.randomUUID());
    // obj.setLastUpdate(System.currentTimeMillis() / 1000);
    // System.out.println("Created EMR object, storing now.");
    // emrRepository.save(obj);
    //
    // System.out.println("Saving prescription and comments");
    //// document.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
    // documentRepository.save(document);
    //// comment.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
    // documentRepository.save(comment);
    // documentRepository.save(test);
    //
    // return ResponseEntity.status(HttpStatus.OK)
    // .body("EMR ID: " + obj.getPublicEmrId());
    //
    // } catch (IOException | NoSuchAlgorithmException e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: "
    // + e.getMessage());
    // }
    // }
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Requesting
    // file upload");
    // }
    // public ResponseEntity<InputStreamResource> getPrescriptionByEmrId(String
    // publicEmrId) throws FileNotFoundException {
    // System.out.println("Calling the database for fetching emr by public id");
    // UUID id = publicPrivateService.privateIdByPublicId(publicEmrId);
    // String hash = emrRepository.findPrescriptionHash(id);
    // File prescription = new
    // File(String.valueOf(emrStorageLocation.resolve("Prescriptions")) + "/" + id
    // +"/" + id +"/" + hash);
    // HttpHeaders header = new HttpHeaders();
    // header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // header.setContentDispositionFormData("attachment", prescription.getName());
    // InputStreamResource resource = new InputStreamResource(new
    // FileInputStream(prescription));
    //
    // // Return ResponseEntity with InputStreamResource and headers
    // return ResponseEntity.ok()
    // .headers(header)
    // .body(resource);
    // }
    //
    //
    // public ResponseEntity<InputStreamResource> getTestsByEmrId(String
    // publicEmrId) throws FileNotFoundException {
    // System.out.println("Calling the database for fetching emr by public id");
    // UUID id = publicPrivateService.privateIdByPublicId(publicEmrId);
    // String hash = emrRepository.findPrescriptionHash(id);
    // File prescription = new
    // File(String.valueOf(emrStorageLocation.resolve("Tests")) + "/" + id +"/" + id
    // +"/" + hash);
    // HttpHeaders header = new HttpHeaders();
    // header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // header.setContentDispositionFormData("attachment", prescription.getName());
    // InputStreamResource resource = new InputStreamResource(new
    // FileInputStream(prescription));
    //
    // // Return ResponseEntity with InputStreamResource and headers
    // return ResponseEntity.ok()
    // .headers(header)
    // .body(resource);
    // }
    ////
    // public ResponseEntity<InputStreamResource> getCommentsByEmrId(String
    // publicEmrId) throws FileNotFoundException {
    // System.out.println("Calling the database for fetching emr by public id");
    // UUID id = publicPrivateService.privateIdByPublicId(publicEmrId);
    // String hash = emrRepository.findCommentHash(id);
    // File comment = new
    // File(String.valueOf(emrStorageLocation.resolve("Comments")) + "/" + id +"/" +
    // id +"/" + hash);
    // HttpHeaders header = new HttpHeaders();
    // header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    // header.setContentDispositionFormData("attachment", comment.getName());
    // InputStreamResource resource = new InputStreamResource(new
    // FileInputStream(comment));
    //
    // // Return ResponseEntity with InputStreamResource and headers
    // return ResponseEntity.ok()
    // .headers(header)
    // .body(resource);
    // }
    //
    // public ResponseEntity<String> updateEmrById (UpdateEmrDto updateEmrDto) {
    // UUID id =
    // publicPrivateService.privateIdByPublicId(updateEmrDto.getPublicEmrId());
    // System.out.println("Updating the emr by the id" + id);
    // if (updateEmrDto.getPrescription() != null) {
    // try {
    // MultipartFile prescriptions = updateEmrDto.getPrescription();
    // Document document = new Document();
    // document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" + id
    // + "/" + id);
    // document.setMimeType(prescriptions.getContentType());
    // document.setSize(prescriptions.getSize());
    // document.setHash();
    // Path prescriptionLocation = this.emrStorageLocation.resolve("/Prescriptions/"
    // + id + "/"+ id + "/");
    // storeDocument(prescriptions, prescriptionLocation, document.getHash());
    // documentRepository.save(document);
    // } catch (IOException | NoSuchAlgorithmException e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in
    // updating columns" );
    // }
    // }
    // if(updateEmrDto.getComments() != null) {
    // try{
    // MultipartFile comments = updateEmrDto.getComments();
    // Document comment = new Document();
    // comment.setName(this.emrStorageLocation.toString() + "/Comments/" + id + "/"
    // + id);
    // comment.setMimeType(comments.getContentType());
    // comment.setSize(comments.getSize());
    // comment.setHash();
    // Path commentLocation = this.emrStorageLocation.resolve("Comments/" + id + "/"
    // + id + "/");
    // storeDocument(comments, commentLocation, comment.getHash());
    // documentRepository.save(comment);
    //// comment.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
    // } catch (IOException | NoSuchAlgorithmException e){
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in
    // updating columns" );
    // }
    // }
    // if(updateEmrDto.getTests() != null) {
    // try{
    // MultipartFile tests = updateEmrDto.getTests();
    // Document test = new Document();
    // test.setName(this.emrStorageLocation.toString() + "/Tests/" + id + "/" + id);
    // test.setMimeType(tests.getContentType());
    // test.setSize(tests.getSize());
    // test.setHash();
    // Path commentLocation = this.emrStorageLocation.resolve("Tests/" + id + "/" +
    // id + "/");
    // storeDocument(tests, commentLocation, test.getHash());
    // documentRepository.save(test);
    // } catch (IOException | NoSuchAlgorithmException e){
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in
    // updating columns" );
    // }
    // }
    //
    // try{
    // System.out.println("Updating values as: " + updateEmrDto.getAccessList()+"
    // "+updateEmrDto.getAccessDepartments() + updateEmrDto.getPublicEmrId());
    // emrRepository.updateVals(updateEmrDto.getAccessList(),
    // updateEmrDto.getAccessDepartments(), updateEmrDto.getPublicEmrId());
    // emrRepository.updateLastUpdate(updateEmrDto.getPublicEmrId(),
    // System.currentTimeMillis() / 1000);
    // }catch(Exception e){
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: "
    // + e.getMessage());
    // }
    // return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
    // }
}