package External.Util;

import java.util.*;
import mHealth.Generic.Utils.*;
import External.BaseDal.*;
import mHealth.Generic.Database.Helper.*;
import External.Model.Entity.Ext.*;
import External.Model.Entity.*;
import Aspose.Pdf.Facades.*;
import mHealth.Generic.Database.Param.*;
import External.Constants.*;

public class HlwsSplitOrder
{

	private IBaseDal dal;
	private Map<SysTypes, IDatabase> db;

	/** 
	 HIS医嘱拆分保存到文书医嘱执行表
	 
	 @param startTime 查询开始时间
	 @param endTime 查询结束时间
	 @param orderType 医嘱类型
	 @param deptCodes 科室code列表，已经转成字符串拼接好
	 @param dal
	 @param database
	 @return 
	*/
	public final boolean HlwsSplitOrders(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime, String orderType, String deptCodes, IBaseDal dal, Map<SysTypes, IDatabase> database)
	{
		this.dal = dal;
		db = database;
		IDatabase sys = db.get(SysTypes.SYS);

		LogUtil.Info("进入医嘱拆分");
		DataTable hisTodayOrders = dal.GetHisOrders(db.get(SysTypes.HIS), deptCodes);
		List<DocsOrdersExecView> hlwsTodaySplitedOrders = DatatableToModelList<DocsOrdersExecView>.ConvertToModel(this.dal.GetOrders(sys, startTime.toString()));

		// 需要拆分的医嘱
		DataTable shouldBeSplitedHisOrders = GetHisShouldBeSplitedOrders(hisTodayOrders, hlwsTodaySplitedOrders);
		ArrayList<DOCS_ORDERS_EXEC_LOG_EXT> splitedHisOrders = SplitLongTempOrders(shouldBeSplitedHisOrders, startTime.toString(), endTime.toString());
		splitedHisOrders = splitedHisOrders.Where(c -> !(c.EXEC_TIME > c.STOP_DATE_TIME && c.REPEAT_INDICATOR == 1 && (tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(c.ADMINISTRATION) || "化疗".equals(c.ADMINISTRATION)))).ToList();
		// 删除的医嘱
		List<DocsOrdersExecView> shouldBeDeleteHlwsOrders = GetHlwsShouldBeDeleteOrders(hisTodayOrders, hlwsTodaySplitedOrders);
		// 删除医嘱过滤
		shouldBeDeleteHlwsOrders = shouldBeDeleteHlwsOrders.Where(x -> tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(x.nurse) && tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(x.print_status)).ToList();

		int bindCount = splitedHisOrders.size()();
		if (bindCount > 0)
		{
			String sql = "INSERT INTO DOCS_ORDERS_EXEC_LOG (" + "\r\n" +
"                               REC_ID," + "\r\n" +
"                               PATIENT_ID," + "\r\n" +
"                               VISIT_ID," + "\r\n" +
"                               PATIENT_NAME," + "\r\n" +
"                               ORDER_NO," + "\r\n" +
"                               ORDER_SUB_NO," + "\r\n" +
"                               EXEC_DATE," + "\r\n" +
"                               EXEC_TIME," + "\r\n" +
"                               ADMINISTRATION," + "\r\n" +
"                               AGE," + "\r\n" +
"                               SEX," + "\r\n" +
"                               BED_LABEL," + "\r\n" +
"                               BED_NO," + "\r\n" +
"                               DOSAGE_UNIT," + "\r\n" +
"                               ORDER_TEXT," + "\r\n" +
"                               STATUS," + "\r\n" +
"                               REPEAT_INDICATOR," + "\r\n" +
"                               START_DATE_TIME," + "\r\n" +
"                               STOP_DATE_TIME," + "\r\n" +
"                               PERFORM_SCHEDULE," + "\r\n" +
"                               FREQUENCY," + "\r\n" +
"                               FREQ_COUNTER," + "\r\n" +
"                               FREQ_INTERVAL," + "\r\n" +
"                               FREQ_INTERVAL_UNIT," + "\r\n" +
"                               FREQ_DETAIL," + "\r\n" +
"                               DEPT_CODE," + "\r\n" +
"                               DOCTOR_NAME," + "\r\n" +
"                               BOTTLES," + "\r\n" +
"                               DATASOURCE_TYPE," + "\r\n" +
"                               ORIGINALORDER)" + "\r\n" +
"                            VALUES" + "\r\n" +
"                              (getnewid()," + "\r\n" +
"                               :patientId," + "\r\n" +
"                               :visitId," + "\r\n" +
"                               :patientName," + "\r\n" +
"                               :orderNo," + "\r\n" +
"                               :orderSubNo," + "\r\n" +
"                               :execDate," + "\r\n" +
"                               :execTime," + "\r\n" +
"                               :administration," + "\r\n" +
"                               :age," + "\r\n" +
"                               :sex," + "\r\n" +
"                               :bedLabel," + "\r\n" +
"                               :bedNo," + "\r\n" +
"                               :dosageUnit," + "\r\n" +
"                               :orderText," + "\r\n" +
"                               :status," + "\r\n" +
"                               :repeatIndicator," + "\r\n" +
"                               :startDateTime," + "\r\n" +
"                               :stopDateTime," + "\r\n" +
"                               :performSchedule," + "\r\n" +
"                               :frequency," + "\r\n" +
"                               :freqCounter," + "\r\n" +
"                               :freqInterval," + "\r\n" +
"                               :freqIntervalUnit," + "\r\n" +
"                               :freqDetail," + "\r\n" +
"                               :deptCodes," + "\r\n" +
"                               :doctorName," + "\r\n" +
"                               :bottles," + "\r\n" +
"                               :datasourceType," + "\r\n" +
"                               :originalorder )";

			// 变量声明
			String[] patientId = new String[bindCount];
			String[] visitId = new String[bindCount];
			String[] patientName = new String[bindCount];
			String[] orderNo = new String[bindCount];
			String[] orderSubNo = new String[bindCount];
			java.time.LocalDateTime[] execDate = new java.time.LocalDateTime[bindCount];
			java.time.LocalDateTime[] execTime = new java.time.LocalDateTime[bindCount];
			String[] administration = new String[bindCount];
			String[] age = new String[bindCount];
			String[] sex = new String[bindCount];
			String[] bedLabel = new String[bindCount];
			String[] bedNo = new String[bindCount];
			String[] dosageUnit = new String[bindCount];
			String[] orderText = new String[bindCount];
			String[] status = new String[bindCount];
			String[] repeatIndicator = new String[bindCount];
			java.time.LocalDateTime[] startDateTime = new java.time.LocalDateTime[bindCount];
			java.time.LocalDateTime[] stopDateTime = new java.time.LocalDateTime[bindCount];
			String[] performSchedule = new String[bindCount];
			String[] frequency = new String[bindCount];
			String[] freqCounter = new String[bindCount];
			String[] freqInterval = new String[bindCount];
			String[] freqIntervalUnit = new String[bindCount];
			String[] freqDetail = new String[bindCount];
			String[] deptCodeList = new String[bindCount];
			String[] doctorName = new String[bindCount];
			String[] bottles = new String[bindCount];
			String[] datasourceType = new String[bindCount];
			String[] originalorder = new String[bindCount];

			// 变量赋值
			for (int count = 0; count < bindCount; count++)
			{
				DOCS_ORDERS_EXEC_LOG_EXT detail = splitedHisOrders.get(count);
				patientId[count] = detail.PATIENT_ID;
				visitId[count] = detail.VISIT_ID;
				patientName[count] = detail.PATIENT_NAME;
				orderNo[count] = detail.ORDER_NO;
				orderSubNo[count] = detail.ORDER_SUB_NO;
				execDate[count] = detail.EXEC_DATE;
				execTime[count] = detail.EXEC_TIME;
				administration[count] = detail.ADMINISTRATION;
				age[count] = detail.AGE;
				sex[count] = detail.SEX;
				bedLabel[count] = detail.BED_LABEL;
				bedNo[count] = detail.BED_NO;
				dosageUnit[count] = detail.DOSAGE_UNIT;
				orderText[count] = detail.ORDER_TEXT;
				status[count] = detail.STATUS.toString();
				repeatIndicator[count] = detail.REPEAT_INDICATOR.toString();
				startDateTime[count] = detail.START_DATE_TIME;
				stopDateTime[count] = detail.STOP_DATE_TIME;
				performSchedule[count] = detail.PERFORM_SCHEDULE;
				frequency[count] = detail.FREQUENCY;
				freqCounter[count] = detail.FREQ_COUNTER;
				freqInterval[count] = detail.FREQ_INTERVAL;
				freqIntervalUnit[count] = detail.FREQ_INTERVAL_UNIT;
				freqDetail[count] = detail.FREQ_DETAIL;
				deptCodeList[count] = detail.DEPT_CODE;
				doctorName[count] = detail.DOCTOR_NAME;
				bottles[count] = detail.BOTTLES;
				datasourceType[count] = detail.DATASOURCE_TYPE;
				originalorder[count] = detail.ORIGINALORDER;
			}
			List<CisDbParameter> parameters = new ArrayList<CisDbParameter>();
			parameters.add(new CisDbParameter("patientId", CisDbType.VarChar,12, patientId));
			parameters.add(new CisDbParameter("visitId", CisDbType.VarChar,6, visitId));
			parameters.add(new CisDbParameter("patientName", CisDbType.VarChar,50, patientName));
			parameters.add(new CisDbParameter("orderNo", CisDbType.VarChar,20, orderNo));
			parameters.add(new CisDbParameter("orderSubNo", CisDbType.VarChar,20, orderSubNo));
			parameters.add(new CisDbParameter("execDate", CisDbType.DateTime, execDate));
			parameters.add(new CisDbParameter("execTime", CisDbType.DateTime, execTime));
			parameters.add(new CisDbParameter("administration", CisDbType.VarChar,50, administration));
			parameters.add(new CisDbParameter("age", CisDbType.VarChar,10, age));
			parameters.add(new CisDbParameter("sex", CisDbType.VarChar,10, sex));
			parameters.add(new CisDbParameter("bedLabel", CisDbType.VarChar,20, bedLabel));
			parameters.add(new CisDbParameter("bedNo", CisDbType.VarChar,10, bedNo));
			parameters.add(new CisDbParameter("dosageUnit", CisDbType.VarChar,20, dosageUnit));
			parameters.add(new CisDbParameter("orderText", CisDbType.VarChar,1000, orderText));
			parameters.add(new CisDbParameter("status", CisDbType.VarChar, status));
			parameters.add(new CisDbParameter("repeatIndicator", CisDbType.VarChar,10, repeatIndicator));
			parameters.add(new CisDbParameter("startDateTime", CisDbType.DateTime, startDateTime));
			parameters.add(new CisDbParameter("stopDateTime", CisDbType.DateTime, stopDateTime));
			parameters.add(new CisDbParameter("performSchedule", CisDbType.VarChar,200, performSchedule));
			parameters.add(new CisDbParameter("frequency", CisDbType.VarChar,20, frequency));
			parameters.add(new CisDbParameter("freqCounter", CisDbType.VarChar,50, freqCounter));
			parameters.add(new CisDbParameter("freqInterval", CisDbType.VarChar,10, freqInterval));
			parameters.add(new CisDbParameter("freqIntervalUnit", CisDbType.VarChar,10, freqIntervalUnit));
			parameters.add(new CisDbParameter("freqDetail", CisDbType.VarChar,200, freqDetail));
			parameters.add(new CisDbParameter("deptCodes", CisDbType.VarChar,50, deptCodeList));
			parameters.add(new CisDbParameter("doctorName", CisDbType.VarChar,20, doctorName));
			parameters.add(new CisDbParameter("bottles", CisDbType.VarChar,3, bottles));
			parameters.add(new CisDbParameter("datasourceType", CisDbType.VarChar,2, datasourceType));
			parameters.add(new CisDbParameter("originalorder", CisDbType.VarChar,1000, originalorder));
			int add = sys.ExecuteArray(sql, parameters, bindCount);
		}

		// 存储拆分后的医嘱批量删除数据
		int deleteCount = shouldBeDeleteHlwsOrders.size()();
		if (deleteCount > 0)
		{
			String sql = "" + "\r\n" +
"                    DELETE" + "\r\n" +
"                    FROM DOCS_ORDERS_EXEC_LOG" + "\r\n" +
"                    WHERE PATIENT_ID = :patientId" + "\r\n" +
"                      AND VISIT_ID = :visitId" + "\r\n" +
"                      AND ORDER_NO = :orderNo" + "\r\n" +
"                      AND ORDER_SUB_NO = :orderSubNo" + "\r\n" +
"                      AND EXEC_TIME = :execTime " + "\r\n" +
"                ";

			String[] patientId = new String[deleteCount];
			String[] visitId = new String[deleteCount];
			String[] orderNo = new String[deleteCount];
			String[] orderSubNo = new String[deleteCount];
			java.time.LocalDateTime[] execTime = new java.time.LocalDateTime[deleteCount];
			for (int count = 0; count < deleteCount; count++)
			{
				DocsOrdersExecView detail = shouldBeDeleteHlwsOrders.get(count);
				patientId[count] = detail.patient_id;
				visitId[count] = detail.visit_id;
				orderNo[count] = detail.order_no;
				orderSubNo[count] = detail.order_sub_no;
				execTime[count] = detail.exec_time;
			}
			List<CisDbParameter> parameters = new ArrayList<CisDbParameter>();
			parameters.add(new CisDbParameter("patientId", CisDbType.VarChar, patientId));
			parameters.add(new CisDbParameter("visitId", CisDbType.VarChar, visitId));
			parameters.add(new CisDbParameter("orderNo", CisDbType.VarChar, orderNo));
			parameters.add(new CisDbParameter("orderSubNo", CisDbType.VarChar, orderSubNo));
			parameters.add(new CisDbParameter("execTime", CisDbType.DateTime, execTime));
			int delete = sys.ExecuteArray(sql, parameters, deleteCount);
		}
		return true;
	}

