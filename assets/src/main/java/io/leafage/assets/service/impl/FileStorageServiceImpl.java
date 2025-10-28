package io.leafage.assets.service.impl;

import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.service.FileStorageService;
import org.springframework.core.io.buffer.DataBuffer;
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
        return file.content()
                .collectList()
                .map(dataBuffers -> {
                    // 计算文件大小
                    long size = dataBuffers.stream()
                            .mapToInt(DataBuffer::readableByteCount)
                            .sum();

                    // 创建文件记录
                    FileRecordDTO dto = new FileRecordDTO();
                    dto.setName(file.filename());
                    dto.setMimeType(MediaTypeFactory.getMediaType(file.filename()).toString());
                    dto.setSize(size);
                    return dto;
                });
    }

    @Override
    public Mono<Boolean> exists(String fileName) {
        return null;
    }
}
