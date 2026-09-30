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

package top.leafage.hypervisor.docs.service;

import top.leafage.common.data.core.domain.TreeNode;
import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.docs.domain.Section;
import top.leafage.hypervisor.docs.domain.dto.SectionDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionDataDTO;
import top.leafage.hypervisor.docs.domain.dto.SectionFieldDTO;
import top.leafage.hypervisor.docs.domain.vo.SectionDataVO;
import top.leafage.hypervisor.docs.domain.vo.SectionFieldVO;
import top.leafage.hypervisor.docs.domain.vo.SectionVO;

import java.util.List;

/**
 * Section service.
 *
 * @author wq li
 */
public interface SectionService extends JpaCrudService<SectionDTO, SectionVO> {

    /**
     * tree.
     *
     * @param ownerId the pk of archive.
     * @return the result.
     */
    List<TreeNode<Long>> tree(Long ownerId, Section.OwnerType ownerType);

    /**
     * Retrieve section fields.
     *
     * @param id the pk of section.
     * @return the result.
     */
    List<SectionFieldVO> fields(Long id);

    /**
     * Retrieve section datas.
     *
     * @param id the pk of section.
     * @return the result.
     */
    List<SectionDataVO> datas(Long id);

    /**
     * Create section field.
     *
     * @param dto the data of section field.
     * @return the result.
     */
    SectionFieldVO createField(SectionFieldDTO dto);

    /**
     * Create section data.
     *
     * @param dto the data of section data.
     * @return the result.
     */
    SectionDataVO createData(SectionDataDTO dto);

    /**
     * Modify section field.
     *
     * @param id  the pk of section field.
     * @param dto the data of section field.
     * @return the result.
     */
    SectionFieldVO modifyField(Long id, SectionFieldDTO dto);

    /**
     * Modify section data.
     *
     * @param id  the pk of section data.
     * @param dto the data of section data.
     * @return the result.
     */
    SectionDataVO modifyData(Long id, SectionDataDTO dto);

    /**
     * Remove section field.
     *
     * @param id the pk of section field.
     */
    void removeField(Long id);

    /**
     * Remove section data.
     *
     * @param id the pk of section data.
     */
    void removeData(Long id);
}
