package io.leafage.assets.service;


import io.leafage.assets.dto.FileRecordDTO;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * file storage service.
 *
 * @author wq li
 */
public interface FileStorageService {

    /**
     * 上传
     *
     * @param file 文件
     * @return 结果
     */
    Mono<FileRecordDTO> upload(FilePart file);

    /**
     * 是否存在
     *
     * @param fileName 文件名
     * @return 结果
     */
    Mono<Boolean> exists(String fileName);
}
