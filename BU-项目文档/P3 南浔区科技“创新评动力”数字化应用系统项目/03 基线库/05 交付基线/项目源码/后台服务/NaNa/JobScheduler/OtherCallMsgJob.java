package NaNa.JobScheduler;

import mHealth.Generic.JobScheduler.*;
import NaNa.JobScheduler.Base.*;
import StandardVersion.NuringIndicators.JobScheduler.*;

public class OtherCallMsgJob extends Executor
{
	public OtherCallMsgJob(String jobName, String jobDesc, String jobId, String cronExpr)
	{
		super(jobName, jobDesc, jobId, cronExpr);
	}


	@Override
	public void Start()
	{
		if (!CheckCronExpr())
		{
			return;
		}
		if (JobHelper.CreateJob(jobName, jobDesc, NewOtherCallMsgIndicator.class, jobGroup, jobId, cronExpr))
		{
			var sched = Scheduler.GetIntance();
			sched.Start();
		}

	}
}