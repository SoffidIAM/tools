//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.sync.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( serverOnly=true,
	 serverPath="/seycon/SecretStoreService",
	 serverRole="agent")
@Depends ({es.caib.seycon.ng.model.ServerEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.SecretEntity.class,
	es.caib.seycon.ng.sync.servei.SecretConfigurationService.class,
	com.soffid.iam.model.MetaAccountEntity.class,
	es.caib.seycon.ng.model.DominiContrasenyaEntity.class})
public abstract class SecretStoreService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<es.caib.seycon.ng.comu.sso.Secret> getSecrets(
		es.caib.seycon.ng.comu.Usuari user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password getSecret(
		es.caib.seycon.ng.comu.Usuari user, 
		java.lang.String secret)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void putSecret(
		es.caib.seycon.ng.comu.Usuari user, 
		java.lang.String secret, 
		es.caib.seycon.ng.comu.Password value)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void reencode(
		es.caib.seycon.ng.comu.Usuari user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Usuari> getUsersWithSecrets()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void removeSecret(
		es.caib.seycon.ng.comu.Usuari user, 
		java.lang.String secret)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setPassword(
		long accountId, 
		es.caib.seycon.ng.comu.Password value)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password getPassword(
		long accountId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Account> getAccountsWithPassword()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.List<es.caib.seycon.ng.comu.sso.Secret> getAllSecrets(
		es.caib.seycon.ng.comu.Usuari user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setPassword(
		es.caib.seycon.ng.comu.Usuari user, 
		java.lang.String passwordDomain, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
