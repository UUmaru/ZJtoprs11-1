package External.Util;

import java.util.*;
import External.Model.Entity.*;
import mHealth.Generic.Database.Helper.*;
import mHealth.Generic.Utils.*;
import External.BaseDal.*;
import External.Model.EntityDto.*;

public class PatMessage
{
	/** 
	 护理级别项目
	 
	 @param itemCode
	 @param itemKeyWords
	 @param dt
	 @param GetNurseNameInfo
	 @param lst
	*/
	public final void HuLiJiBie(String itemCode, String itemKeyWords, List<PatientInfo> dt, List<LoginUserView> GetNurseNameInfo, tangible.RefObject<ArrayList<PatMessagesView>> lst)
	{
		String NurseName = "";
		switch (itemKeyWords)
		{
			case "特级":
				HiLi(itemCode, dt, lst.argValue, NurseName, "0", itemKeyWords);
				break;
			case "一级":
				HiLi(itemCode, dt, lst.argValue, NurseName, "1", itemKeyWords);
				break;
			case "二级":
				HiLi(itemCode, dt, lst.argValue, NurseName, "2", itemKeyWords);
				break;
			default:
				break;
		}

	}

	private void HiLi(String itemCode, List<PatientInfo> dt, List<PatMessagesView> lst, String NurseName, String level, String levelName)
	{
		ArrayList<PatientInfo> drs1 = dt.Where(x -> x.nurseLevel.equals(level)).ToList();
		PatMessagesView model = new PatMessagesView();
		model.formName = "护理级别类";
		model.itemCode = itemCode;
		model.itemName = levelName;
		ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
		if (drs1.size() > 0)
		{
			for (PatientInfo dr : drs1)
			{
				String value = dr.bedNo.toString();
				ArrayList<SubListView> subList = new ArrayList<SubListView>();
				subList.add(fill_data(value, value, "", NurseName, java.time.LocalDateTime.now().toString(), "")); //sublist里面内容
				patients.add(filldata(dr.patientId.toString(), dr.patientName.toString(), dr.visitId.toString(), dr.nurseLevel.toString(), dr.bedNo.toString(), dr.bedLabel.toString(), subList));
			}
			if (patients.size() > 0)
			{
				model.patients = patients;
			}
		}
		else
		{
			model.patients = patients;
		}
		lst.add(model);
	}

	/** 
	 获取病情状态
	 
	 @param itemCode
	 @param itemKeyWords
	 @param dt
	 @param GetNurseNameInfo
	 @param lst
	*/
	public final void BingQing(String itemCode, String itemKeyWords, List<PatientInfo> dt, List<LoginUserView> GetNurseNameInfo, tangible.RefObject<ArrayList<PatMessagesView>> lst)
	{
		String NurseName = "";
		switch (itemKeyWords)
		{
			case "病危":
				GetBingQing(itemCode, dt, lst.argValue, NurseName, "1", itemKeyWords);
				break;
			case "病重":
				GetBingQing(itemCode, dt, lst.argValue, NurseName, "2", itemKeyWords);
				break;
			default:
				break;
		}

	}

	private void GetBingQing(String itemCode, List<PatientInfo> dt, List<PatMessagesView> lst, String NurseName, String level, String levelName)
	{
		ArrayList<PatientInfo> drs1 = dt.Where(x -> x.patientCondition.equals(level)).ToList();
		PatMessagesView model = new PatMessagesView();
		model.formName = "病情状态类";
		model.itemCode = itemCode;
		model.itemName = levelName;
		if (drs1.size() > 0)
		{
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			for (PatientInfo dr : drs1)
			{
				String value = dr.bedNo.toString();
				ArrayList<SubListView> subList = new ArrayList<SubListView>();
				subList.add(fill_data(value, value, "", NurseName, java.time.LocalDateTime.now().toString(), "")); //sublist里面内容
				patients.add(filldata(dr.patientId.toString(), dr.patientName.toString(), dr.visitId.toString(), dr.nurseLevel.toString(), dr.bedNo.toString(), dr.bedLabel.toString(), subList));
			}
			if (patients.size() > 0)
			{
				model.patients = patients;
			}
		}
		else
		{
			model.itemCode = itemCode;
			model.itemName = levelName;
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			model.patients = patients;
		}
		lst.add(model);
	}


