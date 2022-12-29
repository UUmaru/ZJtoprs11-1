package NaNa.Bll.SysUser;

import java.util.*;
import mHealth.Generic.Bll.*;
import mHealth.Generic.Model.AutoMapper.*;
import Model.Base.Entity.Ext.*;
import External.Service.*;
import External.Model.Entity.*;
import NaNa.Service.SysUser.*;
import mHealth.Generic.Utils.*;
import mHealth.Model.Base.Ext.*;
import mHealth.Generic.Database.Helper.*;
import External.Model.EntityDto.*;
import Model.Base.EntityDto.Config.*;
import NaNa.Service.Config.*;
import Model.Base.EntityDto.SysUser.*;
import NaNa.Bll.Config.*;

public class SysUserBll extends BaseBll
{
		///#region   初始化信息
	private SysUserService _sysUserService;
	private ExternalService _externalService;
	private ConfigService _configService;
	private ConfigBll _configBll;

	/** 
	 构造函数实例化对象
	*/
	public SysUserBll()
	{
		//初始化server层
		_sysUserService = new SysUserService();
		_externalService = new ExternalService();
		_configService = new ConfigService();
		_configBll = new ConfigBll();
	}
	/** 
	 初始化函数赋值用户信息和数据库连接
	*/
	@Override
	public void init()
	{
		SetParamsServer(_sysUserService);
		SetParamsServer(_externalService);
		SetParamsServer(_configService);
		SetParamsServer(_configBll);
	}
		///#endregion

		///#region 实现方法
	/** 
	 //绑定人脸信息
	 
	 @return 
	*/
	public final Object UploadUserImg()
	{
		int i = 0;
		FaceLand img = (FaceLand)JsonUtil.Json2Object(GetParams("postBoby"), FaceLand.class);
		String userId = GetParams("userId").toString();
		ArrayList<NANA_SYS_USER_EXT> userEntity = _sysUserService.GetUserInfo(userId);

		if (userEntity.size() > 0)
		{
			NANA_SYS_USER_EXT userEnt = userEntity.get(0);
			userEnt.USER_IMG = img.userImg;
			userEnt.FACE_IMG = img.faceImg;
			i = _sysUserService.updateUser(userEnt);
		}
		else
		{
			NANA_SYS_USER_EXT userEnt = new NANA_SYS_USER_EXT();
			userEnt.USER_ID = userId;
			userEnt.USER_IMG = img.userImg;
			userEnt.FACE_IMG = img.faceImg;
			i = Database[SysTypes.CIS].Insert(userEnt);
		}

		return i;
	}

	/** 
	 //解绑人脸信息
	 
	 @return 
	*/
	public final Object DeleteUserImg()
	{
		int i = 0;
		String userId = GetParams("userId").toString();

		ArrayList<NANA_SYS_USER_EXT> userEntity = _sysUserService.GetUserInfo(userId);

		if (userEntity.size() > 0)
		{
			NANA_SYS_USER_EXT userEnt = userEntity.get(0);
			userEnt.USER_IMG = null;
			userEnt.FACE_IMG = null;
			i = _sysUserService.UpdateUserInfo(userEnt.USER_ID);
		}

		return i;
	}

	/** 
	 //获取当前病区所有有权限的护士信息（人脸登录）
	 
	 @return 
	*/
	public final List<LoginUserView> GetUserInfo()
	{
		String deptCode = GetParams("deptCode").toString();

		List<LoginUserView> userEntity = _externalService.GetUserInfo("", "", deptCode);

		for (LoginUserView user : userEntity)
		{
			ArrayList<NANA_SYS_USER_EXT> userList = _sysUserService.GetUserInfo(user.userId);

			if (userList.size() > 0)
			{
				user.dbUser = userList.get(0).DB_USER;
				user.userImg = userList.get(0).USER_IMG;
				user.imgUrl = userList.get(0).IMG_URL;
				user.isEnable = userList.get(0).IS_ENABLE;
				user.meetingTimes = (int)(userList.get(0).MEETING_TIMES);
				user.roleCode = GetRoles(user.userId);
			}
			else
			{
				user.dbUser = "";
				user.userImg = null;
				user.imgUrl = "";
				user.isEnable = "1";
				user.meetingTimes = 0;
			}
		}

		return userEntity;
	}


