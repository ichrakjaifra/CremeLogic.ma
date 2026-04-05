package ma.cremelogic.CremeLogic.ma.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadFile(MultipartFile file);
}
