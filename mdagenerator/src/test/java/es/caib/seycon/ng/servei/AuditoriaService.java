//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.AuditoriaEntity.class})
public abstract class AuditoriaService {

	@Operation ( grantees={Roles.audit_query.class},
	translated="findAuditById")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Auditoria findAuditoriaById(
		java.lang.Long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.audit_query.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String[] find(
		java.lang.String sqlQuery)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.audit_query.class},
	translated="findAuditsByCriteria")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Auditoria> findAuditoriesByCriteri(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String dataFi, 
		@Nullable java.lang.String autor, 
		@Nullable java.lang.String objecte, 
		@Nullable java.lang.String usuari, 
		@Nullable java.lang.String accio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.audit_custom_query.class,Roles.audit_query.class},
	translated="findAuditsByCriteria")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Auditoria> findAuditoriesByCriteri(
		@Nullable java.lang.String dataIni, 
		@Nullable java.lang.String dataFi, 
		@Nullable java.lang.String autor, 
		@Nullable java.lang.String objecte, 
		@Nullable java.lang.String usuari, 
		@Nullable java.lang.String objecteAuditat, 
		@Nullable java.lang.String valorOA, 
		@Nullable java.lang.String accio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.audit_query.class},
	translated="findAuditsByCriteria")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Auditoria> findAuditoriesByCriteri(
		@Nullable java.lang.String data, 
		@Nullable java.lang.String autor, 
		@Nullable java.lang.String objecte, 
		@Nullable java.lang.String usuari, 
		@Nullable java.lang.String accio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.audit_query.class},
	translated="findAuditsByCriteria")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Auditoria> findAuditoriesByCriteri(
		@Nullable java.lang.String data, 
		@Nullable java.lang.String autor, 
		@Nullable java.lang.String objecte, 
		@Nullable java.lang.String usuari, 
		@Nullable java.lang.String objecteAuditat, 
		@Nullable java.lang.String valorOA, 
		@Nullable java.lang.String accio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Auditoria create(
		es.caib.seycon.ng.comu.Auditoria auditoria)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
