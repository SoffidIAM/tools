//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package es.caib.seycon.ng.comu;
import com.soffid.mda.annotation.*;

@ValueObject 
@Criteria
public abstract class AccountCriteria {

	@Nullable
	public java.lang.String name;

	@Nullable
	public java.lang.String description;

	@Nullable
	public es.caib.seycon.ng.comu.AccountType type;

	@Nullable
	public java.lang.String grantedGroups;

	@Nullable
	public java.lang.String grantedUsers;

	@Nullable
	public java.lang.String grantedRoles;

	@Nullable
	public java.lang.String dispatcher;

	@Nullable
	@CriteriaColumn(parameter="type", comparator="NOT_LIKE_COMPARATOR")
	public es.caib.seycon.ng.comu.AccountType excludeType;

}
