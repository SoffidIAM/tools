//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( grantees={Roles.Tothom.class})
@Depends ({es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DominiContrasenyaEntity.class,
	es.caib.seycon.ng.model.PoliticaContrasenyaEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	com.soffid.iam.model.MetaAccountEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class})
public abstract class PasswordService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PolicyCheckResult checkPolicy(
		java.lang.String account, 
		java.lang.String dispatcher, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkPassword(
		java.lang.String account, 
		java.lang.String dispatcher, 
		es.caib.seycon.ng.comu.Password password, 
		boolean checkTrusted, 
		boolean checkExpired)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkPin(
		java.lang.String user, 
		java.lang.String pin)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void changePassword(
		java.lang.String account, 
		java.lang.String dispatcher, 
		es.caib.seycon.ng.comu.Password oldPassword, 
		es.caib.seycon.ng.comu.Password newPassword)
		throws es.caib.seycon.ng.exception.InternalErrorException, es.caib.seycon.ng.exception.BadPasswordException, es.caib.seycon.ng.exception.InvalidPasswordException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean checkPasswordExpired(
		java.lang.String accoount, 
		@Nullable java.lang.String dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation ( grantees={Roles.user_password_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getDefaultDispatcher()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getPolicyDescription(
		java.lang.String account, 
		java.lang.String dispatcher)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