	public final byte[] GetUserFace()
	{
		ArrayList<NANA_SYS_USER_EXT> userList = _sysUserService.GetUserInfo(GetParams("userId").toString());

		if (userList.size() > 0)
		{
			return userList.get(0).FACE_IMG;

		}
		else
		{
			return null;
		}
	}

	/** 
	 人脸签到
	 
	 @return 
	*/
	public final Object SaveFaceLand()
	{
		FaceLandInfo userInfos = (FaceLandInfo)JsonUtil.Json2Object(GetParams("postBoby"), FaceLandInfo.class);
		if (userInfos.userId != null)
		{
			return _sysUserService.SaveFaceLand(userInfos);
		}
		return "";
	}

	/** 
	 周年纪念
	 
	 @return 
	*/
	public final List<AnniversaryView> GetAnniversary()
	{
		String deptCode = GetParams("dept_code").toString();
		String hospitalName = GetParams("hospitalName").toString();
		return _externalService.GetAnniversary(deptCode, hospitalName);
	}




	/** 
	 数字风云榜
	 
	 @return 
	*/
	public final List<BillboardDataView> GetBillboardData()
	{
		String deptCode = GetParams("deptCode").toString();
		return _externalService.GetBillboardData(deptCode);
	}


	/** 
	 预入科患者
	 
	 @return 
	*/
	public final List<HisPrePatsView> GetHisPrePats()
	{
		String deptCode = GetParams("DEPT_CODE").toString();
		return _externalService.GetHisPrePats(deptCode);
	}

	/** 
	 排换床
	 
	 @return 
	*/
	public final Object SaveSynPatBedInfo()
	{
		var pid = GetParams("PATIENT_ID"); //病人ID
		var vid = GetParams("VISIT_ID"); //住院次
		var deptcode = GetParams("DEPT_CODE"); //病区代码
		var newBed = GetParams("NEW_BED"); //新床号
		var oldBed = GetParams("OLD_BED"); //旧床号
		var nurse = GetParams("NURSE"); //操作人姓名

		AdtLogRecDto model = new AdtLogRecDto();
		model.patient_id = pid;
		model.visit_id = vid;
		model.dept_code = deptcode;
		model.bed_no = newBed;
		model.old_bed = oldBed;
		model.creator = nurse;
		model.log_time = java.time.LocalDateTime.now();
		model.event_time = java.time.LocalDateTime.now();
		model.stamp_name = "换床";
		return _externalService.SaveSynPatBedInfo(model);
	}


	/** 
	 修改医嘱状态
	 
	 @return 
	*/
	public final Object SaveSynNaNaOrdersRec()
	{
		SyncOrderDto model = (SyncOrderDto)JsonUtil.Json2Object(GetParams("Data"), SyncOrderDto.class);
		return _externalService.SaveSynNaNaOrdersRec(model);
	}

	/** 
	 获取子科室信息
	 
	 @return 
	*/
	public final List<WardInfoView> GetWardSubDept()
	{
		String deptCode = GetParams("wardCode").toString();
		return _externalService.GetWardSubDept(deptCode);
	}


	/** 
	 获取护士4周排班信息
	 
	 @return 
	*/
	public final List<NurseScheduleView> GetSchedule()
	{
		String deptCode = GetParams("wardNo");
		return _sysUserService.GetSchedule(deptCode);
	}

	/** 
	 从外部同步护士排班数据到本地
	 当前方法只针对定时器使用
	*/
	public final Object SaveSchedule()
	{
		ArrayList<String> deptCodes = _configBll.GetAllDeptCode();
		DataTable hisSchedule = _externalService.GetScheduleDataTable(deptCodes);

		// 删除娜娜库四周内所有的排班数据
		_sysUserService.DeleteSchedule();
		// 保存数据
		_sysUserService.SaveScheduleBatch(hisSchedule);
		return 1;
	}


	/** 
	 获取护士4周排班信息
	 
	 @return 
	*/
	public final List<DutyNursePlanView> GetNursePlan()
	{
		String deptCode = GetParams("deptCode");
		// 责任护士数据来源
		NANA_CONFIG_VALUE_EXT config = _configService.GetConfigValuesuniquetempvar.FirstOrDefault();
		if (config == null)
		{
			throw new RuntimeException("不存则责任护士数据来源配置项！请检查！");
		}
		if (config.CONFIG_VALUE.Contains("第三方系统-默认"))
		{
			// 不可编辑取外部数据
			return _externalService.GetNursePlan(deptCode, 1);
		}

		if (config.CONFIG_VALUE.equals("NANA手动编辑"))
		{
			return _sysUserService.GetNursePlan(deptCode, 1);
		}

		throw new RuntimeException("未找到合适的责任护士数据来源！请检查！");
	}

