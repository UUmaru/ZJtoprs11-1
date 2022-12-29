package NaNa.Bll.Orders;

import java.util.*;
import mHealth.Generic.Bll.*;
import Model.Base.Entity.Ext.*;
import Model.Base.EntityDto.Orders.*;
import NaNa.Service.Orders.*;
import External.Service.*;
import External.Model.Entity.*;
import mHealth.Generic.Utils.*;
import External.Util.*;
import Model.Base.EntityDto.Orders.ext.*;
import NaNa.Service.AiTask.*;
import NaNa.Util.*;
import Quartz.*;

//using DateUtil = mHealth.Generic.Utils.DateUtil;

public class OrdersBll extends BaseBll
{
		///#region   初始化信息
	private OrdersService _ordersService;
	private ExternalService _externalService;
	private AiTaskService _aiTaskService;

	/** 
	 构造函数实例化对象
	*/
	public OrdersBll()
	{
		//初始化server层
		_ordersService = new OrdersService();
		_externalService = new ExternalService();
		_aiTaskService = new AiTaskService();
	}
	/** 
	 初始化函数赋值用户信息和数据库连接
	*/
	@Override
	public void init()
	{
		SetParamsServer(_ordersService);
		SetParamsServer(_externalService);
		SetParamsServer(_aiTaskService);
	}
		///#endregion

	/** 
	 //查询有忽略医嘱的床号列表
	 
	 @return 
	*/
	public final List<String> GetIgnoreBeds()
	{
		var deptCode = GetParams("deptCode");
		List<PatientInfo> patients = _externalService.GetPatientInfo(deptCode);
		List<NANA_AI_ORDER_EXT> ignoreOrders = _ordersService.GetIgnoreOrderDistinct(deptCode, ToolsUtil.ExtractPVid(patients));
		ArrayList<Object> lists = (from u in patients join j in ignoreOrders on new {pid = u.patientId, vid = u.visitId} equals new {pid = j.PATIENT_ID, vid = j.VISIT_ID} orderby u.bedNo.getLength(), u.bedNo select u.bedNo).Distinct().ToList();
		return lists;
	}

	/** 
	 查询忽略医嘱详情
	 
	 @return 
	*/
	public final IgnoreOrdersList GetIgnoreOrderDetail()
	{
		String deptCode = GetParams("deptCode");
		ArrayList<PatientInfoBase> patientList = (ArrayList<PatientInfoBase>)JsonUtil.Json2Object(GetParams("postBoby"), ArrayList<PatientInfoBase>.class);
		// 去除出院病人
		List<PatientInfo> patsIn = _externalService.GetPatientInfo(deptCode);
		ArrayList<PatientInfoBase> patsOut = (from u in patientList join j in patsIn on new {pid = u.patientId, vid = u.visitId, deptCode} equals new {pid = j.patientId, vid = j.visitId, j.deptCode} into x from cx in x.DefaultIfEmpty() where cx == null select u).ToList();
		LogUtil.Warn("部分病人已出院： " + patsOut);
		patientList = patientList.Except(patsOut).ToList();

		// 娜娜库忽略医嘱 （今天在院且仍旧在拆分的忽略医嘱）
		List<String> pvid = patientList.Selectuniquetempvar.ToList();
		List<NANA_AI_ORDER_EXT> nanaOrders = _ordersService.GetIgnoreOrders(deptCode, tangible.DotNetToJavaStringHelper.join(",", pvid));

		// 查询操作日志
		ArrayList<NANA_AI_ORDER_LOG_EXT> logList = _aiTaskService.GetOrderLog(deptCode, nanaOrders.Selectuniquetempvar.Distinct().ToList()).ToList();
		// 组装医嘱和病人数据
		IgnoreOrdersList result = new IgnoreOrdersList();
		result.twoDays = GetPeriod(nanaOrders, logList, -2, tangible.DotNetToJavaStringHelper.join(",", pvid), deptCode);
		result.week = GetPeriod(nanaOrders, logList, -7, tangible.DotNetToJavaStringHelper.join(",", pvid), deptCode);
		result.month = GetPeriod(nanaOrders, logList, -30, tangible.DotNetToJavaStringHelper.join(",", pvid), deptCode);
		result.monthAgo = GetPeriod(nanaOrders, logList, -100, tangible.DotNetToJavaStringHelper.join(",", pvid), deptCode);
		return result;
	}

