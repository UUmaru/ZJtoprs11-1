package External.BaseDal;

import java.util.*;
import mHealth.Generic.Database.Helper.*;
import mHealth.Model.*;
import External.Model.EntityDto.*;

/** 
 数据访问层接口类
*/
public interface IBaseDal
{
	/** 
	 获取检查安排数据
	 
	 @param dbHelper 数据库操作实体
	 @param deptCode 科室code
	 @param queryStart 查询起始时间
	 @param queryEnd 查询结束时间
	 @return 
	*/
	List<BaseEntity> GetArrangements(IDatabase dbHelper, String deptCode, java.time.LocalDateTime queryStart, java.time.LocalDateTime queryEnd);

	/** 
	 获取责医数据
	 
	 @param dbHelper 数据库操作实体
	 @param deptCode 科室号
	 @return 
	*/
	List<BaseEntity> GetDutyDoc(IDatabase dbHelper, String deptCode);

	/** 
	 获取指定科室列表的手术安排
	 
	 @param deptCode
	 @return 
	*/
	DataTable GetSurgicalArrangements(IDatabase iDatabase, ArrayList<String> deptCode);

	/** 
	 获取科室列表
	 
	 @param iDatabase
	 @return 
	*/
	DataTable GetDeptList(IDatabase iDatabase, String hbranchCode);

	/** 
	 获取患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/

	DataTable GetPatientInfo(IDatabase iDatabase, String deptCode, String wardCode);
	DataTable GetPatientInfo(IDatabase iDatabase, String deptCode);
	DataTable GetPatientInfo(IDatabase iDatabase, String deptCode, String wardCode, boolean isQueryBaby);


	/** 
	 获取患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/

	DataTable GetPatientInfoByBedNo(IDatabase iDatabase, String deptCode);
	DataTable GetPatientInfoByBedNo(IDatabase iDatabase, String deptCode, String wardCode);

	/** 
	 获取当日出院患者基本信息
	 
	 @param iDatabase 数据库操作实体
	 @param deptCode 科室code
	 @param isToday 是否只查询今日出院患者 true 查询今日 false 查询该科室所有数据
	 @return 
	*/

	DataTable GetPatientDischargeInfo(IDatabase iDatabase, String deptCode);
	DataTable GetPatientDischargeInfo(IDatabase iDatabase, String deptCode, boolean isToday);


	/** 
	 获取当日转科患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/
	DataTable GetPatientChangeInfo(IDatabase iDatabase, String deptCode);

	/** 
	 获取患者评估信息
	 
	 @param pats 病人id串
	 @return 
	*/
	DataTable GetPatsRisk(IDatabase iDatabase, String pats);


	/** 
	 获取巡视医嘱
	 
	 @param iDatabase
	 @param pats
	 @param today
	 @return 
	*/
	DataTable GetOrdersExec(IDatabase iDatabase, String pats, String today);

	/** 
	 根据医嘱原文查找当天所有医嘱原文一致的医嘱数据
	 
	 @param dbHelper 数据库操作实体
	 @param orderText 医嘱原文
	 @param deptCode 科室号
	 @param time 执行时间，精确到天，时分秒会处理成0，即查询某一天得数据，而不是某个时刻得数据
	 @return 
	*/
	DataTable GetOrdersByOrderText(IDatabase dbHelper, String orderText, String deptCode, java.time.LocalDateTime time);

