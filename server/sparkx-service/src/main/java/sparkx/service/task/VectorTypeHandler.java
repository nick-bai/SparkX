// +----------------------------------------------------------------------
// | SparkX 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://ai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkX 并不是自由软件，未经许可不能去掉 SparkX 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkx.service.task;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import com.pgvector.PGvector;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VectorTypeHandler extends BaseTypeHandler<List<Float>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Float> embedding, JdbcType jdbcType) throws SQLException {
        // 将 List<Float> 转为 PGvector 对象
        float[] array = toPrimitiveArray(embedding);
        PGvector pgvector = new PGvector(array);
        ps.setObject(i, pgvector);
    }

    @Override
    public List<Float> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从结果集中解析 PGvector
        PGvector pgvector = (PGvector) rs.getObject(columnName);
        return toFloatList(pgvector.toArray());
    }

    @Override
    public List<Float> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        PGvector pgvector = (PGvector) rs.getObject(columnIndex);
        return toFloatList(pgvector.toArray());
    }

    @Override
    public List<Float> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        PGvector pgvector = (PGvector) cs.getObject(columnIndex);
        return toFloatList(pgvector.toArray());
    }

    // 辅助方法：List<Float> 转 float[]
    private float[] toPrimitiveArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    // 辅助方法：float[] 转 List<Float>
    private List<Float> toFloatList(float[] arr) {

        List<Float> floatList = new ArrayList<>();
        for (float num : arr) {
            floatList.add(num);
        }

        return floatList;
    }
}