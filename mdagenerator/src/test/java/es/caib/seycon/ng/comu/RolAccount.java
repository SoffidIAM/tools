//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject ( 
	 )
public abstract class RolAccount {

	@Nullable
	public java.lang.Long accountId;

	@Nullable
	public java.lang.String accountName;

	@Nullable
	public java.lang.String accountDispatcher;

	@Nullable
	@Attribute(translated = "roleName" )
	public java.lang.String nomRol;

	@Nullable
	@Attribute(translated = "applicationCode" )
	public java.lang.String codiAplicacio;

	@Nullable
	@Attribute(translated = "roleDescription" )
	public java.lang.String descripcioRol;

	@Nullable
	public java.lang.Long id;

	@Nullable
	@Attribute(translated = "userFullName" )
	public java.lang.String nomComplertUsuari;

	@Nullable
	@Attribute(translated = "groupDescription" )
	public java.lang.String descripcioGrup;

	@Nullable
	@Attribute(translated = "domainValue" )
	public es.caib.seycon.ng.comu.ValorDomini valorDomini;

	@Nullable
	@Attribute(translated = "system" )
	public java.lang.String baseDeDades;

	@Nullable
	@Attribute(translated = "userGroupCode" )
	public java.lang.String codiGrupUsuari;

	@Nullable
	@Attribute(translated = "bpmEnforced" )
	public java.lang.String gestionableWF;

	@Nullable
	@Attribute(translated = "userCode" )
	public java.lang.String codiUsuari;

	@Description("Rule that has cretaed the role assignment")
	@Nullable
	public java.lang.Long ruleId;

	@Nullable
	public java.lang.String ruleDescription;

	@Nullable
	public es.caib.seycon.ng.comu.SoDRisk sodRisk;

	@Nullable
	public java.util.Collection<es.caib.seycon.ng.comu.SoDRule> sodRules;

}
