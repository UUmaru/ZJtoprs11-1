package NaNa.Service.Config;

import java.util.*;
import External.Model.Entity.*;
import mHealth.Generic.Database.Helper.*;
import mHealth.Generic.Database.Param.*;
import mHealth.Generic.Service.*;
import mHealth.Generic.Utils.*;
import mHealth.Model.Base.Ext.*;
import Model.Base.Entity.Ext.*;
import Model.Base.EntityDto.Config.*;
import NaNa.Dal.AiTask.*;
import NaNa.Dal.Config.*;

public class ConfigService extends BaseService
{
		///#region   初始化信息
	private ConfigDal _dal;
	private AiTaskDal _AiTaskDal;

	@Override
	public void init()
	{
		_dal = new ConfigDal();
		_AiTaskDal = new AiTaskDal();
	}
		///#endregion

	/** 
	 绑定mac
	*/
	public final int SaveBindingDept(String clientKey, String deptCode, String hbranchCode)
	{
		IDatabase cis = Database[SysTypes.CIS];
		//先根据clientKey查询是否之前绑定过
		List<NANA_DEVICE_BINDING_EXT> bindInfos = GetBindingInfo(clientKey, cis);
		int result = 0;
		if (bindInfos != null && bindInfos.size() > 0)
		{
			//若绑定，则更新绑定信息
			result = _dal.UpdateDeviceBinding(cis, clientKey, deptCode, hbranchCode);
		}
		else
		{
			//没有绑定过，则进行绑定
			NANA_DEVICE_BINDING_EXT ext = new NANA_DEVICE_BINDING_EXT();
			ext.CLIENT_KEY = clientKey;
			ext.DEPT_CODE = deptCode;
			ext.HBRANCH_CODE = hbranchCode;
			result = cis.Insert(ext);
		}
		return result;
	}

	/** 
	 根据clientKey查询是否之前绑定过
	 
	 @param clientKey
	 @param iDatabase
	 @return 
	*/
	public final List<NANA_DEVICE_BINDING_EXT> GetBindingInfo(String clientKey, IDatabase iDatabase)
	{
		if (iDatabase == null)
		{
			iDatabase = Database[SysTypes.CIS];
		}
		return _dal.selectByClientKey(iDatabase, clientKey).Select(x -> (NANA_DEVICE_BINDING_EXT)((x instanceof NANA_DEVICE_BINDING_EXT) ? x : null)).ToList();
	}

	/** 
	 获取医院信息
	 
	 @return 
	*/
	public final DataTable GetHospitalBranches()
	{
		return _dal.GetHospitalBranches(Database[SysTypes.CIS]);
	}

	/** 
	 获取显示偏好下特色功能配置
	 
	 @param clientKey 科室唯一码
	 @return 
	*/
	public final List<NANA_CONFIG_VALUE_EXT> GetConfigValuesFEATURE(String clientKey)
	{
		IDatabase cis = Database[SysTypes.CIS];
		return _dal.GetConfigValuesFEATURE(cis, clientKey).Select(x -> (NANA_CONFIG_VALUE_EXT)((x instanceof NANA_CONFIG_VALUE_EXT) ? x : null)).ToList();
	}

	/** 
	 获取配置项
	 
	 @param clientKey 科室唯一码
	 @return 
	*/
	public final List<NANA_CONFIG_VALUE_EXT> GetConfigValues(String clientKey)
	{
		IDatabase cis = Database[SysTypes.CIS];
		// 更新当前科室数据
		MergeConfig(clientKey);
		return _dal.GetConfigValuesVisible(cis, clientKey).Select(x -> (NANA_CONFIG_VALUE_EXT)((x instanceof NANA_CONFIG_VALUE_EXT) ? x : null)).ToList();
	}

