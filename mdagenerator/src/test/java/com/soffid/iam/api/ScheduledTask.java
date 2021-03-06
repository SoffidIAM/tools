//
// (C) 2013 Soffid
// 
// This file is licensed by Soffid under GPL v3 license
//

package com.soffid.iam.api;
import com.soffid.mda.annotation.*;

@ValueObject 
public abstract class ScheduledTask {

	@Nullable
	public java.lang.Long id;

	public java.lang.String name;

	@Nullable
	public java.lang.String params;

	public java.lang.String handlerName;

	@Nullable
	public java.util.Calendar nextExecution;

	@Nullable
	public java.util.Calendar lastExecution;

	@Nullable
	public java.lang.StringBuffer lastLog;

	public java.lang.String dayPattern;

	public java.lang.String hoursPattern;

	public java.lang.String monthsPattern;

	public java.lang.String dayOfWeekPattern;

	public java.lang.String minutesPattern;

	public boolean error;

	public boolean active;

	@Nullable
	public java.lang.String serverName;

}
