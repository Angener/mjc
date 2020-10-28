package com.epam.esm.util;

import com.epam.esm.entity.Tag;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class BeanFactory {

    @Lookup
    public Tag getTag(String name){
        return null;
    }
}
