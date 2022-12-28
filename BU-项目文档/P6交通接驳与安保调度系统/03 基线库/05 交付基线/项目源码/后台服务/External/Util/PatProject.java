package External.Util;

import java.util.*;
import mHealth.Model.Base.*;
import External.Model.Entity.*;
import mHealth.Generic.Utils.*;
import Newtonsoft.Json.*;

public class PatProject
{
	/** 
	 过敏标签
	 
	 @param dtPat
	 @param drs
	 @param patientId
	 @param StatusDatas
	*/
	public final void GuoMin(DataTable dtPat, DataTable drs, String patientId, ArrayList<StatusData> StatusDatas)
	{
		ArrayList<String> list = new ArrayList<String>();
		DataRow[] drs_gm = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		String gms = "";
		for (DataRow dr1 : drs_gm)
		{
			String gm = dr1["ITEM_VALUE"].toString().trim();
			//if (!gm.Contains("(+)")) continue;
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
		DataRow[] dr = drs.Select(String.format("ALLERGY <> '' and  ALLERGY <> '无' and PATIENT_ID = '%1$s'", patientId));
		if (dr.length > 0)
		{

			String value = dr[0]["ALLERGY"].toString();
			gms = tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms) ? value : gms + "," + value;
		}

		String[] sarray = gms.split("[,]", -1); //拆分subarray

		//strList += "[";
		//foreach (var ar in sarray)
		//{
		//    //list.Add(ar);
		//    strList += "'" + ar + "',";
		//}
		//strList.TrimEnd(',');
		//strList += "]";

		for (String ar : sarray)
		{
			list.add(ar);
		}

		if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms))
		{
			StatusDatas.add(FillDataAllargy(null, "敏", null, null, list.toArray(new String[0]))); //JsonUtil.Object2JsonString(list)//strList
		}
	}


	/** 
	 组装病危病重信息
	 
	 @param model
	 @param StatusDatas
	*/
	public final void Status(String status, ArrayList<StatusData> StatusDatas)
	{
		if (status.toString().equals("1"))
		{
			StatusDatas.add(FillData(null, "危", null, null, "病危", ""));
		}
		else if (status.toString().equals("2"))
		{
			StatusDatas.add(FillData(null, "重", null, null, "病危", ""));
		}
	}

	/** 
	 组装风险评估信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void PingGu(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		//压疮
		DataRow[] drs_yc = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='压疮风险护理单'", patientId));
		if (drs_yc.length > 0)
		{
			LogUtil.Info("***********************ITEM_VALUE**********************");
			String value = drs_yc[0]["item_value"].toString();
			LogUtil.Info("***********************NURSE_FLAG**********************");
			String nurse_flag = drs_yc[0]["nurse_flag"].toString();
			LogUtil.Info("***********************获取完成**********************");
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "压疮信息-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "压疮信息-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "压疮信息-高危";
			}
			else
			{
				nurse_flag = "压疮信息-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_yc[0]["time_point"].toString()), "压", value, drs_yc[0]["creator"].toString() + " 护士", null, nurse_flag));
		}

		//跌倒
		DataRow[] drs_dd = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='跌倒坠床评估单'", patientId));
		if (drs_dd.length > 0)
		{
			String value = drs_dd[0]["item_value"].toString();
			String nurse_flag = drs_dd[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "跌倒坠床-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "跌倒坠床-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "跌倒坠床-高危";
			}
			else
			{
				nurse_flag = "跌倒坠床-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_dd[0]["time_point"].toString()), "跌", value, drs_dd[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
		//自理
		DataRow[] drs_zl = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='自理能力评估单'", patientId));
		if (drs_zl.length > 0)
		{
			String value = drs_zl[0]["item_value"].toString();
			String nurse_flag = drs_zl[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "自理-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "自理-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "自理-高危";
			}
			else
			{
				nurse_flag = "自理-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_zl[0]["time_point"].toString()), "自", value, drs_zl[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
		//导管滑脱
		DataRow[] drs_gl = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='导管滑脱评估单'", patientId));
		if (drs_gl.length > 0)
		{
			String value = drs_gl[0]["item_value"].toString();
			String nurse_flag = drs_gl[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "导管滑脱-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "导管滑脱-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "导管滑脱-高危";
			}
			else
			{
				nurse_flag = "导管滑脱-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_gl[0]["time_point"].toString()), "导", value, drs_gl[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
		//暴力
		DataRow[] drs_bl = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='暴力风险评估及护理干预记录表'", patientId));
		if (drs_bl.length > 0)
		{
			String value = drs_bl[0]["item_value"].toString();
			String nurse_flag = drs_bl[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "暴力-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "暴力-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "暴力-高危";
			}
			else
			{
				nurse_flag = "暴力-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_bl[0]["time_point"].toString()), "暴", value, drs_bl[0]["creator"].toString() + " 护士", null, nurse_flag));
		}

		//自伤
		DataRow[] drs_zs = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='自杀风险评估及护理干预记录表'", patientId));
		if (drs_zs.length > 0)
		{
			String value = drs_zs[0]["item_value"].toString();
			String nurse_flag = drs_zs[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "自伤-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "自伤-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "自伤-高危";
			}
			else
			{
				nurse_flag = "自伤-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_zs[0]["time_point"].toString()), "伤", value, drs_zs[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
		//擅自离院
		DataRow[] drs_sl = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='外走风险评估与护理干预记录表'", patientId));
		if (drs_sl.length > 0)
		{
			String value = drs_sl[0]["item_value"].toString();
			String nurse_flag = drs_sl[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "擅自离院-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "擅自离院-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "擅自离院-高危";
			}
			else
			{
				nurse_flag = "擅自离院-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_sl[0]["time_point"].toString()), "离", value, drs_sl[0]["creator"].toString() + " 护士", null, nurse_flag));
		}

		//噎食
		DataRow[] drs_yeshi = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='噎食风险评估与护理干预记录表'", patientId));
		if (drs_yeshi.length > 0)
		{
			String value = drs_yeshi[0]["item_value"].toString();
			String nurse_flag = drs_yeshi[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "噎食-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "噎食-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "噎食-高危";
			}
			else
			{
				nurse_flag = "噎食-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_yeshi[0]["time_point"].toString()), "噎", value, drs_yeshi[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
		//VTE静脉血栓栓塞
		DataRow[] drs_vte = dtPat.Select(String.format("patient_id = '%1$s' and theme_code='静脉血栓栓塞风险评估表'", patientId));
		if (drs_vte.length > 0)
		{
			String value = drs_vte[0]["item_value"].toString();
			String nurse_flag = drs_vte[0]["nurse_flag"].toString();
			if (nurse_flag.equals("低危"))
			{
				value = "低度风险/" + value + "分";
				nurse_flag = "静脉血栓-低危";
			}
			else if (nurse_flag.equals("中危"))
			{
				value = "中度风险/" + value + "分";
				nurse_flag = "静脉血栓-中危";
			}
			else if (nurse_flag.equals("高危"))
			{
				value = "高度风险/" + value + "分";
				nurse_flag = "静脉血栓-高危";
			}
			else
			{
				nurse_flag = "静脉血栓-" + nurse_flag;
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_vte[0]["time_point"].toString()), "塞", value, drs_vte[0]["creator"].toString() + " 护士", null, nurse_flag));
		}
	}
	/** 
	 组装饮食信息
	 
	 @param dtPat
	 @param  drs
	 @param patientId
	 @param StatusDatas
	*/
	public final void YinShi(DataTable dtPat, DataTable drs, String patientId, ArrayList<StatusData> StatusDatas)
	{
		String gms = "";
		DataRow[] drs_ys = drs.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		String nurseName = "";
		java.time.LocalDateTime dataTime = null;
		if (drs_ys.length > 0)
		{
			for (DataRow dr1 : drs_ys)
			{
				gms += dr1["ITEM_NAME"].toString().split("[_]", -1)[1] + ",";
			}
			if (gms.contains(','))
			{
				gms = gms.substring(0, gms.length() - 1);
			}
			nurseName = drs_ys[0]["CREATOR"].toString();
			dataTime = java.time.LocalDateTime.parse(drs_ys[0]["TIME_POINT"].toString());
		}

		DataRow[] dr = dtPat.Select(String.format("MEAL <> '' and  MEAL <> '无' and PATIENT_ID = '%1$s'", patientId));
		if (dr.length > 0)
		{
			String value = dr[0]["MEAL"].toString();
			if (!value.contains("普食"))
			{
				gms = tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms) ? value : gms + "," + value;
			}
		}
		if (!tangible.DotNetToJavaStringHelper.isNullOrEmpty(gms))
		{
			StatusDatas.add(FillData(dataTime, "食", gms, nurseName, null, ""));
		}
	}


	/** 
	 组装手术信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void ShouShu(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs_ss = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));

		if (drs_ss.length > 0)
		{
			int shoushu = (((java.time.LocalDateTime)(java.time.LocalDateTime.now().toString("yyyy-MM-dd"))).Date - ((java.time.LocalDateTime)(java.time.LocalDateTime.parse(drs_ss[0]["SCHEDULED_DATE_TIME"].toString()).toString("yyyy-MM-dd"))).Date).Days;
			String shuhou = "";
			if (shoushu > 0)
			{
				shuhou = "(" + shoushu + "天)";
			}
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_ss[0]["SCHEDULED_DATE_TIME"].toString()), "术", drs_ss[0]["OPERATION"].toString() + shuhou, "", null, ""));
		}
	}


	/** 
	 组装高温信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void GaoWen(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs_tw = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		if (drs_tw.length > 0 && (double)(drs_tw[0]["ITEM_VALUE"]) >= 38.5)
		{
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_tw[0]["TIME_POINT"].toString()), "高", "体温：" + drs_tw[0]["ITEM_VALUE"].toString() + "℃", drs_tw[0]["CREATOR"].toString() + "护士", null, "高温"));
		}
	}

	/** 
	 组装发热信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void FaRe(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs_tw = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		if (drs_tw.length > 0 && (double)(drs_tw[0]["ITEM_VALUE"]) >= 37.5 && (double)(drs_tw[0]["ITEM_VALUE"]) < 38.5)
		{
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs_tw[0]["TIME_POINT"].toString()), "热", "体温：" + drs_tw[0]["ITEM_VALUE"].toString() + "℃", drs_tw[0]["CREATOR"].toString() + "护士", null, ""));
		}
	}

	/** 
	 组装隔离信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void GeLi(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		if (drs.length > 0)
		{
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs[0]["TIME_POINT"].toString()), "隔", "隔离：" + drs[0]["ITEM_VALUE"].toString(), drs[0]["CREATOR"].toString() + "护士", null, ""));
		}
	}
	/** 
	 组装陪护信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void PeiHu(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		if (drs.length > 0)
		{
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs[0]["TIME_POINT"].toString()), "陪", drs[0]["ITEM_VALUE"].toString(), drs[0]["CREATOR"].toString() + "护士", null, ""));
		}
	}
	/** 
	 组装保密信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void BaoMi(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		DataRow[] drs = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
		if (drs.length > 0)
		{
			StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs[0]["TIME_POINT"].toString()), "密", drs[0]["ITEM_VALUE"].toString(), drs[0]["CREATOR"].toString() + "护士", null, ""));
		}
	}
	/** 
	 组装优势病种信息
	 
	 @param dtPat
	 @param patientId
	 @param StatusDatas
	*/
	public final void YouShi(DataTable dtPat, String patientId, ArrayList<StatusData> StatusDatas)
	{
		if (dtPat != null)
		{
			DataRow[] drs = dtPat.Select(String.format("PATIENT_ID = '%1$s'", patientId));
			if (drs.length > 0)
			{
				StatusDatas.add(FillData(java.time.LocalDateTime.parse(drs[0]["TIME_POINT"].toString()), "优", drs[0]["ITEM_VALUE"].toString(), drs[0]["CREATOR"].toString() + "护士", null, ""));
			}
		}
	}
	/** 
	 数据组装
	 
	 @param oper_time
	 @param status_name
	 @param oper_name
	 @param Operator
	 @param subarray
	 @param nurse_flag
	 @return 
	*/
	private StatusData FillData(java.time.LocalDateTime oper_time, String status_name, String oper_name, String Operator, String subarray, String nurse_flag)
	{
		StatusData StatusData = new StatusData();
		if (oper_time != null)
		{
			StatusData.oper_time = DateUtil.DateTimeToStamp((java.time.LocalDateTime)oper_time);
		}
		StatusData.status_name = status_name;
		StatusData.oper_name = oper_name;
		StatusData.Operator = Operator;
		StatusData.subarray = null;
		StatusData.nurse_flag = nurse_flag;
		return StatusData;
	}


	/** 
	 数据组装
	 
	 @param oper_time
	 @param status_name
	 @param oper_name
	 @param Operator
	 @param subarray
	 @return 
	*/
	private StatusData FillDataAllargy(java.time.LocalDateTime oper_time, String status_name, String oper_name, String Operator, String[] subarray)
	{
		StatusData StatusData = new StatusData();
		if (oper_time != null)
		{
			StatusData.oper_time = DateUtil.DateTimeToStamp((java.time.LocalDateTime)oper_time);
		}
		StatusData.status_name = status_name;
		StatusData.oper_name = oper_name;
		StatusData.Operator = Operator;
		StatusData.subarray = subarray;
		return StatusData;
	}

}