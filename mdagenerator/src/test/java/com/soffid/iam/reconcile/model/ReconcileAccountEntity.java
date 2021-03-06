//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.reconcile.model;
import com.soffid.mda.annotation.*;

@Entity (table="SC_RECACO" )
@Depends ({com.soffid.iam.reconcile.common.ReconcileAccount.class})
public abstract class ReconcileAccountEntity {

	@Column (name="RAC_ID")
	@Nullable
	@Identifier
	public java.lang.Long id;

	@Column (name="RAC_ACNAME")
	public java.lang.String accountName;

	@Column (name="RAC_DESC")
	public java.lang.String description;

	@Column (name="RAC_PROCID")
	public java.lang.Long processId;

	@Column (name="RAC_ACTION")
	public com.soffid.iam.reconcile.common.AccountProposedAction proposedAction;

	@Column (name="RAC_DISPAT")
	public java.lang.String dispatcher;

	@Column (name="RAC_USUCOD")
	@Nullable
	public java.lang.String userCode;

	@Column (name="RAC_GROUP")
	@Nullable
	public java.lang.String primaryGroup;

	@Column (name="RAC_USUTYP")
	@Nullable
	public java.lang.String userType;

	@Column (name="RAC_USUNAM")
	@Nullable
	public java.lang.String userFullName;

	@Column (name="RAC_ACTYPE")
	@Nullable
	public es.caib.seycon.ng.comu.AccountType accountType;

	@Column (name="RAC_ACTIVE",
		defaultValue="true")
	public boolean active;

	@DaoFinder
	public java.util.List<com.soffid.iam.reconcile.model.ReconcileAccountEntity> findByProcessId(
		java.lang.Long processId) {
	 return null;
	}
}
