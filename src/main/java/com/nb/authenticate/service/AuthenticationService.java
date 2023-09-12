package com.nb.authenticate.service;

import com.nb.authenticate.entity.Userinfo;
import com.nb.authenticate.repository.UserinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

@Service
public class AuthenticationService implements UserDetailsService {
    @Autowired
    private UserinfoRepository userinfoRepository;
    private final LdapTemplate ldapTemplate;

    public AuthenticationService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        Optional<Userinfo> userinfo = userinfoRepository.existingDbuser(userid);
        if(userinfo.isPresent()){
            return new org.springframework.security.core.userdetails.User(userinfo.get().getUserid(), userinfo.get().getUserpassword(), new ArrayList<>());
        }
        else{
            String password=checkLdapUser(userid);
            return new org.springframework.security.core.userdetails.User(
                    userid,
                    password,
                    new ArrayList<>()
            );
        }

    }
    public String checkLdapUser(String userid){
        String ldapUrl = "ldap://192.168.2.111:389";
        String ldapUsername = "cn=admin,dc=nichebit,dc=com";
        String ldapPassword = "nichebit";
        String searchBase = "dc=nichebit,dc=com";
        String userToSearch = userid;
        try {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
            DirContext ctx = new InitialDirContext(env);
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> results = ctx.search(searchBase, "uid=" + userToSearch, searchControls);
            SearchResult searchResult = results.next();
            Attributes attributes = searchResult.getAttributes();
            Attribute passwordAttribute = attributes.get("userPassword");
            String password = new String((byte[]) passwordAttribute.get());
            ctx.close();
            return password;
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
