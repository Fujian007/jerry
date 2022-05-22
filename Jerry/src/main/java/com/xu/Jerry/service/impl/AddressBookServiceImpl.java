package com.xu.Jerry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.Jerry.entity.AddressBook;
import com.xu.Jerry.mapper.AddressBookMapper;
import com.xu.Jerry.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressBookServiceImpl extends
        ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
