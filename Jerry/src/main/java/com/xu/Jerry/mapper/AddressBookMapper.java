package com.xu.Jerry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.Jerry.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}