	/** 
	 获取当天入院状态
	 
	 @param itemCode
	 @param itemKeyWords
	 @param dt
	 @param GetNurseNameInfo
	 @param lst
	*/
	public final void RuYuan(String itemCode, String itemKeyWords, List<PatientInfo> dt, List<LoginUserView> GetNurseNameInfo, tangible.RefObject<ArrayList<PatMessagesView>> lst)
	{
		String NurseName = "";
		switch (itemKeyWords)
		{
			case "入院":
				GetRuYuan(itemCode, dt, lst.argValue, NurseName, java.time.LocalDateTime.now().toString("yyyy-MM-dd"), itemKeyWords);
				break;
			default:
				break;
		}
	}

	private void GetRuYuan(String itemCode, List<PatientInfo> dt, List<PatMessagesView> lst, String NurseName, String level, String levelName)
	{
		ArrayList<PatientInfo> drs1 = dt.Where(x -> x.admissionTime > DateUtil.DateTimeToStamp(java.time.LocalDateTime.parse(level))).ToList();
		PatMessagesView model = new PatMessagesView();
		model.formName = "转运类";
		model.itemCode = itemCode;
		model.itemName = levelName;
		if (drs1.size() > 0)
		{
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			for (PatientInfo dr : drs1)
			{
				String value = "入院时间：" + DateUtil.StampToDateTime(dr.admissionTime.toString());
				ArrayList<SubListView> subList = new ArrayList<SubListView>();
				subList.add(fill_data(value, value, "", NurseName, java.time.LocalDateTime.now().toString(), "")); //sublist里面内容
				patients.add(filldata(dr.patientId.toString(), dr.patientName.toString(), dr.visitId.toString(), dr.admissionTime.toString(), dr.bedNo.toString(), dr.bedLabel.toString(), subList));
			}
			if (patients.size() > 0)
			{
				model.patients = patients;
			}
		}
		else
		{
			model.itemCode = itemCode;
			model.itemName = levelName;
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			model.patients = patients;
		}
		lst.add(model);
	}


	public final void GuoMin(String itemCode, String itemKeyWords, List<PatientInfo> dt, List<LoginUserView> GetNurseNameInfo, tangible.RefObject<ArrayList<PatMessagesView>> lst, DataTable drs)
	{
		String NurseName = "";
		switch (itemKeyWords)
		{
			case "药物过敏":
				GetGuoMinYaoWu(itemCode, dt, lst.argValue, NurseName, "", itemKeyWords, drs);
				break;
			case "青霉素(+)":
			case "青霉素( )":
				GetGuoMinYaoWu(itemCode, dt, lst.argValue, NurseName, "青霉素", itemKeyWords, drs);
				break;
			default:
				break;
		}

	}

