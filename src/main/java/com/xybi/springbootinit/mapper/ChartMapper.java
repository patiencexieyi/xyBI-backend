package com.xybi.springbootinit.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xybi.springbootinit.model.entity.Chart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author hore1
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2025-08-14 09:48:56
* @Entity generator.domain.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    /*
     * 方法的返回类型是 List<Map<String, Object>>,
     * 表示返回的是一个由多个 map 组成的集合,每个map代表了一行查询结果，
     * 并将其封装成了一组键值对形式的对象。其中,String类型代表了键的类型为字符串，
     * Object 类型代表了值的类型为任意对象,使得这个方法可以适应不同类型的数据查询。
     *
     */
    @MapKey("id")
    List<Map<String, Object>> queryChartData(String querySql);

    /**
     * 创建图表数据表
     * @param tableName 表名
     */
    void createChartTable(@Param("tableName") String tableName);

    /**
     * 向图表数据表插入数据
     * @param tableName 表名
     * @param chart 图表数据
     */
    void insertChartData(@Param("tableName") String tableName, @Param("chart") Chart chart);
}
