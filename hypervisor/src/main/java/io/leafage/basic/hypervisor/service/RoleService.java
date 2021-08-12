/*
 * Copyright (c) 2021. Leafage All Right Reserved.
 */
package io.leafage.basic.hypervisor.service;

import io.leafage.basic.hypervisor.document.Role;
import io.leafage.basic.hypervisor.dto.RoleDTO;
import io.leafage.basic.hypervisor.vo.RoleVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.leafage.common.basic.TreeNode;
import top.leafage.common.reactive.ReactiveBasicService;
import top.leafage.common.reactive.ReactiveTreeNodeAware;

/**
 * 角色信息service
 *
 * @author liwenqiang 2018/9/27 14:18
 **/
public interface RoleService extends ReactiveBasicService<RoleDTO, RoleVO>, ReactiveTreeNodeAware<Role> {

    /**
     * 查询构造树结构的数据
     *
     * @return 数据集
     */
    Flux<TreeNode> tree();

    /**
     * 是否已存在
     *
     * @param name 名称
     * @return true-是，false-否
     */
    Mono<Boolean> exists(String name);
}
