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

package top.leafage.hypervisor.docs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.docs.domain.SectionField;

import java.util.List;

/**
 * section field repository.
 *
 * @author wq li
 */
@Repository
public interface SectionFieldRepository extends JpaRepository<SectionField, Long> {

    /**
     * exists by sectionId and name.
     *
     * @param sectionId the pk of section.
     * @param name      a {@link String} object.
     * @return a boolean
     */
    boolean existsBySectionIdAndName(Long sectionId, String name);

    /**
     * Retrieve section fields.
     *
     * @param sectionId the pk of section.
     * @return the result.
     */
    List<SectionField> findAllBySectionId(Long sectionId);
}
