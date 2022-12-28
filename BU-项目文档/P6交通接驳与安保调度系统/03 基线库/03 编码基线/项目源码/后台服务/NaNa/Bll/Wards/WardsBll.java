package NaNa.Bll.Wards;

import java.util.*;
import mHealth.Generic.Bll.*;
import mHealth.Generic.Model.AutoMapper.*;
import Model.Base.Entity.Ext.*;
import NaNa.Service.Wards.*;
import External.Service.*;
import External.Model.Entity.*;
import Model.Base.EntityDto.Config.*;
import Model.Base.EntityDto.Commons.*;
import mHealth.Model.Base.Ext.*;
import Model.Base.EntityDto.Wards.*;
import NaNa.Service.Config.*;
import NaNa.Util.*;
import mHealth.Generic.Utils.*;

public class WardsBll extends BaseBll
{
		///#region   初始化信息
	private WardsService _wardsService;
	private ExternalService _externalService;
	private ConfigService _configService;

	/** 
	 构造函数实例化对象
	*/
	public WardsBll()
	{
		//初始化server层
		_wardsService = new WardsService();
		_externalService = new ExternalService();
		_configService = new ConfigService();
	}
	/** 
	 初始化函数赋值用户信息和数据库连接
	*/
	@Override
	public void init()
	{
		SetParamsServer(_wardsService);
		SetParamsServer(_externalService);
		SetParamsServer(_configService);
	}
		///#endregion

	/** 
	 保存空床备注（新增和修改）
	*/
	public final Object SaveEmptyBedNote()
	{
		EMPTY_BED_NOTES_EXT emptyNote = new EMPTY_BED_NOTES_EXT();
		emptyNote.BED_NO = GetParams("bedNo");
		emptyNote.NOTE = GetParams("note");
		emptyNote.WARD_CODE = GetParams("wardCode");
		emptyNote.WARD_NAME = GetParams("wardName");
		emptyNote.DEPT_CODE = GetParams("deptCode");
		emptyNote.DEPT_NAME = GetParams("deptName");
		// 先判断是否存在
		EMPTY_BED_NOTES_EXT bedNote = _wardsService.GetBedNote(emptyNote.BED_NO, emptyNote.WARD_CODE, emptyNote.DEPT_CODE);
		// 根据存在与否决定是更新或者
		if (bedNote == null)
		{
			return _wardsService.InsertWardNote(emptyNote);
		}

		emptyNote.ID = bedNote.ID;
		return _wardsService.UpdateWardNote(emptyNote);
	}

	/** 
	 获取科室标签
	 
	 @return 
	*/
	public final Page<AiProjectDictDto> GetWardLabel()
	{
		String wardCode = GetParams("wardCode");
		int pageNo = Integer.parseInt(GetParams("pageNo"));
		int pageSize = Integer.parseInt(GetParams("pageSize"));
		String itemName = GetParams("itemName");
		String columnName = GetParams("columnName");
		String isActive = GetParams("isActive");

		int total = _wardsService.GetLabelCount(wardCode, itemName, columnName, isActive);
		// 获取该科室的标签信息
		List<AiProjectDictDto> deptProjects = AutoMapperHelper.<NANA_AI_PROJECT_DICT_EXT, AiProjectDictDto>MapToList(_wardsService.GetWardLabel(wardCode, itemName, columnName, isActive, pageSize, pageNo));

		Page<AiProjectDictDto> tempVar = new Page<AiProjectDictDto>();
		tempVar.Total = total;
		tempVar.PageNo = pageNo;
		tempVar.PageSize = pageSize;
		tempVar.List = deptProjects;
		return tempVar;
	}

	/** 
	 获取患者基本信息
	*/
	public final List<PatientInfo> GetPatientInfo()
	{
		String deptCode = GetParams("wardCode");
		String wardCode = GetParams("deptCode");
		//获取配置项目(病区特色)
		List<NANA_CONFIG_VALUE_EXT> configList = _configService.GetConfigValues(deptCode + ":::01", "WARD_FEATURE");
		String configValue = "";
		if (configList != null && configList.size() > 0)
		{
			configValue = ((configList.get(0).CONFIG_VALUE) != null) ? configList.get(0).CONFIG_VALUE : "";
		}
		ArrayList<PatientInfo> patientList = new ArrayList<PatientInfo>();
		if (configValue.contains("产科病区母子共用卡片"))
		{
			patientList = _externalService.GetPatientInfo(deptCode, wardCode, true, true, false);
		}
		else if (configValue.contains("产科病区母子分卡片"))
		{
			patientList = _externalService.GetPatientInfo(deptCode, wardCode, true, true, true);
		}
		else
		{
			patientList = _externalService.GetPatientInfo(deptCode, wardCode, true, false);
		}
		return _wardsService.assembleBedNote(patientList, wardCode, deptCode);
	}

	/** 
	 获取病人项目
	*/
	public final List<PatientProject> GetPatientProjectInfo()
	{
		String wardCode = GetParams("wardCode");
		String identifyNames = GetParams("identifyNames");
		List<PatientProject> patList = _externalService.GetPatientProjectInfo(wardCode, identifyNames);
		return patList;
	}