	/** 
	 查询不同时段的文书数据，并根据娜娜和日志进行组合
	 
	 @param nanaOrders 娜娜的医嘱数据
	 @param logList 日志数据
	 @param offset 日期偏差范围
	 @param pvid 要查询的病人列表
	 @param deptCode 科室号
	 @return 
	*/
	public final List<Patient> GetPeriod(List<NANA_AI_ORDER_EXT> nanaOrders, ArrayList<NANA_AI_ORDER_LOG_EXT> logList, int offset, String pvid, String deptCode)
	{
		// 查询病人在这指定时间（根据day往前推）在文书的医嘱数据
		java.time.LocalDateTime today = java.time.LocalDateTime.Today;
		List<DocsOrdersExecView> cisOrders;
		if (offset == -100)
		{
			java.time.LocalDateTime endDateTime = today.plusMonths(-1);
			cisOrders = _externalService.GetOrders(deptCode, pvid, null, null, null, null, null, endDateTime.toString());
		}
		else
		{
			var startDateTime = offset == -30 ? today.plusMonths(-1) : today.plusDays((int)offset);
			cisOrders = _externalService.GetOrders(deptCode, pvid, null, null, null, null, startDateTime.toString()).ToList();
		}

		return Encode(nanaOrders, logList, cisOrders);
	}

	/** 
	 根据娜娜/文书/日志数据来组装忽略医嘱的数据
	 
	 @param nanaOrders 娜娜医嘱数据
	 @param logList 日志数据
	 @param cisOrders 文书数据
	 @return 
	*/
	public final List<Patient> Encode(List<NANA_AI_ORDER_EXT> nanaOrders, ArrayList<NANA_AI_ORDER_LOG_EXT> logList, List<DocsOrdersExecView> cisOrders)
	{
		cisOrders = cisOrders.OrderBy(g -> g.start_date_time).GroupBy(g -> new {g.patient_id, g.visit_id, g.order_no}).Select(g -> g.FirstOrDefault()).ToList();

		Order tempVar = new Order();
		tempVar.id = e.j.REC_ID.toString();
		tempVar.orderNo = e.j.ORDER_NO;
		tempVar.doctor = e.u.doctor;
		tempVar.enterTime = mHealth.Generic.Utils.DateUtil.DateTimeToStamp(e.u.start_date_time);
		tempVar.repeatIndicator = e.u.repeat_indicator;
		tempVar.originalOrder = e.u.order_text;
		tempVar.referenceId = e.j.AI_ID.toString();
		tempVar.remarkType = e.j.MEDICINE_ORDERS;
		tempVar.recordTime = mHealth.Generic.Utils.DateUtil.DateTimeToStamp((java.time.LocalDateTime)e.j.RECORD_TIME) < mHealth.Generic.Utils.DateUtil.DateTimeToStamp(e.u.start_date_time) ? e.u.start_date_time : e.j.RECORD_TIME;
		tempVar.operationLogs = GetOrderLog(logList, x.Key.patient_id, x.Key.visit_id, e.j.ORDER_NO, e.u.start_date_time, e.j.RECORD_TIME);
		ArrayList<Patient> pats = (from u in cisOrders from j in nanaOrders where u.patient_id == j.PATIENT_ID && u.visit_id == j.VISIT_ID && u.order_no == j.ORDER_NO group new {u, j} by new {u.patient_id, u.visit_id, u.order_no} into x select new Patient {patientId = x.Key.patient_id, visitId = x.Key.visit_id, patientName = x.FirstOrDefault().u.patient_name, bedNo = x.FirstOrDefault().u.bed_no, bedLabel = x.FirstOrDefault().u.bed_label, deptCode = x.FirstOrDefault().j.DEPT_CODE, orders = x.Select(e -> tempVar).ToList()}).ToList();
		return pats;
	}