	/** 
	 比对HIS和文书医嘱
	 1. 获取文书比HIS多的部分
	 2. 获取文书和HIS都有，且HIS方已经【停止】和【作废】的医嘱
	 
	 @param hisOrders HIS医嘱
	 @param hlwsOrders 护理文书医嘱
	 @return 
	*/
	private List<DocsOrdersExecView> GetHlwsShouldBeDeleteOrders(DataTable hisOrders, List<DocsOrdersExecView> hlwsOrders)
	{
		ArrayList<DocsOrdersExecView> shouldBeDeleteOrders = new ArrayList<DocsOrdersExecView>();
		// 处理HIS【停止】或者【作废】的医嘱
		List<DocsOrdersExecView> invalidHlwsOrders = hisOrders.AsEnumerable().Where(row ->
		{
				boolean isInvalid = OrderConstants.Stoped.equals(row["ORDER_STATUS"].toString()) || OrderConstants.Invalid.equals(row["ORDER_STATUS"].toString());
				boolean isLongTermOrTempOrder = OrderConstants.LongTermOrder.equals(row["REPEAT_INDICATOR"].toString()) || OrderConstants.TemporaryOrder.equals(row["REPEAT_INDICATOR"].toString());
				return isInvalid && isLongTermOrTempOrder;
		}).Join(hlwsOrders, hisOrder -> new {pid = hisOrder["PATIENT_ID"].toString(), vid = hisOrder["VISIT_ID"].toString(), orderNo = hisOrder["ORDER_NO"].toString(), orderSubNo = hisOrder["ORDER_SUB_NO"].toString()}, hlwsOrder -> new {pid = hlwsOrder.patient_id, vid = hlwsOrder.visit_id, orderNo = hlwsOrder.order_no, orderSubNo = hlwsOrder.order_sub_no}, (hisOrder, hlwsOrder) -> new {IsLongTermOrTemp = hisOrder["REPEAT_INDICATOR"].toString(), StopTime = hisOrder["STOP_DATE_TIME"].toString(), hlwsOrder}).Where(item ->
		{
				// 临时医嘱，删除全部的数据
				if (OrderConstants.TemporaryOrder.equals(item.IsLongTermOrTemp.toString()))
				{
					return true;
				}

				// 长期医嘱，删除超出停止时间的频次
				java.time.LocalDateTime stopTime = java.time.LocalDateTime.MIN;
				tangible.RefObject<java.time.LocalDateTime> tempRef_stopTime = new tangible.RefObject<java.time.LocalDateTime>(stopTime);
				boolean isDateTime = java.time.LocalDateTime.TryParse(item.StopTime, tempRef_stopTime);
				stopTime = tempRef_stopTime.argValue;
				if (isDateTime && item.hlwsOrder.exec_time != null && java.time.LocalDateTime.Compare((java.time.LocalDateTime) item.hlwsOrder.exec_time, stopTime) > 0)
				{
					return true;
				}

				return false;
			}).Select(item -> item.hlwsOrder).ToList();

		// 文书比HIS多的部分
		List <DocsOrdersExecView> rebundantOrders = (from u in hlwsOrders join row in hisOrders.AsEnumerable() on new {pid = u.patient_id, vid = u.visit_id, orderNo = u.order_no, orderSubNo = u.order_sub_no} equals new {pid = row["PATIENT_ID"].toString(), vid = row["VISIT_ID"].toString(), orderNo = row["ORDER_NO"].toString(), orderSubNo = row["ORDER_SUB_NO"].toString()} into x from cx in x.DefaultIfEmpty() where cx == null select u).ToList();

		shouldBeDeleteOrders.addAll(invalidHlwsOrders);
		shouldBeDeleteOrders.addAll(rebundantOrders);
		return shouldBeDeleteOrders;
	}

