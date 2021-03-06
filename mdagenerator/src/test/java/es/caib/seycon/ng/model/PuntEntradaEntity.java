//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_PUNENT" )
@Depends ({es.caib.seycon.ng.comu.PuntEntrada.class,
	es.caib.seycon.ng.model.AutoritzacioPUERolEntity.class,
	es.caib.seycon.ng.model.AutoritzacioPUEGrupEntity.class,
	es.caib.seycon.ng.model.AutoritzacioPUEUsuariEntity.class,
	es.caib.seycon.ng.model.ExecucioPuntEntradaEntity.class,
	es.caib.seycon.ng.model.AplicacioEntity.class,
	es.caib.seycon.ng.model.IconaEntity.class,
	es.caib.seycon.ng.model.AuditoriaEntity.class,
	es.caib.seycon.ng.model.ArbrePuntEntradaEntity.class})
public abstract class PuntEntradaEntity {

	@Column (name="PUE_ID")
	@Identifier
	public java.lang.Long id;

	@Column (name="PUE_CODI", length=10)
	@Nullable
	public java.lang.String codi;

	@Column (name="PUE_NOM", length=128)
	public java.lang.String nom;

	@Column (name="PUE_VISIBL", length=1)
	public java.lang.String visible;

	@Column (name="PUE_MENU", length=1)
	public java.lang.String menu;

	@Column (name="PUE_NUMCOL")
	@Nullable
	public java.lang.Long numcolumnes;

	@Column (name="PUE_PUBLIC", length=1)
	@Nullable
	public java.lang.String esPublic;

	@Column (name="PUE_TIPMEN", length=1)
	@Nullable
	public java.lang.String tipusMenu;

	@ForeignKey (foreignColumn="RPE_IDPUE")
	public java.util.Collection<es.caib.seycon.ng.model.AutoritzacioPUERolEntity> autoritzaRol;

	@ForeignKey (foreignColumn="UPE_IDPUE")
	public java.util.Collection<es.caib.seycon.ng.model.AutoritzacioPUEUsuariEntity> autoritzaUsuari;

	@ForeignKey (foreignColumn="GPE_IDPUE")
	public java.util.Collection<es.caib.seycon.ng.model.AutoritzacioPUEGrupEntity> autoritzaGrup;

	@ForeignKey (foreignColumn="EPE_IDPUE")
	public java.util.Collection<es.caib.seycon.ng.model.ExecucioPuntEntradaEntity> metodesExecucio;

	@Column (name="PUE_ICON")
	@Nullable
	public java.lang.Long icona1;

	@Column (name="PUE_ICON2")
	@Nullable
	public java.lang.Long icona2;

	@Column (name="PUE_IDAPL")
	public java.lang.Long idAplicacio;

	@ForeignKey (foreignColumn="PPE_IDPEFI")
	public java.util.Collection<es.caib.seycon.ng.model.ArbrePuntEntradaEntity> arbrePuntEntradaSocFill;

	@ForeignKey (foreignColumn="PPE_IDPEPA")
	public java.util.Collection<es.caib.seycon.ng.model.ArbrePuntEntradaEntity> arbrePuntEntradaSocPare;

	@Column (name="PUE_XML", length=64000)
	@Nullable
	public java.lang.String xmlPUE;

	@DaoFinder
	public es.caib.seycon.ng.model.PuntEntradaEntity findById(
		java.lang.Long id) {
	 return null;
	}
	@DaoFinder("select pue from es.caib.seycon.ng.model.PuntEntradaEntity as pue where (:nom is null or upper(pue.nom) like upper(:nom)) and (:codi is null or (pue.codi is not null and upper(pue.codi) like upper(:codi))) ")
	public java.util.List<es.caib.seycon.ng.model.PuntEntradaEntity> findByCriteris(
		@Nullable java.lang.String nom, 
		@Nullable java.lang.String codi) {
	 return null;
	}
}
