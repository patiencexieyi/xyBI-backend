package com.xybi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper;
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xybi.springbootinit.model.entity.Chart;
import com.xybi.springbootinit.service.ChartService;
import com.xybi.springbootinit.mapper.ChartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
* @author hore1
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2025-08-14 09:48:56
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
implements ChartService{

    @Autowired
    ChartMapper chartMapper;

    @Override
    public boolean save(Chart entity) {
        return super.save(entity);
    }

    @Override
    public boolean saveBatch(Collection<Chart> entityList) {
        return super.saveBatch(entityList);
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Chart> entityList) {
        return super.saveOrUpdateBatch(entityList);
    }

    @Override
    public boolean removeById(Chart entity) {
        return super.removeById(entity);
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        return super.removeByMap(columnMap);
    }

    @Override
    public boolean remove(Wrapper<Chart> queryWrapper) {
        return super.remove(queryWrapper);
    }

    @Override
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        return super.removeByIds(list, useFill);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        return super.removeBatchByIds(list);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, boolean useFill) {
        return super.removeBatchByIds(list, useFill);
    }

    @Override
    public boolean updateById(Chart entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean update(Wrapper<Chart> updateWrapper) {
        return super.update(updateWrapper);
    }

    @Override
    public boolean update(Chart entity, Wrapper<Chart> updateWrapper) {
        return super.update(entity, updateWrapper);
    }

    @Override
    public boolean updateBatchById(Collection<Chart> entityList) {
        return super.updateBatchById(entityList);
    }

    @Override
    public Chart getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<Chart> listByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    public List<Chart> listByMap(Map<String, Object> columnMap) {
        return super.listByMap(columnMap);
    }

    @Override
    public Chart getOne(Wrapper<Chart> queryWrapper) {
        return super.getOne(queryWrapper);
    }

    @Override
    public long count() {
        return super.count();
    }

    @Override
    public long count(Wrapper<Chart> queryWrapper) {
        return super.count(queryWrapper);
    }

    @Override
    public List<Chart> list(Wrapper<Chart> queryWrapper) {
        return super.list(queryWrapper);
    }

    @Override
    public List<Chart> list() {
        return super.list();
    }

    @Override
    public <E extends IPage<Chart>> E page(E page, Wrapper<Chart> queryWrapper) {
        return super.page(page, queryWrapper);
    }

    @Override
    public <E extends IPage<Chart>> E page(E page) {
        return super.page(page);
    }

    @Override
    public List<Map<String, Object>> listMaps(Wrapper<Chart> queryWrapper) {
        return super.listMaps(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listMaps() {
        return super.listMaps();
    }

    @Override
    public List<Object> listObjs() {
        return super.listObjs();
    }

    @Override
    public <V> List<V> listObjs(Function<? super Object, V> mapper) {
        return super.listObjs(mapper);
    }

    @Override
    public List<Object> listObjs(Wrapper<Chart> queryWrapper) {
        return super.listObjs(queryWrapper);
    }

    @Override
    public <V> List<V> listObjs(Wrapper<Chart> queryWrapper, Function<? super Object, V> mapper) {
        return super.listObjs(queryWrapper, mapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page, Wrapper<Chart> queryWrapper) {
        return super.pageMaps(page, queryWrapper);
    }

    @Override
    public <E extends IPage<Map<String, Object>>> E pageMaps(E page) {
        return super.pageMaps(page);
    }

    @Override
    public QueryChainWrapper<Chart> query() {
        return super.query();
    }

    @Override
    public LambdaQueryChainWrapper<Chart> lambdaQuery() {
        return super.lambdaQuery();
    }

    @Override
    public KtQueryChainWrapper<Chart> ktQuery() {
        return super.ktQuery();
    }

    @Override
    public KtUpdateChainWrapper<Chart> ktUpdate() {
        return super.ktUpdate();
    }

    @Override
    public UpdateChainWrapper<Chart> update() {
        return super.update();
    }

    @Override
    public LambdaUpdateChainWrapper<Chart> lambdaUpdate() {
        return super.lambdaUpdate();
    }

    @Override
    public boolean saveOrUpdate(Chart entity, Wrapper<Chart> updateWrapper) {
        return super.saveOrUpdate(entity, updateWrapper);
    }

    @Override
    public void createChartDataTable(Long id, Chart chart) {
        // 构造表名
        String tableName = "chart_" + id;

        // 创建数据表
        chartMapper.createChartTable(tableName);

        // 插入数据
        chartMapper.insertChartData(tableName, chart);
    }

    @Override
    public List<Map<String, Object>> queryChartData(Long id) {
        String querySql = String.format("select * from chart_%s", id);
        List<Map<String, Object>> resultData = chartMapper.queryChartData(querySql);
        return resultData;
    }
}
