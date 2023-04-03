/subsystem=security/security-domain=PfadisRealm:add
/subsystem=security/security-domain=PfadisRealm/authentication=classic:add
/subsystem=security/security-domain=PfadisRealm/authentication=classic/login-module=RealmDirect:add(code=RealmDirect,flag=required)
/subsystem=security/security-domain=PfadisRealm/authentication=classic/login-module=RealmDirect:write-attribute(name=module-options,value={realm=PfadisRealm})
/subsystem=security/security-domain=PfadisRealm/authentication=classic/login-module=RealmDirect:write-attribute(name=module-options,value={password-stacking=useFirstPass})

/core-service=management/security-realm=PfadisRealm:add
/core-service=management/security-realm=PfadisRealm/authentication=properties:add(path=pfadis-users.properties,relative-to=jboss.server.config.dir)
/core-service=management/security-realm=PfadisRealm/authorization=properties:add(path=pfadis-roles.properties,relative-to=jboss.server.config.dir)