	/** 
	 获取护士4周排班信息第三方
	 
	 @return 
	*/
	public final List<DutyNurseBed> GetNursePlanNew()
	{
		String deptCode = GetParams("deptCode");
		return _sysUserService.GetNursePlanNew(deptCode, 1);
	}

	/** 
	 同步责护信息
	 
	 @return 
	*/
	public final String SynNursePlan()
	{
		//责任护士是否能够编辑0表示不可编辑 1表示可以编辑 默认0
		String systemCodes = "('0074')";
		List<NANA_MAIN_CONFIG_EXT> sysConfigs = _configService.GetSystemConfig(systemCodes);
		String dutyNurseEdit = "0";
		if (sysConfigs != null && sysConfigs.size() > 0)
		{
			dutyNurseEdit = tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(sysConfigs.get(0).SYSTEM_VALUE) ? "" : sysConfigs.get(0).SYSTEM_VALUE.toString();
		}
		if ("1".equals(dutyNurseEdit))
		{
			List<DeptView> deptList = _externalService.GetDeptList();
			for (DeptView item : deptList)
			{
				//可编辑取本地数据
				DataTable dt = _sysUserService.SynNursePlan(item.dept_code);
				//组装数据进行修改病人信息中的责护信息
				NursesBedEdit model = new NursesBedEdit();
				ArrayList<NursesBedDto> updatelist = new ArrayList<NursesBedDto>();
				for (DataRow row : dt.Rows)
				{
					NursesBedDto bed = new NursesBedDto();
					bed.bedLabelList = new ArrayList<String>();
					bed.groupIdentity = row["group_identity"].toString();
					bed.groupName = row["group_name"].toString();
					bed.headNurseId = row["head_nurse_id"].toString();
					bed.headNurseName = row["head_nurse_name"].toString();
					for (String bedit : row["bed_label"].toString().split("[,]", -1))
					{
						bed.bedLabelList.Add(bedit);
					}
					bed.bedLabel = "";
					bed.nursingTime = row["nursing_time"].toString();
					bed.dutyNurseId = row["duty_nurse_id"].toString();
					bed.dutyNurseName = row["duty_nurse_name"].toString();
					bed.night = row["night"].toString();
					updatelist.add(bed);
				}
				model.deptCode = item.dept_code;
				model.insertDutyNurse = new ArrayList<NursesBedDto>();
				model.deleteDutyNurse = new ArrayList<NursesBedDto>();
				model.updateDutyNurse = updatelist;
				_externalService.ChangeDutyNurse(model);
			}
		}
		return "true";
	}

	/** 
	 修改责护管床信息
	 
	 @return 
	*/
	public final Object ChangeDutyNurse()
	{
		NursesBedEdit model = (NursesBedEdit)JsonUtil.Json2Object(GetParams("postBoby"), NursesBedEdit.class);
		if (_sysUserService.ChangeDutyNurse(model) > 0)
		{
			return _externalService.ChangeDutyNurse(model);
		}
		return 0;
	}

	/** 
	 查询权限
	 
	 @param ?
	 @return 
	*/
	private String GetRoles(String userId)
	{
		String roles = "";

		DataTable dt = _externalService.GetDeptsByUserId(userId);

		if (dt.Rows.size() > 0)
		{
			String strCode = "";

			for (DataRow dr : dt.Rows)
			{
				strCode += dr["DEPT_CODE"].toString() + "#";
			}
			roles = tangible.DotNetToJavaStringHelper.trimEnd(strCode, '#');
		}
		return roles;
	}
		///#endregion

