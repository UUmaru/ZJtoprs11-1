package External.Util;

import java.util.*;
import mHealth.Generic.Utils.*;
import External.Model.Entity.*;

public class ToolsUtil
{

	/** 
	 获取本周的周一日期
	 
	 @return 
	*/
	public static String GetThisWeekMonday()
	{
		java.time.LocalDateTime date = java.time.LocalDateTime.now();
		java.time.LocalDateTime firstDate = java.time.LocalDateTime.now();
		switch (date.getDayOfWeek())
		{
			case java.time.DayOfWeek.MONDAY:
				firstDate = date;
				break;
			case java.time.DayOfWeek.TUESDAY:
				firstDate = date.plusDays(-1);
				break;
			case java.time.DayOfWeek.WEDNESDAY:
				firstDate = date.plusDays(-2);
				break;
			case java.time.DayOfWeek.THURSDAY:
				firstDate = date.plusDays(-3);
				break;
			case java.time.DayOfWeek.FRIDAY:
				firstDate = date.plusDays(-4);
				break;
			case java.time.DayOfWeek.SATURDAY:
				firstDate = date.plusDays(-5);
				break;
			case java.time.DayOfWeek.SUNDAY:
				firstDate = date.plusDays(-6);
				break;
		}
		return firstDate.toString("yyyy-MM-dd");
	}

	/** 
	 根据间隔时间计算年龄
	 
	 @param dateOfBirth 出生日期，TIMESTAMP 时间戳
	 @return 
	*/
	public static String CalAge(long dateOfBirth)
	{
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		TimeSpan interval = (new TimeSpan(now.getTime() - DateUtil.StampToDateTime(String.valueOf(dateOfBirth)).Ticks)).TotalHours;

		String age = "";
		if (interval < (365 * 24))
		{
			java.math.BigDecimal d = Math.floor(interval / (30 * 24));
//C# TO JAVA CONVERTER TODO TASK: The following line could not be converted:
			age = d > 0 ? d + "月" + Math.Ceiling(((interval - (30 * 24) * d)) / 24) + "天" : Math.Ceiling(interval / 24) + "天";
		}
		else
		{
			age = DateUtil.GetAgeOfDateTime(DateUtil.StampToDateTime(String.valueOf(dateOfBirth)), now) + "岁";
		}

		return age;
	}

	/** 
	 从病人列表提取出PVID
	 
	 @param patients 病人列表
	 @return 
	*/
	public static String ExtractPVid(List<PatientInfo> patients)
	{
		return tangible.DotNetToJavaStringHelper.join(",", patients.Select(e -> "'" + e.patientId + e.visitId + "'"));
	}
}