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
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.docs.domain.Template;

/**
 * template repository.
 *
 * @author wq li
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

    /**
     * existsByName.
     *
     * @param name a {@link String} object
     * @return a boolean
     */
    boolean existsByName(String name);

    /**
     * enable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Template t SET t.enabled = true WHERE t.id = :id AND t.enabled = false")
    int enableById(Long id);

    /**
     * disable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Template t SET t.enabled = false WHERE t.id = :id AND t.enabled = true")
    int disableById(Long id);

    /**
     * update status by pk.
     *
     * @param id The pk.
     * @param status the status.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Template t SET t.status = :status WHERE t.id = :id AND t.enabled = true")
    int updateStatusById(Long id, Template.Status status);
}
