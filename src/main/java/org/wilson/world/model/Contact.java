package org.wilson.world.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Contact {
    public int id;
    
    public String name;
    
    public String content;
    
    public long createdTime;
    
    public long modifiedTime;
    
    public List<ContactAttr> attrs = new ArrayList<ContactAttr>();
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "[" + id + "]");
        sb.append(":");
        for(int i = 0; i < attrs.size(); i++) {
            ContactAttr attr = attrs.get(i);
            sb.append(attr.name + "=" + attr.value);
            if(i != attrs.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    public String getValue(String attrName) {
        if(StringUtils.isBlank(attrName)) {
            return null;
        }
        
        for(ContactAttr attr : this.attrs) {
            if(attrName.equals(attr.name)) {
                return attr.value;
            }
        }
        return null;
    }
}
