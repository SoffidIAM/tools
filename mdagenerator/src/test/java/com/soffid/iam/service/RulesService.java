//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.service;
import com.soffid.iam.model.MetaAccountEntity;
import com.soffid.mda.annotation.*;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

@Service ( 
	 )
@Depends ({com.soffid.iam.model.RuleEntity.class,
	com.soffid.iam.model.RuleAssignedRoleEntity.class,
	es.caib.seycon.ng.model.RolEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class})
public abstract class RulesService {

	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.api.Rule create(
		com.soffid.iam.api.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.api.Rule update(
		com.soffid.iam.api.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.api.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.rule_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.api.Rule> findRules(
		@Nullable java.lang.String description)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.api.RuleAssignedRole create(
		com.soffid.iam.api.RuleAssignedRole ruleAssignment)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public com.soffid.iam.api.RuleAssignedRole update(
		com.soffid.iam.api.RuleAssignedRole ruleAssignment)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		com.soffid.iam.api.RuleAssignedRole ruleAssignment)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.rule_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<com.soffid.iam.api.RuleAssignedRole> findRuleAssignments(
		com.soffid.iam.api.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.rule_admin.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void apply(
		com.soffid.iam.api.Rule rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	
	public void test(Collection<MetaAccountEntity> acc ) { }	
}
