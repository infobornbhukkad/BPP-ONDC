// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.common.model;

import java.io.Serializable;

import com.bb.beckn.search.extension.Schema;

public class DomainsParamModel implements Serializable
{
    private static final long serialVersionUID = 7461538270839107324L;
    private String domain;
    private String domainName;
    private String methodName;
    
    public String getDomain() {
        return this.domain;
    }

    public String getDomainName() {
        return this.domainName;
    }
    
    public String getMethodName(Schema request, ConfigModel configModel) {
        return this.methodName;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }
    
    @Override
    public String toString() {
        return "DomainsParamModel(domain=" + this.getDomain() + ", domainName=" + this.getDomainName() + ", methodName=" + this.getMethodName(null, null) + ")";
    }
}
