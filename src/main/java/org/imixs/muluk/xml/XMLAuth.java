package org.imixs.muluk.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Defines an authentication object
 * 
 * @author rsoika
 *
 */
@XmlRootElement(name = "auth")
public class XMLAuth implements java.io.Serializable {

    @XmlTransient
    private static final long serialVersionUID = 1L;
    private String type;
    private String user;
    private String password;
    private String token;
    private int tokenCacheTimeout = 3600; // in seconds - default 1 hour
    private long tokenCreationTime = 0;

    public XMLAuth() {
        super();
    }

    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute
    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    @XmlAttribute
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @XmlTransient
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @XmlTransient
    public int getTokenCacheTimeout() {
        return tokenCacheTimeout;
    }

    public void setTokenCacheTimeout(int tokenCacheTimeout) {
        this.tokenCacheTimeout = tokenCacheTimeout;
    }

    @XmlTransient
    public long getTokenCreationTime() {
        return tokenCreationTime;
    }

    public void setTokenCreationTime(long tokenCreationTime) {
        this.tokenCreationTime = tokenCreationTime;
    }

}
