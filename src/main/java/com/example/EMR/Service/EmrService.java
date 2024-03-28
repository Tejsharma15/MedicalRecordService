package com.example.EMR.Service;

import com.example.EMR.DTO.EmrDto;
import com.example.EMR.DTO.UpdateEmrDto;
import com.example.EMR.Model.Document;
import com.example.EMR.Model.Emr;
import com.example.EMR.Repository.DocumentRepository;
import com.example.EMR.Repository.EmrRepository;
import com.example.EMR.property.DocumentStorageProperty;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
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
                document.setName(prescriptions.getOriginalFilename());
                document.setMimeType(prescriptions.getContentType());
                document.setSize(prescriptions.getSize());
                document.setHash();
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/" + emrDto.getPatientId());
                storeDocument(prescriptions, prescriptionLocation, document.getHash());

                MultipartFile comments = emrDto.getComments();
                Document comment = new Document();
                comment.setName(comments.getOriginalFilename());
                comment.setMimeType(comments.getContentType());
                comment.setSize(comments.getSize());
                comment.setHash();
                Path commentLocation = this.emrStorageLocation.resolve("Comments/" + emrDto.getPatientId());
                storeDocument(comments, commentLocation, comment.getHash());


                Emr obj = new Emr();
                obj.setPatientId(emrDto.getPatientId());
                obj.setAccessDepartments(emrDto.getAccessDepartments());
                obj.setAccessList(emrDto.getAccessList());
                obj.setComments(comment.getHash());
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

                return ResponseEntity.status(HttpStatus.OK)
                        .body("EMR ID: " + obj.getPublicEmrId());

            } catch (IOException | NoSuchAlgorithmException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Requesting file upload");
    }
    public ResponseEntity<String> updateEmrById (UpdateEmrDto updateEmrDto) {
        UUID id = updateEmrDto.getPublicEmrId();
        System.out.println("Updating the emr by the id" + id);
        if (updateEmrDto.getPrescription() != null) {
            try {
                MultipartFile prescriptions = updateEmrDto.getPrescription();
                Document document = new Document();
                document.setName(prescriptions.getOriginalFilename());
                document.setMimeType(prescriptions.getContentType());
                document.setSize(prescriptions.getSize());
                document.setHash();
                Path prescriptionLocation = this.emrStorageLocation.resolve("Prescriptions/" + updateEmrDto.getPatientId());
                storeDocument(prescriptions, prescriptionLocation, document.getHash());


//                document.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
                documentRepository.save(document);
            } catch (IOException | NoSuchAlgorithmException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
            }
        }
        if(updateEmrDto.getComments() != null) {
            try{
                MultipartFile comments = updateEmrDto.getComments();
                Document comment = new Document();
                comment.setName(comments.getOriginalFilename());
                comment.setMimeType(comments.getContentType());
                comment.setSize(comments.getSize());
                comment.setHash();
                Path commentLocation = this.emrStorageLocation.resolve("Comments/" + updateEmrDto.getPatientId());
                storeDocument(comments, commentLocation, comment.getHash());
                documentRepository.save(comment);
//                comment.setPatientId(emrRepository.getPrivatePatientId(emrDto.getPatientId()));
            } catch (IOException | NoSuchAlgorithmException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
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

            // Now that directories are empty, delete them
            emrRepository.deleteByPatientId(patientId);
        } catch (IOException e) {
            System.err.println("Failed to delete directories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete directories");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Successfully removed all records for given Patient-ID");
    }
}
