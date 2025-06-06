//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( grantees={Roles.Tothom.class})
@Depends ({com.soffid.iam.model.MetaAccountEntity.class})
public abstract class SelfService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Account> getUserAccounts()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setAccountPassword(
		es.caib.seycon.ng.comu.Account account, 
		es.caib.seycon.ng.comu.Password password)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void setHPAccountPassword(
		es.caib.seycon.ng.comu.Account account, 
		es.caib.seycon.ng.comu.Password password, 
		java.util.Date untilDate, 
		boolean force)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="getCurrentUser")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Usuari getCurrentUsuari()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findUserGroupsByUserCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariGrup> findUsuariGrupsByCodiUsuari()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PuntEntrada findRoot()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.PuntEntrada> findChildren(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_role_query.class,Roles.application_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.RolAccount> findRolAccounts()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Password queryAccountPassword(
		es.caib.seycon.ng.comu.Account account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String getClientHost()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.EstatContrasenya passwordsStatus(
		es.caib.seycon.ng.comu.Account account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String queryOtherAffectedAccounts(
		es.caib.seycon.ng.comu.Account account)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Dispatcher getDispatcherInformation(
		java.lang.String dispatcherCode)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
