package External.Service;

import java.util.*;
import External.BaseDal.*;
import External.Dal.*;
import External.Model.Entity.*;
import External.Model.EntityDto.*;
import External.Util.*;
import mHealth.Generic.Database.Helper.*;
import mHealth.Generic.Service.*;
import mHealth.Generic.Utils.*;
import External.Model.Entity.Ext.*;
import mHealth.Model.Base.*;
import Quartz.*;

/** 
 业务层接口实现及访问数据层
*/
public class ExternalService extends BaseService
{

		///#region   初始化信息
	/** 
	 
	*/
	private IBaseDal _BaseFuctions;
	/** 
	 护理文书API HOST
	*/
	private String _hlwsApiHost;

	private static final String RgbPattern = "^#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})$";

	/** 
	 医院CODE
	*/
	private String _hospitalCode;

	/** 
	 构造体
	*/
	public ExternalService()
	{
		_BaseFuctions = new Orcale();
		_hlwsApiHost = ConfigUtil.GetAppSettings("HlwsApiHost");
		_hospitalCode = ConfigUtil.GetAppSettings("HospitalCode");

		if (_hlwsApiHost == null || _hospitalCode == null)
		{
			throw new RuntimeException("护理文书API HOST或者医院CODE缺失！");
		}
	}
	/** 
	 
	*/
	@Override
	public void init()
	{
		DataFactory factory = new DataFactory(super.Database);
		//通过配置文档初始化属性,把CisUser对象赋值给Platform的变量,用于调用平台接口时传参.
		_BaseFuctions = factory.CreateBaseFuctions(CisUser);
	}

		///#endregion


		///#region   患者信息相关

	/** 
	 组装科室患者住院号与住院次数
	 
	 @param deptCode 病区代码
	 @return 
	*/
	public final String GetPatPidVidList(String deptCode)
	{
		String strPat = "";
		List<PatientInfo> patList = GetPatientInfo(deptCode);
		for (PatientInfo pat : patList)
		{
			strPat += "'" + pat.patientId + pat.visitId + "',";
		}
		if (strPat.endsWith(","))
		{
			strPat = tangible.DotNetToJavaStringHelper.trimEnd(strPat, ',');
		}
		return strPat;
	}

	/** 
	 组装科室患者住院号与住院次数
	 
	 @param deptCode 病区代码
	 @return 
	*/
	public final String GetPatPidVidListWithBaby(String deptCode)
	{
		String strPat = "";
		List<PatientInfo> patList = GetPatientInfo(deptCode, null, false, false, true);
		for (PatientInfo pat : patList)
		{
			strPat += "'" + pat.patientId + pat.visitId + "',";
		}
		if (strPat.endsWith(","))
		{
			strPat = tangible.DotNetToJavaStringHelper.trimEnd(strPat, ',');
		}
		return strPat;
	}

