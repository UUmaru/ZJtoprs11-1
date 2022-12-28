package External.Util;

import java.util.*;
import External.Model.EntityDto.*;
import mHealth.Generic.Utils.*;
import mHealth.Generic.Database.Helper.*;
import External.BaseDal.*;

public class SplitOrdersUtil
{

	/** 
	 医嘱拆分
	 
	 @param freqDict
	 @param advice
	 @param referenceTime
	 @param aId
	 @param remarks
	 @return 
	*/
	public final List<AiTaskDetail> SplitOrders(List<FreqNormalDictDto> freqDict, AddAiAdviceDto advice, java.time.LocalDateTime referenceTime, java.math.BigDecimal aId, String remarks, Map<SysTypes, IDatabase> Database, IBaseDal _dal)
	{
		List<AiTaskDetail> result = new ArrayList<AiTaskDetail>();
		// 重新计算出主任务的开始时间，便于定时拆分医嘱的正确执行
		java.time.LocalDateTime mStartTime = GenerateStartTime(freqDict, referenceTime, advice.relativeDay, advice.relativeTime, tangible.DotNetToJavaStringHelper.isNullOrEmpty(advice.frequency) ? "" : advice.frequency, advice.execTimeType, advice.orderRemark);
		if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(advice.repeatIndicator) && "0".equals(advice.repeatIndicator) && !tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(advice.startDateTime.toString()))
		{
			IDatabase his = Database.get(SysTypes.HIS);
			//医嘱id
			String datasourceNo = his.GetNewID().toString();
			advice.datasourceNo = datasourceNo;
			advice.datasourceSubNo = datasourceNo;
			//临时医嘱
			spliteOrdersByFrequency(result, freqDict, advice, remarks, mStartTime);
		}
		else if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(advice.repeatIndicator) && "1".equals(advice.repeatIndicator) && !tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(advice.startDateTime.toString()))
		{
			//长期医嘱
			SplitLongOrders(result, freqDict, advice, java.time.LocalDateTime.now(), mStartTime, remarks, Database, _dal);
		}
		return result;
	}

	/** 
	 长期医嘱拆分
	 
	 @param freqDict
	 @param advice
	 @param dateTime
	*/
	private void SplitLongOrders(List<AiTaskDetail> aiTaskDetails, List<FreqNormalDictDto> freqDict, AddAiAdviceDto advice, java.time.LocalDateTime dateTime, java.time.LocalDateTime mStartTime, String remarks, Map<SysTypes, IDatabase> Database, IBaseDal _dal)
	{
		// 如果没有停止时间，或者还没到停止时间，就需要拆分
		if (tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(advice.stopDateTime) || DateUtil.StampToDateTime(advice.stopDateTime.toString()) > dateTime)
		{
			// 频次记录
			FreqNormalDictDto freqDto = GetFreqByName(freqDict, advice.frequency);
			if (freqDto == null)
			{
				return;
			}
			//存储到his医嘱原始表中
			if (advice.execPlan != null)
			{
				String[] ep = advice.execPlan.split("[/]", -1);
				IDatabase his = Database.get(SysTypes.HIS);
				//医嘱id
				String datasourceNo = his.GetNewID().toString();
				advice.datasourceNo = datasourceNo;
				advice.datasourceSubNo = datasourceNo;
				//创建新增对象
				AddOrder addmodel = new AddOrder();
				addmodel.PATIENT_ID = advice.patientId;
				addmodel.VISIT_ID = advice.visitId;
				addmodel.ORDER_NO = advice.datasourceNo;
				addmodel.ORDER_SUB_NO = advice.datasourceSubNo;
				addmodel.FREQUENCY = freqDto.frequency;
				addmodel.FREQ_COUNTER = (freqDto.time.split("[;]", -1).getLength() - 1).toString();
				addmodel.FREQ_INTERVAL = "1";
				addmodel.FREQ_INTERVAL_UNIT = freqDto.frequencyCycle > 168 ? "周" : "天";
				addmodel.FREQ_DETAIL = remarks;
				addmodel.PERFORM_SCHEDULE = freqDto.time.Replace(';', '-').substring(0, freqDto.time.Replace(';', '-').getLength() - 1);
				addmodel.ORDER_TEXT = advice.datasourceText;
				addmodel.REPEAT_INDICATOR = "1";
				addmodel.ADMINISTRATION = "";
				addmodel.START_DATE_TIME = DateUtil.StampToDateTime(advice.startDateTime).AddDays(Integer.parseInt(ep[0]));
				addmodel.ORDER_STATUS = "2";
				addmodel.PATIENT_NAME = advice.patientName;
				addmodel.DEPT_CODE = advice.deptCode;
				addmodel.BED_LABEL = advice.bedNo;
				addmodel.BED_NO = advice.bedNo;
				if (tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(advice.stopDateTime))
				{
					addmodel.STOP_DATE_TIME = null;
				}
				else
				{
					addmodel.STOP_DATE_TIME = DateUtil.StampToDateTime(advice.stopDateTime);
				}
				addmodel.DATASOURCE_TYPE = "2";
				//新增his医嘱
				_dal.SaveOrder(addmodel, his);
			}

			// 因为判断周的频次是按照一周的小时数来定的，只要频次间隔大于等于168小时，就认为是周类的频次，但是Q7D 除外，
			// 七天一次，类似于一周一次
			// 这里判断是不是天类频次，或者是不是Q7D，然后判断当天是否需要拆分
			if ((freqDto.frequencyCycle < 168 || "Q7D".equals(freqDto.frequency)) && dayOrderIsNeedSplit(dateTime, mStartTime, freqDto.frequencyCycle))
			{
				spliteOrdersByFrequency(aiTaskDetails, freqDict, advice, remarks, dateTime);
			}
			// 这里判断是不是周类频次
			ArrayList<String> daysOfWeekStr;
			String daysOfWeekColumn = "";
			if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(advice.daysOfWeek))
			{
				daysOfWeekColumn = advice.daysOfWeek;
			}
			else if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(freqDto.daysOfWeek))
			{
				daysOfWeekColumn = freqDto.daysOfWeek;
			}

			if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(daysOfWeekColumn))
			{
				// 取得具体的周次
				daysOfWeekStr = new ArrayList<String>();
				ArrayList<Integer> dayWeek = new ArrayList<Integer>();
				for (String day : daysOfWeekStr)
				{
					dayWeek.add(Integer.parseInt(day));
				}
				if (freqDto.frequencyCycle == 168 && weekOrderIsNeedSplit(dateTime, mStartTime, dayWeek))
				{
					spliteOrdersByFrequency(aiTaskDetails, freqDict, advice, remarks, dateTime);
				}
			}
		}
	}

	/** 
	 判断周次医嘱是否需要拆分
	 
	 @param dateTime
	 @param mStartTime
	 @param dayWeek
	 @return 
	*/
	private boolean weekOrderIsNeedSplit(java.time.LocalDateTime dateTime, java.time.LocalDateTime mStartTime, ArrayList<Integer> dayWeek)
	{
		java.time.LocalDateTime currentTime = (java.time.LocalDateTime)dateTime.toString("yyyy-MM-dd");
		java.time.LocalDateTime startTime = (java.time.LocalDateTime)mStartTime.toString("yyyy-MM-dd");
		if (currentTime.compareTo(startTime) >= 0)
		{
			// 判断当天的日期对应的周次是否存在周频次里
			int weekNow = GetDayRankOfWeek(currentTime);
			for (int dayOfWeek : dayWeek)
			{
				if (String.valueOf(dayOfWeek).contains(String.valueOf(weekNow)))
				{
					return true;
				}
			}
		}
		return false;
	}

	/** 
	 
	 
	 @param dateTime
	 @return 
	*/
	private int GetDayRankOfWeek(java.time.LocalDateTime dateTime)
	{
		GregorianCalendar gc = new GregorianCalendar();
		int week = gc.GetWeekOfYear(dateTime, CalendarWeekRule.FirstDay, java.time.DayOfWeek.SUNDAY);
		week -= 1;
		if (week > 0)
		{
			return week;
		}
		else
		{
			return 7;
		}
	}

	/** 
	 判断医嘱是否需要拆分
	 
	 @param dateTime
	 @param mStartTime
	 @param nullable
	 @return 
	*/
	private boolean dayOrderIsNeedSplit(java.time.LocalDateTime dateTime, java.time.LocalDateTime mStartTime, java.math.BigDecimal frequencyCycle)
	{
		java.time.LocalDateTime currentTime = (java.time.LocalDateTime)dateTime.toString("yyyy-MM-dd");
		java.time.LocalDateTime startTime = (java.time.LocalDateTime)mStartTime.toString("yyyy-MM-dd");
		if (currentTime.compareTo(startTime) >= 0)
		{
			//判断开始时间与当天时间的间隔小时数
			int timeSpan = (int)((DateUtil.DateTimeToStamp(currentTime) - DateUtil.DateTimeToStamp(currentTime)) / (1000 * 3600));
			// 是否为整数间隔
			return timeSpan % frequencyCycle == 0;
		}
		return false;
	}

	/** 
	 根据频次拆分医嘱
	 
	 @param freqDict
	 @param advice
	*/
	private void spliteOrdersByFrequency(List<AiTaskDetail> aiTaskDetails, List<FreqNormalDictDto> freqDict, AddAiAdviceDto advice, String remarks, java.time.LocalDateTime mStartTime)
	{
		if ("0".equals(advice.repeatIndicator))
		{
			// 临时医嘱，直接拆分成子任务，主任务开始时间就是子任务执行时间
			AiTaskDetail detail = GetAiTaskDetail(advice, remarks, mStartTime);
			detail.execTime = DateUtil.DateTimeToStamp((java.time.LocalDateTime)(mStartTime.toString("yyyy-MM-dd HH:mm") + ":59")).toString();
			aiTaskDetails.add(detail);
		}
		else if ("1".equals(advice.repeatIndicator))
		{
			// 查询频次记录
			FreqNormalDictDto freqDto = GetFreqByName(freqDict, advice.frequency);
			// 时间点 ';'隔开
			ArrayList<String> timePoints = new ArrayList<String>();
			for (String timePoint : timePoints)
			{
				String tempTp = timePoint;
				//长度为2，则只包含时从，长度为5，则包含时分，长度为7，则包含时分秒
				if (timePoint.length() == 1)
				{
					tempTp = "0" + timePoint + ":00:59";
				}
				else if (timePoint.length() == 2)
				{
					tempTp = timePoint + ":00:59";
				}
				else if (timePoint.length() == 5)
				{
					tempTp = timePoint + ":59";
				}
				java.time.LocalDateTime exeTime = (java.time.LocalDateTime)(((java.time.LocalDateTime)(mStartTime)).toString("yyyy-MM-dd") + " " + tempTp);
				AiTaskDetail detail = GetAiTaskDetail(advice, remarks, mStartTime);
				// 新任务的拆分时间
				detail.createTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.now()).toString();
				// 新任务的执行时间
				detail.execTime = DateUtil.DateTimeToStamp(exeTime).toString();
				aiTaskDetails.add(detail);
			}
		}
	}

	/** 
	 查询频次记录
	 
	 @param freqDict
	 @param freqName
	 @return 
	*/
	private FreqNormalDictDto GetFreqByName(List<FreqNormalDictDto> freqDict, String freqName)
	{
		List<FreqNormalDictDto> freqList = freqDict.Where(x -> x.frequency.equals(freqName)).ToList();
		return freqList.size() > 0 ? freqList.get(0) : null;
	}

	/** 
	 组装任务明细数据
	 
	 @param advice
	 @return 
	*/
	private AiTaskDetail GetAiTaskDetail(AddAiAdviceDto advice, String remarks, java.time.LocalDateTime mStartTime)
	{
		AiTaskDetail detail = new AiTaskDetail();
		detail.recId = 0;
		detail.hospitalCode = advice.hospitalCode;
		detail.wardCode = advice.wardCode;
		detail.deptCode = advice.deptCode;
		detail.patientId = advice.patientId;
		detail.visitId = advice.visitId;
		detail.inpNo = advice.inpNo;
		detail.bedNo = advice.bedNo;
		detail.createTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.now()).toString();
		detail.creater = advice.updater;
		detail.doctor = advice.doctor;
		detail.datasourceNo = advice.datasourceNo;
		detail.datasourceType = advice.datasourceType;
		detail.repeatIndecator = advice.repeatIndicator;
		detail.itemCode = advice.itemCode;
		detail.startTime = DateUtil.DateTimeToStamp(mStartTime).toString();
		detail.endTime = advice.stopDateTime;
		//任务状态：0：待做，1：已做,2：作废,3:重复医嘱作废,4:手动添加任务停止，5:化疗任务附属药嘱,11:出院患者停止,12:PC端回滚停止任务
		detail.status = "0";
		//清洗数据类型，0：无，1：床号，2：日期，3：化疗药嘱
		if ("1".equals(advice.orderRemarkType))
		{
			detail.remark = advice.orderRemark + " " + remarks;
		}
		else
		{
			detail.remark = remarks;
		}
		detail.orderRemark = advice.orderRemark;
		detail.timeRemark = "";
		detail.datasourceText = advice.originalOrder;
		detail.datasourceCreateTime = advice.startDateTime;
		detail.orderRemarkType = advice.orderRemarkType;
		detail.datasourceSubNo = advice.datasourceSubNo;
		detail.orderId = advice.orderId;
		detail.frequency = tangible.DotNetToJavaStringHelper.isNullOrEmpty(advice.frequency) ? "" : advice.frequency.toUpperCase();
		detail.orderClass = advice.orderClass;
		detail.eventContent = advice.eventContent;
		detail.order = null;
		return detail;
	}

	/** 
	 这里需要计算重新计算出主任务的开始时间，便于定时拆分医嘱的正确执行
	 
	 @param freqDict
	 @param referenceTime
	 @param relativeDay 偏移的天数
	 @param relativeTime
	 @param freq
	 @param execTimeType
	 @param orderRemark
	 @return 
	*/
	private java.time.LocalDateTime GenerateStartTime(List<FreqNormalDictDto> freqDict, java.time.LocalDateTime referenceTime, String relativeDay, String relativeTime, String freq, String execTimeType, String orderRemark)
	{
		FreqNormalDictDto freqDto = GetFreqByName(freqDict, freq);
		if (freqDto == null)
		{
			throw new RuntimeException("频次不存在");
		}
		relativeTime = tangible.DotNetToJavaStringHelper.isNullOrEmpty(relativeTime) ? freqDto.time : relativeTime;
		if ("0".equals(execTimeType))
		{
			//时间规则是天偏移
			String sDate = referenceTime.plusDays(Integer.parseInt(relativeDay)).toString("yyyy-MM-dd HH:mm:ss");
			String startTimeStr = sDate;
			if (relativeTime.length() == 1)
			{
				startTimeStr += " " + "0" + relativeTime.substring(0, 1) + referenceTime.toString("mm:ss");
			}
			else if (relativeTime.length() == 2)
			{
				startTimeStr += " " + relativeTime.substring(0, 2) + referenceTime.toString("mm:ss");
			}
			return java.time.LocalDateTime.parse(((java.time.LocalDateTime)(startTimeStr)).toString("yyyy-MM-dd HH:mm:ss"));
		}
		else if ("1".equals(execTimeType))
		{
			//时间规则是周偏移，开始时间只取第一个周次
			java.time.LocalDateTime startDate = java.time.LocalDateTime.MIN;
			if (relativeDay.contains("+"))
			{
				//下周
				startDate = GetStartTime(referenceTime.plusDays(7), Integer.parseInt(relativeDay.substring(1, 3)) - 1);
			}
			else
			{
				//本周
				startDate = GetStartTime(referenceTime, Integer.parseInt(relativeDay.substring(0, 1)) - 1);
			}
			return startDate;
		}
		else if ("2".equals(execTimeType))
		{
			//时间规则是特殊类型，特殊时间的日期在抽取数据的时候被清洗到备注里，这里直接去备注里的日期转化
			if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(orderRemark))
			{
				return java.time.LocalDateTime.parse(((java.time.LocalDateTime)(orderRemark)).toString("yyyy-MM-dd"));
			}
			else
			{
				return referenceTime;
			}
		}
		else
		{
			return referenceTime;
		}
	}

	/** 
	 计算开始时间
	 
	 @param mStartTime
	 @param days
	 @return 
	*/
	public final java.time.LocalDateTime GetStartTime(java.time.LocalDateTime mStartTime, int days)
	{
		//根据日期计算出本周第一天
		int weekNow = mStartTime.getDayOfWeek().getValue();
		//因为是以星期一为第一天，所以要判断weeknow等于0时，要向前推6天。  
		weekNow = (weekNow == 0 ? (7 - 1) : (weekNow - 1));
		int daydiff = (-1) * weekNow;
		//本周第一天  
		String FirstDay = mStartTime.plusDays(daydiff).toString("yyyy-MM-dd");
		return ((java.time.LocalDateTime)(FirstDay)).AddDays(days);
	}
}