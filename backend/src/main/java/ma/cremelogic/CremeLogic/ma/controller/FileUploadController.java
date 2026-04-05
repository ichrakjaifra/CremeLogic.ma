package ma.cremelogic.CremeLogic.ma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.response.ApiResponse;
import ma.cremelogic.CremeLogic.ma.service.interfaces.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Gestion des uploads de fichiers")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF', 'MAGASINIER')")
    @Operation(summary = "Uploader une image")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileUploadService.uploadFile(file);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Fichier uploadé avec succès")
                        .data(fileUrl)
                        .build()
        );
    }
}