	public final String GetRemand()
	{
		String wardCode = GetParams("wardNo");
		List<NANA_DEPT_REMAND_EXT> list = _wardsService.GetRemand(wardCode);
		return list.size() > 0 ? ((list.get(0).REMAND) != null) ? list.get(0).REMAND : "" : "";
	}


	public final Object SaveRemand()
	{
		String wardCode = GetParams("wardNo");
		String remand = GetParams("announcement");
		NANA_DEPT_REMAND_EXT model = new NANA_DEPT_REMAND_EXT();
		model.DEPT_CODE = wardCode;
		model.REMAND = remand;
		return _wardsService.SaveRemand(model);
	}

	/** 
	 获取检查安排
	 
	 @return 
	*/
	public final Arrangements GetArrangements()
	{
		String deptCode = GetParams("deptCode");
		List<OrigArrangements> origArrangements = _externalService.GetArrangements(deptCode).OrderBy(e -> e.TIME).ToList();

		List<String> headers = origArrangements.Where(e -> !tangible.DotNetToJavaStringHelper.isNullOrEmpty(e.ITEM)).Select(e -> e.ITEM).Distinct().ToList();
		List<Patient> patients = origArrangements.GroupBy(e -> new {e.TIME, e.NAME, e.BED_NO, e.INP_NO, e.CHECK_ITEM}).Select(group ->
		{
				KVObject tempVar = new KVObject();
				tempVar.key = e.ITEM;
				tempVar.value = e.VALUE;
				ArrayList<KVObject> kv = group.Where(e -> !tangible.DotNetToJavaStringHelper.isNullOrEmpty(e.ITEM)).Select(e -> tempVar).ToList();
				// 补齐当前类没有的数据
				List<String> list = headers.Except(kv.Select(e -> e.key.toString())).ToList();
				if (CollectionUtil.IsNotEmpty(list))
				{
					KVObject tempVar2 = new KVObject();
					tempVar2.key = e;
					tempVar2.value = "";
					List<KVObject> notInclued = list.Select(e -> tempVar2).ToList();
					kv.addAll(notInclued);
				}
				Patient patient = new Patient();
				patient.time = group.Key.TIME.toString("MM/dd HH:mm");
				patient.bedNo = group.Key.BED_NO;
				patient.inpNo = group.Key.INP_NO;
				patient.name = group.Key.NAME;
				patient.checkitem = group.Key.CHECK_ITEM;
				patient.others = kv;

				return patient;
		}).ToList();

		Arrangements tempVar3 = new Arrangements();
		tempVar3.headers = headers;
		tempVar3.patients = patients;
		return tempVar3;
	}