	/** 
	 组装病人信息
	 
	 @param drs
	 @return 
	*/
	private static PatientInfo SetPatientInfo(DataRow drs)
	{
		long admissionTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.now());
		if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(drs["ADMISSION_DATE"].toString()))
		{
			admissionTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.parse(drs["ADMISSION_DATE"].toString()));
		}

		long deptedTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.now());
		if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(drs["DEPTED_TIME"].toString()))
		{
			deptedTime = DateUtil.DateTimeToStamp(java.time.LocalDateTime.parse(drs["DEPTED_TIME"].toString()));
		}

		long dateOfBirth = DateUtil.DateTimeToStamp(java.time.LocalDateTime.now());
		if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(drs["DATE_OF_BIRTH"].toString()))
		{
			dateOfBirth = DateUtil.DateTimeToStamp(java.time.LocalDateTime.parse(drs["DATE_OF_BIRTH"].toString()));
		}

		PatientInfo model = new PatientInfo();

		model.patientId = drs["PATIENT_ID"].toString();
		model.visitId = drs["VISIT_ID"].toString();
		model.chargeType = drs["CHARGE_TYPE"].toString();
		model.admissionTime = admissionTime;
		model.doctorInCharge = drs["DOCTOR_IN_CHARGE"].toString();
		model.dutyNurseName = drs["NURSE_IN_CHARGE"].toString();
		model.balance = tangible.DotNetToJavaStringHelper.isNullOrEmpty(drs["BALANCE"].toString()) ? "0" : drs["BALANCE"].toString();
		model.deptedTime = deptedTime;
		model.patientName = drs["PATIENT_NAME"].toString();
		model.diagnosisInfo = drs["DIAGNOSIS"].toString();
		model.daysAfterAdmission = ((java.time.LocalDateTime.now().Date - DateUtil.StampToDateTime(String.valueOf(admissionTime)).Date).Days + 1).toString();
		model.dayOfWeek = DateUtil.GetDayOfWeekCn(DateUtil.StampToDateTime(String.valueOf(admissionTime))).toString();
		model.sex = drs["SEX"].toString().trim();
		model.nurseLevel = tangible.DotNetToJavaStringHelper.isNullOrEmpty(drs["NURSE_LEVEL"].toString()) ? "2" : drs["NURSE_LEVEL"].toString();
		model.dateOfBirth = dateOfBirth;
		model.inpNo = drs["INP_NO"].toString();
		model.admissionId = drs["INP_NO"].toString();
		model.patientCondition = drs["STATUS"].toString();
		// 患者信息显示床号
		model.bedLabel = drs["BED_LABEL"].toString();
		// 系统床号，无该字段则赋值为显示床号
		if (drs.Table.Columns.Contains("SYS_BED_LABEL"))
		{
			model.sysBedLabel = drs["SYS_BED_LABEL"].toString();
		}
		else
		{
			model.sysBedLabel = model.bedLabel;
			LogUtil.Warn("不存在系统床号字段SYS_BED_LABEL，赋值为显示床号字段BED_LABEL：" + model.bedLabel);
		}

		model.age = ToolsUtil.CalAge(dateOfBirth);
		model.bedFlag = "1";
		model.bedNo = drs["BED_No"].toString();
		model.allergy = drs["ALLERGY"].toString();
		model.adtFlag = drs["ADTFLAG"].toString();
		model.meal = drs["MEAL"].toString();
		// HIS 取过来的数据是反的；正常情况 WARD -> 病区 DEPT -> 科室
		// 但是在那边对应的是 WARD 是科室，DEPT 是病区
		model.deptCode = drs.Table.Columns.Contains("WARD_CODE") ? drs["WARD_CODE"].toString() : "";
		model.deptName = drs.Table.Columns.Contains("WARD_NAME") ? drs["WARD_NAME"].toString() : "";

		model.wardCode = drs.Table.Columns.Contains("DEPT_CODE") ? drs["DEPT_CODE"].toString() : "";
		model.wardName = drs.Table.Columns.Contains("DEPT_NAME") ? drs["DEPT_NAME"].toString() : "";

		model.motherId = drs.Table.Columns.Contains("MOTHER_ID") ? drs["MOTHER_ID"].toString() : "";

		// 填充扩展字段
		String firstReservedData = drs.Table.Columns.Contains("FIRST_RESERVED_DATA") ? drs["FIRST_RESERVED_DATA"].toString() : "";
		String firstReservedName = drs.Table.Columns.Contains("FIRST_RESERVED_NAME") ? drs["FIRST_RESERVED_NAME"].toString() : "";
		String secondReservedData = drs.Table.Columns.Contains("SECOND_RESERVED_DATA") ? drs["SECOND_RESERVED_DATA"].toString() : "";
		String secondReservedName = drs.Table.Columns.Contains("SECOND_RESERVED_NAME") ? drs["SECOND_RESERVED_NAME"].toString() : "";
		model.patInfoExtend.Add(new PatInfoExtend("firstReservedData", firstReservedData, firstReservedName));
		model.patInfoExtend.Add(new PatInfoExtend("secondReservedData", secondReservedData, secondReservedName));
		return model;
	}

	/** 
	 组装婴儿信息
	 
	 @param drs
	 @return 
	*/
	private static BabyInfo SetBabyInfo(DataRow drs)
	{
		BabyInfo model = new BabyInfo();
		if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(drs["BABY_BIRTH"].toString()))
		{
			model.dateOfBirth = DateUtil.DateTimeToStamp(java.time.LocalDateTime.parse(drs["BABY_BIRTH"].toString()));
		}
		model.sex = drs["BABY_SEX"].toString().trim();
		model.isPediatrics = "1".equals(drs["BABY_IN_PEDIATRICS"].toString()) ? true : false;
		model.weight = drs["BABY_WEIGHT"].toString();
		return model;
	}

	/** 
	 根据床号获取此床的在院患者信息
	 
	 @param wardCode 病区号
	 @param bedNo 床号
	 @return 若查询到多条或者0条，则返回为null，否则返回该患者的信息
	*/
	public final PatientInfo GetPatientInfoByBedNo(String wardCode, String bedNo)
	{
		IDatabase hisDb = Database[SysTypes.HIS];
		DataTable patientDt = _BaseFuctions.GetPatientInfoByBedNo(hisDb, wardCode, bedNo);
		if (patientDt.Rows.size() != 1)
		{
			LogUtil.Error(String.format("根据病区号（%1$s）与病床号（%2$s）获取到%3$s条患者信息,数据有误！！", wardCode, bedNo, patientDt.Rows.size()));
			return null;
		}

		PatientInfo model = SetPatientInfo(patientDt.Rows[0]);
		tangible.RefObject<PatientInfo> tempRef_model = new tangible.RefObject<PatientInfo>(model);
		SetCardColor(patientDt.Rows[0], tempRef_model);
		model = tempRef_model.argValue;
		model.customCardColor = CheckColorLegal(model.customCardColor);
		model.daysAfterOperation = patientDt.Rows[0]["DAYS_AFTER_OPERATION"].toString();
		model.dutyNurseId = patientDt.Rows[0]["NURSE_IN_CHARGE_ID"].toString();
		return model;
	}

	/** 
	 获取在院患者基本信息
	 
	 @param deptCode 科室code
	 @param wardCode 病区code
	 @param isBed 是否展示空床标记默认不展示
	 @param isMaternity 是否展示产妇信息
	 @return 
	*/

	public final java.util.ArrayList<PatientInfo> GetPatientInfo(String deptCode, String wardCode, boolean isBed, boolean isMaternity)
	{
		return GetPatientInfo(deptCode, wardCode, isBed, isMaternity, false);
	}

	public final java.util.ArrayList<PatientInfo> GetPatientInfo(String deptCode, String wardCode, boolean isBed)
	{
		return GetPatientInfo(deptCode, wardCode, isBed, false, false);
	}

	public final java.util.ArrayList<PatientInfo> GetPatientInfo(String deptCode, String wardCode)
	{
		return GetPatientInfo(deptCode, wardCode, false, false, false);
	}

	public final java.util.ArrayList<PatientInfo> GetPatientInfo(String deptCode)
	{
		return GetPatientInfo(deptCode, null, false, false, false);
	}

	public final ArrayList<PatientInfo> GetPatientInfo(String deptCode, String wardCode, boolean isBed, boolean isMaternity, boolean isQueryBaby)
	{
		IDatabase hisDb = Database[SysTypes.HIS];
		DataTable patientList = _BaseFuctions.GetPatientInfo(hisDb, deptCode, wardCode, isQueryBaby);
		ArrayList<PatientInfo> result = new ArrayList<PatientInfo>();
		for (DataRow row : patientList.Rows)
		{
			PatientInfo model = SetPatientInfo(row);
			tangible.RefObject<PatientInfo> tempRef_model = new tangible.RefObject<PatientInfo>(model);
			SetCardColor(row, tempRef_model);
			model = tempRef_model.argValue;
			model.customCardColor = CheckColorLegal(model.customCardColor);
			model.daysAfterOperation = row["DAYS_AFTER_OPERATION"].toString();
			model.dutyNurseId = row["NURSE_IN_CHARGE_ID"].toString();
			model.isPregnantWomen = row["IS_PREGNANT_WOMEN"].toString();
			result.add(model);
		}

			///#region 产科病区获取孕产信息和婴儿信息
		if (isMaternity)
		{
			DataTable DtMaternity = _BaseFuctions.GetMaternityInfo(hisDb, deptCode, wardCode);
			for (PatientInfo model : result)
			{
				DataRow[] drs_maternity = DtMaternity.Select(String.format("PATIENT_ID = '%1$s'", model.patientId));
				if (drs_maternity.length <= 0)
				{
					continue;
				}
				model.maternityStatus = drs_maternity[0]["MATERNITY_STATUS"].toString();
				model.pregnancyCycle = drs_maternity[0]["PREGNANCY_CYCLE"].toString();
				boolean hasBaby = "1".equals(drs_maternity[0]["HAS_BABY"].toString()) ? true : false;
				if (hasBaby)
				{
					for (DataRow drs : drs_maternity)
					{
						BabyInfo baby = SetBabyInfo(drs);
						model.babyInfo.Add(baby);
					}
				}
			}
		}
			///#endregion
		// 是否展示空床
		if (isBed)
		{
			DataTable bedDt = _BaseFuctions.GetBedInfo(hisDb, deptCode);
			for (DataRow drs : bedDt.Rows)
			{
				PatientInfo bedPat = new PatientInfo();
				bedPat.bedFlag = "0";
				bedPat.bedLabel = drs["material_name"].toString();
				bedPat.bedNo = drs["material_code"].toString();
				bedPat.dutyNurseId = drs["nurse_in_charge_id"].toString();
				bedPat.dutyNurseName = drs["nurse_in_charge"].toString();
				// 系统床号，无该字段则赋值为显示床号
				if (bedDt.Columns.Contains("sys_material_name"))
				{
					bedPat.sysBedLabel = drs["sys_material_name"].toString();
				}
				else
				{
					bedPat.sysBedLabel = bedPat.bedLabel;
					LogUtil.Warn("不存在系统床号字段sys_material_name，赋值为显示床号字段material_name：" + bedPat.bedLabel);
				}
				result.add(bedPat);
			}
		}
		return result;
	}

	/** 
	 根据病人基本数据自定义安卓端显示的卡片颜色
	 
	 @param row 病人数据
	 @param patient 组装后的病人数据
	*/
	public final void SetCardColor(DataRow row, tangible.RefObject<PatientInfo> patient)
	{
		// color 值现场需要根据实际情况进行设置，格式:#A132F4 或者#A132F421
		String color = "undefined";
		patient.argValue.customCardColor = color;
	}

	/** 
	 检查自定义的颜色是否合法
	 
	 @param color
	 @return 
	*/
	public final String CheckColorLegal(String color)
	{
		return Regex.IsMatch(color, RgbPattern) ? color : "";
	}

	/** 
	 获取出院患者基本信息
	 
	 @param wardCode 病区代码
	 @param isToday 是否只查询今日出院患者 true 查询今日 false 查询该科室所有数据
	 @return 
	*/

	public final java.util.ArrayList<PatientInfo> GetPatientDischargeInfo(String wardCode)
	{
		return GetPatientDischargeInfo(wardCode, true);
	}

	public final ArrayList<PatientInfo> GetPatientDischargeInfo(String wardCode, boolean isToday)
	{
		DataTable dtPatList = _BaseFuctions.GetPatientDischargeInfo(Database[SysTypes.HIS], wardCode, isToday);
		ArrayList<PatientInfo> list = new ArrayList<PatientInfo>();
		for (DataRow drs : dtPatList.Rows)
		{
			PatientInfo model = SetPatientInfo(drs);
			list.add(model);
		}

		return list;
	}

	/** 
	 获取转科患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/
	public final ArrayList<PatientInfo> GetPatientChangeInfo(String wardCode)
	{
		DataTable DtPatList = _BaseFuctions.GetPatientChangeInfo(Database[SysTypes.HIS], wardCode);
		ArrayList<PatientInfo> list = new ArrayList<PatientInfo>();
		for (DataRow drs : DtPatList.Rows)
		{
			PatientInfo model = SetPatientInfo(drs);
			list.add(model);
		}
		return list;
	}

	/** 
	 预入科患者
	 
	 @param deptCode
	 @return 
	*/
	public final List<HisPrePatsView> GetHisPrePats(String deptCode)
	{
		ArrayList<HisPrePatsView> lst = new ArrayList<HisPrePatsView>();
		DataTable dt = _BaseFuctions.GetHisPrePats(deptCode, Database[SysTypes.HIS]);
		for (DataRow r : dt.Rows)
		{
			HisPrePatsView l = new HisPrePatsView();
			l.patientId = r["PATIENT_ID"].toString();
			l.visitId = r["VISIT_ID"].toString();
			l.inpNo = r["INP_NO"].toString();
			l.deptCode = r["DEPT_CODE"].toString();
			l.deptName = r["DEPT_NAME"].toString();
			l.wardCode = r["WARD_CODE"].toString();
			l.wardName = r["WARD_NAME"].toString();
			l.patientName = r["PATIENT_NAME"].toString();
			l.nation = r["NATION"].toString();
			l.dateOfBirth = (java.time.LocalDateTime)(r["DATE_OF_BIRTH"].toString());
			l.sex = r["SEX"].toString();
			l.chargeType = r["CHARGE_TYPE"].toString();
			l.admissionDate = (java.time.LocalDateTime)(r["ADMISSION_DATE"].toString());
			l.address = r["ADDRESS"].toString();
			l.balance = r["BALANCE"].toString();
			l.age = DateUtil.GetAgeOfDateTime((java.time.LocalDateTime)(r["DATE_OF_BIRTH"].toString()), java.time.LocalDateTime.now()) + "岁";
			lst.add(l);
		}
		return lst;
	}

	/** 
	 排床操作
	 
	 @param model
	 @return 
	*/
	public final int SaveSynPatBedInfo(AdtLogRecDto model)
	{
		return _BaseFuctions.SaveSynPatBedInfo(model, Database[SysTypes.SYS]);
	}

	/** 
	 获取全科体征的时间点
	 
	 @param deptCode
	 @return 
	*/
	public final ArrayList<NanaTimePoint> GetTimePoint(String deptCode)
	{
		return _BaseFuctions.GetTimePoint(_hlwsApiHost, deptCode);
	}

	/** 
	 获取全科体征数据
	 
	 @param timePoint 全科体征的时刻点，如：2020-04-02 14:00:00
	 @param deptCode 科室号
	 @return 
	*/
	public final ArrayList<NanaSigns> GetSign(String timePoint, String deptCode)
	{
		return _BaseFuctions.GetSigns(_hlwsApiHost, timePoint, _hospitalCode, deptCode);
	}

	/** 
	 获取全科体征的映射关系
	 
	 @return 
	*/
	public final List<NanaSignItem> GetSignItemsMap()
	{
		return _BaseFuctions.GetSignItemsMap(_hlwsApiHost, _hospitalCode);
	}


		///#endregion


		///#region   用户信息相关

	/** 
	 获取用户信息
	 
	 @param userId
	 @param password
	 @param deptCode
	 @param cardId
	 @return 
	*/

	public final java.util.List<LoginUserView> GetUserInfo(String userId, String password, String deptCode)
	{
		return GetUserInfo(userId, password, deptCode, null);
	}

	public final java.util.List<LoginUserView> GetUserInfo(String userId, String password)
	{
		return GetUserInfo(userId, password, null, null);
	}

	public final java.util.List<LoginUserView> GetUserInfo(String userId)
	{
		return GetUserInfo(userId, null, null, null);
	}

	public final java.util.List<LoginUserView> GetUserInfo()
	{
		return GetUserInfo(null, null, null, null);
	}

	public final List<LoginUserView> GetUserInfo(String userId, String password, String deptCode, String cardId)
	{
		DataTable dt = _BaseFuctions.GetUserInfo(userId, password, deptCode, cardId, Database[SysTypes.SYS]);
		ArrayList<LoginUserView> list = new ArrayList<LoginUserView>();
		for (DataRow drs : dt.Rows)
		{
			LoginUserView model = new LoginUserView();
			model.cardId = drs["CARD_ID"].toString();
			model.userId = drs["USER_ID"].toString();
			model.deptCode = drs["DEPT_CODE"].toString();
			model.deptName = drs["DEPT_NAME"].toString();
			model.hospitalCode = drs["HOSPITAL_CODE"].toString();
			model.userName = drs["USER_NAME"].toString();
			model.gender = drs["GENDER"].toString();
			model.loginName = drs["LOGIN_NAME"].toString();
			model.loginPass = drs["LOGIN_PASS"].toString();
			model.dateOfBirth = (java.time.LocalDateTime)(tangible.DotNetToJavaStringHelper.isNullOrEmpty(drs["DATE_OF_BIRTH"].toString()) ? null : drs["DATE_OF_BIRTH"].toString());
			model.email = drs["EMAIL"].toString();
			model.phoneNumber = drs["PHONE_NUMBER"].toString();
			model.cardNo = drs["CARD_NO"].toString();
			model.job = drs["JOB"].toString();
			model.dbUser = drs["USER_ID"].toString();
			model.imgUrl = drs["IMG_URL"].toString();
			model.roleCode = drs["ROLE"].toString();
			model.isEnable = drs["ACTIVE"].toString();
			list.add(model);
		}
		return list;
	}

	/** 
	 获取周年纪念
	 
	 @param deptCode
	 @return 
	*/
	public final List<AnniversaryView> GetAnniversary(String deptCode, String hospitalName)
	{
		DataTable dt = _BaseFuctions.GetAnniversary(deptCode, Database[SysTypes.HIS]);
		ArrayList<AnniversaryView> list = new ArrayList<AnniversaryView>();
		for (DataRow r : dt.Rows)
		{
			java.time.LocalDateTime in_hos_time = java.time.LocalDateTime.parse(r["in_hos_time"].toString());
			String userName = r["user_name"].toString();
			int sex = r["sex"].toString().equals("女") ? 0 : 1;
			int span = java.time.LocalDateTime.now().getYear() - in_hos_time.getYear();
			if (span > 0)
			{
				//获取头像
				String user_id = r["user_id"].toString();
				String imgUrl = null;
				if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(user_id))
				{
					DataTable dtUser = _BaseFuctions.GetUserInfo(user_id, null, deptCode, null, Database[SysTypes.SYS]);
					if (dtUser.Rows.size() == 1 && dtUser.Rows[0]["IMG_URL"] != null)
					{
						imgUrl = dtUser.Rows[0]["IMG_URL"].toString();
					}
				}

				String desc = String.format("  今天是您在%1$s工作满%2$s年的日子,感谢您全情的付出,感谢您不懈的守候！", hospitalName, span);
				AnniversaryView model = new AnniversaryView();
				model.desc = desc;
				model.name = userName;
				model.gender = sex;
				model.picture = imgUrl;
				list.add(model);
			}
		}
		return list;
	}

	/** 
	 数字风云榜
	 
	 @param deptCode
	 @return 
	*/
	public final List<BillboardDataView> GetBillboardData(String deptCode)
	{
		//扫描率
		DataTable dt = _BaseFuctions.GetBillboardData(deptCode, Database[SysTypes.SYS]);
		ArrayList<BillboardDataView> list = new ArrayList<BillboardDataView>();
		if (dt.Rows.size() > 0)
		{

			//获取头像
			String user_id = dt.Rows[0]["nurse"].toString();
			String imgUrl = null;
			if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(user_id))
			{
				DataTable dtUser = _BaseFuctions.GetUserInfo(user_id, null, deptCode, null, Database[SysTypes.SYS]);
				if (dtUser.Rows.size() == 1 && dtUser.Rows[0]["IMG_URL"] != null)
				{
					imgUrl = dtUser.Rows[0]["IMG_URL"].toString();
				}
			}

			//扫描率
			BillboardDataView l = new BillboardDataView();
			l.title = "最爱使用移动护理的护士";
			l.sex = "女";
			l.nurseName = dt.Rows[0]["nurse_name"].toString();
			l.headPic = imgUrl;
			l.description = "PDA扫描医嘱数量";
			l.value = dt.Rows[0]["pdasum"].toString();
			;
			l.date = java.time.LocalDateTime.now().toString("yyyy-MM-dd");
			list.add(l);
		}
		return list;
	}

	/** 
	 获取子科室信息
	 
	 @param deptCode
	 @return 
	*/
	public final List<WardInfoView> GetWardSubDept(String deptCode)
	{
		IDatabase db = Database[SysTypes.HIS];
		DataTable DtPatList = _BaseFuctions.GetWardSubDept(db, deptCode);

		ArrayList<WardInfoView> list = new ArrayList<WardInfoView>();
		for (DataRow r : DtPatList.Rows)
		{
			WardInfoView model = new WardInfoView();
			model.code = r["ward_code"].toString();
			model.name = r["ward_name"].toString();
			list.add(model);
		}
		return list;
	}

	/** 
	 批量查询所有科室排班的原始数据信息（四周以内的）
	 
	 @param deptCodeList 科室号列表
	 @return 
	*/
	public final DataTable GetScheduleDataTable(List<String> deptCodeList)
	{
		// 起：本周第一天。 止：四周过后的第一天
		String start = ToolsUtil.GetThisWeekMonday();
		String end = ((java.time.LocalDateTime)(start)).AddDays(4 * 7).toString();
		DataTable schedule = _BaseFuctions.GetSchedule(deptCodeList, start, end, Database[SysTypes.HIS]);
		return schedule;
	}

	/** 
	 获取排班信息
	 当前方法已经弃用，改成从定时器同步数据到娜娜库，排班直接数据来源改成娜娜本地
	 
	 @param deptCode 科室code
	 @return 
	*/
	public final List<NurseScheduleView> GetSchedule(String deptCode)
	{
		ArrayList<NurseScheduleView> list = new ArrayList<NurseScheduleView>();
		// 起：本周第一天。 止：四周过后的第一天
		String start = ToolsUtil.GetThisWeekMonday();
		String end = ((java.time.LocalDateTime)(start)).AddDays(4 * 7).toString();
		DataTable schedule = _BaseFuctions.GetSchedule(new ArrayList<String>(Arrays.asList(new String[] {deptCode})), start, end, Database[SysTypes.HIS]);
		// 四周数据
		for (int i = 0; i < 4; i++)
		{
			java.time.LocalDateTime monday = ((java.time.LocalDateTime)(start)).AddDays(i * 7);
			NurseScheduleView model = new NurseScheduleView();
			model.startDateTime = DateUtil.DateTimeToStamp(monday);
			// 只会筛选出来START_DATE是周一的数据
			String mondayFilter = String.format("START_DATE=#%1$s#", monday.toString("yyyy-MM-dd"));
			DataRow[] item = schedule.Select(mondayFilter);
			if (item.length > 0)
			{
				model.remark = item[0]["WEEK_REMARK"].toString();
				for (DataRow r : item)
				{
					String[] str = r["SCHEDULE_VALUE"].toString().split("[,]", -1);
					NurseSchedule detail = new NurseSchedule();
					detail.nurseId = r["NURSE_ID"].toString();
					detail.nurseName = r["NURSE_NAME"].toString();
					detail.Mon = str[0];
					detail.Tue = str[1];
					detail.Wed = str[2];
					detail.Thu = str[3];
					detail.Fri = str[4];
					detail.Sat = str[5];
					detail.Sun = str[6];
					detail.taskTime = r["TASK_TIME"].toString();
					detail.remark = r["REMARK"].toString();
					detail.createTime = DateUtil.DateTimeToStamp((java.time.LocalDateTime)r["START_DATE"]);
					detail.level = r["NURSE_LEVEL"].toString();

					model.nurseScheduleList.Add(detail);
				}
			}
			list.add(model);
		}
		return list;
	}


	/** 
	 获取责任护士管床信息
	 
	 @param deptCode
	 @param days 获取天数，从今天开始起
	 @return 
	*/
	public final List<DutyNursePlanView> GetNursePlan(String deptCode, int days)
	{
		IDatabase db = Database[SysTypes.HIS];
		ArrayList<DutyNursePlanView> list = new ArrayList<DutyNursePlanView>();
		DataTable dt = _BaseFuctions.GetNursePlanDefault(deptCode, days, db);
		if (dt.Rows.size() > 0)
		{
			//取多少天内的数据
			for (int i = 0; i < days; i++)
			{
				java.time.LocalDateTime dtn = java.time.LocalDateTime.now().plusDays(i);
				//获取某天的责任护士排班信息
				String filterExpression = String.format("NURSING_TIME=#%1$s#", dtn.toString("yyyy-MM-dd"));
				DataRow[] item = dt.Select(filterExpression);
				DutyNursePlanView Planmodel = new DutyNursePlanView();
				Planmodel.nursingTime = DateUtil.DateTimeToStamp(dtn);
				Planmodel.updateTime = DateUtil.DateTimeToStamp(dtn);
				DataTable dtName = new DataTable();
				if (item.length > 0)
				{
					dtName = item[0].Table.DefaultView.ToTable(true, new String[] {"GROUP_IDENTITY", "GROUP_NAME"});
				}
				//分组
				for (DataRow dr : item)
				{
					int count = Planmodel.groupDutyNurse.Where(x -> x.groupIdentity.equals(dr["GROUP_IDENTITY"].toString())).ToList().size();
					if (count == 0)
					{
						String filterGroupName = String.format("GROUP_IDENTITY='%1$s'", dr["GROUP_IDENTITY"].toString());
						DataRow[] itemName = dtName.Select(filterGroupName);
						if (itemName.length > 1)
						{
							Planmodel.repeatGroupName = true;
						}
						DutyNurse Nursemodel = new DutyNurse();
						Nursemodel.groupIdentity = dr["GROUP_IDENTITY"].toString();
						Nursemodel.groupName = dr["GROUP_NAME"].toString();
						Nursemodel.headNurseId = dr["HEAD_NURSE_ID"].toString();
						Nursemodel.headNurseName = dr["HEAD_NURSE_NAME"].toString();
						Nursemodel.nursingTime = DateUtil.DateTimeToStamp((java.time.LocalDateTime)dr["NURSING_TIME"]);
						Nursemodel.updateTime = DateUtil.DateTimeToStamp((java.time.LocalDateTime)dr["UPDATE_TIME"]);
						//分组
						Planmodel.groupDutyNurse.Add(Nursemodel);
					}
				}
				//组装责护管床信息
				for (DutyNurse nu : Planmodel.groupDutyNurse)
				{
					DataRow[] itembed = item.Where(x -> nu.groupIdentity.equals(x["GROUP_IDENTITY"].toString())).ToArray();
					for (DataRow dr : itembed)
					{
						ArrayList<BedDutyNurse> bedlist = nu.bedDutyNurseList.Where(x -> x.dutyNurseId.equals(dr["DUTY_NURSE_ID"].toString())).ToList();
						if (bedlist.isEmpty())
						{
							BedDutyNurse Bedmodel = new BedDutyNurse();
							Bedmodel.dutyNurseId = dr["DUTY_NURSE_ID"].toString();
							Bedmodel.dutyNurseName = dr["DUTY_NURSE_NAME"].toString();
							Bedmodel.dutyNurseSex = dr["DUTY_NURSE_SEX"].toString();
							Bedmodel.dutyNurseImg = dr["DUTY_NURSE_IMG"].toString();
							Bedmodel.night = dr["NIGHT"].toString();
							//分床
							Bedmodel.bedLabelList.Add(dr["BED_LABEL"].toString());
							nu.bedDutyNurseList.Add(Bedmodel);
						}
						else
						{
							bedlist.get(0).bedLabelList.Add(dr["BED_LABEL"].toString());
						}
					}
				}
				list.add(Planmodel);
			}
		}
		return list;
	}


	/** 
	 获取评估项目明细地址
	 
	 @param url
	 @param Patientid
	 @param Visit_id
	 @param ThemeCode
	 @return 
	*/
	public final String GetReport(String url, String Patientid, String Visit_id, String ThemeCode)
	{
		//组装url地址
		String remove = "";
		if (url.contains("/"))
		{
			String[] urls = url.split("[/]", -1);
			remove = urls[urls.length - 1];
		}
		return url.replace(remove, "report") + "/patientreport.aspx?" + "Patientid=" + Patientid + "&Visit_id=" + Visit_id + "&ThemeCode=" + ThemeCode;
	}


	/** 
	 获取360视图信息
	 
	 @param url
	 @param Patientid
	 @return 
	*/
	public final String Get360Url(String url, String Patientid)
	{
		//组装url地址
		return url;
	}

	/** 
	 根据用户编号获取用户科室权限列表
	 
	 @param userId
	 @return 
	*/
	public final DataTable GetDeptsByUserId(String userId)
	{
		return _BaseFuctions.GetDeptsByUserId(userId, Database[SysTypes.SYS]);
	}

	/** 
	 更新权限信息
	 
	 @param roleInfos
	 @param userId
	 @return 
	*/
	public final int UpdateUserVsDept(String deptInfos, String userId)
	{
		int i = 0;

		_BaseFuctions.UpdateUserVsDept(deptInfos, userId, Database[SysTypes.SYS]);

		String[] arr = deptInfos.split("[#]", -1);

		for (String array : arr)
		{
			SYS_USER_VS_DEPT user = new SYS_USER_VS_DEPT();

			user.UVD_ID = Database[SysTypes.SYS].GetNewID();
			user.USER_ID = userId;
			user.DEPT_CODE = array;
			i += Database[SysTypes.SYS].Insert(user);
		}

		return i;
	}

	/** 
	 修改责护管床
	 
	 @param model
	 @return 
	*/
	public final int ChangeDutyNurse(NursesBedEdit model)
	{
		IDatabase db = Database[SysTypes.HIS];
		//科室编号
		String deptCode = model.deptCode;
		int i = 0;
		//新增
		for (NursesBedDto ad : model.insertDutyNurse)
		{
			for (String item : ad.bedLabelList)
			{
				i += _BaseFuctions.UpdateDutyNurse(ad.dutyNurseId, ad.dutyNurseName, item, deptCode, db);
			}
		}

		//修改
		for (NursesBedDto up : model.updateDutyNurse)
		{
			//先删除
			i += _BaseFuctions.UpdateDutyNurse(up.dutyNurseId, up.dutyNurseName, "", deptCode, db);
			for (String item : up.bedLabelList)
			{
				i += _BaseFuctions.UpdateDutyNurse(up.dutyNurseId, up.dutyNurseName, item, deptCode, db);
			}
		}

		//删除
		for (NursesBedDto de : model.deleteDutyNurse)
		{
			i += _BaseFuctions.UpdateDutyNurse(de.dutyNurseId, de.dutyNurseName, "", deptCode, db);
		}
		return 1;
	}

	/** 
	 获取对应科室下的责医数据
	 
	 @param deptCode 科室号
	 @return 
	*/
	public final List<DutyDoc> GetDutyDoc(String deptCode)
	{
		List<DUTY_DOC_EXT> originDutyDOc = _BaseFuctions.GetDutyDoc(Database[SysTypes.SYS], deptCode).Select(e -> (DUTY_DOC_EXT)((e instanceof DUTY_DOC_EXT) ? e : null)).ToList();
		// 聚合数据
		List<DutyDoc> decodeData = DecodeOriginDutyDoc(originDutyDOc);
		return decodeData;
	}

	/** 
	 对原始的责医数据进行解析
	 
	 @param origin 原始的责医数据
	 @return 
	*/
	public final List<DutyDoc> DecodeOriginDutyDoc(List<DUTY_DOC_EXT> origin)
	{
		if (origin.isEmpty())
		{
			LogUtil.Warn("外部接口责医列表为空！");
			return new ArrayList<DutyDoc>();
		}
		Doctor tempVar = new Doctor();
		tempVar.name = doc.DOC_NAME;
		tempVar.bed = doc.BED;
		List<DutyDoc> decodeData = origin.GroupBy(e -> new {e.GROUP_ID, e.GROUP_NAME}).Select(group -> new DutyDoc() {groupName = group.Key.GROUP_NAME, doctor = group.Select(doc -> tempVar).ToList()}).ToList();

		return decodeData;
	}

		///#endregion



		///#region   科室信息相关
	/** 
	 获取科室列表
	 
	 @return 
	*/

	public final java.util.List<DeptView> GetDeptList()
	{
		return GetDeptList("01");
	}

	public final List<DeptView> GetDeptList(String hbranchCode)
	{
		return DatatableToModelList<DeptView>.ConvertToModel(_BaseFuctions.GetDeptList(Database[SysTypes.SYS], hbranchCode));
	}

	/** 
	 获取今明两天的检查安排数据
	 
	 @param deptCode
	 @return 
	*/
	public final List<OrigArrangements> GetArrangements(String deptCode)
	{
		java.time.LocalDateTime now = java.time.LocalDateTime.now().Date;
		return _BaseFuctions.GetArrangements(Database[SysTypes.SYS], deptCode, now, now.plusDays(2).plusSeconds(-1)).Select(x -> (OrigArrangements)((x instanceof OrigArrangements) ? x : null)).ToList();
	}

		///#endregion


		///#region   NANA第一屏护理项目信息相关

	/** 
	 获取病人项目
	 
	 @param wardCode 病区代码
	 @return 
	*/
	public final List<PatientProject> GetPatientProjectInfo(String wardCode, String identifyNames)
	{
		ArrayList<PatientProject> list = new ArrayList<PatientProject>();

		DataTable DtPatList = _BaseFuctions.GetPatientInfo(Database[SysTypes.HIS], wardCode);

		String pats = "";

		for (int h = 0; h < DtPatList.Rows.size(); h++)
		{
			if (h == 0)
			{
				pats = DtPatList.Rows[h]["PATIENT_ID"].toString();
			}
			else
			{
				pats += "','" + DtPatList.Rows[h]["PATIENT_ID"].toString();
			}
		}

		DataTable dtPatGuoMing = identifyNames.contains("敏") ? _BaseFuctions.GetPatGuoMing(pats, Database[SysTypes.SYS]) : new DataTable(); //只要阳性
		DataTable dtPatRiskScore = identifyNames.contains("压") || identifyNames.contains("跌") || identifyNames.contains("自") || identifyNames.contains("导") || identifyNames.contains("暴") || identifyNames.contains("伤") || identifyNames.contains("噎") || identifyNames.contains("离") || identifyNames.contains("塞") ? _BaseFuctions.GetPatsRisk(Database[SysTypes.SYS], pats) : new DataTable();
		DataTable dtPatYinShi = identifyNames.contains("食") ? _BaseFuctions.GetPatYinShi(pats, Database[SysTypes.SYS]) : new DataTable(); //患者最新的一个
		DataTable dtPatShouShu = identifyNames.contains("术") ? _BaseFuctions.GetPatShouShu(pats, Database[SysTypes.HIS]) : new DataTable(); //手术30天内，最新的一个
		DataTable dtPatTempGao = (identifyNames.contains("高") || identifyNames.contains("热")) ? _BaseFuctions.GetPatGaoTemp(pats, Database[SysTypes.SYS]) : new DataTable(); //3天内体温高于37.5
		DataTable dtPatGeLi = identifyNames.contains("隔") ? _BaseFuctions.GetPatGeLi(pats, Database[SysTypes.SYS]) : new DataTable(); //获取隔离患者表示
		DataTable dtPatPeiHu = identifyNames.contains("陪") ? _BaseFuctions.GetPatPeiHu(pats, Database[SysTypes.SYS]) : new DataTable();
		DataTable dtPatBaoMi = identifyNames.contains("密") ? _BaseFuctions.GetPatBaoMi(pats, Database[SysTypes.SYS]) : new DataTable();
		DataTable dtPatYS = identifyNames.contains("优") ? _BaseFuctions.GetPatYouShi(pats, Database[SysTypes.SYS]) : new DataTable(); //优势病种
		for (DataRow dr : DtPatList.Rows)
		{
			PatientProject model = new PatientProject();
			model.patientId = dr["PATIENT_ID"].toString();
			model.visitId = Integer.parseInt(dr["VISIT_ID"].toString());
			model.patientName = dr["PATIENT_NAME"].toString();
			model.bedLable = dr["BED_NO"].toString();
			ArrayList<StatusData> StatusDatas = new ArrayList<StatusData>();
			PatProject patProject = new PatProject();
			//组装过敏信息
			if (identifyNames.contains("敏"))
			{
				patProject.GuoMin(dtPatGuoMing, DtPatList, model.patientId, StatusDatas);
			}
			//组装病危病重信息
			if (identifyNames.contains("危") || identifyNames.contains("重"))
			{
				patProject.Status(dr["STATUS"].toString(), StatusDatas);
			}
			//组装压疮\跌倒评估信息
			if (identifyNames.contains("压") || identifyNames.contains("跌") || identifyNames.contains("自") || identifyNames.contains("导") || identifyNames.contains("暴") || identifyNames.contains("伤") || identifyNames.contains("噎") || identifyNames.contains("离") || identifyNames.contains("塞"))
			{
				patProject.PingGu(dtPatRiskScore, model.patientId, StatusDatas);
			}
			//组装饮食信息
			if (identifyNames.contains("食"))
			{
				patProject.YinShi(DtPatList, dtPatYinShi, model.patientId, StatusDatas);
			}
			//组装手术信息
			if (identifyNames.contains("术"))
			{
				patProject.ShouShu(dtPatShouShu, model.patientId, StatusDatas);
			}
			//组装高温信息
			if (identifyNames.contains("高"))
			{
				patProject.GaoWen(dtPatTempGao, model.patientId, StatusDatas);
			}
			//组装发热信息
			if (identifyNames.contains("热"))
			{
				patProject.FaRe(dtPatTempGao, model.patientId, StatusDatas);
			}
			//组装隔离信息
			if (identifyNames.contains("隔"))
			{
				patProject.GeLi(dtPatGeLi, model.patientId, StatusDatas);
			}
			//组装陪护信息
			if (identifyNames.contains("陪"))
			{
				patProject.PeiHu(dtPatPeiHu, model.patientId, StatusDatas);
			}
			//组装保密信息
			if (identifyNames.contains("密"))
			{
				patProject.BaoMi(dtPatBaoMi, model.patientId, StatusDatas);
			}
			//组装优势病种信息
			if (identifyNames.contains("优"))
			{
				patProject.YouShi(dtPatYS, model.patientId, StatusDatas);
			}
			if (StatusDatas.size() > 0)
			{
				model.status_data = StatusDatas.toArray(new StatusData[0]);
			}
			list.add(model);
		}

		return list;
	}

		///#endregion



		///#region   NANA第三屏固定项目信息相关



	/** 
	 获取护理任务
	 
	 @param deptCode
	 @param page
	 @param column
	 @param time
	 @param items
	 @return 
	*/
	public final List<PatMessagesView> GetProjects(String deptCode, String page, String column, String time, ArrayList<MessagesItem> itemList)
	{
		IDatabase db = Database[SysTypes.SYS];
		ArrayList<PatMessagesView> lst = new ArrayList<PatMessagesView>();
		String wardCode = deptCode.trim();
		String today = java.time.LocalDateTime.now().toString("yyyy-MM-dd") + " 00:00:00";
		// 获取病区病人列表
		List<PatientInfo> DtPatList = GetPatientInfo(wardCode);
		String pats = "";
		for (int h = 0; h < DtPatList.size(); h++)
		{
			if (h == 0)
			{
				pats = DtPatList.get(h).patientId.toString();
			}
			else
			{
				pats += "','" + DtPatList.get(h).patientId.toString();
			}
		}
		PatMessage pm = new PatMessage();
		// 只要阳性
		DataTable dtPatGuoMing = _BaseFuctions.GetPatGuoMing(pats, Database[SysTypes.SYS]);
		// 获取压疮跌倒名单
		List<PatsRisk> dtPatRiskScore = DatatableToModelList<PatsRisk>.ConvertToModel(_BaseFuctions.GetPatsRisk(db, pats));
		// 获取科室护士名单
		List<LoginUserView> GetNurseNameInfo = GetUserInfo("", "", wardCode, "");
		// 获得需要巡视名单
		List<OrderExec> DtXunshi = DatatableToModelList<OrderExec>.ConvertToModel(_BaseFuctions.GetOrdersExec(db, pats, today));
		for (MessagesItem item : itemList)
		{
			switch (item.itemKeyWords)
			{
				case "特级":
				case "一级":
				case "二级":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.HuLiJiBie(item.itemCode, item.itemKeyWords.trim(), DtPatList, GetNurseNameInfo, tempRef_lst);
						lst = tempRef_lst.argValue;
						break;
				}
				case "病危":
				case "病重":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst2 = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.BingQing(item.itemCode, item.itemKeyWords.trim(), DtPatList, GetNurseNameInfo, tempRef_lst2);
						lst = tempRef_lst2.argValue;
						break;
				}
				case "药物过敏":
				case "青霉素(+)":
				case "青霉素( )":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst3 = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.GuoMin(item.itemCode, item.itemKeyWords.trim(), DtPatList, GetNurseNameInfo, tempRef_lst3, dtPatGuoMing);
						lst = tempRef_lst3.argValue;
						break;
				}
				case "输液巡视":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst4 = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.Xunshilei(item.itemCode, item.itemKeyWords.trim(), DtXunshi, DtPatList, tempRef_lst4, _BaseFuctions, db);
						lst = tempRef_lst4.argValue;
						break;
				}
				case "防压疮":
				case "防跌倒/坠床":
				case "防导管滑脱":
				case "防自理":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst5 = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.PingGu(item.itemCode, item.itemKeyWords.trim(), DtPatList, dtPatRiskScore, GetNurseNameInfo, tempRef_lst5);
						lst = tempRef_lst5.argValue;
						break;
				}
				case "入院":
				{
						tangible.RefObject<ArrayList<PatMessagesView>> tempRef_lst6 = new tangible.RefObject<ArrayList<PatMessagesView>>(lst);
						pm.RuYuan(item.itemCode, item.itemKeyWords.trim(), DtPatList, GetNurseNameInfo, tempRef_lst6);
						lst = tempRef_lst6.argValue;
						break;
				}
			}
		}
		return lst;
	}

		///#endregion


		///#region   医嘱信息相关
	/** 
	 获取医嘱原文一致得所有医嘱
	 
	 @param orderText
	 @param deptCode
	 @param time
	 @return 
	*/
	public final List<DocsOrdersExecView> GetOrdersByOrderText(String orderText, String deptCode, java.time.LocalDateTime time)
	{
		return DatatableToModelList<DocsOrdersExecView>.ConvertToModel(_BaseFuctions.GetOrdersByOrderText(Database[SysTypes.SYS], orderText, deptCode, time));
	}

	/** 
	 获取病人列表某个科室某天的医嘱数据
	 
	 @param deptCode
	 @param pvId
	 @param startTime
	 @param day
	 @param recordTime
	 @param pvIdNoEx
	 @return 
	*/

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day, String recordTime, String pvIdNoEx, String startDateTime, String endDateTime)
	{
		return GetOrders(deptCode, pvId, startTime, day, recordTime, pvIdNoEx, startDateTime, endDateTime, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day, String recordTime, String pvIdNoEx, String startDateTime)
	{
		return GetOrders(deptCode, pvId, startTime, day, recordTime, pvIdNoEx, startDateTime, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day, String recordTime, String pvIdNoEx)
	{
		return GetOrders(deptCode, pvId, startTime, day, recordTime, pvIdNoEx, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day, String recordTime)
	{
		return GetOrders(deptCode, pvId, startTime, day, recordTime, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day)
	{
		return GetOrders(deptCode, pvId, startTime, day, null, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime)
	{
		return GetOrders(deptCode, pvId, startTime, null, null, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode, String pvId)
	{
		return GetOrders(deptCode, pvId, null, null, null, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetOrders(String deptCode)
	{
		return GetOrders(deptCode, null, null, null, null, null, null, null, null);
	}

	public final List<DocsOrdersExecView> GetOrders(String deptCode, String pvId, String startTime, String day, String recordTime, String pvIdNoEx, String startDateTime, String endDateTime, String datasourceType)
	{
		return DatatableToModelList<DocsOrdersExecView>.ConvertToModel(_BaseFuctions.GetOrders(Database[SysTypes.SYS], startTime, deptCode, pvId, day, recordTime, pvIdNoEx, startDateTime, endDateTime, datasourceType));
	}

	/** 
	 根据patientId + visitId + orderNo三者结合的数据查询文书指定医嘱
	 查询时间：不限
	 
	 @param deptCode 科室号
	 @param pvIdAndOrderNo patientId + visitId + orderNo 三者拼接起来的数据
	 @return 
	*/
	public final List<DocsOrdersExecView> GetOrders(String deptCode, List<String> pvIdAndOrderNo)
	{
		// 只要开嘱时间最早的那一条
		List<DocsOrdersExecView> orders = DatatableToModelList<DocsOrdersExecView>.ConvertToModel(_BaseFuctions.GetOrders(Database[SysTypes.SYS], deptCode, pvIdAndOrderNo));
		orders = orders.OrderBy(e -> e.start_date_time).GroupBy(e -> new {e.patient_id, e.visit_id, e.order_no}).Select(e -> e.FirstOrDefault()).ToList();

		return orders;
	}

	/** 
	 获取病人列表多个科室某天的医嘱数据
	 
	 @param deptCode
	 @param pvId
	 @param startTime
	 @param day
	 @param recordTime
	 @param pvIdNoEx
	 @return 
	*/

	public final java.util.List<DocsOrdersExecView> GetDeptOrders(java.util.ArrayList<String> deptCodes, String pvId, String startTime, String day, String recordTime)
	{
		return GetDeptOrders(deptCodes, pvId, startTime, day, recordTime, null);
	}

	public final java.util.List<DocsOrdersExecView> GetDeptOrders(java.util.ArrayList<String> deptCodes, String pvId, String startTime, String day)
	{
		return GetDeptOrders(deptCodes, pvId, startTime, day, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetDeptOrders(java.util.ArrayList<String> deptCodes, String pvId, String startTime)
	{
		return GetDeptOrders(deptCodes, pvId, startTime, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetDeptOrders(java.util.ArrayList<String> deptCodes, String pvId)
	{
		return GetDeptOrders(deptCodes, pvId, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetDeptOrders(java.util.ArrayList<String> deptCodes)
	{
		return GetDeptOrders(deptCodes, null, null, null, null, null);
	}

	public final java.util.List<DocsOrdersExecView> GetDeptOrders()
	{
		return GetDeptOrders(null, null, null, null, null, null);
	}

	public final List<DocsOrdersExecView> GetDeptOrders(ArrayList<String> deptCodes, String pvId, String startTime, String day, String recordTime, String pvIdNoEx)
	{
		return DatatableToModelList<DocsOrdersExecView>.ConvertToModel(_BaseFuctions.GetDeptOrders(Database[SysTypes.SYS], startTime, deptCodes, pvId, day, recordTime, pvIdNoEx));
	}


	/** 
	 NANA新增任务
	 
	 @param freqDict
	 @param advice
	 @param referenceTime
	 @param aId
	 @param remarks
	 @return 
	*/
	public final String SplitOrders(String freqDictJson, String adviceJson, java.time.LocalDateTime referenceTime, java.math.BigDecimal aId, String remarks)
	{
		//先拆分任务
		SplitOrdersUtil ordersUtil = new SplitOrdersUtil();
		List<FreqNormalDictDto> freqDict = (ArrayList<FreqNormalDictDto>) JsonUtil.Json2Object(freqDictJson, ArrayList<FreqNormalDictDto>.class);
		AddAiAdviceDto advice = (AddAiAdviceDto)JsonUtil.Json2Object(adviceJson, AddAiAdviceDto.class);
		List<AiTaskDetail> aiTaskDetails = ordersUtil.SplitOrders(freqDict, advice, referenceTime, aId, remarks, Database, _BaseFuctions);
		IDatabase sys = Database[SysTypes.SYS];
		var patientName = advice.patientName;
		//处理子任务
		for (AiTaskDetail detail : aiTaskDetails)
		{
			detail.detailId = sys.GetNewID();
			detail.mergeShow = "0";
			//化疗任务，添加药品简称到药品备注,并标记查询任务合并显示
			if ("3".equals(detail.orderRemarkType))
			{
				detail.medicineRemark = detail.orderRemark;
				detail.mergeShow = "1";
			}
			_BaseFuctions.SaveOredersExeLog(detail, patientName, sys);
		}
		//拆分后的医嘱任务保存至拆分表中
		return JsonUtil.Object2JsonString(aiTaskDetails);
	}

	/** 
	 HIS医嘱拆分，并保存到文书医嘱执行表
	 
	 @return 
	*/
	public final void HlwsSplitOrders()
	{
		// 医嘱类型
		final String orderType = "'0','1'";
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		java.time.LocalDateTime startTime = (java.time.LocalDateTime)now.toString("yyyy-MM-dd 00:00:00");
		java.time.LocalDateTime endTime = (java.time.LocalDateTime)now.toString("yyyy-MM-dd 23:59:59");
		// 科室列表
		DataTable dtDept = _BaseFuctions.GetDeptList(Database[SysTypes.SYS], "01");
		if (dtDept.Rows.size() <= 0)
		{
			return;
		}
		String deptCodesString = tangible.DotNetToJavaStringHelper.join(",", dtDept.AsEnumerable().Select(row -> "'" + row["DEPT_CODE"] + "'"));
		// 拆分全科医嘱
		HlwsSplitOrder da = new HlwsSplitOrder();
		da.HlwsSplitOrders(startTime, endTime, orderType, deptCodesString, _BaseFuctions, Database);
	}


	/** 
	 医嘱回写操作
	 
	 @param model
	 @return 
	*/
	public final int SaveSynNaNaOrdersRec(SyncOrderDto model)
	{
		return _BaseFuctions.SaveSynNaNaOrdersRec(model, Database[SysTypes.SYS]);
	}

	/** 
	 修改医嘱拆分记录
	 
	 @param patientId
	 @param orderNo
	 @param execTime
	 @param oldExccTime
	 @return 
	*/
	public final int UpdateOredersExeLog(String patientId, String orderNo, String execTime, String oldExccTime)
	{
		return _BaseFuctions.UpdateOredersExeLog(patientId, orderNo, execTime, oldExccTime, Database[SysTypes.SYS]);
	}

	/** 
	 删除nana新增原始医嘱及已拆分医嘱
	 
	 @param pid
	 @param vid
	 @param orderNo
	 @return 
	*/
	public final int DeleteDocsOrder(String pid, String vid, String orderNo)
	{
		_BaseFuctions.DeleteVhisOrder(pid, vid, orderNo, Database[SysTypes.HIS]);
		return _BaseFuctions.DeleteDocsOrder(pid, vid, orderNo, Database[SysTypes.SYS]);
	}
	/** 
	 根据ordeNo获取相关医嘱信息
	 
	 @param patientId 病人ID
	 @param visitId 住院次数
	 @param orderNo 医嘱号
	 @param execTime 医嘱执行时间
	 @return 
	*/
	public final DataTable GetOrderExeLog(String patientId, String visitId, String orderNo, String execTime)
	{
		return _BaseFuctions.GetOrderExeLog(patientId, visitId, orderNo, execTime, Database[SysTypes.SYS]);
	}
		///#endregion


		///#region   手术信息相关

	/** 
	 获取手术信息
	 20-02-19 此处是从HIS取数据，非对接的手麻系统，目前也不清楚该方法对应在了哪块业务
	 
	 @param deptCode
	 @return 
	*/
	public final List<OperationRec> GetOperationRecs(String deptCode)
	{
		DataTable patList = _BaseFuctions.GetPatientInfo(Database[SysTypes.HIS], deptCode);
		String pats = tangible.DotNetToJavaStringHelper.join(",", patList.AsEnumerable().Select(e -> "'" + e["PATIENT_ID"] + "'"));
		if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(pats))
		{
			int length = pats.length();
			pats = pats.substring(1, 1 + length - 2);
		}
		DataTable dtPatShouShu = _BaseFuctions.GetPatShouShu(pats, Database[SysTypes.HIS]); //手术30天内，最新的一个
		ArrayList<OperationRec> list = new ArrayList<OperationRec>();
		for (DataRow dr : dtPatShouShu.Rows)
		{
			OperationRec model = new OperationRec();
			model.operId = dr["OPER_ID"].toString();
			model.patientId = dr["PATIENT_ID"].toString();
			model.visitId = (int)dr["VISIT_ID"];
			model.operation = dr["OPERATION"].toString();
			model.surgeon = "";
			model.startDateTime = (java.time.LocalDateTime)dr["SCHEDULED_DATE_TIME"];
			list.add(model);
		}
		return list;
	}

	/** 
	 获取多个科室今明两天的手术信息
	 这里对接的是手麻系统，从文书获取 IOTBEDSIDE-13111
	 
	 @param deptCode 科室列表
	 @return 
	*/
	public final List<SurgicalArrangementsView> GetSurgicalArrangements(ArrayList<String> deptCode)
	{
		DataTable arrangements = _BaseFuctions.GetSurgicalArrangements(Database[SysTypes.SYS], deptCode);
		List<SurgicalArrangementsView> result = new ArrayList<SurgicalArrangementsView>();
		boolean isExitColumn = false;
		if (arrangements.Columns.Contains("OPER_PREPAR"))
		{
			isExitColumn = true;
		}
		for (DataRow dr : arrangements.Rows)
		{
			SurgicalArrangementsView model = new SurgicalArrangementsView();
			model.surgicalCode = dr["SURGICAL_CODE"].toString();
			model.deptCode = dr["DEPT_CODE"].toString();
			model.opTime = (java.time.LocalDateTime)dr["OP_TIME"];
			model.roomNumber = dr["ROOM_NUMBER"].toString();
			model.opTableNumber = dr["OP_TABLE_NUMBER"].toString();
			model.patientId = dr["PATIENT_ID"].toString();
			model.visitId = (int)dr["VISIT_ID"];
			model.patientName = dr["PATIENT_NAME"].toString();
			model.bedNo = dr["BED_NO"].toString();
			model.inpNo = dr["INP_NO"].toString();
			model.surgicalName = dr["SURGICAL_NAME"].toString();
			model.operatorName = dr["OPERATOR_NAME"].toString();
			model.status = dr["STATUS"].toString();
			model.preSurgicalPrepareText = isExitColumn ? dr["OPER_PREPAR"].toString() : "";
			result.add(model);
		}

		return result;
	}
		///#endregion

		///#region   交班报告相关

	/** 
	 获取交班信息2天内
	 
	 @param deptCode
	 @return 
	*/
	public final List<ShiftMain> GetShift(String deptCode)
	{
		DataTable dt = _BaseFuctions.GetShift(deptCode, Database[SysTypes.SYS]);
		DataTable detail = _BaseFuctions.GetShiftDetail(deptCode, Database[SysTypes.SYS]);
		ArrayList<ShiftMain> list = new ArrayList<ShiftMain>();
		for (DataRow dr : dt.Rows)
		{
			ShiftMain model = new ShiftMain();
			model.rptId = dr["rpt_id"].toString();
			model.title = dr["rpt_date"].toString() + " " + dr["shift_name"].toString();
			model.wardContent = dr["ward_content"].toString();

			//班次信息组装
			ArrayList<ShiftRec> shiftRecList = new ArrayList<ShiftRec>();
			ShiftRec tempVar = new ShiftRec();
			tempVar.key = "科室";
			tempVar.value = dr["nursing_unit_name"].toString();
			shiftRecList.add(tempVar);
			ShiftRec tempVar2 = new ShiftRec();
			tempVar2.key = "总责任护士";
			tempVar2.value = dr["head_duty_nurse_name"].toString();
			shiftRecList.add(tempVar2);
			ShiftRec tempVar3 = new ShiftRec();
			tempVar3.key = "值班医生";
			tempVar3.value = dr["duty_doctor"].toString();
			shiftRecList.add(tempVar3);
			ShiftRec tempVar4 = new ShiftRec();
			tempVar4.key = "二线医生";
			tempVar4.value = dr["second_doctor"].toString();
			shiftRecList.add(tempVar4);

			//病区信息组装
			ArrayList<DeptRec> deptRecList = new ArrayList<DeptRec>();
			DeptRec tempVar5 = new DeptRec();
			tempVar5.key = "病人总数";
			tempVar5.value = dr["current_sum"].toString();
			deptRecList.add(tempVar5);
			DeptRec tempVar6 = new DeptRec();
			tempVar6.key = "特殊护理";
			tempVar6.value = dr["special_nurse"].toString();
			deptRecList.add(tempVar6);
			DeptRec tempVar7 = new DeptRec();
			tempVar7.key = "一级护理";
			tempVar7.value = dr["one_level"].toString();
			deptRecList.add(tempVar7);
			DeptRec tempVar8 = new DeptRec();
			tempVar8.key = "二级护理";
			tempVar8.value = dr["two_level"].toString();
			deptRecList.add(tempVar8);
			DeptRec tempVar9 = new DeptRec();
			tempVar9.key = "三级护理";
			tempVar9.value = dr["three_level"].toString();
			deptRecList.add(tempVar9);
			DeptRec tempVar10 = new DeptRec();
			tempVar10.key = "出院";
			tempVar10.value = dr["discharge"].toString();
			deptRecList.add(tempVar10);
			DeptRec tempVar11 = new DeptRec();
			tempVar11.key = "入院";
			tempVar11.value = dr["newer"].toString();
			deptRecList.add(tempVar11);
			DeptRec tempVar12 = new DeptRec();
			tempVar12.key = "转入";
			tempVar12.value = dr["trans_in"].toString();
			deptRecList.add(tempVar12);
			DeptRec tempVar13 = new DeptRec();
			tempVar13.key = "转出";
			tempVar13.value = dr["trans_out"].toString();
			deptRecList.add(tempVar13);
			DeptRec tempVar14 = new DeptRec();
			tempVar14.key = "死亡";
			tempVar14.value = dr["death"].toString();
			deptRecList.add(tempVar14);
			DeptRec tempVar15 = new DeptRec();
			tempVar15.key = "病危";
			tempVar15.value = dr["critically"].toString();
			deptRecList.add(tempVar15);
			DeptRec tempVar16 = new DeptRec();
			tempVar16.key = "病重";
			tempVar16.value = dr["seriously"].toString();
			deptRecList.add(tempVar16);
			DeptRec tempVar17 = new DeptRec();
			tempVar17.key = "今日手术";
			tempVar17.value = dr["operation"].toString();
			deptRecList.add(tempVar17);

			//病情摘要
			ArrayList<Illness> illnessList = new ArrayList<Illness>();
			String illnessFilter = String.format("rpt_id=%1$s and category='病情摘要'", model.rptId);
			DataRow[] illness = detail.Select(illnessFilter);
			for (DataRow item : illness)
			{
				Illness illnessModel = new Illness();
				illnessModel.recId = item["rec_id"].toString();
				illnessModel.subclass = item["subclass"].toString();
				illnessModel.classColor = item["subclass_color"].toString();
				illnessModel.patientId = item["patient_id"].toString();
				illnessModel.visitId = item["visit_id"].toString();
				illnessModel.bedNo = item["bed_no"].toString();
				illnessModel.patientName = item["patient_name"].toString();
				illnessModel.diagnosis = item["diagnosis"].toString();
				illnessModel.contentValue = item["content_value"].toString();
				illnessModel.dutyNurse = item["duty_nuse"].toString();
				illnessModel.dutyDoctor = dr["duty_doctor"].toString();
				illnessModel.sex = item["sex"].toString();
				illnessModel.age = item["age"].toString();
				illnessModel.situation = item["situation"].toString();
				illnessModel.advice = item["advice"].toString();
				illnessModel.background = item["background"].toString();

				illnessModel.evaluate = new ArrayList<Illness.EvaluateModel>();
				String emValue = item["evaluate"].toString();
				//此处代码现场同事需结合具体的数据格式来处理
				if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(emValue))
				{
					emValue = emValue.replace("；", ";");
					String[] slist = emValue.split("[;]", -1);
					for (String s : slist)
					{
						if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(s))
						{
							Illness.EvaluateModel em = new Illness.EvaluateModel();
							if (s.contains("中危"))
							{
								em.state = 1;
							}
							else if (s.contains("高危"))
							{
								em.state = 2;
							}
							else
							{
								em.state = 0;
							}
							em.value = s;
							illnessModel.evaluate.Add(em);
						}
					}
				}
				illnessList.add(illnessModel);
			}

			//交代事项
			ArrayList<Matters> mattersList = new ArrayList<Matters>();
			String mattersFilter = String.format("rpt_id=%1$s and category='交代事项'", model.rptId);
			DataRow[] matters = detail.Select(mattersFilter);
			for (DataRow item : matters)
			{
				Matters matterModel = new Matters();
				matterModel.recId = item["rec_id"].toString();
				matterModel.subclass = item["subclass"].toString();
				matterModel.classColor = item["subclass_color"].toString();
				matterModel.mattersObjects = item["matters_objects"].toString();
				matterModel.mattersValue = item["content_value"].toString();
				mattersList.add(matterModel);
			}

			model.deptRecList = deptRecList;
			model.illnessList = illnessList;
			model.mattersList = mattersList;
			model.shiftRecList = shiftRecList;
			list.add(model);
		}
		return list;
	}

		///#endregion


	/** 
	 获取该科室下的病区名称
	 
	 @param deptCode
	 @return 
	*/
	public final List<String> GetWrdNamesByDept(String deptCode)
	{
		List<String> list = _BaseFuctions.GetWrdNamesByDept(Database[SysTypes.HIS], deptCode).ToList();
		return list;
	}

	/** 
	 获取出院患者基本信息
	 
	 @param deptCode 科室code
	 @param wardCode 病区code
	 @param isBed 是否展示空床标记默认不展示
	 @param isMaternity 是否展示产妇信息
	 @return 
	*/

	public final java.util.ArrayList<PatientInfo> GetLeavePatientInfo(String deptCode, String wardCode, boolean isBed)
	{
		return GetLeavePatientInfo(deptCode, wardCode, isBed, false);
	}

	public final java.util.ArrayList<PatientInfo> GetLeavePatientInfo(String deptCode, String wardCode)
	{
		return GetLeavePatientInfo(deptCode, wardCode, false, false);
	}

	public final java.util.ArrayList<PatientInfo> GetLeavePatientInfo(String deptCode)
	{
		return GetLeavePatientInfo(deptCode, null, false, false);
	}

	public final ArrayList<PatientInfo> GetLeavePatientInfo(String deptCode, String wardCode, boolean isBed, boolean isMaternity)
	{
		IDatabase hisDb = Database[SysTypes.HIS];
		DataTable patientList = _BaseFuctions.GetLeavePatientInfo(hisDb, deptCode, wardCode);
		ArrayList<PatientInfo> result = new ArrayList<PatientInfo>();
		for (DataRow row : patientList.Rows)
		{
			PatientInfo model = SetPatientInfo(row);
			tangible.RefObject<PatientInfo> tempRef_model = new tangible.RefObject<PatientInfo>(model);
			SetCardColor(row, tempRef_model);
			model = tempRef_model.argValue;
			model.customCardColor = CheckColorLegal(model.customCardColor);
			model.daysAfterOperation = row["DAYS_AFTER_OPERATION"].toString();
			model.dutyNurseId = row["NURSE_IN_CHARGE_ID"].toString();
			result.add(model);
		}
		return result;
	}

	/** 
	 获取转入科室患者基本信息
	 
	 @param deptCode 科室code
	 @param wardCode 病区code
	 @param isBed 是否展示空床标记默认不展示
	 @param isMaternity 是否展示产妇信息
	 @return 
	*/

	public final java.util.ArrayList<PatientInfo> GetInPatientInfo(String deptCode, String wardCode, boolean isBed)
	{
		return GetInPatientInfo(deptCode, wardCode, isBed, false);
	}

	public final java.util.ArrayList<PatientInfo> GetInPatientInfo(String deptCode, String wardCode)
	{
		return GetInPatientInfo(deptCode, wardCode, false, false);
	}

	public final java.util.ArrayList<PatientInfo> GetInPatientInfo(String deptCode)
	{
		return GetInPatientInfo(deptCode, null, false, false);
	}

	public final ArrayList<PatientInfo> GetInPatientInfo(String deptCode, String wardCode, boolean isBed, boolean isMaternity)
	{
		IDatabase hisDb = Database[SysTypes.HIS];
		DataTable patientList = _BaseFuctions.GetInPatientInfo(hisDb, deptCode, wardCode);
		ArrayList<PatientInfo> result = new ArrayList<PatientInfo>();
		for (DataRow row : patientList.Rows)
		{
			PatientInfo model = SetPatientInfo(row);
			tangible.RefObject<PatientInfo> tempRef_model = new tangible.RefObject<PatientInfo>(model);
			SetCardColor(row, tempRef_model);
			model = tempRef_model.argValue;
			model.customCardColor = CheckColorLegal(model.customCardColor);
			model.daysAfterOperation = row["DAYS_AFTER_OPERATION"].toString();
			model.dutyNurseId = row["NURSE_IN_CHARGE_ID"].toString();
			result.add(model);
		}
		return result;
	}

	/** 
	 获取转出科室患者基本信息
	 
	 @param deptCode 科室code
	 @param wardCode 病区code
	 @param isBed 是否展示空床标记默认不展示
	 @param isMaternity 是否展示产妇信息
	 @return 
	*/

	public final java.util.ArrayList<PatientInfo> GetOutPatientInfo(String deptCode, String wardCode, boolean isBed)
	{
		return GetOutPatientInfo(deptCode, wardCode, isBed, false);
	}

	public final java.util.ArrayList<PatientInfo> GetOutPatientInfo(String deptCode, String wardCode)
	{
		return GetOutPatientInfo(deptCode, wardCode, false, false);
	}

	public final java.util.ArrayList<PatientInfo> GetOutPatientInfo(String deptCode)
	{
		return GetOutPatientInfo(deptCode, null, false, false);
	}

	public final ArrayList<PatientInfo> GetOutPatientInfo(String deptCode, String wardCode, boolean isBed, boolean isMaternity)
	{
		IDatabase hisDb = Database[SysTypes.HIS];
		DataTable patientList = _BaseFuctions.GetOutPatientInfo(hisDb, deptCode, wardCode);
		ArrayList<PatientInfo> result = new ArrayList<PatientInfo>();
		for (DataRow row : patientList.Rows)
		{
			PatientInfo model = SetPatientInfo(row);
			tangible.RefObject<PatientInfo> tempRef_model = new tangible.RefObject<PatientInfo>(model);
			SetCardColor(row, tempRef_model);
			model = tempRef_model.argValue;
			model.customCardColor = CheckColorLegal(model.customCardColor);
			model.daysAfterOperation = row["DAYS_AFTER_OPERATION"].toString();
			model.dutyNurseId = row["NURSE_IN_CHARGE_ID"].toString();
			result.add(model);
		}
		return result;
	}

}