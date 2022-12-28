package NaNa.JobScheduler;

import mHealth.Generic.JobScheduler.*;
import StandardVersion.NuringIndicators.JobScheduler.*;
import NaNa.JobScheduler.Base.*;

public class StartJob extends Executor
{
	public StartJob(String jobName, String jobDesc, String jobId, String cronExpr)
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
		if (JobHelper.CreateJob(jobName, jobDesc, NewGenerateIndicator.class, jobGroup, jobId, cronExpr))
		{
			var sched = Scheduler.GetIntance();
			sched.Start();
		}
	}
}