	/** 
	 获取某个科室某天的数据
	 
	 @param iDatabase
	 @param startTime
	 @param deptCode
	 @param pvId
	 @param day
	 @param recordTime
	 @param pvIdNoEx
	 @return 
	*/

	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day, String recordTime, String pvIdNoEx, String startDateTime, String endDateTime);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day, String recordTime, String pvIdNoEx, String startDateTime);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day, String recordTime, String pvIdNoEx);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day, String recordTime);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode);
	DataTable GetOrders(IDatabase iDatabase, String startTime);
	DataTable GetOrders(IDatabase iDatabase);
	DataTable GetOrders(IDatabase iDatabase, String startTime, String deptCode, String pvId, String day, String recordTime, String pvIdNoEx, String startDateTime, String endDateTime, String datasourceType);

	/** 
	 根据patientId + visitId + orderNo三者结合的数据查询文书指定医嘱
	 查询时间：不限
	 
	 @param dbHelper 数据库操作实体
	 @param deptCode 科室号
	 @param pvIdAndOrderNo patientId + visitId + orderNo 三者拼接起来的数据
	 @return 
	*/
	DataTable GetOrders(IDatabase dbHelper, String deptCode, List<String> pvIdAndOrderNo);

	/** 
	 获取多个科室某天的数据
	 
	 @param iDatabase
	 @param startTime
	 @param deptCodes
	 @param pvId
	 @param day
	 @param recordTime
	 @param pvIdNoEx
	 @return 
	*/

	DataTable GetDeptOrders(IDatabase iDatabase, String startTime, java.util.ArrayList<String> deptCodes, String pvId, String day, String recordTime);
	DataTable GetDeptOrders(IDatabase iDatabase, String startTime, java.util.ArrayList<String> deptCodes, String pvId, String day);
	DataTable GetDeptOrders(IDatabase iDatabase, String startTime, java.util.ArrayList<String> deptCodes, String pvId);
	DataTable GetDeptOrders(IDatabase iDatabase, String startTime, java.util.ArrayList<String> deptCodes);
	DataTable GetDeptOrders(IDatabase iDatabase, String startTime);
	DataTable GetDeptOrders(IDatabase iDatabase);
	DataTable GetDeptOrders(IDatabase iDatabase, String startTime, ArrayList<String> deptCodes, String pvId, String day, String recordTime, String pvIdNoEx);

	/** 
	 获取医嘱信息
	 
	 @param iDatabase
	 @param patientId
	 @param orderNo
	 @param execTime
	 @return 
	*/
	DataTable GetOrderInfo(IDatabase iDatabase, String patientId, String orderNo, String execTime);

	/** 
	 过敏信息
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatGuoMing(String pats, IDatabase dbHelper);

	/** 
	 饮食标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatYinShi(String pats, IDatabase dbHelper);

	/** 
	 手术标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatShouShu(String pats, IDatabase dbHelper);

	/** 
	 高温标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatGaoTemp(String pats, IDatabase dbHelper);


	/** 
	 高温标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatGeLi(String pats, IDatabase dbHelper);
	/** 
	 获取用户信息
	 
	 @param userId
	 @param password
	 @param deptCode
	 @param cardId
	 @param iDatabase
	 @return 
	*/
	DataTable GetUserInfo(String userId, String password, String deptCode, String cardId, IDatabase iDatabase);

	/** 
	 根据用户编号获取用户科室权限列表
	 
	 @param userId
	 @return 
	*/
	DataTable GetDeptsByUserId(String userId, IDatabase iDatabase);

	/** 
	 新增任务，保存至医嘱拆分表
	 
	 @param detail
	 @param patientName
	 @param iDatabase
	 @return 
	*/
	int SaveOredersExeLog(AiTaskDetail detail, String patientName, IDatabase iDatabase);

	/** 
	 修改医嘱
	 
	 @param orderNo
	 @param execTime
	 @param oldExccTime
	 @param iDatabase
	 @return 
	*/
	int UpdateOredersExeLog(String patientId, String orderNo, String execTime, String oldExccTime, IDatabase iDatabase);

	/** 
	 修改原始医嘱
	 
	 @param orderNo
	 @param execTime
	 @param iDatabase
	 @return 
	*/
	int UpdateOreders(String patientId, String orderNo, String execTime, IDatabase iDatabase);


	/** 
	 获取空床床位
	 
	 @param iDatabase
	 @param deptCode
	 @return 
	*/
	DataTable GetBedInfo(IDatabase iDatabase, String deptCode);

	/** 
	 更新用户信息
	 
	 @param deptInfos
	 @param userId
	 @return 
	*/
	int UpdateUserVsDept(String deptInfos, String userId, IDatabase iDatabase);

	/** 
	 获取周年纪念
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetAnniversary(String deptCode, IDatabase iDatabase);

	/** 
	 获取子科室集合
	 
	 @param iDatabase
	 @param deptCode
	 @return 
	*/
	DataTable GetWardSubDept(IDatabase iDatabase, String deptCode);

	/** 
	 获取数字风云榜
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetBillboardData(String deptCode, IDatabase iDatabase);

	/** 
	 获取预入科患者
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetHisPrePats(String deptCode, IDatabase iDatabase);

	/** 
	 换床操作
	 
	 @param model
	 @param iDatabase
	 @return 
	*/
	int SaveSynPatBedInfo(AdtLogRecDto model, IDatabase iDatabase);

	/** 
	 医嘱回写操作
	 
	 @param model
	 @param iDatabase
	 @return 
	*/
	int SaveSynNaNaOrdersRec(SyncOrderDto model, IDatabase iDatabase);

	/** 
	 获取HIS方需要处理的医嘱
	 
	 @param dbHelper 数据库操作类
	 @param deptCodes 拼接好的多科室code
	 @return 
	*/
	DataTable GetHisOrders(IDatabase dbHelper, String deptCodes);

	/** 
	 获取班次时间
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetHeadDayByToday(String deptCode, IDatabase iDatabase);

	/** 
	 获取排班信息
	 
	 @param deptCodeList
	 @param start
	 @param end
	 @param iDatabase
	 @return 
	*/
	DataTable GetSchedule(List<String> deptCodeList, String start, String end, IDatabase iDatabase);

	/** 
	 获取责任护士管床信息
	 
	 @param deptCode
	 @param days
	 @param iDatabase
	 @return 
	*/
	DataTable GetNursePlanDefault(String deptCode, int days, IDatabase iDatabase);

	/** 
	 新增医嘱
	 
	 @param add
	 @param dbHelper
	 @return 
	*/
	int SaveOrder(AddOrder add, IDatabase dbHelper);

	/** 
	 删除his医嘱表娜娜新增医嘱
	 
	 @param pid
	 @param vid
	 @param orderNo
	 @param dbHelper
	 @return 
	*/
	int DeleteVhisOrder(String pid, String vid, String orderNo, IDatabase dbHelper);

	/** 
	 医嘱拆分表娜娜新增医嘱
	 
	 @param pid
	 @param vid
	 @param orderNo
	 @param dbHelper
	 @return 
	*/
	int DeleteDocsOrder(String pid, String vid, String orderNo, IDatabase dbHelper);

	/** 修改责护床位
	 
	 @param nurseId
	 @param nurseName
	 @param bedNo
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	int UpdateDutyNurse(String nurseId, String nurseName, String bedNo, String deptCode, IDatabase iDatabase);

	/** 获取交班主记录
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetShift(String deptCode, IDatabase iDatabase);

	/** 
	 获取交班明细记录
	 
	 @param deptCode
	 @param iDatabase
	 @return 
	*/
	DataTable GetShiftDetail(String deptCode, IDatabase iDatabase);

	/** 
	 产科病区获取孕产信息和婴儿信息
	 
	 @param iDatabase
	 @param deptCode 病区号
	 @param wardCode 科室号
	 @return 
	*/

	DataTable GetMaternityInfo(IDatabase iDatabase, String deptCode);
	DataTable GetMaternityInfo(IDatabase iDatabase, String deptCode, String wardCode);

	/** 
	 获取全科体征数据
	 
	 @param hlwsApiHost 护理文书HOST
	 @param timePoint 时刻点
	 @param hospitalCode 医院code
	 @param deptCode 科室code
	 @return 
	*/
	ArrayList<NanaSigns> GetSigns(String hlwsApiHost, String timePoint, String hospitalCode, String deptCode);

	/** 
	 获取体征时间点
	 
	 @param hlwsApiHost 护理文书HOST
	 @param deptCode 科室code
	 @return 
	*/
	ArrayList<NanaTimePoint> GetTimePoint(String hlwsApiHost, String deptCode);

	/** 
	 获取全科体征的映射关系
	 
	 @param hlwsApiHost 护理文书HOST
	 @param hospitalCode 医院code
	 @return 
	*/
	List<NanaSignItem> GetSignItemsMap(String hlwsApiHost, String hospitalCode);

	/** 
	 根据ordeNo获取相关医嘱信息
	 
	 @param patientId 病人ID
	 @param visitId 住院次数
	 @param orderNo 医嘱号
	 @param execTime 医嘱执行时间
	 @return 
	*/
	DataTable GetOrderExeLog(String patientId, String visitId, String recId, String execTime, IDatabase iDatabase);

	/** 
	 陪护标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatPeiHu(String pats, IDatabase dbHelper);
	/** 
	 保密标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatBaoMi(String pats, IDatabase dbHelper);
	/** 
	 优势病种标签
	 
	 @param pats
	 @param dbHelper
	 @return 
	*/
	DataTable GetPatYouShi(String pats, IDatabase dbHelper);

	/** 
	 获取该科室下的病区名称
	 
	 @param database
	 @param deptCode
	 @return 
	*/
	ArrayList<String> GetWrdNamesByDept(IDatabase database, String deptCode);

	/** 
	 获取出院患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/

	DataTable GetLeavePatientInfo(IDatabase iDatabase, String deptCode);
	DataTable GetLeavePatientInfo(IDatabase iDatabase, String deptCode, String wardCode);

	/** 
	 获取转入科室患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/

	DataTable GetInPatientInfo(IDatabase iDatabase, String deptCode);
	DataTable GetInPatientInfo(IDatabase iDatabase, String deptCode, String wardCode);

	/** 
	 获取转出科室患者基本信息
	 
	 @param wardCode 病区代码
	 @return 
	*/

	DataTable GetOutPatientInfo(IDatabase iDatabase, String deptCode);
	DataTable GetOutPatientInfo(IDatabase iDatabase, String deptCode, String wardCode);

}