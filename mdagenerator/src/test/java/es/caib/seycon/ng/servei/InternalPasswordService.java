//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( internal=true)
@Depends ({es.caib.seycon.ng.model.ContrasenyaEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.sync.servei.TaskQueue.class,
	es.caib.seycon.ng.model.PoliticaContrasenyaEntity.class,
	es.caib.seycon.ng.model.DominiContrasenyaEntity.class,
	es.caib.seycon.ng.model.DominiUsuariEntity.class,
	es.caib.seycon.ng.model.TipusUsuariEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.model.AccountPasswordEntity.class,
	com.soffid.iam.model.MetaAccountEntity.class})
public abstract class InternalPasswordService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PolicyCheckResult checkPolicy(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.PoliticaContrasenyaEntity politica, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PolicyCheckResult checkAccountPolicy(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storePassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAccountPassword(
		java.lang.String account, 
		java.lang.String dispatcher, 
		java.lang.String password, 
		boolean mustChange, 
		@Nullable java.util.Date expirationDate)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storePassword(
		java.lang.String user, 
		java.lang.String passwordDomain, 
		java.lang.String password, 
		boolean mustChange)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange, 
		@Nullable java.util.Date expirationDate)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void disableExpiredPasswords()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void disableUntrustedPasswords()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PasswordValidation checkPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password, 
		boolean checkTrusted, 
		boolean checkExpired)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PasswordValidation checkAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password, 
		boolean checkTrusted, 
		boolean checkExpired)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void confirmPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void confirmAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkPin(
		es.caib.seycon.ng.model.UsuariEntity user, 
		java.lang.String pin)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isOldPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isOldAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password generateNewPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passDomain, 
		boolean mustBeChanged)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password generateNewAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		boolean mustBeChanged)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.EstatContrasenya getPasswordsStatus(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity domini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.EstatContrasenya getAccountPasswordsStatus(
		com.soffid.iam.model.MetaAccountEntity account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.EstatContrasenya> getExpiredPasswords(
		java.util.Date desde, 
		java.util.Date finsa, 
		es.caib.seycon.ng.model.TipusUsuariEntity tipusUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password generateFakePassword(
		@Nullable es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passDomain)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password generateFakeAccountPassword(
		@Nullable com.soffid.iam.model.MetaAccountEntity account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndForwardPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndForwardAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange, 
		@Nullable java.util.Date expirationDate)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isAccountPasswordExpired(
		com.soffid.iam.model.MetaAccountEntity account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean isPasswordExpired(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean existsPassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean existsAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getDefaultDispatcher()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PolicyCheckResult checkPolicy(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getPolicyDescription(
		es.caib.seycon.ng.model.PoliticaContrasenyaEntity politica)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PolicyCheckResult checkPolicy(
		es.caib.seycon.ng.model.PoliticaContrasenyaEntity policy, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean updateExpiredPasswords(
		es.caib.seycon.ng.model.UsuariEntity usuari, 
		boolean externalAuth)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.model.DominiContrasenyaEntity> enumExpiredPasswords(
		es.caib.seycon.ng.model.UsuariEntity usuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndSynchronizePassword(
		es.caib.seycon.ng.model.UsuariEntity user, 
		es.caib.seycon.ng.model.DominiContrasenyaEntity passwordDomain, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndSynchronizeAccountPassword(
		com.soffid.iam.model.MetaAccountEntity account, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange, 
		@Nullable java.util.Date expirationDate)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndForwardPasswordById(
		long user, 
		long passwordDomain, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void storeAndForwardAccountPasswordById(
		long account, 
		es.caib.seycon.ng.comu.Password password, 
		boolean mustChange, 
		@Nullable java.util.Date expirationDate)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.EstatContrasenya getPasswordsStatusById(
		long user, 
		long domini)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.EstatContrasenya getAccountPasswordsStatusById(
		long account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