	/** 
	 比对HIS和文书医嘱，获取HIS应该被拆分的医嘱
	 
	 @param hisOrders HIS医嘱
	 @param hlwsOrders 护理文书医嘱
	 @return 需要被拆分的医嘱
	*/
	private DataTable GetHisShouldBeSplitedOrders(DataTable hisOrders, List<DocsOrdersExecView> hlwsOrders)
	{
		List<DataRow> shouldBeSplitedOrders = (from row in hisOrders.AsEnumerable().Where(CanBeCompared) join u in hlwsOrders on new {pid = row["PATIENT_ID"].toString(), vid = row["VISIT_ID"].toString(), orderNo = row["ORDER_NO"].toString(), orderSubNo = row["ORDER_SUB_NO"].toString()} equals new {pid = u.patient_id, vid = u.visit_id, orderNo = u.order_no, orderSubNo = u.order_sub_no} into x from cx in x.DefaultIfEmpty() where cx == null select row).ToList();

		return shouldBeSplitedOrders.isEmpty() ? new DataTable() : ToDataTable(shouldBeSplitedOrders);
	}

	/** 
	 判断一条HIS医嘱是否可以和文书进行比较
	 以下条件符合均会被过滤出来
	 1. 【新开】状态医嘱
	 2. 【执行】状态医嘱且长期医嘱且开嘱时间是今天以前
	 
	 @param row
	 @return 
	*/
	public final boolean CanBeCompared(DataRow row)
	{
		java.time.LocalDateTime startDateTime = java.time.LocalDateTime.MIN;
		// 开嘱时间是否是今天以前
		boolean isBeforeToday = false;
		tangible.RefObject<java.time.LocalDateTime> tempRef_startDateTime = new tangible.RefObject<java.time.LocalDateTime>(startDateTime);
		boolean tempVar = java.time.LocalDateTime.TryParse(row["START_DATE_TIME"].toString(), tempRef_startDateTime);
			startDateTime = tempRef_startDateTime.argValue;
		if (tempVar)
		{
			if (java.time.LocalDateTime.Compare(java.time.LocalDateTime.now().Date, startDateTime) > 0)
			{
				isBeforeToday = true;
			}
		}

		boolean isNewOrder = OrderConstants.NewOrder.equals(row["ORDER_STATUS"].toString());
		boolean isExecutedOrder = OrderConstants.LongTermOrder.equals(row["REPEAT_INDICATOR"].toString()) && OrderConstants.Executed.equals(row["ORDER_STATUS"].toString()) && isBeforeToday;
		return isNewOrder || isExecutedOrder;
	}