	private void GetGuoMinYaoWu(String itemCode, List<PatientInfo> dt, List<PatMessagesView> lst, String NurseName, String level, String levelName, DataTable drs)
	{
		PatMessagesView model = new PatMessagesView();
		model.formName = "过敏类";
		model.itemCode = itemCode;
		model.itemName = levelName;
		ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
		for (PatientInfo dr : dt)
		{
			ArrayList<SubListView> subList = new ArrayList<SubListView>();
			DataRow[] drs_gm = drs.Select(String.format("PATIENT_ID = '%1$s'", dr.patientId));
			String gms = "";
			for (DataRow dr1 : drs_gm)
			{
				String gm = dr1["ITEM_VALUE"].toString().trim();
				gm = gm.replace("(+)", "$");
				String[] ss = gm.split("[$,，]", -1);
				for (String s : ss)
				{
					if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(s) && !gms.contains(s))
					{
						gms = tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms) ? s : gms + "," + s;
					}
				}
			}
			ArrayList<PatientInfo> drs1 = dt.Where(x -> (!x.allergy.equals("") && !x.allergy.equals("无") && x.patientId == dr.patientId)).ToList();
			if (drs1.size() > 0)
			{
				String value = drs1.get(0).allergy.toString();
				if (!gms.contains(value))
				{
					gms = tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms) ? value : gms + "," + value;
				}
			}
			if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms))
			{
				if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(level))
				{
					//药物过敏
					subList.add(fill_data(gms, gms, "", NurseName, java.time.LocalDateTime.now().toString(), "")); //sublist里面内容
					patients.add(filldata(dr.patientId.toString(), dr.patientName.toString(), dr.visitId.toString(), dr.nurseLevel.toString(), dr.bedNo.toString(), dr.bedLabel.toString(), subList));
				}
				else
				{
					//特定过敏项目
					if (gms.contains(level))
					{
						subList.add(fill_data(gms, gms, "", NurseName, java.time.LocalDateTime.now().toString(), "")); //sublist里面内容
						patients.add(filldata(dr.patientId.toString(), dr.patientName.toString(), dr.visitId.toString(), dr.nurseLevel.toString(), dr.bedNo.toString(), dr.bedLabel.toString(), subList));
					}
				}
			}
		}
		if (patients.size() > 0)
		{
			model.patients = patients;
		}
		lst.add(model);
	}


	/** 
	 跌倒压疮类
	 
	 @param itemCode
	 @param itemKeyWords
	 @param dt
	 @param GetNurseNameInfo
	 @param lst
	*/
	public final void PingGu(String itemCode, String itemKeyWords, List<PatientInfo> patdt, List<PatsRisk> dt, List<LoginUserView> GetNurseNameInfo, tangible.RefObject<ArrayList<PatMessagesView>> lst)
	{
		switch (itemKeyWords)
		{
			case "防压疮":
				GetPingGu(itemCode, patdt, dt, lst.argValue, "压疮风险护理单", itemKeyWords, GetNurseNameInfo);
				break;
			case "防跌倒/坠床":
				GetPingGu(itemCode, patdt, dt, lst.argValue, "跌倒坠床评估单", itemKeyWords, GetNurseNameInfo);
				break;
			case "防导管滑脱":
				GetPingGu(itemCode, patdt, dt, lst.argValue, "导管滑脱评估单", itemKeyWords, GetNurseNameInfo);
				break;
			case "防自理":
				GetPingGu(itemCode, patdt, dt, lst.argValue, "自理能力评估单", itemKeyWords, GetNurseNameInfo);
				break;
			default:
				break;
		}
	}

	private void GetPingGu(String itemCode, List<PatientInfo> patdt, List<PatsRisk> dt, List<PatMessagesView> lst, String level, String levelName, List<LoginUserView> GetNurseNameInfo)
	{
		ArrayList<PatsRisk> drs1 = dt.Where(x -> x.theme_code.equals(level)).ToList();
		PatMessagesView model = new PatMessagesView();
		model.formName = "评估类";
		model.itemCode = itemCode;
		model.itemName = levelName;
		if (drs1.size() > 0)
		{
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			for (PatsRisk dr : drs1)
			{
				ArrayList<PatientInfo> patdr = patdt.Where(x -> x.patientId.equals(dr.patient_id)).ToList();
				String value = dr.item_value.toString();
				ArrayList<LoginUserView> drs = GetNurseNameInfo.Where(x -> x.userId.equals(dr.creator)).ToList();
				String NurseName = "";
				if (drs.size() > 0)
				{
					NurseName = drs.get(0).userName.toString();
				}
				float sum = 0;
				if (level.equals("压疮风险护理单"))
				{
					tangible.RefObject<Float> tempRef_sum = new tangible.RefObject<Float>(sum);
					boolean tempVar = tangible.TryParseHelper.tryParseFloat(value, tempRef_sum);
						sum = tempRef_sum.argValue;
					if (tempVar)
					{
						if (sum >= 15 && sum <= 19)
						{
							value = "高度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
						else if (sum >= 20)
						{
							value = "极度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
					}
				}
				if (level.equals("跌倒坠床评估单"))
				{
					tangible.RefObject<Float> tempRef_sum2 = new tangible.RefObject<Float>(sum);
					boolean tempVar2 = tangible.TryParseHelper.tryParseFloat(value, tempRef_sum2);
						sum = tempRef_sum2.argValue;
					if (tempVar2)
					{
						if (sum >= 4 && sum <= 5)
						{
							value = "中度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
						else if (sum >= 6)
						{
							value = "高度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
					}
				}
				if (level.equals("导管滑脱评估单"))
				{
					tangible.RefObject<Float> tempRef_sum3 = new tangible.RefObject<Float>(sum);
					boolean tempVar3 = tangible.TryParseHelper.tryParseFloat(value, tempRef_sum3);
						sum = tempRef_sum3.argValue;
					if (tempVar3)
					{
						if (sum >= 8)
						{
							value = "高度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
					}
				}
				if (level.equals("自理能力评估单"))
				{
					tangible.RefObject<Float> tempRef_sum4 = new tangible.RefObject<Float>(sum);
					boolean tempVar4 = tangible.TryParseHelper.tryParseFloat(value, tempRef_sum4);
						sum = tempRef_sum4.argValue;
					if (tempVar4)
					{
						if (sum < 40)
						{
							value = "重度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
						else if (sum >= 40 && sum < 60)
						{
							value = "中度风险/" + value + "分";
							SetMessage(NurseName, patients, dr, patdr, value);
						}
					}
				}
			}
			if (patients.size() > 0)
			{
				model.patients = patients;
			}
		}
		else
		{
			model.itemCode = itemCode;
			model.itemName = levelName;
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			model.patients = patients;
		}
		lst.add(model);
	}

	private void SetMessage(String NurseName, List<PatientsView> patients, PatsRisk dr, ArrayList<PatientInfo> patdr, String value)
	{
		ArrayList<SubListView> subList = new ArrayList<SubListView>();
		String values = value + " 评估时间：" + String.format("%0s", (java.time.LocalDateTime)(dr.time_point.toString())) + " 操作人员：" + NurseName;
		subList.add(fill_data(value, values, "", NurseName, dr.time_point.toString(), "")); //sublist里面内容
		patients.add(filldata(patdr.get(0).patientId.toString(), patdr.get(0).patientName.toString(), patdr.get(0).visitId.toString(), patdr.get(0).nurseLevel.toString(), patdr.get(0).bedNo.toString(), patdr.get(0).bedLabel.toString(), subList));
	}

	public final void Xunshilei(String itemCode, String itemKeyWords, List<OrderExec> DtXunshi, List<PatientInfo> DtPatList, tangible.RefObject<ArrayList<PatMessagesView>> lst, IBaseDal _dal, IDatabase dbHelper) //巡视类
	{
		java.time.LocalDateTime currentTime = java.time.LocalDateTime.now(); //获取当前时间
		String order_no = ""; //医嘱号
		String patient_id = "";
		String exec_time = ""; //医嘱执行时间
		String today = java.time.LocalDateTime.now().toString("yyyy-MM-dd") + " 00:00:00";

		PatMessagesView model = new PatMessagesView();
		model.formName = "巡视类";
		if (DtXunshi.size() > 0)
		{
			model.itemCode = itemCode;
			model.itemName = "巡视";
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			for (OrderExec dr : DtXunshi)
			{
				String value = "";
				patient_id = dr.patient_id.toString();
				order_no = dr.order_no.toString();
				exec_time = dr.exec_time.toString();
				int timeLine = tangible.DotNetToJavaStringHelper.isNullOrEmpty(dr.time_line) ? 15 : Integer.parseInt(dr.time_line.toString());
				String Nexttimepoint = ((java.time.LocalDateTime)(dr.record_time.toString())).AddMinutes(timeLine).toString("yyyy-MM-dd HH:mm:ss");
				if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(Nexttimepoint))
				{
					String orderSpeed = tangible.DotNetToJavaStringHelper.isNullOrEmpty(dr.order_speed) ? "" : dr.order_speed;
					ArrayList<SubListView> subList = new ArrayList<SubListView>();
					List<OrderInfo> dt_order = DatatableToModelList<OrderInfo>.ConvertToModel(_dal.GetOrderInfo(dbHelper, patient_id, order_no, exec_time)); //获得巡视的医嘱
					if (dt_order.size() > 0)
					{
						for (OrderInfo drs : dt_order)
						{
							String ordertext = tangible.DotNetToJavaStringHelper.isNullOrEmpty(drs.orderText) ? "" : drs.orderText;
							if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(value))
							{
								value = ordertext;
							}
							else
							{
								value = value + "," + ordertext;
							}
						}
					}
					String values = value + "\n" + "执行时间:" + dr.record_time.toString() + " 执行人:" + dr.nurse_name.toString() + " 状态:" + dr.job_desc.toString() + " 滴速:" + orderSpeed;
					subList.add(fill_data("", values, dr.nurse_code.toString(), dr.nurse_name.toString(), dr.record_time.toString(), String.valueOf(timeLine))); //sublist里面内容
					ArrayList<PatientInfo> drpatient = DtPatList.Where(x -> x.patientId.equals(dr.patient_id)).ToList();
					if (drpatient.size() == 1)
					{
						patients.add(filldata(dr.patient_id.toString(), drpatient.get(0).patientName.toString(), drpatient.get(0).visitId, drpatient.get(0).nurseLevel.toString(), drpatient.get(0).bedNo.toString(), drpatient.get(0).bedLabel.toString(), subList));
					}
				}
			}
			if (patients.size() > 0)
			{
				model.patients = patients;
			}
		}
		else
		{
			model.itemCode = itemCode;
			model.itemName = "巡视";
			ArrayList<PatientsView> patients = new ArrayList<PatientsView>();
			model.patients = patients;
		}
		lst.argValue.add(model);
	}

	/** 
	 主表数据组装
	 
	 @param PATIENT_ID
	 @param patientName
	 @param VISIT_ID
	 @param nurseLevel
	 @param bedNO
	 @param bedLabel
	 @param subList
	 @return 
	*/
	private PatientsView filldata(String PATIENT_ID, String patientName, String VISIT_ID, String nurseLevel, String bedNO, String bedLabel, ArrayList<SubListView> subList)
	{
		PatientsView patients = new PatientsView();
		patients.patientId = PATIENT_ID;
		patients.patientName = patientName;
		patients.visitId = VISIT_ID;
		patients.nurseLevel = nurseLevel;
		patients.bedNo = bedNO;
		patients.bedLabel = bedLabel;
		patients.subList = subList;
		return patients;
	}

	/** 
	 明细表数据组装
	 
	 @param score
	 @param itemValue
	 @param NurseCode
	 @param NurseName
	 @param createTime
	 @param remindInterval
	 @return 
	*/
	private SubListView fill_data(String score, String itemValue, String NurseCode, String NurseName, String createTime, String remindInterval)
	{
		SubListView subList = new SubListView();
		subList.score = score;
		ArrayList<String> list = new ArrayList<String>();
		subList.signValue = list;
		subList.nurseCode = NurseCode;
		subList.nurseName = NurseName;
		subList.createTime = createTime;
		subList.remindInterval = remindInterval;
		return subList;

	}

}