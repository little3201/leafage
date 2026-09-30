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

import top.leafage.common.data.jpa.JpaCrudService;
import top.leafage.hypervisor.docs.domain.dto.TemplateDTO;
import top.leafage.hypervisor.docs.domain.vo.TemplateVO;

/**
 * Template service.
 *
 * @author wq li
 */
public interface TemplateService extends JpaCrudService<TemplateDTO, TemplateVO> {

    /**
     * Publish.
     *
     * @param id the pk of record.
     * @return publish result.
     */
    boolean publish(Long id);

    /**
     * Archive.
     *
     * @param id the pk of record.
     * @return publish result.
     */
    boolean archive(Long id);
}
