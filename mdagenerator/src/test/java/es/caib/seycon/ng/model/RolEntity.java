//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_ROLES" )
@Depends ({es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.ValorDominiAplicacioEntity.class,
	es.caib.seycon.ng.model.GrupEntity.class,
	es.caib.seycon.ng.model.UsuariEntity.class,
	es.caib.seycon.ng.model.DispatcherEntity.class,
	es.caib.seycon.ng.comu.Identitat.class,
	es.caib.seycon.ng.model.DominiAplicacioEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.comu.Rol.class,
	es.caib.seycon.ng.comu.ContenidorRol.class,
	es.caib.seycon.ng.model.RolAssociacioRolEntity.class,
	es.caib.seycon.ng.model.TasqueEntity.class,
	es.caib.seycon.ng.model.RolsGrupEntity.class,
	es.caib.seycon.ng.model.RolAccountEntity.class,
	es.caib.seycon.ng.model.XarxaACEntity.class,
	es.caib.seycon.ng.model.ControlAccessEntity.class,
	es.caib.seycon.ng.model.NotificacioEntity.class,
	es.caib.seycon.ng.model.AutoritzacioRolEntity.class,
	es.caib.seycon.ng.model.AccountAccessEntity.class,
	com.soffid.iam.model.RuleAssignedRoleEntity.class,
	es.caib.seycon.ng.model.SoDRoleEntity.class})
public abstract class RolEntity {

