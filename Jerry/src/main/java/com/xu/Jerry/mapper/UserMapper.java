package com.xu.Jerry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.Jerry.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
