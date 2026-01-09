/*
 *  Copyright 2018-2025 little3201.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package top.leafage.hypervisor.assets.domain;

import org.jspecify.annotations.NonNull;
import org.springframework.data.relational.core.mapping.Table;
import top.leafage.common.data.domain.AbstractAuditable;

/**
 * entity class for file record.
 *
 * @author wq li
 */
@Table(name = "file_records")
public class FileRecord extends AbstractAuditable<@NonNull String, @NonNull Long> {

    private Long superiorId;

    private String name;

    private String extension;

    private String path;

    private String contentType;

    private long size;

    private boolean directory;

    private boolean regularFile;

    private boolean symbolicLink;


    public FileRecord() {
    }

    public FileRecord(Long superiorId, String name, String extension, String path, String contentType, long size, boolean directory, boolean regularFile, boolean symbolicLink) {
        this.superiorId = superiorId;
        this.name = name;
        this.extension = extension;
        this.path = path;
        this.contentType = contentType;
        this.size = size;
        this.directory = directory;
        this.regularFile = regularFile;
        this.symbolicLink = symbolicLink;
    }

    public Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isRegularFile() {
        return regularFile;
    }

    public void setRegularFile(boolean regularFile) {
        this.regularFile = regularFile;
    }

    public boolean isSymbolicLink() {
        return symbolicLink;
    }

    public void setSymbolicLink(boolean symbolicLink) {
        this.symbolicLink = symbolicLink;
    }
}
