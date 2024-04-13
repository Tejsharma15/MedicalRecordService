package com.example.EMR.service;

import com.example.EMR.dto.EmrDto;
import com.example.EMR.dto.UpdateEmrDto;
import com.example.EMR.dto.UpdateEmrDtoText;
import com.example.EMR.models.Document;
import com.example.EMR.models.Emr;
import com.example.EMR.property.DocumentStorageProperty;
import com.example.EMR.repository.DocumentRepository;
import com.example.EMR.repository.EmrRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class EmrService {
    private final EmrRepository emrRepository;
    private final DocumentRepository documentRepository;
    private final Path emrStorageLocation;

    @Autowired
    EmrService(EmrRepository emrRepository, DocumentRepository documentRepository, DocumentStorageProperty documentStorageProperty) {
        this.emrRepository = emrRepository;
        this.emrStorageLocation = Paths.get(documentStorageProperty.getUploadDirectory());
        this.documentRepository = documentRepository;
    }

    public ResponseEntity<InputStreamResource> getPrescriptionByEmrId(UUID publicEmrId) throws FileNotFoundException {
        System.out.println("Calling the database for fetching emr by public id");
        String hash = emrRepository.findPrescriptionHash(publicEmrId);
        File prescription = new File(String.valueOf(emrStorageLocation.resolve("Prescriptions")) + "/" + hash);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachment", prescription.getName());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(prescription));

        // Return ResponseEntity with InputStreamResource and headers
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }

    public ResponseEntity<InputStreamResource> getTestsByEmrId(UUID publicEmrId) throws FileNotFoundException {
        System.out.println("Calling the database for fetching emr by public id");
        String hash = emrRepository.findPrescriptionHash(publicEmrId);
        File prescription = new File(String.valueOf(emrStorageLocation.resolve("Tests")) + "/" + hash);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachment", prescription.getName());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(prescription));

        // Return ResponseEntity with InputStreamResource and headers
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }

    public ResponseEntity<InputStreamResource> getCommentsByEmrId(UUID publicEmrId) throws FileNotFoundException {
        System.out.println("Calling the database for fetching emr by public id");
        String hash = emrRepository.findCommentHash(publicEmrId);
        File comment = new File(String.valueOf(emrStorageLocation.resolve("Comments")) + "/" + hash);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        header.setContentDispositionFormData("attachment", comment.getName());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(comment));

        // Return ResponseEntity with InputStreamResource and headers
        return ResponseEntity.ok()
                .headers(header)
                .body(resource);
    }

    public ResponseEntity<byte[]> getEmrByPatientId(UUID patientId) throws FileNotFoundException {
        List<UUID> publicEmrId = emrRepository.getEmrByPatientId(patientId);
        System.out.println("Calling the database for fetching emr by public id");

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        List<InputStream> inputStreams = new ArrayList<>();

        try {
            for (UUID emrId : publicEmrId) {
                String prescriptionHash = emrRepository.findPrescriptionHash(emrId);
                String commentHash = emrRepository.findCommentHash(emrId);
                String testHash = emrRepository.findTestHash(emrId);

                File prescription = new File(String.valueOf(emrStorageLocation.resolve("Prescriptions")) + "/" + patientId.toString() + "/" + prescriptionHash);
                File comment = new File(String.valueOf(emrStorageLocation.resolve("Comments")) + "/" + patientId.toString() + "/" + commentHash);
                File test = new File(String.valueOf(emrStorageLocation.resolve("Tests")) + "/" + patientId.toString() + "/" + testHash);

                InputStream prescriptionInputStream = new FileInputStream(prescription);
                InputStream commentInputStream = new FileInputStream(comment);
                InputStream testInputStream = new FileInputStream(test);

                inputStreams.add(prescriptionInputStream);
                inputStreams.add(commentInputStream);
                inputStreams.add(testInputStream);
            }
        } catch (FileNotFoundException e) {
            // Handle file not found exception
            throw new RuntimeException(e);
        }

        // Combine all InputStreams into one byte array
        byte[] combinedBytes = combineInputStreams(inputStreams);

        // Return ResponseEntity with byte array and headers
        return new ResponseEntity<>(combinedBytes, header, HttpStatus.OK);
    }

    private byte[] combineInputStreams(List<InputStream> inputStreams) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        try {
            for (InputStream inputStream : inputStreams) {
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error combining InputStreams", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                // Ignore
            }
            for (InputStream inputStream : inputStreams) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return outputStream.toByteArray();
    }

    private void storeDocument(MultipartFile file, Path targetLocation, String hash) throws IOException{
        if (!Files.exists(targetLocation)) {
            Files.createDirectories(targetLocation);
        }
        Files.copy(file.getInputStream(), targetLocation.resolve(hash));
    }

    public ResponseEntity<String> insertNewEmr(EmrDto emrDto) {
        System.out.println("Converting the EmrDto to storable entity");
        if (emrDto != null && emrDto.getPrescription() != null &&
                emrDto.getComments() != null) {
            try {
                MultipartFile prescriptions = emrDto.getPrescription();
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" + emrDto.getPatientId().toString() + prescriptions.getOriginalFilename());
                document.setMimeType(prescriptions.getContentType());
                document.setSize(prescriptions.getSize());
                document.setHash();
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/" + emrDto.getPatientId());
                storeDocument(prescriptions, prescriptionLocation, document.getHash());

                MultipartFile comments = emrDto.getComments();
                Document comment = new Document();
                comment.setName(this.emrStorageLocation.toString() + "/Comments/" + emrDto.getPatientId().toString() + comments.getOriginalFilename());
                comment.setMimeType(comments.getContentType());
                comment.setSize(comments.getSize());
                comment.setHash();
                Path commentLocation = this.emrStorageLocation.resolve("Comments/" + emrDto.getPatientId());
                storeDocument(comments, commentLocation, comment.getHash());

                MultipartFile tests = emrDto.getTests();
                Document test = new Document();
                test.setMimeType(tests.getContentType());
                test.setName(this.emrStorageLocation.toString() + "/Tests/" + emrDto.getPatientId().toString() + tests.getOriginalFilename());
                test.setSize(tests.getSize());
                test.setHash();
                Path testLocation = this.emrStorageLocation.resolve("Tests/" + emrDto.getPatientId());
                storeDocument(tests, testLocation, test.getHash());


                Emr obj = new Emr();
                obj.setPatientId(emrDto.getPatientId());
                obj.setAccessDepartments(emrDto.getAccessDepartments());
                obj.setAccessList(emrDto.getAccessList());
                obj.setComments(comment.getHash());
                obj.setTests(test.getHash());
                obj.setPrescription(document.getHash());
                obj.setPublicEmrId(UUID.randomUUID());
                obj.setLastUpdate(System.currentTimeMillis() / 1000);
                System.out.println("Created EMR object, storing now.");
                emrRepository.save(obj);

                System.out.println("Saving prescription and comments");
//                document.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
                documentRepository.save(document);
//                comment.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
                documentRepository.save(comment);
                documentRepository.save(test);

                return ResponseEntity.status(HttpStatus.OK)
                        .body("EMR ID: " + obj.getPublicEmrId());

            } catch (IOException | NoSuchAlgorithmException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Requesting file upload");
    }

    public UUID insertConsulationEmr(EmrDto emrDto){
        Emr obj = new Emr();
        obj.setPatientId(emrDto.getPatientId());
        obj.setAccessDepartments(emrDto.getAccessDepartments());
        obj.setAccessList(emrDto.getAccessList());
        obj.setComments(null);
        obj.setPrescription(null);
        obj.setPublicEmrId(UUID.randomUUID());
        obj.setLastUpdate(System.currentTimeMillis() / 1000);
        System.out.println("Created EMR object, storing now.");
        emrRepository.save(obj);
        return obj.getPublicEmrId();
    }

    public static void convertStringToFile(String content, Path filePath) throws IOException {
        // Create parent directories if they don't exist
        Files.createDirectories(filePath.getParent());

        FileWriter writer = null;
        try {
            writer = new FileWriter(filePath.toFile(), true);
            LocalDateTime currentDate = LocalDateTime.now();
            // Format date and time
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Write formatted date, content, and newline character to file
            writer.write("\n"+formattedDate + " " + content + "\n");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    public ResponseEntity<String> updateEmrByIdText (UpdateEmrDtoText updateEmrDtoText) throws NoSuchAlgorithmException {
        UUID id = updateEmrDtoText.getPublicEmrId();
        if(updateEmrDtoText.getPrescription() != null){
            try {
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" + updateEmrDtoText.getPatientId().toString() + id);
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getPrescription().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getPrescription());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/" + updateEmrDtoText.getPatientId() + "/" + id);
                convertStringToFile(updateEmrDtoText.getPrescription(), prescriptionLocation);
            }
            catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving prescription");
            }

        }
        if(updateEmrDtoText.getComments() != null) {
            try {
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Comments/" + updateEmrDtoText.getPatientId().toString() + "/" + id);
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getComments().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getComments());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Comments/" + updateEmrDtoText.getPatientId() + "/" + id);
                convertStringToFile(updateEmrDtoText.getComments(), prescriptionLocation);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving comments");
            }
        }
        if(updateEmrDtoText.getTests() != null) {
            try {
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Tests/" + updateEmrDtoText.getPatientId().toString() + "/" + id);
                document.setMimeType("text/plain");
                document.setSize(updateEmrDtoText.getTests().length());
                document.setHash();
                System.out.println(updateEmrDtoText.getTests());
                Path prescriptionLocation = this.emrStorageLocation.resolve("Tests/" + updateEmrDtoText.getPatientId() + "/" + id);
                convertStringToFile(updateEmrDtoText.getTests(), prescriptionLocation);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in saving tests");
            }
        }
        emrRepository.updateLastUpdate(updateEmrDtoText.getPublicEmrId(), System.currentTimeMillis() / 1000);
        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
    }

    public ResponseEntity<Map<String, Map<String, String>>> getEmrByPatientIdText(UUID patientId) throws IOException{
        Map<String, Map<String, String>> nestedMap = new HashMap<>();
        // Define the categories and corresponding file paths
        String[] categories = {"Prescriptions", "Comments", "Tests"};
        String basePath = this.emrStorageLocation.toString() + "/"; // Adjust the base path

        for (String category : categories) {
            String categoryPath = basePath + category + "/" + patientId.toString() + "/";
            Map<String, String> fileTextMap = new HashMap<>();
            try {
                Files.walk(Path.of(categoryPath))
                        .filter(Files::isRegularFile)
                        .forEach(filePath -> {
                            try {
                                String fileName = filePath.getFileName().toString();
                                String textContent = Files.readString(filePath);
                                fileTextMap.put(fileName, textContent);
                            } catch (IOException e) {
                                e.printStackTrace(); // Handle or log the exception
                            }
                        });
                nestedMap.put(category, fileTextMap);
            } catch (IOException e) {
                throw e;
            }
        }


        return ResponseEntity.ok().body(nestedMap);
    }

    private static String readTextFromFile(String filePath) throws IOException{
        try {
            // Read text content from file
            byte[] bytes = Files.readAllBytes(Path.of(filePath));
            return new String(bytes);
        } catch (IOException e) {
            throw e; // Return empty string if file not found or cannot be read
        }
    }
    public ResponseEntity<String> updateEmrById (UpdateEmrDto updateEmrDto) {
        UUID id = updateEmrDto.getPublicEmrId();
        System.out.println("Updating the emr by the id" + id);
        if (updateEmrDto.getPrescription() != null) {
            try {
                MultipartFile prescriptions = updateEmrDto.getPrescription();
                Document document = new Document();
                document.setName(this.emrStorageLocation.toString() + "/Prescriptions/" + updateEmrDto.getPatientId().toString() + prescriptions.getOriginalFilename());
                document.setMimeType(prescriptions.getContentType());
                document.setSize(prescriptions.getSize());
                document.setHash();
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/" + updateEmrDto.getPatientId());
                storeDocument(prescriptions, prescriptionLocation, document.getHash());
                documentRepository.save(document);
            } catch (IOException | NoSuchAlgorithmException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in updating columns" );
            }
        }
        if(updateEmrDto.getComments() != null) {
            try{
                MultipartFile comments = updateEmrDto.getComments();
                Document comment = new Document();
                comment.setName(this.emrStorageLocation.toString() + "/Comments/" + updateEmrDto.getPatientId().toString() + comments.getOriginalFilename());
                comment.setMimeType(comments.getContentType());
                comment.setSize(comments.getSize());
                comment.setHash();
                Path commentLocation = this.emrStorageLocation.resolve("Comments/" + updateEmrDto.getPatientId());
                storeDocument(comments, commentLocation, comment.getHash());
                documentRepository.save(comment);
//                comment.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
            } catch (IOException | NoSuchAlgorithmException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in updating columns" );
            }
        }
        if(updateEmrDto.getTests() != null) {
            try{
                MultipartFile tests = updateEmrDto.getTests();
                Document test = new Document();
                test.setName(this.emrStorageLocation.toString() + "/Tests/" + updateEmrDto.getPatientId().toString() + tests.getOriginalFilename());
                test.setMimeType(tests.getContentType());
                test.setSize(tests.getSize());
                test.setHash();
                Path commentLocation = this.emrStorageLocation.resolve("Tests/" + updateEmrDto.getPatientId());
                storeDocument(tests, commentLocation, test.getHash());
                documentRepository.save(test);
            } catch (IOException | NoSuchAlgorithmException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in updating columns" );
            }
        }

        try{
            System.out.println("Updating values as: " + updateEmrDto.getAccessList()+" "+updateEmrDto.getAccessDepartments() + updateEmrDto.getPublicEmrId());
            emrRepository.updateVals(updateEmrDto.getAccessList(), updateEmrDto.getAccessDepartments(), updateEmrDto.getPublicEmrId());
            emrRepository.updateLastUpdate(updateEmrDto.getPublicEmrId(), System.currentTimeMillis() / 1000);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully");
    }

    public ResponseEntity<String> deleteEmrByPatientId(UUID patientId) {
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
}