	public final List<LoginUserView> UserInfo()
	{
		String deptCode = GetParams("deptCode");
		String cardId = GetParams("cardId");
		if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(cardId))
		{
			return _externalService.GetUserInfo("", "", "", cardId);
		}
		else
		{
			List<LoginUserView> users = _externalService.GetUserInfo("", "", deptCode);

			for (LoginUserView user : users)
			{
				user.roleCode = user.roleCode.getLength() > 0 ? user.roleCode : GetRoles(user.userId);
			}

			return users;
		}
	}

	public final Object updateUserLoginTimes()
	{
		String userId = GetParams("userId");
		String cardId = GetParams("cardId");
		String meetingTime = GetParams("meetingTimes");
		int numUpdate = 0;
		numUpdate = _sysUserService.updateUserLoginTimes(userId, meetingTime);
		return numUpdate;
	}

	/** 
	 管理端登录方法
	 
	 @return 
	*/
	public final Object GetLoginUser()
	{
		String userId = GetParams("userId");
		String loginPass = GetParams("loginPass");
		String hbranchCode = GetParams("hbranchCode");
		String cardId = GetParams("cardId");
		//获取登录用户
		List<LoginUserView> loginUser = _externalService.GetUserInfo(userId, loginPass);
		if (loginUser != null && loginUser.size() > 0)
		{
			HashMap<String, Object> loginDic = new HashMap<String, Object>();
			//处理用户数据
			LoginUserView localUser = loginUser.get(0);
			//就因为前端需要出生日期为毫秒数，就作了个转换。。。
			SysUserDto sysUserDto = new SysUserDto();
			sysUserDto.cardId = localUser.cardId;
			sysUserDto.cardNo = localUser.cardNo;
			sysUserDto.dateOfBirth = DateUtil.DateTimeToStamp((java.time.LocalDateTime)localUser.dateOfBirth);
			sysUserDto.dbUser = localUser.dbUser;
			sysUserDto.deptCode = localUser.deptCode;
			sysUserDto.deptName = localUser.deptName;
			sysUserDto.email = localUser.email;
			sysUserDto.gender = localUser.gender;
			sysUserDto.hbranchCode = hbranchCode;
			sysUserDto.hospitalCode = localUser.hospitalCode;
			sysUserDto.imgUrl = localUser.imgUrl;
			sysUserDto.isEnable = localUser.isEnable;
			sysUserDto.job = localUser.job;
			sysUserDto.loginName = localUser.loginName;
			sysUserDto.loginPass = localUser.loginPass;
			sysUserDto.meetingTimes = localUser.meetingTimes;
			sysUserDto.phoneNumber = localUser.phoneNumber;
			sysUserDto.roleCode = localUser.roleCode.getLength() > 0 ? localUser.roleCode : GetRoles(userId);
			sysUserDto.userId = localUser.userId;
			sysUserDto.userImg = localUser.userImg;
			sysUserDto.userName = localUser.userName;
			//处理科室数据
			List<DeptView> deptList = _externalService.GetDeptList(hbranchCode);
			List<DeptDto> deptDtoList = new ArrayList<DeptDto>();
			int idNum = 0;
			for (DeptView dept : deptList)
			{
				DeptDto deptDto = new DeptDto();
				deptDto.active = dept.active;
				deptDto.deptCode = dept.dept_code;
				deptDto.deptName = dept.dept_name;
				deptDto.hospitalBranchCode = hbranchCode;
				deptDto.id = idNum + 1;
				deptDto.deptLevel = "";
				deptDto.outCode = "";
				deptDto.parentDeptCode = "";
				deptDtoList.add(deptDto);
				idNum++;
			}
			loginDic.put("userInfo", sysUserDto);
			loginDic.put("deptList", deptDtoList);
			return loginDic;
		}
		else
		{
			return null;
		}
	}

	/** 
	 更新用户信息
	 
	 @return 
	*/
	public final Object UpdateUserInfo()
	{
		int i = 0;
		ArrayList<PostLoginUserInfo> userInfos = (ArrayList<PostLoginUserInfo>)JsonUtil.Json2Object(GetParams("postBoby"), ArrayList<PostLoginUserInfo>.class);

		if (userInfos.size() > 0)
		{
			for (PostLoginUserInfo user : userInfos)
			{
				i += _externalService.UpdateUserVsDept(user.roleCode, user.userId);
			}
		}

		return i;
	}

	/** 
	 获取责医信息
	 
	 @return 
	*/
	public final List<DutyDoc> GetDutyDoc()
	{
		String deptCode = GetParams("deptCode");
		List<DutyDoc> dutyDoc = _externalService.GetDutyDoc(deptCode);
		return dutyDoc;
	}
}