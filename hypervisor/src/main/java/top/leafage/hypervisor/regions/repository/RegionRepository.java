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

package top.leafage.hypervisor.regions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import top.leafage.hypervisor.regions.domain.Region;

import java.util.Collection;
import java.util.List;

/**
 * region repository.
 *
 * @author wq li
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {

    /**
     * existsByName.
     *
     * @param name a {@link String} object
     * @return a boolean
     */
    boolean existsByName(String name);

    /**
     * find the superior is null.
     *
     * @return 关联的数据
     */
    List<Region> findAllBySuperiorIdIsNull();

    /**
     * find by superior id.
     *
     * @param superiorId the pk of superior.
     * @return the result.
     */
    List<Region> findAllBySuperiorId(Long superiorId);

    /**
     * Counts the number of records by superior ID.
     *
     * @param superiorIds The pk of superiors.
     * @return The count of records.
     */
    @Query("SELECT t.superiorId, COUNT(t.id) FROM Region t WHERE t.superiorId IN :superiorIds GROUP BY t.superiorId")
    List<Object[]> countBySuperiorIdsGrouped(Collection<Long> superiorIds);

    /**
     * enable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Region t SET t.enabled = true WHERE t.id = :id AND t.enabled = false")
    int enableById(Long id);

    /**
     * disable a record by pk.
     *
     * @param id The pk.
     * @return result.
     */
    @Modifying
    @Query("UPDATE Region t SET t.enabled = false WHERE t.id = :id AND t.enabled = true")
    int disableById(Long id);
}