	/** 
	 将多个DataRow转成DataTable
	 
	 @param rows
	 @return 
	*/
	private DataTable ToDataTable(List<DataRow> rows)
	{
		if (rows == null || rows.isEmpty())
		{
			return null;
		}
		DataTable tmp = rows.get(0).Table.Clone();
		for (DataRow row : rows)
		{
			tmp.ImportRow(row);
		}
		return tmp;
	}

	/** 
	 拆分HIS过来的医嘱
	 
	 @param hisOrders HIS医嘱
	 @param startTime 开始时间
	 @param endTime 结束时间
	 @return 
	*/
	private ArrayList<DOCS_ORDERS_EXEC_LOG_EXT> SplitLongTempOrders(DataTable hisOrders, String startTime, String endTime)
	{
		ArrayList<DOCS_ORDERS_EXEC_LOG_EXT> list = new ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>();
		for (DataRow row : hisOrders.Rows)
		{
			java.time.LocalDateTime stoptime = null;
			if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(row["STOP_DATE_TIME"].toString()))
			{
				stoptime = (java.time.LocalDateTime)(row["STOP_DATE_TIME"].toString());
			}

			java.time.LocalDateTime dateOfBirth = java.time.LocalDateTime.now();
			if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(row["DATE_OF_BIRTH"].toString()))
			{
				dateOfBirth = (java.time.LocalDateTime)(row["DATE_OF_BIRTH"].toString());
			}
			DOCS_ORDERS_EXEC_LOG_EXT ordersModel = new DOCS_ORDERS_EXEC_LOG_EXT();
			ordersModel.PATIENT_ID = row["PATIENT_ID"].toString();
			ordersModel.VISIT_ID = row["VISIT_ID"].toString();
			ordersModel.ORDER_NO = row["ORDER_NO"].toString();
			ordersModel.ORDER_SUB_NO = row["ORDER_SUB_NO"].toString();
			ordersModel.FREQUENCY = row["FREQUENCY"].toString();
			ordersModel.FREQ_COUNTER = row["FREQ_COUNTER"].toString();
			ordersModel.FREQ_INTERVAL = row["FREQ_INTERVAL"].toString();
			ordersModel.FREQ_INTERVAL_UNIT = row["FREQ_INTERVAL_UNIT"].toString();
			ordersModel.FREQ_DETAIL = row["FREQ_DETAIL"] == DBNull.Value ? "" : row["FREQ_DETAIL"].toString();
			ordersModel.PERFORM_SCHEDULE = row["PERFORM_SCHEDULE"] == DBNull.Value ? "" : row["PERFORM_SCHEDULE"].toString();
			ordersModel.DOSE = row["DOSE"].toString();
			ordersModel.DOSAGE_UNIT = row["DOSAGE_UNIT"] == DBNull.Value ? "" : row["DOSAGE_UNIT"].toString();
			ordersModel.ORDER_TEXT = row["ORDER_TEXT"] == DBNull.Value ? "" : row["ORDER_TEXT"].toString();
			ordersModel.REPEAT_INDICATOR = Integer.parseInt(row["REPEAT_INDICATOR"].toString());
			ordersModel.ADMINISTRATION = row["ADMINISTRATION"] == DBNull.Value ? "" : row["ADMINISTRATION"].toString();
			ordersModel.START_DATE_TIME = (java.time.LocalDateTime)(row["START_DATE_TIME"].toString());
			ordersModel.STATUS = Integer.parseInt(row["ORDER_STATUS"].toString());
			ordersModel.PATIENT_NAME = row["PATIENT_NAME"] == DBNull.Value ? "" : row["PATIENT_NAME"].toString();
			ordersModel.SEX = row["SEX"] == DBNull.Value ? "" : row["SEX"].toString();
			ordersModel.DEPT_CODE = row["DEPT_CODE"] == DBNull.Value ? "" : row["DEPT_CODE"].toString();
			ordersModel.BED_LABEL = row["BED_LABEL"] == DBNull.Value ? "" : row["BED_LABEL"].toString();
			ordersModel.BED_NO = row["BED_NO"] == DBNull.Value ? "" : row["BED_NO"].toString();
			ordersModel.exec_start_time = (java.time.LocalDateTime)(row["exec_start_time"].toString());
			ordersModel.exec_end_time = tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(row["exec_end_time"].toString()) ? (java.time.LocalDateTime)endTime : (java.time.LocalDateTime)(row["exec_end_time"].toString());
			ordersModel.DOCTOR_NAME = row["DOCTOR_NAME"].toString();
			ordersModel.STOP_DATE_TIME = stoptime;
			ordersModel.DATASOURCE_TYPE = tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(row["DATASOURCE_TYPE"].toString()) ? "1" : row["DATASOURCE_TYPE"].toString();
			ordersModel.AGE = DateUtil.GetAgeOfDateTime(dateOfBirth, java.time.LocalDateTime.now()) + "岁";
			ordersModel.ORIGINALORDER = row["ORDER_TEXT"] == DBNull.Value ? "" : row["ORDER_TEXT"].toString();

			tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>> tempRef_list = new tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>>(list);
			SplitOneOrders(ordersModel, startTime, endTime, ordersModel.DEPT_CODE, tempRef_list);
			list = tempRef_list.argValue;
		}
		return list;
	}


	/** 
	 拆分单条医嘱
	 
	 @param ordersModel 
	 @param starttime
	 @param endtime
	 @param wardCode 
	*/
	private void SplitOneOrders(DOCS_ORDERS_EXEC_LOG_EXT ordersModel, String starttime, String endtime, String wardCode, tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>> list)
	{
		String str = ordersModel.ORDER_TEXT;
		if ((str.contains("转") || str.contains("换")) && str.contains("床"))
		{
			//备注组装
			ordersModel.FREQ_DETAIL = ordersModel.ORDER_TEXT;
			//规则处理
			ordersModel.ORIGINALORDER = "转床";
		}
		if ((str.contains("转") || str.contains("换")) && (str.contains("科") || str.contains("区")))
		{
			//备注组装
			ordersModel.FREQ_DETAIL = ordersModel.ORDER_TEXT;
			//规则处理
			ordersModel.ORIGINALORDER = "转科";
		}
		if (str.contains("测血压"))
		{
			//医嘱原文处理
			ordersModel.ORDER_TEXT = ordersModel.ORDER_TEXT + ordersModel.FREQUENCY;
			//规则处理
			ordersModel.ORIGINALORDER = ordersModel.ORDER_TEXT;
		}
		if (str.contains("会诊"))
		{
			//备注组装
			ordersModel.FREQ_DETAIL = ordersModel.ORDER_TEXT;
			//规则处理
			ordersModel.ORIGINALORDER = "会诊";
		}
		//手术处理
		Regex regext = new Regex("于.*([0-9]+)月([0-9]+)日.*");
		if (str.contains("术") || ((str.contains("今日") || str.contains("明日")) && str.contains("术")) || str.endsWith("术"))
		{
			java.time.LocalDateTime dt = java.time.LocalDateTime.now().plusDays(1);
			String month = dt.getMonthValue() > 9 ? dt.getMonthValue().toString() : "0" + dt.getMonthValue();
			String day = dt.getDayOfMonth() > 9 ? dt.getDayOfMonth().toString() : "0" + dt.getDayOfMonth();
			Regex regex = new Regex(".*(" + month + "+)月(" + day + "+)日.*");

			if (str.contains("明日") || regex.IsMatch(str))
			{
				//规则处理
				ordersModel.ORIGINALORDER = "明日手术";
			}
			else
			{
				if (str.contains("今日"))
				{
					//规则处理
					ordersModel.ORIGINALORDER = "今日手术";
				}
			}
		}
		//分娩处理
		Regex regexm = new Regex("于.*([0-9]+)月([0-9]+)日.*");
		if ((regext.IsMatch(str) && str.contains("接生")) || ((str.contains("今日") || str.contains("明日")) && str.contains("接生")) || str.endsWith("接生"))
		{
			java.time.LocalDateTime dt = java.time.LocalDateTime.now().plusDays(1);
			String month = dt.getMonthValue() > 9 ? dt.getMonthValue().toString() : "0" + dt.getMonthValue();
			String day = dt.getDayOfMonth() > 9 ? dt.getDayOfMonth().toString() : "0" + dt.getDayOfMonth();
			Regex regexf = new Regex("于.*(" + month + "+)月(" + day + "+)日.*");
			if (regexf.IsMatch(str) || str.contains("明日"))
			{
				//规则处理
				ordersModel.ORIGINALORDER = "分娩";
			}
			else
			{
				//规则处理
				ordersModel.ORIGINALORDER = "今日分娩";
			}
		}
		//标准拆分方法
		standardSplit(ordersModel, starttime, endtime, wardCode, list);
	}

	/** 
	 标准拆分方法
	 
	 @param ordersModel
	 @param starttime
	 @param endtime
	 @param wardCode
	 @param list
	*/
	private void standardSplit(DOCS_ORDERS_EXEC_LOG_EXT ordersModel, String starttime, String endtime, String wardCode, tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>> list)
	{
		//按周拆分
		if ((ordersModel.FREQ_INTERVAL_UNIT.trim().equals("周") && !tangible.DotNetToJavaStringHelper.isNullOrEmpty(ordersModel.PERFORM_SCHEDULE) && Integer.parseInt(ordersModel.FREQ_INTERVAL) == 1) || (ordersModel.FREQ_INTERVAL_UNIT.trim().equals("天") && !tangible.DotNetToJavaStringHelper.isNullOrEmpty(ordersModel.PERFORM_SCHEDULE) && Integer.parseInt(ordersModel.FREQ_INTERVAL) == 7))
		{

			int k = java.time.LocalDateTime.now().getDayOfWeek().getValue();
			String re = k == 0 ? "7" : k + "";
			String[] freqs = ordersModel.PERFORM_SCHEDULE.split("[-]", -1);
			for (String freq : freqs)
			{
				String[] sche = freq.split("[/]", -1);
				if (sche.length > 1)
				{
					if (sche[0].trim().contains(re))
					{
						AddNewOrdersDoByWeek(ordersModel, sche[1], starttime, endtime, wardCode, ordersModel.FREQUENCY, list);
					}
				}
			}
			return;
		}

		//按天、小时拆分的，GetSchedule为获取医嘱执行时间，处理办法是，把perform_schedule打散
		String[] schedules = GetSchedule(ordersModel);
		if (schedules.length == Integer.parseInt(tangible.DotNetToJavaStringHelper.isNullOrEmpty(ordersModel.FREQ_COUNTER) ? "0" : ordersModel.FREQ_COUNTER) && "小时,天,日".contains(ordersModel.FREQ_INTERVAL_UNIT))
		{
			AddNewOrdersNormal(ordersModel, schedules, starttime, endtime, wardCode, list);
			return;
		}
		else
		{
			//只拆一次的默认
			AddNewOrdersNormal(ordersModel, schedules, starttime, endtime, wardCode, list);
			return;
		}
	}


	/** 
	 按小时拆分的医嘱
	 
	 @param ordersModel 
	 @param schedules
	 @param starttime
	 @param endtime
	 @param wardCode 
	*/
	private void AddNewOrdersNormal(DOCS_ORDERS_EXEC_LOG_EXT ordersModel, String[] schedules, String starttime, String endtime, String wardCode, tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>> list)
	{
		int bottles = 0; //初始为第一瓶
		for (String schedule : schedules)
		{
			DOCS_ORDERS_EXEC_LOG_EXT ordersNewModel = PackOrderModel(ordersModel);
			String[] time = schedule.split(new char[] {':'}, StringSplitOptions.RemoveEmptyEntries);
			java.time.LocalDateTime dtStarttime = (java.time.LocalDateTime)starttime;
			java.time.LocalDateTime dtEndtime = (java.time.LocalDateTime)endtime;
			int hours = 0;
			int minutes = 0;
			if ("24".equals(time[0]))
			{
				// 24点数据默认拆到当天
				hours = 23;
				minutes = 59;
			}
			else if (time.length > 1)
			{
				hours = Integer.parseInt(time[0]);
				minutes = Integer.parseInt(time[1]);
			}
			else
			{
				hours = Integer.parseInt(time[0]);
			}
			java.time.LocalDateTime exectime = dtStarttime.Date.AddHours(hours).AddMinutes(minutes).AddSeconds(59);
			//执行时间大于等于医嘱开始时间则进行拆分记录
			if (exectime.compareTo(ordersNewModel.START_DATE_TIME) >= 0)
			{
				bottles++;
				ordersNewModel.EXEC_DATE = (java.time.LocalDateTime)(((java.time.LocalDateTime)(exectime)).toString("yyyy-MM-dd"));
				ordersNewModel.EXEC_TIME = exectime;
				ordersNewModel.BOTTLES = String.valueOf(bottles);
				list.argValue.add(ordersNewModel);
			}
		}
	}



	/** 
	拆分按周走的医嘱
	 
	 @param ordersModel
	 @param re
	 @param starttime
	 @param endtime
	 @param wardCode
	@param frequency 
	*/
	private void AddNewOrdersDoByWeek(DOCS_ORDERS_EXEC_LOG_EXT ordersModel, String re, String starttime, String endtime, String wardCode, String frequency, tangible.RefObject<ArrayList<DOCS_ORDERS_EXEC_LOG_EXT>> list)
	{
		DOCS_ORDERS_EXEC_LOG_EXT ordersNewModel = PackOrderModel(ordersModel);
		String exectime = java.time.LocalDateTime.parse(starttime).Date.toString("yyyy-MM-dd") + " " + re + ":00:59";
		ordersNewModel.EXEC_DATE = (java.time.LocalDateTime)(((java.time.LocalDateTime)(exectime)).toString("yyyy-MM-dd"));
		ordersNewModel.EXEC_TIME = (java.time.LocalDateTime)exectime;
		ordersNewModel.BOTTLES = "1";
		list.argValue.add(ordersNewModel);
	}

	/** 
	 获取医嘱执行时间
	 
	 @param freq
	 @param schedule
	 @return 
	*/
	private String[] GetSchedule(DOCS_ORDERS_EXEC_LOG_EXT ordersModel)
	{
		String freq = ordersModel.FREQUENCY;
		String schedule = ordersModel.PERFORM_SCHEDULE;
		String freq_intval = ordersModel.FREQ_INTERVAL;
		String freq_intval_unit = ordersModel.FREQ_INTERVAL_UNIT;
		java.time.LocalDateTime exec_start = ordersModel.exec_start_time;
		java.time.LocalDateTime exec_end = ordersModel.exec_end_time;

		if (Integer.parseInt(tangible.DotNetToJavaStringHelper.isNullOrEmpty(ordersModel.FREQ_COUNTER) ? "0" : ordersModel.FREQ_COUNTER) == 1 && ordersModel.FREQ_INTERVAL_UNIT.equals("小时"))
		{
			String exectimes = "";
			java.time.LocalDateTime exectime = ordersModel.exec_start_time;
			while (exectime.compareTo(ordersModel.exec_end_time) <= 0)
			{
				exectimes += exectime.toString("HH:mm") + ",";
				exectime = exectime.plusHours(Integer.parseInt(ordersModel.FREQ_INTERVAL));
			}
			String[] freqs = exectimes.split(new char[] {','}, StringSplitOptions.RemoveEmptyEntries);
			ordersModel.FREQ_COUNTER = freqs.length.toString();
			return freqs;
		}

		for (int i = 0; i < schedule.length(); i++)
		{
			char c = schedule.charAt(i);
			if (Character.IsNumber(c) || c == ' ' || c == ':')
			{
				continue;
			}
			else
			{
				schedule = schedule.replace(c, ' ');
			}
		}
		// 设置默认时间
		if (schedule.equals(""))
		{
			schedule = ordersModel.START_DATE_TIME.Value.toString("HH:mm");
		}
		return schedule.split(new char[] {' '}, StringSplitOptions.RemoveEmptyEntries);
	}


	private DOCS_ORDERS_EXEC_LOG_EXT PackOrderModel(DOCS_ORDERS_EXEC_LOG_EXT model)
	{
		DOCS_ORDERS_EXEC_LOG_EXT entity = new DOCS_ORDERS_EXEC_LOG_EXT();
		entity.PATIENT_ID = model.PATIENT_ID;
		entity.VISIT_ID = model.VISIT_ID;
		entity.DOSE_ACTUAL = model.DOSE_ACTUAL;
		entity.ORDER_SUB_NO = model.ORDER_SUB_NO;
		entity.ORDER_NO = model.ORDER_NO;
		entity.ADMINISTRATION = model.ADMINISTRATION;
		entity.AGE = model.AGE;
		entity.SEX = model.SEX;
		entity.BED_LABEL = model.BED_LABEL;
		entity.BED_NO = model.BED_NO;
		entity.DOSAGE_UNIT = model.DOSAGE_UNIT;
		entity.ORDER_TEXT = model.ORDER_TEXT;
		entity.ORDER_TYPE = model.ORDER_TYPE;
		entity.PATIENT_NAME = model.PATIENT_NAME;
		entity.PERFORM_SCHEDULE = model.PERFORM_SCHEDULE;
		entity.REPEAT_INDICATOR = model.REPEAT_INDICATOR;
		entity.START_DATE_TIME = model.START_DATE_TIME;
		entity.FREQUENCY = model.FREQUENCY;
		entity.FREQ_COUNTER = model.FREQ_COUNTER;
		entity.FREQ_DETAIL = model.FREQ_DETAIL;
		entity.FREQ_INTERVAL = model.FREQ_INTERVAL;
		entity.FREQ_INTERVAL_UNIT = model.FREQ_INTERVAL_UNIT;
		entity.DEPT_CODE = model.DEPT_CODE;
		entity.DATASOURCE_TYPE = model.DATASOURCE_TYPE;
		entity.DOCTOR_NAME = model.DOCTOR_NAME;
		entity.STOP_DATE_TIME = model.STOP_DATE_TIME;
		entity.ORIGINALORDER = model.ORIGINALORDER;

		return entity;
	}


}