		///#region 床位类型(传染病类型)和床位信息  ZHBFDEVIMP-306
	public final Object SaveBedTypesAndBedInfos()
	{
		String userId = GetParams("userId");
		String deleteBedTypes = GetParams("deleteBedTypes");
		String saveBedTypes = GetParams("saveBedTypes");
		String deleteBedInfos = GetParams("deleteBedInfos");
		String saveBedInfos = GetParams("saveBedInfos");
		ArrayList<BedTypeDto> deleteBts = (ArrayList<BedTypeDto>)JsonUtil.Json2Object(deleteBedTypes, ArrayList<BedTypeDto>.class);
		ArrayList<BedTypeDto> saveBts = (ArrayList<BedTypeDto>)JsonUtil.Json2Object(saveBedTypes, ArrayList<BedTypeDto>.class);
		ArrayList<BedInfoDto> deleteBis = (ArrayList<BedInfoDto>)JsonUtil.Json2Object(deleteBedInfos, ArrayList<BedInfoDto>.class);
		ArrayList<BedInfoDto> saveBis = (ArrayList<BedInfoDto>)JsonUtil.Json2Object(saveBedInfos, ArrayList<BedInfoDto>.class);
		for (BedTypeDto item : deleteBts)
		{
			NANA_BED_TYPE_EXT bt = new NANA_BED_TYPE_EXT();
			bt.ID = (java.math.BigDecimal)item.id;
			bt.NAME = item.name;
			bt.DEPT_CODE = item.deptCode;
			bt.IS_DELETED = "Y";
			bt.UPDATER_ID = userId;
			bt.UPDATE_TIME = java.time.LocalDateTime.now();
			_wardsService.UpdateBedType(bt);
		}
		for (BedTypeDto item : saveBts)
		{
			NANA_BED_TYPE_EXT bt = new NANA_BED_TYPE_EXT();
			bt.NAME = item.name;
			bt.DEPT_CODE = item.deptCode;
			if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(item.id))
			{
				bt.CREATE_ID = userId;
				bt.CREATE_TIME = java.time.LocalDateTime.now();
				_wardsService.InsertBedType(bt);
			}
			else
			{
				bt.ID = (java.math.BigDecimal)item.id;
				bt.UPDATER_ID = userId;
				bt.UPDATE_TIME = java.time.LocalDateTime.now();
				_wardsService.UpdateBedType(bt);
			}
		}
		if (deleteBis.size() > 0)
		{
			String deleteBedInfoIds = "'" + tangible.DotNetToJavaStringHelper.join("','", deleteBis.Select(i -> i.id)) + "'";
			_wardsService.DeleteBedInfos(deleteBedInfos);
		}
		for (BedInfoDto item : saveBis)
		{
			NANA_BED_INFO_EXT bi = new NANA_BED_INFO_EXT();
			bi.BED_TYPE = item.typename;
			bi.DEPT_CODE = item.deptCode;
			bi.BED_LABEL = item.bedLabel;
			if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(item.id))
			{
				bi.CREATE_ID = userId;
				bi.CREATE_TIME = java.time.LocalDateTime.now();
				_wardsService.InsertBedInfo(bi);
			}
			else
			{
				bi.ID = (java.math.BigDecimal)item.id;
				bi.UPDATER_ID = userId;
				bi.UPDATE_TIME = java.time.LocalDateTime.now();
				_wardsService.UpdateBedInfo(bi);
			}
		}
		return 0;
	}
	public final Object QueryBedTypes()
	{
		String deptCode = GetParams("deptCode");
		var bts = _wardsService.QueryBedTypes(deptCode);
		ArrayList<BedTypeDto> btDtos = new ArrayList<BedTypeDto>();
		for (var item : bts)
		{
			BedTypeDto tempVar = new BedTypeDto();
			tempVar.id = item.ID != null? item.ID.toString():"";
			tempVar.name = item.NAME;
			tempVar.deptCode = item.DEPT_CODE;
			btDtos.add(tempVar);
		}
		return btDtos;
	}
	public final Object QueryBedInfos()
	{
		String deptCode = GetParams("deptCode");
		var bis = _wardsService.QueryBedInfos(deptCode);
		ArrayList<BedInfoDto> biDtos = new ArrayList<BedInfoDto>();
		for (var item : bis)
		{
			BedInfoDto tempVar = new BedInfoDto();
			tempVar.id = item.ID != null ? item.ID.toString() : "";
			tempVar.deptCode = item.DEPT_CODE;
			tempVar.typename = item.BED_TYPE;
			tempVar.bedLabel = item.BED_LABEL;
			biDtos.add(tempVar);
		}
		return biDtos;
	}
	public final Object GetWrdNamesByDept()
	{
		String deptCode = GetParams("deptCode");
		List<String> bis = _externalService.GetWrdNamesByDept(deptCode);
		return bis;
	}
		///#endregion

	/** 
	 获取出院患者基本信息
	*/
	public final List<PatientInfo> GetLeavePatientInfo()
	{
		String deptCode = GetParams("wardCode");
		String wardCode = GetParams("deptCode");
		//获取配置项目(病区特色)
		List<NANA_CONFIG_VALUE_EXT> configList = _configService.GetConfigValues(deptCode + ":::01", "WARD_FEATURE");
		String configValue = ((configList.get(0).CONFIG_VALUE) != null) ? configList.get(0).CONFIG_VALUE : "";
		boolean isMaternity = configValue.contains("产科病区");
		ArrayList<PatientInfo> patientList = _externalService.GetLeavePatientInfo(deptCode, wardCode, true, isMaternity);
		return _wardsService.assembleBedNote(patientList, wardCode, deptCode);
	}

	/** 
	 获取转入患者基本信息
	*/
	public final List<PatientInfo> GetInPatientInfo()
	{
		String deptCode = GetParams("wardCode");
		String wardCode = GetParams("deptCode");
		//获取配置项目(病区特色)
		List<NANA_CONFIG_VALUE_EXT> configList = _configService.GetConfigValues(deptCode + ":::01", "WARD_FEATURE");
		String configValue = ((configList.get(0).CONFIG_VALUE) != null) ? configList.get(0).CONFIG_VALUE : "";
		boolean isMaternity = configValue.contains("产科病区");
		ArrayList<PatientInfo> patientList = _externalService.GetInPatientInfo(deptCode, wardCode, true, isMaternity);
		return _wardsService.assembleBedNote(patientList, wardCode, deptCode);
	}

	/** 
	 获取转出患者基本信息
	*/
	public final List<PatientInfo> GetOutPatientInfo()
	{
		String deptCode = GetParams("wardCode");
		String wardCode = GetParams("deptCode");
		//获取配置项目(病区特色)
		List<NANA_CONFIG_VALUE_EXT> configList = _configService.GetConfigValues(deptCode + ":::01", "WARD_FEATURE");
		String configValue = ((configList.get(0).CONFIG_VALUE) != null) ? configList.get(0).CONFIG_VALUE : "";
		boolean isMaternity = configValue.contains("产科病区");
		ArrayList<PatientInfo> patientList = _externalService.GetOutPatientInfo(deptCode, wardCode, true, isMaternity);
		return _wardsService.assembleBedNote(patientList, wardCode, deptCode);
	}

}