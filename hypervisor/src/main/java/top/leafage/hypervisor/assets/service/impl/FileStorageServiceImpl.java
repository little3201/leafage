package top.leafage.hypervisor.assets.service.impl;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import top.leafage.hypervisor.assets.service.FileStorageService;


/**
 * file storage service impl
 *
 * @author wq li
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public Mono<Void> upload(FilePart file) {
        return Mono.empty();
    }

    @Override
    public Mono<Boolean> exists(String fileName) {
        return null;
    }
}