	/** 
	 获取医嘱的执行日志
	 
	 @param optLogs 日志记录
	 @param visitId 第几次来院
	 @param orderNo 医嘱号
	 @param startDateTime 医嘱开始执行时间（NANA当开嘱时间使用）
	 @param nanaRecordTime 日志生成时间
	 @param patientId 病人id
	 @return 
	*/
	public final List<CdrOperationLogDto> GetOrderLog(List<NANA_AI_ORDER_LOG_EXT> optLogs, String patientId, String visitId, String orderNo, java.time.LocalDateTime startDateTime, java.time.LocalDateTime nanaRecordTime)
	{
		java.time.LocalDateTime recordTime = startDateTime.compareTo((java.time.LocalDateTime)nanaRecordTime) < 0 ? (java.time.LocalDateTime)nanaRecordTime : startDateTime;
		ArrayList<NANA_AI_ORDER_LOG_EXT> logDtos = optLogs.Where(x -> patientId == x.PATIENT_ID && visitId.equals(x.VISIT_ID) && x.AI_ORDER_ID.toString().equals(orderNo) && (java.time.LocalDateTime)x.RECORD_TIME > startDateTime && ((java.time.LocalDateTime)(x.RECORD_TIME)).Date == ((java.time.LocalDateTime)(recordTime)).Date).OrderBy(x -> x.RECORD_TIME).ToList();

		ArrayList<CdrOperationLogDto> optLogList = new ArrayList<CdrOperationLogDto>();
		if (logDtos.size() > 0)
		{
			for (NANA_AI_ORDER_LOG_EXT dt : logDtos)
			{
				CdrOperationLogDto log = new CdrOperationLogDto();
				log.operateTime = mHealth.Generic.Utils.DateUtil.DateTimeToStamp((java.time.LocalDateTime)dt.RECORD_TIME).toString();
				log.operator = dt.RECORDER;
				log.operation = dt.OPERATION;
				optLogList.add(log);
			}
		}
		return optLogList;
	}

	/** 
	 还原已忽略的医嘱
	 
	 @return 
	*/
	public final Object RevertOrders()
	{
		int count = 0;

		ArrayList<IntermediateOrders> ignoreOrders = (ArrayList<IntermediateOrders>)JsonUtil.Json2Object(GetParams("postBoby"), ArrayList<IntermediateOrders>.class);
		LogUtil.Info("还原医嘱参数： " + GetParams("postBoby"));
		for (IntermediateOrders order : ignoreOrders)
		{
			String aiId = order.referenceId.toString();
			// AI_ID 为空：娜娜手动新增任务； 0 智能建议或者备忘
			if (!tangible.DotNetToJavaStringHelper.isNullOrWhiteSpace(aiId) && !"0".equals(aiId))
			{
				// 查询原始医嘱数
				count = _aiTaskService.GetAiOrderCount(order.deptCode, aiId).Rows.size();
				// 删除日志
				_aiTaskService.DeleteAiOrderLog(order.deptCode, order.id.toString(), "");
				// 删除规则
				_aiTaskService.DeleteAiText(order.deptCode, aiId);
				// 删除娜娜医嘱属性表 TODO 这部分不起作用，需要修改
				_aiTaskService.UpdateOrderAttr(order.deptCode, aiId);
				// 撤销任务
				_aiTaskService.UpdateAiOrder(order.deptCode, aiId);
			}
			else
			{
				//撤销单条任务
				count = _aiTaskService.UpdateAiOrderByOrderNo(order.deptCode, order.orderNo.toString());
				//删除娜娜医嘱属性表
				_aiTaskService.UpdateAiTaskAttrRemark(order.patientId.toString(), order.visitId.toString(), order.orderNo.toString(), "","");
			}
		}

		return count;
	}

	/** 
	 医嘱拆分
	 
	 @return 
	*/
	public final Object SplitOrders()
	{
		LogUtil.Info("HIS数据处理进入文书开始！");
		_externalService.HlwsSplitOrders();
		LogUtil.Info("HIS数据处理进入文书结束！");
		return 1;
	}

}