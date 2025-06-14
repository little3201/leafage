/*
 * Copyright (c) 2024-2025.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.leafage.assets.service;

import io.leafage.assets.dto.FileRecordDTO;
import io.leafage.assets.vo.FileRecordVO;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.common.jdbc.JdbcCrudService;

import java.io.OutputStream;

/**
 * file service.
 *
 * @author wq li
 */
public interface FileRecordService extends JdbcCrudService<FileRecordDTO, FileRecordVO> {

    /**
     * 上传
     *
     * @param file 文件
     * @return 结果
     */
    FileRecordVO upload(MultipartFile file);

    String download(Long id, OutputStream outputStream);
}