	@Column (name="ROL_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="ROL_NOM", length=150)
	public java.lang.String nom;

	@Column (name="ROL_DESCRI", length=150)
	public java.lang.String descripcio;

	@Column (name="ROL_DEFECT", length=1)
	public java.lang.String defecte;

	@Column (name="ROL_CONTRA", length=1)
	@Nullable
	public java.lang.String contrasenya;

	@Column (name="ROL_IDAPL")
	public es.caib.seycon.ng.model.AplicacioEntity aplicacio;

	@ForeignKey (foreignColumn="RLU_IDROL")
	public java.util.Collection<es.caib.seycon.ng.model.RolAccountEntity> accounts;

	@Column (name="ROL_IDDISPAT")
	@Nullable
	public es.caib.seycon.ng.model.DispatcherEntity baseDeDades;

	@Column (name="ROL_DOMAPP")
	@Nullable
	public es.caib.seycon.ng.model.DominiAplicacioEntity dominiAplicacio;

	@Column (name="ROL_TIPDOM", length=50)
	@Nullable
	public java.lang.String tipusDomini;

	@ForeignKey (foreignColumn="RRL_CONTINGUT")
	public java.util.Collection<es.caib.seycon.ng.model.RolAssociacioRolEntity> rolAssociacioRolSocContingut;

	@ForeignKey (foreignColumn="RRL_CONTENIDOR")
	public java.util.Collection<es.caib.seycon.ng.model.RolAssociacioRolEntity> rolAssociacioRolSocContenidor;

	@ForeignKey (foreignColumn="RLG_ROL")
	public java.util.Collection<es.caib.seycon.ng.model.RolsGrupEntity> grupsPosseidorsRol;

	@ForeignKey (foreignColumn="AXA_IDROL")
	public java.util.Collection<es.caib.seycon.ng.model.XarxaACEntity> autoritzacionsXarxa;

	@ForeignKey (foreignColumn="CAC_ROL_ID")
	public java.util.Collection<es.caib.seycon.ng.model.ControlAccessEntity> controlAccess;

	@Column (name="ROL_GEST_WF", length=1)
	@Nullable
	public java.lang.String gestionableWF;

	@ForeignKey (foreignColumn="NTF_ROL")
	public java.util.Collection<es.caib.seycon.ng.model.NotificacioEntity> notificacioEntities;

	@ForeignKey (foreignColumn="AUR_ROL")
	public java.util.Collection<es.caib.seycon.ng.model.AutoritzacioRolEntity> autoritzacions;

	@ForeignKey (foreignColumn="AAC_ROL_ID")
	public java.util.Collection<es.caib.seycon.ng.model.AccountAccessEntity> accountAccess;

	@ForeignKey (foreignColumn="RUR_ROL_ID")
	public java.util.Collection<com.soffid.iam.model.RuleAssignedRoleEntity> rules;

	@ForeignKey (foreignColumn="SOR_ROL_ID")
	public java.util.Collection<es.caib.seycon.ng.model.SoDRoleEntity> sodRules;

	@DaoFinder("select rol \nfrom \nes.caib.seycon.ng.model.RolEntity rol \nwhere \nrol.aplicacio.codi = :codiAplicacio \norder by rol.nom, rol.baseDeDades.codi")
	public java.util.List<es.caib.seycon.ng.model.RolEntity> findByCodiAplicacio(
		java.lang.String codiAplicacio) {
	 return null;
	}
	@DaoFinder
	public es.caib.seycon.ng.model.RolEntity findById(
		java.lang.Long id) {
	 return null;
	}
	@DaoFinder("select rolEntity \nfrom es.caib.seycon.ng.model.RolEntity rolEntity \nwhere \nrolEntity.aplicacio.codi = :codiAplicacio and\nrolEntity.nom = :nomRol and\nrolEntity.baseDeDades.codi = :codiDispatcher")
	public es.caib.seycon.ng.model.RolEntity findByNomRolAndCodiAplicacioAndCodiDispatcher(
		java.lang.String nomRol, 
		java.lang.String codiAplicacio, 
		java.lang.String codiDispatcher) {
	 return null;
	}
	@DaoFinder("select rol from es.caib.seycon.ng.model.RolEntity rol left join rol.baseDeDades baseDeDades where \n(:nom is null or rol.nom like :nom) and (:descripcio is null or rol.descripcio like :descripcio) and (:defecte is null or rol.defecte = :defecte) and (:baseDeDades is null or baseDeDades.codi like :baseDeDades) and (:contrasenya is null or rol.contrasenya = :contrasenya)  and (:codiAplicacio is null or rol.aplicacio.codi like :codiAplicacio) \norder by rol.nom, rol.baseDeDades.codi")
	public java.util.List<es.caib.seycon.ng.model.RolEntity> findRolsByFiltre(
		java.lang.String nom, 
		java.lang.String descripcio, 
		java.lang.String defecte, 
		java.lang.String baseDeDades, 
		java.lang.String contrasenya, 
		java.lang.String codiAplicacio) {
	 return null;
	}
	@DaoFinder("select rol \nfrom es.caib.seycon.ng.model.UsuariEntity usu\n join usu.accounts as accounts\n join accounts.account as account with account.type='U'\n join account.roles as roles\n join roles.rol as rol\n join rol.aplicacio as aplicacio\nwhere usu.codi = :codiUsuari and aplicacio.codi = :codiAplicacio")
	public java.util.List<es.caib.seycon.ng.model.RolEntity> getRolsAplicacioByCodiUsuariAndCodiAplicacio(
		java.lang.String codiUsuari, 
		java.lang.String codiAplicacio) {
	 return null;
	}
	@DaoFinder("select rol \nfrom es.caib.seycon.ng.model.UsuariEntity usu\n join usu.accounts as accounts\n join accounts.account as account with account.type='U'\n join account.roles as roles\n join roles.rol as rol\nwhere usu.codi = :codiUsuari ")
	public java.util.List<es.caib.seycon.ng.model.RolEntity> findRolsByCodiUsuari(
		java.lang.String codiUsuari) {
	 return null;
	}
	public java.lang.String toString() {
	 return null;
	}
	public java.lang.String toDescripcioRol() {
	 return null;
	}
	@DaoFinder("select rol from es.caib.seycon.ng.model.RolEntity rol left join rol.baseDeDades baseDeDades where \n(:nom is null or rol.nom like :nom) and (:descripcio is null or rol.descripcio like :descripcio) and (:defecte is null or rol.defecte = :defecte) and (:baseDeDades is null or baseDeDades.codi like :baseDeDades) and (:contrasenya is null or rol.contrasenya = :contrasenya)  and (:codiAplicacio is null or rol.aplicacio.codi like :codiAplicacio) and (:gestionableWF is null or rol.gestionableWF =:gestionableWF)\norder by rol.nom, rol.baseDeDades.codi")
	public java.util.List<es.caib.seycon.ng.model.RolEntity> findRolsByFiltreGestionablesWF(
		java.lang.String nom, 
		java.lang.String descripcio, 
		java.lang.String defecte, 
		java.lang.String baseDeDades, 
		java.lang.String contrasenya, 
		java.lang.String codiAplicacio, 
		java.lang.String gestionableWF) {
	 return null;
	}
	@DaoFinder("select rolEntity \nfrom es.caib.seycon.ng.model.RolEntity rolEntity \nwhere \nrolEntity.nom = :nomRol and\nrolEntity.baseDeDades.codi = :codiDispatcher")
	public es.caib.seycon.ng.model.RolEntity findByNameAndDispatcher(
		java.lang.String nomRol, 
		java.lang.String codiDispatcher) {
	 return null;
	}

	@DaoFinder("select rolEntity \nfrom es.caib.seycon.ng.model.RolEntity rolEntity \nwhere \nrolEntity.nom = :shortName")
	public es.caib.seycon.ng.model.RolEntity findByShortName(
		java.lang.String shortName) {
	 return null;
	}
}
