package io.leafage.assets.service.impl;

import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.service.FileStorageService;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


/**
 * file storage service impl
 *
 * @author wq li
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public Mono<FileRecordDTO> upload(FilePart file) {
        return DataBufferUtils.join(file.content())
                .map(dataBuffer -> {
                    FileRecordDTO dto = new FileRecordDTO();
                    dto.setName(file.filename());
                    dto.setMimeType(MediaTypeFactory.getMediaType(file.filename()).toString());
                    dto.setSize(dataBuffer.readableByteCount());
                    return dto;
                });
    }

    @Override
    public Mono<Boolean> exists(String fileName) {
        return null;
    }
}
