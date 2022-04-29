package com.atguigu.yygh.cmn.service.impl;



import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.listener.DictListener;

import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry
 * @create 2022-02-16 22:28
 */
@Service
/*可以不加，也可以正常使用缓存*/@CacheConfig(cacheNames = "dictServiceImpl-->",keyGenerator = "keyGenerator")
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    //根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictcode 获取对于id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> childData = this.findChildData(dict.getId());
        return childData;
    }

    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode值为空，直接根据value查询
        if(StringUtils.isEmpty(dictCode)){
            //直接根据value查询
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else{
            //如果不为空，根据dictCode和value查询
            //根据dictcode查询dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parent_id = codeDict.getId();
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parent_id)
                    .eq("value", value));
            return finalDict.getName();
        }
    }
    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codeDict = baseMapper.selectOne(wrapper);
        return codeDict;
    }

    //导入字典
    @Override
    @CacheEvict(value = "dict", allEntries=true)
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper))
                     .sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //这里面mapper注入已经不需要写了，ServerImpl已经帮我们注入了
    @Override
    public void exportDictData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict ---> DictEeVo
        List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
        for(Dict dict : dictList) {
            DictEeVo dictVo = new DictEeVo();
          //  dictVo.setId(dict.getId());
            BeanUtils.copyProperties(dict, dictVo);
            dictVoList.add(dictVo);
        }
        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict")
                     .doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //根据数据id子数据列表
    @Override
    //@Cacheable(value = "dict",keyGenerator = "keyGenerator")  //不加@cacheConfig
    @Cacheable  //前提是类上面配置了@CacheConfig(cacheNames = "dictServiceImpl-->",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for(Dict dict : dictList){
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    //判断id下面是否有子数据
    private Boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
