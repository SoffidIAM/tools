//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.service;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( internal=true,
	 
	 )
@Depends ({es.caib.seycon.ng.model.RolAccountEntity.class,
	com.soffid.iam.model.RuleAssignedRoleEntity.class,
	com.soffid.iam.model.RuleEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class})
public abstract class RuleEvaluatorService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public void apply(
		com.soffid.iam.model.RuleEntity rule, 
		es.caib.seycon.ng.model.UsuariEntity user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void applyRules(
		es.caib.seycon.ng.model.UsuariEntity user)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void apply(
		com.soffid.iam.model.RuleEntity rule)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
}
