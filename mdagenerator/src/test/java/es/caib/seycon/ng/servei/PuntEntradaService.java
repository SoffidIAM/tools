//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service ( grantees={Roles.intranetMenus_admin.class,Roles.Tothom.class})
@Depends ({es.caib.seycon.ng.model.PuntEntradaEntity.class,
	es.caib.seycon.ng.model.TipusExecucioPuntEntradaEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.ExecucioPuntEntradaEntity.class,
	es.caib.seycon.ng.model.ArbrePuntEntradaEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.AutoritzacioPUERolEntity.class,
	es.caib.seycon.ng.model.AutoritzacioPUEGrupEntity.class,
	es.caib.seycon.ng.model.AutoritzacioPUEUsuariEntity.class,
	es.caib.seycon.ng.model.IconaEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class})
public abstract class PuntEntradaService {

	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PuntEntrada create(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PuntEntrada update(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
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
	@Operation (translated="getAllMimeTypeExecution")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.TipusExecucioPuntEntrada> getAllTipusMimeExecucio()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getAllApplications")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Aplicacio> getAllAplicacions(
		java.lang.Boolean aplicacioBuida)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getAuthorizationsApplicationAcessTree")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.AutoritzacioPuntEntrada> getAutoritzacionsPUE(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean canView(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean canQuery(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean canAdmin(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean canExecute(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="isAuthorized")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean esAutoritzat(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		java.lang.String nivell)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="reorderApplicationAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean reordenaPuntEntrada(
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaOrdenar, 
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaSeguent)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="moveApplicationAccessTreeMenu")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean moureMenusPuntEntrada(
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaMoure, 
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaMenuDesti)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="copyApplicationAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean copiaPuntEntrada(
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaCopiar, 
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaMenuDesti)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="copyApplicationAccessLink")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean copiaEnlacePuntEntrada(
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaCopiar, 
		es.caib.seycon.ng.comu.PuntEntrada puntEntradaMenuDesti)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="createAuthorization")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.AutoritzacioPuntEntrada createAutoritzacio(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		es.caib.seycon.ng.comu.AutoritzacioPuntEntrada autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="deleteAuthorization")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void deleteAutoritzacio(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		es.caib.seycon.ng.comu.AutoritzacioPuntEntrada autoritzacio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="createExecution")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ExecucioPuntEntrada createExecucio(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		es.caib.seycon.ng.comu.ExecucioPuntEntrada execucio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="updateExecution")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.ExecucioPuntEntrada updateExecucio(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		es.caib.seycon.ng.comu.ExecucioPuntEntrada execucio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="deleteExecution")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void deleteExecucio(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada, 
		es.caib.seycon.ng.comu.ExecucioPuntEntrada execucio)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation (translated="getExecutions")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.ExecucioPuntEntrada> getExecucions(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.PuntEntrada> findMenuChildren(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="validateXMLApplicationAccess")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.lang.String validaXMLPUE(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="findApplicationAccessById")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.PuntEntrada> findPuntsEntrada(
		java.lang.String nomPUE, 
		java.lang.String codiPUE, 
		java.lang.String codiAplicacio, 
		java.lang.String codiRol, 
		java.lang.String codiGrup, 
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="getReverseApplicationAccessTree")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<java.lang.String> getArbreInversPuntEntrada(
		es.caib.seycon.ng.comu.PuntEntrada puntEntrada)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation (translated="isAuthorized")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean esAutoritzat(
		java.lang.String codiUsuari, 
		java.lang.Long idPuntEntrada, 
		java.lang.String nivell)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="applicationAccessTreeHasAnyACL")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public boolean hasAnyACLPUE(
		java.lang.String codiUsuari)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return false;
	}
	@Operation (translated="findApplicationAccessById")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.PuntEntrada findPuntEntradaById(
		long id)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
