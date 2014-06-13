//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.servei;
import com.soffid.mda.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@Service @Depends ({es.caib.seycon.ng.model.GrupImpressoraEntity.class,
	es.caib.seycon.ng.model.UsuariImpressoraEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.ImpressoraEntity.class})
public abstract class ImpressoraService {

	@Operation ( grantees={Roles.Tothom.class},
	translated="getPrinters")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Impressora> getImpressores()
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.printer_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Impressora create(
		es.caib.seycon.ng.comu.Impressora impressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.printer_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.Impressora impressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.printer_update.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Impressora update(
		es.caib.seycon.ng.comu.Impressora impressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findPrintersByPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Impressora> findImpressoresByCodiImpressora(
		@Nullable java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.printer_query.class},
	translated="findPrintersByFilter")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.Impressora> findImpressoresByCriteri(
		@Nullable java.lang.String codi, 
		@Nullable java.lang.String model, 
		@Nullable java.lang.String local, 
		@Nullable java.lang.String maquina)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findPrinterByPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.Impressora findImpressoraByCodiImpressora(
		java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_printer_create.class,Roles.user_printer_acl_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariImpressora create(
		es.caib.seycon.ng.comu.UsuariImpressora usuariImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_printer_delete.class,Roles.user_printer_acl_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.UsuariImpressora usuariImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.group_printer_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.GrupImpressora create(
		es.caib.seycon.ng.comu.GrupImpressora grupImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_printer_delete.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public void delete(
		es.caib.seycon.ng.comu.GrupImpressora grupImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findPrinterGroupByGroupCodeAndPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.GrupImpressora findGrupImpressoraByCodiGrupAndCodiImpressora(
		java.lang.String codiGrup, 
		java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="findPrinterUserByUserCodeAndPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariImpressora findUsuariImpressoraByCodiUsuariAndCodiImpressora(
		java.lang.String codiUsuari, 
		java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.printer_query.class},
	translated="getUserPrintersByPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.UsuariImpressora> getUsuariImpressoresByCodiImpressora(
		java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class},
	translated="getPrintersGroupByPrinterCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection getGrupImpressoresByCodiImpressora(
		java.lang.String codiImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.user_printer_create.class,Roles.user_custom_update.class,Roles.user_printer_acl_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.UsuariImpressora update(
		es.caib.seycon.ng.comu.UsuariImpressora usuariImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.Tothom.class,Roles.group_query.class},
	translated="findPrintersGroupByGroupCode")
	@Transactional(rollbackFor={java.lang.Exception.class})
	public java.util.Collection<es.caib.seycon.ng.comu.GrupImpressora> findGrupImpressoresByCodiGrup(
		java.lang.String codiGrup)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
	@Operation ( grantees={Roles.group_printer_create.class})
	@Transactional(rollbackFor={java.lang.Exception.class})
	public es.caib.seycon.ng.comu.GrupImpressora update(
		es.caib.seycon.ng.comu.GrupImpressora grupImpressora)
		throws es.caib.seycon.ng.exception.InternalErrorException {
	 return null;
	}
}