	/** 
	 
	 
	 @return 
	*/
	public final List<NANA_CONFIGTYPE_DICT_EXT> GetConfigtypeDict()
	{
		IDatabase cis = Database[SysTypes.CIS];
		return _dal.GetConfigtypeDict(cis).Select(x -> (NANA_CONFIGTYPE_DICT_EXT)((x instanceof NANA_CONFIGTYPE_DICT_EXT) ? x : null)).ToList();
	}
	/** 
	 获取配置项
	 
	 @param clientKey
	 @return 
	*/
	public final List<NANA_CONFIG_VALUE_EXT> GetConfigValuesAll(String clientKey)
	{
		IDatabase cis = Database[SysTypes.CIS];
		//获取配置字典
		DataTable configDictDt = GetConfigDict();
		List<NANA_CONFIG_VALUE_EXT> configValuesList = _dal.GetConfigValues(cis, clientKey).Select(x -> (NANA_CONFIG_VALUE_EXT)((x instanceof NANA_CONFIG_VALUE_EXT) ? x : null)).ToList();
		if (configDictDt == null || configDictDt.Rows.size() == 0)
		{
			return null;
		}
		for (DataRow row : configDictDt.Rows)
		{
			String configKey = row["CONFIG_KEY"].toString();
			ArrayList<NANA_CONFIG_VALUE_EXT> list = configValuesList.Where(x -> configKey.equals(x.CONFIG_KEY)).ToList();
			if (list.isEmpty())
			{
				NANA_CONFIG_VALUE_EXT ext = GetConfigValueModel(row, clientKey, cis);
				cis.Insert(ext);
			}
		}
		return _dal.GetConfigValues(cis, clientKey).Select(x -> (NANA_CONFIG_VALUE_EXT)((x instanceof NANA_CONFIG_VALUE_EXT) ? x : null)).ToList();
	}

	/** 
	 获取配置项(单条目)
	 
	 @param clientKey
	 @param configKey
	 @return 
	*/
	public final List<NANA_CONFIG_VALUE_EXT> GetConfigValues(String clientKey, String configKey)
	{
		IDatabase cis = Database[SysTypes.CIS];
		//获取配置字典
		List<NANA_CONFIG_VALUE_EXT> configValuesList = _dal.GetConfigValues(cis, clientKey, configKey).Select(x -> (NANA_CONFIG_VALUE_EXT)((x instanceof NANA_CONFIG_VALUE_EXT) ? x : null)).ToList();
		return configValuesList;
	}


	/** 
	 封装保存配置数据
	 
	 @param row 从DICT表查出来的数据
	 @param clientKey 当前的客户端key
	 @param iDatabase 数据库操作实体
	 @return 
	*/
	public final NANA_CONFIG_VALUE_EXT GetConfigValueModel(DataRow row, String clientKey, IDatabase iDatabase)
	{
		NANA_CONFIG_VALUE_EXT ext = new NANA_CONFIG_VALUE_EXT();
		ext.CLIENT_KEY = clientKey;
		ext.CONFIG_COMMENT = row["CONFIG_COMMENT"].toString();
		ext.CONFIG_KEY = row["CONFIG_KEY"].toString();
		ext.CONFIG_TAG = row["CONFIG_TAG"].toString();
		ext.CONFIG_VALUE = row["DEFAULT_VALUE"].toString();
		ext.CONFIGTYPE_CODE = row["CONFIGTYPE_CODE"].toString();
		ext.CONTROL_TYPE = row["CONTROL_TYPE"].toString();
		ext.DEFAULT_VALUE = row["DEFAULT_VALUE"].toString();
		ext.ID = iDatabase.GetNewID();
		ext.OPTIONS_CONTENT = row["OPTIONS_CONTENT"].toString();
		ext.OPTIONS_DESC = row["OPTIONS_DESC"].toString();
		ext.VISIBLE = row["VISIBLE"].toString();
		ext.ABBREVIATION = row["ABBREVITATION"].toString();
		return ext;
	}

	/** 
	 获取配置字典
	 
	 @return 
	*/
	public final DataTable GetConfigDict()
	{
		return _dal.GetConfigDict(Database[SysTypes.CIS]);
	}

