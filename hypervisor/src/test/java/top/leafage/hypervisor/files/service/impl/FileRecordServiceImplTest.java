/*
 * Copyright(c) 2019-present the original author or authors.
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

package top.leafage.hypervisor.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import top.leafage.hypervisor.docs.domain.vo.FileStatisticsVO;
import top.leafage.hypervisor.files.domain.FileRecord;
import top.leafage.hypervisor.files.domain.dto.FileRecordDTO;
import top.leafage.hypervisor.files.domain.vo.FileRecordVO;
import top.leafage.hypervisor.files.repository.FileRecordRepository;
import top.leafage.hypervisor.files.service.FileService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * File record service test
 *
 * @author wq li
 **/
@ExtendWith(MockitoExtension.class)
class FileRecordServiceImplTest {

    @Mock
    private FileRecordRepository fileRecordRepository;

    @Mock
    private FileService fileService;

    @InjectMocks
    private FileRecordServiceImpl fileRecordService;


    private FileRecord entity;

    @BeforeEach
    void setUp() {
        entity = new FileRecord(null, "test", ".txt", "/text/plain", "test", 121L, false, true);
    }

    @Test
    void retrieve() {
        Page<FileRecord> page = new PageImpl<>(List.of(entity));

        when(fileRecordRepository.findAll(ArgumentMatchers.<Specification<FileRecord>>any(),
                any(Pageable.class))).thenReturn(page);

        Page<FileRecordVO> voPage = fileRecordService.retrieve(0, 2, "id", true, "name:like:test");
        assertEquals(1, voPage.getTotalElements());
        assertEquals(1, voPage.getContent().size());
        verify(fileRecordRepository).findAll(ArgumentMatchers.<Specification<FileRecord>>any(), any(Pageable.class));
    }

    @Test
    void fetch() {
        when(fileRecordRepository.findById(anyLong())).thenReturn(Optional.of(entity));

        FileRecordVO vo = fileRecordService.fetch(anyLong());
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(fileRecordRepository).findById(anyLong());
    }

    @Test
    void fetch_not_found() {
        when(fileRecordRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.fetch(anyLong())
        );
        assertEquals("file record not found: 0", exception.getMessage());
        verify(fileRecordRepository).findById(anyLong());
    }

    @Test
    void create() {
        FileRecordDTO dto = new FileRecordDTO();
        dto.setName("test");
        dto.setSuperiorId(1L);
        when(fileRecordRepository.existsByName("test")).thenReturn(false);
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(entity);

        FileRecordVO vo = fileRecordService.create(dto);
        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(fileRecordRepository).save(any(FileRecord.class));
    }

    @Test
    void create_name_conflict() {
        FileRecordDTO dto = new FileRecordDTO();
        dto.setName("test");
        when(fileRecordRepository.existsByName("test")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> fileRecordService.create(dto)
        );
        assertEquals("name already exists: test", exception.getMessage());
        verify(fileRecordRepository, never()).save(any(FileRecord.class));
    }

    @Test
    void upload() {
        MultipartFile file = org.mockito.Mockito.mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("test.txt");
        when(file.getContentType()).thenReturn("text/plain");
        when(file.getSize()).thenReturn(11L);
        when(fileRecordRepository.findByName("test.txt")).thenReturn(Optional.empty());
        when(fileService.upload(file)).thenReturn("src/test/resources/test.txt");
        when(fileRecordRepository.save(any(FileRecord.class))).thenReturn(entity);

        FileRecordVO vo = fileRecordService.upload(file, 1L);

        assertNotNull(vo);
        assertEquals("test", vo.name());
        verify(fileRecordRepository).save(any(FileRecord.class));
    }

    @Test
    void statistics() {
        FileRecord image = new FileRecord(null, "image", ".png", "/image.png", "image/png", 10L, false, true);
        FileRecord video = new FileRecord(null, "video", ".mp4", "/video.mp4", "video/mp4", 20L, false, true);
        FileRecord document = new FileRecord(null, "doc", ".pdf", "/doc.pdf", "application/pdf", 30L, false, true);
        FileRecord other = new FileRecord(null, "other", ".bin", "/other.bin", "application/octet-stream", 40L, false, true);
        FileRecord directory = new FileRecord(null, "folder", true);
        when(fileRecordRepository.findAllBy()).thenReturn(List.of(image, video, document, other, directory));

        List<FileStatisticsVO> statistics = fileRecordService.statistics();
        assertEquals(4, statistics.size());
        assertEquals(new FileStatisticsVO("Image", 1, 10), statistics.get(0));
        assertEquals(new FileStatisticsVO("Video", 1, 20), statistics.get(1));
        assertEquals(new FileStatisticsVO("Document", 1, 30), statistics.get(2));
        assertEquals(new FileStatisticsVO("Other", 1, 40), statistics.get(3));
    }

    @Test
    void statistics_empty() {
        when(fileRecordRepository.findAllBy()).thenReturn(List.of());

        List<FileStatisticsVO> statistics = fileRecordService.statistics();
        assertTrue(statistics.isEmpty());
    }

    @Test
    void enable() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(true);
        when(fileRecordRepository.enableById(anyLong())).thenReturn(1);

        boolean enabled = fileRecordService.enable(1L);
        assertTrue(enabled);
    }

    @Test
    void enable_not_found() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.enable(1L)
        );
        assertEquals("file not found: 1", exception.getMessage());
    }

    @Test
    void disable() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(true);
        when(fileRecordRepository.disableById(anyLong())).thenReturn(1);

        boolean disabled = fileRecordService.disable(1L);
        assertTrue(disabled);
    }

    @Test
    void disable_not_found() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.disable(1L)
        );
        assertEquals("file not found: 1", exception.getMessage());
    }

    @Test
    void remove() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(true);

        fileRecordService.remove(11L);
        verify(fileRecordRepository).deleteById(anyLong());
    }

    @Test
    void remove_not_found() {
        when(fileRecordRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> fileRecordService.remove(anyLong())
        );
        assertEquals("file record not found: 0", exception.getMessage());
    }

}
