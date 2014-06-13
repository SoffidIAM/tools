//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( translatedName="SoDRuleService",
	 translatedPackage="es.caib.seycon.ng.servei")
@Depends ({es.caib.seycon.ng.model.SoDRuleEntity.class,
	es.caib.seycon.ng.model.SoDRoleEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class})
public abstract class SoDRuleService {

	@Operation ( grantees={Roles.application_update.class,Roles.application_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.SoDRule> findRuleByApplication(
		java.lang.Long applicationId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_update.class,Roles.application_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.SoDRole> findRolesByRule(
		java.lang.Long ruleId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SoDRule create(
		es.caib.seycon.ng.comu.SoDRule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SoDRule update(
		es.caib.seycon.ng.comu.SoDRule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		es.caib.seycon.ng.comu.SoDRule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SoDRole create(
		es.caib.seycon.ng.comu.SoDRole role)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void remove(
		es.caib.seycon.ng.comu.SoDRole role)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.application_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SoDRule isAllowed(
		es.caib.seycon.ng.comu.RolAccount ra)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_role_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void qualifyRolAccountList(
		java.util.List<es.caib.seycon.ng.comu.RolAccount> ra)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.user_role_query.class,Roles.application_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.SoDRule> findAffectingRulesByRolAccount(
		es.caib.seycon.ng.comu.RolAccount ra)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_role_query.class,Roles.application_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.SoDRule getRuleById(
		java.lang.Long ruleId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void internalRemovingRole(
		java.lang.Long roleId)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