	/** 
	 获取动态配置项目
	 
	 @param deptCode
	 @param isFixed
	 @param isEnable
	 @param page
	 @param column
	 @param fixedCode
	 @return 
	*/
	public final List<NANA_AI_PROJECT_DICT_EXT> GetDynamicConfig(String deptCode, String isFixed, String isEnable, String page, String column, String fixedCode, String allPage, String isAdvice)
	{
		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(page))
		{
			page = "0";
		}

		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(column))
		{
			column = "0";
		}

		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(fixedCode))
		{
			fixedCode = "0";
		}
		return _dal.GetDynamicConfig(deptCode, isFixed, isEnable, page, column, fixedCode, allPage, isAdvice, Database[SysTypes.CIS]).Select(x -> (NANA_AI_PROJECT_DICT_EXT)((x instanceof NANA_AI_PROJECT_DICT_EXT) ? x : null)).ToList();
	}

	/** 
	 获取服务器时间
	 
	 @return 
	*/
	public final String GetServerTime()
	{
		return Database[SysTypes.CIS].ServerDate().toString("yyyy-MM-dd HH:mm:ss");
	}

	/** 
	 修改显示隐藏配置项
	 
	 @param configDetail
	 @return 
	*/
	public final int UpdateConfig(ArrayList<ConfigDetailDto> configDetail)
	{
		int res = 0;
		IDatabase cis = Database[SysTypes.CIS];
		for (ConfigDetailDto detail : configDetail)
		{
			res += _dal.UpdateConfig(detail, cis);
		}
		return res;
	}

	/** 
	 批量更新项目标签表：IS_ENABLE和ROWNUMBER
	 
	 @param updateList
	 @return 
	*/
	public final int UpdatConfigBatch(ArrayList<ConfigDetailDto> updateList)
	{
		return updateList.size() > 0 ? _dal.UpdateConfigBatch(updateList, Database[SysTypes.CIS]) : 0;
	}

	/** 
	 批量插入项目标签表
	 
	 @param insertList
	 @return 
	*/
	public final int InsertConfigBatch(tangible.RefObject<ArrayList<ConfigDetailDto>> insertList)
	{
		if (insertList.argValue.size() <= 0)
		{
			return 0;
		}

		IDatabase cisDbDatabase = Database[SysTypes.CIS];
		// 获取当前最大的ITEM_CODE
		int maxItemCode = _AiTaskDal.GetMaxProjectCode(Database[SysTypes.CIS]);

		// 组装数据
		ArrayList<NANA_AI_PROJECT_DICT_EXT> aiProjectDictList = new ArrayList<NANA_AI_PROJECT_DICT_EXT>();
		for (var i = 0; i < insertList.argValue.size(); i++)
		{
			ConfigDetailDto configDetail = insertList.argValue.get(i);
			int nextItemCode = maxItemCode + (i + 1);
			configDetail.itemCode = String.valueOf(nextItemCode);
			NANA_AI_PROJECT_DICT_EXT label = new NANA_AI_PROJECT_DICT_EXT();
			label.ITEM_CODE = String.valueOf(nextItemCode);
			label.ITEM_NAME = configDetail.itemName;
			label.ITEM_KEYWORDS = configDetail.itemName;
			label.ACTIVE = "1";
			label.DEPT_CODE = configDetail.deptCode;
			label.IS_ENABLE = configDetail.isEnable;
			label.PAGE_NUMBER = Integer.parseInt(configDetail.pageNumber);
			label.COLUMN_NUMBER = Integer.parseInt(configDetail.columnNumber);
			label.COLUMN_NAME = GetColumnName(configDetail.columnNumber, configDetail.pageNumber);
			label.ROW_NUMBER = java.math.BigDecimal.Parse(configDetail.rowNumber);
			label.COLOR = configDetail.color;
			label.IS_FIXED = "0";
			label.START_DAY = "0";
			label.CONTINUE_DAY = "1";
			label.IS_ADVICE = "1";
			label.ITEM_TYPE = "活动项";
			label.CREATE_TIME = java.time.LocalDateTime.now();
			label.IS_TEMP = "1";
			aiProjectDictList.add(label);
		}

		return _dal.InsertConfigBatch(aiProjectDictList, Database[SysTypes.CIS]);
	}

	/** 
	 删除不要的项目标签
	 
	 @param existItemCodeList
	 @param deptCode
	 @return 
	*/
	public final int DeleteUnusedLabel(ArrayList<String> existItemCodeList, String deptCode, int pageNumber, int columnNumber)
	{
		return _dal.DeleteUnusedLabel(existItemCodeList, deptCode, pageNumber, columnNumber, Database[SysTypes.CIS]);
	}

	/** 
	 根据页名和列名获取COLUMN_NAME  TODO 这部分后续需要改善，目前直接写死不利于扩展
	 
	 @param columnNumber
	 @param pageNumber
	 @return 
	*/
	public final String GetColumnName(int columnNumber, int pageNumber)
	{
		String columnName = "";
		switch (String.valueOf(pageNumber) + "-" + String.valueOf(columnNumber))
		{
			case "2-1":
				columnName = "第一列";
				break;
			case "2-2":
				columnName = "第二列";
				break;
			case "2-3":
				columnName = "第三列";
				break;
			case "3-1":
				columnName = "第四列";
				break;
			case "3-2":
				columnName = "第五列";
				break;
			default:
				LogUtil.Error("未匹配到任何的列名 page:" + pageNumber + " column:" + columnName);
				break;
		}

		return columnName;
	}

	/** 
	 根据页名和列名获取COLUMN_NAME 覆写方法
	 
	 @param columnNumber
	 @param pageNumber
	 @return 
	*/
	public final String GetColumnName(String columnNumber, String pageNumber)
	{
		return GetColumnName(Integer.parseInt(columnNumber), Integer.parseInt(pageNumber));
	}

	/** 
	 保存配置项
	 
	 @param configValueList
	 @return 
	*/
	public final int UpdateConfigValues(ArrayList<ConfigValueDto> configValueList)
	{
		int res = 0;
		IDatabase cis = Database[SysTypes.CIS];
		for (ConfigValueDto value : configValueList)
		{
			res += _dal.UpdateConfigValues(value, cis);
		}
		return res;
	}

	/** 
	 根据父类id获取配置项目
	 
	 @param parentCode
	 @return 
	*/
	public final List<NANA_MAIN_CONFIG_EXT> GetMainConfigs(String parentCode)
	{
		IDatabase db = Database[SysTypes.CIS];
		return _dal.GetMainConfigs(db, parentCode).Select(x -> (NANA_MAIN_CONFIG_EXT)((x instanceof NANA_MAIN_CONFIG_EXT) ? x : null)).ToList();
	}

	/** 
	 获取系统配置
	 
	 @return 
	*/
	public final List<NANA_MAIN_CONFIG_EXT> GetSystemConfig(String systemCodes)
	{
		return _dal.GetSystemConfig(systemCodes, Database[SysTypes.CIS]).Select(x -> (NANA_MAIN_CONFIG_EXT)((x instanceof NANA_MAIN_CONFIG_EXT) ? x : null)).ToList();
	}

	/** 
	 获取系统配置
	 
	 @return 
	*/
	public final List<NANA_MAIN_CONFIG_EXT> GetSysConfig()
	{
		return _dal.GetSysConfig(Database[SysTypes.CIS]).Select(x -> (NANA_MAIN_CONFIG_EXT)((x instanceof NANA_MAIN_CONFIG_EXT) ? x : null)).ToList();
	}


	public final int UpdateSysConfig(NANA_MAIN_CONFIG_EXT model)
	{
		return _dal.UpdateSysConfig(Database[SysTypes.CIS], model);
	}

	/** 
	 获取科室映射关系
	 
	 @return 
	*/
	public final Map<String, String> GetDeptCodeMap()
	{
		List<DEPT_CODE_MAP_EXT> map = _dal.GetDeptCodeMap(Database[SysTypes.CIS]).Select(x -> (DEPT_CODE_MAP_EXT)((x instanceof DEPT_CODE_MAP_EXT) ? x : null)).ToList();
		return map.GroupBy(e -> e.NANA_DEPT_CODE).ToDictionary(e -> e.Key.toString(), e -> e.Select(codeMap -> codeMap.OUTER_DEPT_CODE).FirstOrDefault());
	}

	/** 
	 根据科室获取护理标识
	 
	 @return 
	*/
	public final List<NuringFlagDto> GetNursingFlag(String deptCode)
	{
		_dal.InitDeptFlagData(Database[SysTypes.CIS], deptCode);
		DataTable res = _dal.GetNursingFlag(Database[SysTypes.CIS], deptCode);
		return DatatableToModelList<NuringFlagDto>.ConvertToModel(res);
	}

	/** 
	 根据科室获取护理标识(pc端)
	 
	 @return 
	*/
	public final List<NuringFlagDto> GetNursingFlagPC(String deptCode)
	{
		_dal.InitDeptFlagData(Database[SysTypes.CIS], deptCode);
		DataTable res = _dal.GetNursingFlagPC(Database[SysTypes.CIS], deptCode);
		return DatatableToModelList<NuringFlagDto>.ConvertToModel(res);
	}

	/** 
	 更新护理标识颜色代码（PC端）
	 
	 @param id 表主键
	 @param colorCode 颜色编码
	 @return 
	*/
	public final int UpdateFlagColorCode(NuringFlagDto param)
	{
		return _dal.UpdateFlagColorCode(Database[SysTypes.CIS], param);
	}

	/** 
	 更新护理标识颜色代码（PC端）
	 
	 @param id 表主键
	 @param img 图片
	 @return 
	*/
	public final int UploadFlagImg(NuringFlagDto param)
	{
		return _dal.UploadFlagImg(Database[SysTypes.CIS], param);
	}

	/** 
	 根据dict表更新value表
	 
	 @param clientKey 科室唯一码
	*/
	public final void MergeConfig(String clientKey)
	{
		_dal.MergeConfig(Database[SysTypes.CIS], clientKey);
	}

	/** 
	 更新设备信息
	 
	 @param deptCode 科室号
	 @param deptName 科室名称
	 @param mac mac地址
	 @param version 安卓版本号
	*/
	public final void UpdateDevice(String deptCode, String deptName, String mac, String version)
	{
		_dal.UpdateDevice(Database[SysTypes.CIS], deptCode, deptName, mac, version);
	}

	/** 
	 获取当前所有的设备版本号
	 
	 @return 
	*/
	public final ArrayList<String> GetVersion()
	{
		return _dal.GetVersion(Database[SysTypes.CIS]);
	}

	/** 
	 获取全部的设备信息
	 
	 @param deptCode 科室号
	 @param status 状态
	 @param versionName 版本号
	 @param pageSize 分页大小
	 @param pageNo 第几页
	 @return 
	*/
	public final List<DEVICE_INFO_EXT> GetDeviceInfo(String deptCode, String status, String versionName, String pageSize, String pageNo)
	{
		return _dal.GetDeviceInfo(Database[SysTypes.CIS], deptCode, status, versionName, pageSize, pageNo).Select(x -> (DEVICE_INFO_EXT)((x instanceof DEVICE_INFO_EXT) ? x : null)).ToList();
	}

	/** 
	 获取查询设备的总量
	 
	 @param deptCode 科室号
	 @param status 状态
	 @param versionName 版本号
	 @return 
	*/
	public final int GetDeviceCount(String deptCode, String status, String versionName)
	{
		return _dal.GetDeviceCount(Database[SysTypes.CIS], deptCode, status, versionName);
	}

	/** 
	 根据deptCode和项目查找配置项，configvalue
	 
	 @param deptCode 科室号
	 @param item 配置项
	 @return 
	*/
	public final String GetDeptSpecifiedConfig(String deptCode, String item)
	{
		String clientKey = deptCode + ":::01";
		// 更新科室数据
		MergeConfig(clientKey);
		String configValue = _dal.GetDeptSpecifiedConfig(Database[SysTypes.CIS], clientKey, item);
		if (configValue == null)
		{
			throw new RuntimeException("未查找到配置项,，deptCode: " + deptCode + " item: " + item);
		}
		return configValue;
	}


	/** 
	 根据指定的code查找全局配置项，并判断是否和指定的value相等
	 
	 @param code 全局配置项的code
	 @param value 待判断的value
	 @return 
	*/
	public final boolean GetGlobalConfig(String code, String value)
	{
		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(code) || tangible.DotNetToJavaStringHelper.isNullOrEmpty(value))
		{
			throw new RuntimeException("查询全局配置失败, code: " + code + " value: " + value);
		}
		var config = GetSystemConfig("0092").FirstOrDefault();
		return config != null && value.equals(config.SYSTEM_VALUE);
	}


	/** 
	 获取全部的设备信息
	 
	 @param deptCode 科室号
	 @param status 状态
	 @param versionName 版本号
	 @param pageSize 分页大小
	 @param pageNo 第几页
	 @return 
	*/
	public final List<NANA_SKIN_INFO_EXT> GetSkinInfo()
	{
		return _dal.GetSkinInfo(Database[SysTypes.CIS]).Select(x -> (NANA_SKIN_INFO_EXT)((x instanceof NANA_SKIN_INFO_EXT) ? x : null)).ToList();

	}
}