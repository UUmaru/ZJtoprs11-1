<template>
	<div class="home">
		<!-- <div class="content_bg"></div> -->
		<div class="left"></div>
		<div class="left_t_bg"></div>
		<div class="right"></div>
		<div class="top_bg"></div>
		<div class="top">
			<div class="title">
				<img src="../../static/img/icon.png" />
				<span>南浔古镇交通接驳及安保调度管理系统</span>
			</div>
			<!-- <div class="user">
				<img src="../../static/img/user.png" />
				<span>管理员</span>
			</div> -->
			<div class="sys_time">
				<span>{{time}}</span>
				<span>{{date}}</span>
			</div>
			<div class="top_r">
				<img src="../../static/img/admin.png" @click="goManage" />
				<img src="../../static/img/dianci.png" @click="showDianci" />
				<img src="../../static/img/user_r.png" @click="showUserBottom" />
			</div>

			<div v-if="dianciFlag" class="dianci_list">
				<div class="dianci_out"></div>
				<div class="dianci_in"></div>
				<div v-if="alarmList.length>0" style="display: flex; flex-direction: column;overflow: auto;">
					<span class="dianci_title">以下待充电:</span>
					<div class="dianci_i" v-for="(item,index) in alarmList" :key="index">
						<span class="dianci_name" style="font-size: 0.12rem;text-align: left;">{{formatTime(item.alarmTime*1000)}}</span>
						<div class="dianci_i_t">
							<span class="dianci_name">{{item.vehicleName}}</span>
							<span class="dianci_r">{{formatAlarmCode(item.alarmCode)}}</span>
						</div>	
					</div>
				</div>
				<div v-else>
					<span class="dianci_title">无通知</span>
				</div>

			</div>

			<div v-if="showUserFlag" class="top_out">
				<div class="triangle"></div>
				<!-- <span class="top_out_b" style="margin-top: 0.04rem;margin-bottom: 0.04rem;">修改密码</span> -->
				<span class="top_out_b" @click="loginOut">退出登录</span>
			</div>
		</div>
		<div class="content">
			<div id="map"></div>
			<div v-if="!dianciFlag" class="search_c">
				<img class="search_img" src="../../static/img/search.png" />
				<input class="search_input" autocomplete="off" id="search" type="text" placeholder="按名字或者编号搜索" name="searchStr"
				 v-model="searchStr" @keyup.enter="searchText" @input="searchInput" @blur="blur"/>
				<!-- <div class="search_btn">搜索</div> -->
				<div v-if="searchList.length>0" class="search_result">
					<span v-for="(item,index) in searchList" :key="index" class="search_item" @click="searchClick(item)">
						{{item.name?item.name:item.username}}
					</span>
				</div>
			</div>
			<div v-if="!dianciFlag" class="analysis_c">
				<div :class="isAnalysis?'analysis_p':'analysis'" @click="analysisDd">调度分析</div>
				<div v-if="authorities.includes('交通接驳管理人员管理') || authorities.includes('交通接驳打捞船管理') || authorities.includes('交通接驳安保人员管理')"
				 :class="popupWorkFlag?'analysis_p':'analysis'" @click="openPopupWork">工作分析</div>
			</div>
			<!-- <img v-else class="rightMenuImg" src="../../static/img/open_close_l.png" @click="showRight" /> -->
			<div class="main_left">

				<transition name="left">
					<div v-show="showLayersFlag" class="left_bg">
						<!-- <transition-group name="layers" tag="div" class="left_bg"> -->
						<div class="left_back">
							<img src="../../static/img/left_back.png" />
							<div class="left_back_arrow" @click="showLeft"></div>
						</div>
						<img class="layer_manage" src="../../static/img/layer_manage.png" />
						<div v-if="authorities.includes('交通接驳安保车辆管理')" class="tuceng_item" @click="toggleLayer('电动车')">
							<img class="light" :src="layersInfo['电动车'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['电动车'].checked? '':'0.6'}" src="../../static/img/tuceng_ddc.png" />
						</div>
						<div v-if="authorities.includes('交通接驳游船管理')" class="tuceng_item" @click="toggleLayer('游船')">
							<img class="light" :src="layersInfo['游船'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['游船'].checked? '':'0.6'}" src="../../static/img/tuceng_yc.png" />
						</div>
						<div v-if="authorities.includes('交通接驳打捞船管理')" class="tuceng_item" @click="toggleLayer('打捞船')">
							<img class="light" :src="layersInfo['打捞船'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['打捞船'].checked? '':'0.6'}" src="../../static/img/tuceng_dlc.png" />
						</div>
						<div v-if="authorities.includes('交通接驳安保人员管理')" class="tuceng_item" @click="toggleLayer('安保')">
							<img class="light" :src="layersInfo['安保'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['安保'].checked? '':'0.6'}" src="../../static/img/tuceng_ab.png" />
						</div>

						<div v-if="authorities.includes('交通接驳保洁人员管理')" class="tuceng_item" @click="toggleLayer('保洁')">
							<img class="light" :src="layersInfo['保洁'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['保洁'].checked? '':'0.6'}" src="../../static/img/tuceng_bj.png" />
						</div>

						<div v-if="authorities.includes('交通接驳管理人员管理')" class="tuceng_item" @click="toggleLayer('管理人员')">
							<img class="light" :src="layersInfo['管理人员'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['管理人员'].checked? '':'0.6'}" src="../../static/img/tuceng_manager.png" />
						</div>
						<div v-if="authorities.includes('监控点监控视频查看')" class="tuceng_item" @click="toggleLayer('监控')">
							<img class="light" :src="layersInfo['监控'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['监控'].checked? '':'0.6'}" src="../../static/img/tuceng_jk.png" />
						</div>
						<div v-if="authorities.includes('交通接驳打捞船标准路线管理')" class="tuceng_item" @click="toggleLayer('打捞路线')">
							<img class="light" :src="layersInfo['打捞路线'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['打捞路线'].checked? '':'0.6'}" src="../../static/img/tuceng_dllx.png" />
						</div>
						<div v-if="authorities.includes('交通接驳安保人员标准路线管理')" class="tuceng_item" @click="toggleLayer('巡检范围')">
							<img class="light" :src="layersInfo['巡检范围'].checked?imgLightOn:imgLightOff" />
							<img class="tuceng_r" :style="{opacity: layersInfo['巡检范围'].checked? '':'0.6'}" src="../../static/img/tuceng_xjfw.png" />
						</div>
						<!-- </transition-group> -->

					</div>
				</transition>
			</div>
			<transition name="right">
				<div v-show="showRightFlag" class="right_view">
					<div class="right_back">
						<img src="../../static/img/right_back.png" />
						<div class="right_back_arrow" @click="hideRight"></div>
					</div>
					<div class="item_list">
						<div v-for="item in rightItems">
							<div :id="item" v-if="layersInfo[item].checked" :class="selectedRightItem==item?'item_i_p':'item_i'" @click="selectDetail(item)">
								<span>{{item=="管理人员"?"综合管理":item}}</span>
							</div>
						</div>

					</div>
					<div v-if="checkLayer.poiList" class="right_c">
						<div v-if="layersInfo[selectedRightItem].checked">
							<div class="r_top">
								<img :src="checkLayer.iconUrl" />
								<span class="r_name">{{selectedRightItem=="管理人员"?"综合管理":selectedRightItem}}</span>
								<span class="r_num">合计{{getTotal(checkLayer.poiList)}}</span>
								<div v-if="selectedRightItem=='安保'" style="display: flex;align-items: center;">
									<span class="r_num"><span style="color: #00FF00;">在线:</span>{{onlineCount}}</span>
									<span class="r_num" style="margin-left: 0.06rem;"><span style="color: #FF514C;">离线:</span>{{offlineCount}}</span>
								</div>
							</div>
							<div v-if="selectedRightItem=='安保'||selectedRightItem=='管理人员'||selectedRightItem=='保洁'" class="r_list">
								<div v-for="(item,index) in checkLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
									<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
									<div v-if="item.gpsCode" class="layer_item">
									<span :id="item.username" :class="checkedDetailLayer.username==item.username?'item_l_p':'item_l'">{{item.username}}</span>
									<span :class="(item.currentStatus=='静止' || item.currentStatus=='行驶'||item.currentStatus=='定位精度差')?'text_on':'text_off'" :style="{color:formatStatus(item)?'#ff0000':''}">{{item.currentStatus?item.currentStatus:'不在线'}}</span>
									</div>
								</div>
							</div>
							<div v-else-if="selectedRightItem=='游船' || selectedRightItem=='打捞船' || selectedRightItem=='电动车'" class="r_list">
								<div v-for="(item,index) in checkLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
									<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
									<div v-if="item.gpsCode" class="layer_item" >
									<span :id="item.name" :class="(checkedDetailLayer.name)==(item.name)?'item_l_p':'item_l'">{{item.name}}</span>
									<!-- <span :class="item.currentStatus?'text_on':'text_off'">{{item.currentStatus?'在线':'不在线'}}</span> -->
									<span :class="(item.currentStatus=='静止' || item.currentStatus=='行驶'||item.currentStatus=='定位精度差')?'text_on':'text_off'" :style="{color:formatStatus(item)?'#ff0000':''}">{{item.currentStatus?item.currentStatus:'不在线'}}</span>
									</div>
								</div>
							</div>
							<div v-else class="r_list">
								<div class="layer_item" v-for="(item,index) in checkLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
									<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
									<span :id="item.name" :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.name}}</span>
									<!-- <span v-if="selectedRightItem=='电动车'" :class="item.currentStatus?'text_on':'text_off'">{{item.currentStatus?'在线':'不在线'}}</span> -->
								</div>
							</div>
						</div>
						<div v-else style="height: 100%; display: flex; justify-content: center; align-items:center;">
							<span class="r_num">图层未打开</span>
						</div>
						<!-- <div class="right_t">
						<span>详情列表</span>
					</div> -->

						<!-- <img class="r_close" src="../../static/img/close.png" @click="hideRight"></img> -->
					</div>
				</div>
			</transition>
			<img v-if="!showLayersFlag" class="openImg" style="width: 0.33rem;height: 0.93rem;" src="../../static/img/right_back_arrow.png"
			 @click="showLeft" />
			<img v-if="!showRightFlag" class="rightMenuImg" src="../../static/img/right_back_arrow.png" @click="showRight" />
		</div>
		<popup-detail ref="popDetail" v-if="popupDetailFlag" class="pop_d" @closePopupDetail="closePopupDetail" :style="{ top: popY +'px', left: popX + 'px' }"></popup-detail>
		<popup-calendar v-if="popupCalendarFlag" class="pop_d" @closePopupCalendar="closePopupCalendar" :style="{ top: popY +'px', left: popupCalendarX + 'px' }"></popup-calendar>
		<ripple v-if="rippleShowFlag" class="pop_d ripple_mid"></ripple>
		<!-- <analysis-result v-if="analysisResultFlag" class="pop_d ripple_mid"></analysis-result> -->
		<popup-guiji v-if="popupGuijiFlag" class="pop_d pop_g" :guijiInfo="guijiInfo" @closePopupGuiji="closePopupGuiji"></popup-guiji>
		<div class="tools">

			<img v-if="popupGuijiFlag" :src="isCalGuiji?require('../../static/img/jisuan_p.png'):require('../../static/img/jisuan.png')"
			 @click="calGuiji()" />

			<img v-if="isDrawLine" src="../../static/img/tool_line_p.png" @click="clearMeasureDistance" />
			<img v-else src="../../static/img/tool_line.png" title="测距" @click="measureDistance" />
			<img v-if="isDrawPolygon" src="../../static/img/tool_area_p.png" @click="clearMeasureArea" />
			<img v-else src="../../static/img/tool_area.png" title="测面" @click="measureArea" />
			<img src="../../static/img/tool_re.png" title="复位" @click="reset" />
			<img src="../../static/img/sacle_add.png" title="放大" @click="scale(0)" />
			<img src="../../static/img/sacle_reduce.png" title="缩小" @click="scale(1)" />
		</div>
		<popup-work v-if="popupWorkFlag" @closePopupWork="closePopupWork"></popup-work>
		<popup-guijiFx class="pop_d pop_g" :style="{ top: popFxY +'px', left: popFxX + 'px' }" v-if="popupGuijiFxFlag"
		 :fxInfo="fxInfo" :lastFxInfo="lastFxInfo" @closePopupGuijiFx="closePopupGuijiFx"></popup-guijiFx>
		<popup-ply v-if="popupPlyFlag" class="pop_d" :style="{ top: popY +'px', left: popX + 'px' }" :plyInfo="plyInfo"
		 @closePopupPly="closePopupPly"></popup-ply>

		<div v-if="isAnalysis" class="analysis_choose">
			<span class="analysis_title">调度分析</span>
			<div v-if="authorities.includes('交通接驳安保车辆管理')" class="analysis_choose_item">
				<span>电动车</span>
				<my-switch :value="analysisType=='电动车'" :type="'电动车'" @toggle="toggleType"></my-switch>
			</div>
			<div v-if="authorities.includes('交通接驳游船管理')" class="analysis_choose_item">
				<span>游船</span>
				<my-switch :value="analysisType=='游船'" :type="'游船'" @toggle="toggleType"></my-switch>
			</div>
			<div v-if="authorities.includes('交通接驳安保人员管理')" class="analysis_choose_item">
				<span>安保人员</span>
				<my-switch :value="analysisType=='安保'" :type="'安保'" @toggle="toggleType"></my-switch>
			</div>
		</div>
		<popup-ananlysis-tip v-if="ananlysisTipFlag" class="pop_d pop_g" :tip="tipMsg" style="top:40%"></popup-ananlysis-tip>
		<transition name="right">
			<div v-show="showAnalysisRightFlag&&analysisType" class="right_view">
				<div class="right_back">
					<img src="../../static/img/right_back.png" />
					<div class="right_back_arrow" @click="hideRight"></div>
				</div>
				<div class="item_list" style="justify-content: center;">
					<div :id="analysisType" class="item_i_p">
						<span>{{analysisType}}</span>
					</div>
				</div>
				<div v-if="analysisLayer.poiList" class="right_c">
					<div class="r_top">
						<img :src="analysisLayer.iconUrl" />
						<span class="r_name">{{analysisType}}</span>
						<!-- <span class="r_num">合计{{analysisLayer.poiList.length}}</span> -->
					</div>
					<div v-if="analysisType=='安保'" class="r_list">
						<div class="layer_item" v-for="(item,index) in analysisLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
							<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
							<div v-if="item.gpsCode">
							<span :id="item.username" :class="checkedDetailLayer.username==item.username?'item_l_p':'item_l'">{{item.username}}</span>
							<span class="text_on">{{formatDis(item.dis)}}</span>
							</div>
						</div>
					</div>
					<div v-else-if="analysisType=='游船'" class="r_list">
						<div class="layer_item" v-for="(item,index) in analysisLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
							<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
							<div v-if="item.gpsCode">
							<span :id="item.name" :class="(checkedDetailLayer.name)==(item.name)?'item_l_p':'item_l'">{{item.name}}</span>
							<span class="text_on">{{formatDis(item.dis)}}</span>
							</div>
						</div>
					</div>
					<div v-else class="r_list">
						<div class="layer_item" v-for="(item,index) in analysisLayer.poiList" :key="index" @click="chooseDetailLayer(item)">
							<!-- <span :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.serial}}</span> -->
							<div v-if="item.gpsCode">
							<span :id="item.name" :class="checkedDetailLayer.name==item.name?'item_l_p':'item_l'">{{item.name}}</span>
							<span class="text_on">{{formatDis(item.dis)}}</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</transition>
	</div>
</template>

<script>
	
	var x_PI = 3.14159265358979324 * 3000.0 / 180.0;
	var PI = 3.1415926535897932384626;
	var a = 6378245.0;
	var ee = 0.00669342162296594323;
	
	function gcj02towgs84(lng, lat) {
	  var lat = +lat;
	  var lng = +lng;
	  if (out_of_china(lng, lat)) {
	    return [lng, lat]
	  } else {
	    var dlat = transformlat(lng - 105.0, lat - 35.0);
	    var dlng = transformlng(lng - 105.0, lat - 35.0);
	    var radlat = lat / 180.0 * PI;
	    var magic = Math.sin(radlat);
	    magic = 1 - ee * magic * magic;
	    var sqrtmagic = Math.sqrt(magic);
	    dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
	    dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
	    var mglat = lat + dlat;
	    var mglng = lng + dlng;
	    return [lng * 2 - mglng, lat * 2 - mglat]
	  }
	};
	
	function transformlat(lng, lat) {
	  var lat = +lat;
	  var lng = +lng;
	  var ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
	  ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
	  ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
	  ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
	  return ret
	};
	
	function transformlng(lng, lat) {
	  var lat = +lat;
	  var lng = +lng;
	  var ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
	  ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
	  ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
	  ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
	  return ret
	};
	
	/**
	 * 判断是否在国内，不在国内则不做偏移
	 * @param lng
	 * @param lat
	 * @returns {boolean}
	 */
	function out_of_china(lng, lat) {
	  var lat = +lat;
	  var lng = +lng;
	  // 纬度3.86~53.55,经度73.66~135.05
	  return !(lng > 73.66 && lng < 135.05 && lat > 3.86 && lat < 53.55);
	};
	
	import mySwitch from '../../components/my_switch.vue'
	import popupDetail from '../../components/popup_detail/popup_detail.vue'
	import popupCalendar from '../../components/popup_calendar/popup_calendar.vue'
	import ripple from '../../components/ripple/ripple.vue'
	import popupGuiji from '../../components/popup_guiji/popup_guiji.vue'
	import popupGuijiFx from '../../components/popup_guijiFx/popup_guijiFx.vue'
	import analysisResult from '../../components/analysis_result/analysis_result.vue'
	import popupWork from '../../components/popup_work/popup_work.vue'
	import popupPly from '../../components/popup_ply/popup_ply.vue'
	import popupAnanlysisTip from '../../components/popup_ananlysis_tip/popup_ananlysis_tip.vue'
	import http from '../../common/http.js';
	import {Loading} from 'element-ui';
	var Base64 = require('js-base64').Base64;
	// @ is an alias to /src
	var esri = require("esri-leaflet");
	var Map2 = L.Map.extend({
		// 覆盖源码
		openPopup: function(popup, latlng, options) {
			if (!(popup instanceof L.Popup)) {
				popup = new L.Popup(options).setContent(popup);
			}

			if (latlng) {
				popup.setLatLng(latlng);
			}

			if (this.hasLayer(popup)) {
				return this;
			}

			if (this._popup && this._popup.options.autoClose) {
				//                this.closePopup(); 修改了这块
			}

			this._popup = popup;
			return this.addLayer(popup);
		}
	});

	var map2 = function(id, options) {
		return new Map2(id, options);
	};

	export default {
		name: 'Home',
		components: {
			mySwitch,
			popupDetail,
			popupCalendar,
			ripple,
			popupGuiji,
			analysisResult,
			popupWork,
			popupGuijiFx,
			popupPly,
			popupAnanlysisTip
		},
		data() {
			return {
				selectedRightItem: "电动车", //右边选中item
				dianciFlag: false, //电动车电量警报列表框
				popupGuijiFxFlag: false,
				popupPlyFlag: false,

				layersInfo: {
					"电动车": {
						num: 3,
						iconUrl: require("../../static/img/icon_ddc.png"),
						poiUrl: require("../../static/img/ddc.gif"),
						checked: false,
						poiList: []
					},
					"游船": {
						num: 5,
						iconUrl: require("../../static/img/icon_yc.png"),
						poiUrl: require("../../static/img/yc.gif"),
						checked: false,
						poiList: []
					},
					"打捞船": {
						num: 2,
						iconUrl: require("../../static/img/icon_dlc.png"),
						poiUrl: require("../../static/img/dlc.gif"),
						checked: false,
						poiList: []
					},
					"安保": {
						num: 3,
						iconUrl: require("../../static/img/icon_zhzf.png"),
						poiUrl: require("../../static/img/ab.gif"),
						checked: false,
						poiList: []
					},
					"保洁": {
						num: 3,
						iconUrl: require("../../static/img/icon_bj.png"),
						poiUrl: require("../../static/img/icon_bj.png"),
						checked: false,
						poiList: []
					},
					"管理人员": {
						num: 4,
						iconUrl: require("../../static/img/poi_glry.png"),
						poiUrl: require("../../static/img/poi_glry.png"),
						checked: false,
						poiList: []
					},
					"监控": {
						num: 2,
						iconUrl: require("../../static/img/poi_jk.png"),
						poiUrl: require("../../static/img/poi_jk.png"),
						checked: false,
						poiList: []
					},
					"打捞路线": {
						num: 1,
						iconUrl: require("../../static/img/dlclx.png"),
						poiUrl: require("../../static/img/dlzd.gif"),
						checked: false,
						poiList: []
					},
					"打捞站点": {
						num: 1,
						iconUrl: require("../../static/img/dlzd.png"),
						poiUrl: require("../../static/img/dlzd.gif"),
						checked: false,
						poiList: []
					},
					"巡检范围": {
						num: 1,
						iconUrl: require("../../static/img/abxj.png"),
						poiUrl: require("../../static/img/ab.gif"),
						checked: false,
						poiList: []
					},
					"休息点": {
						num: 1,
						iconUrl: require("../../static/img/poi_xxd.png"),
						poiUrl: require("../../static/img/poi_xxd.png"),
						checked: true,
						poiList: []
					},
					"游船码头": {
						num: 1,
						iconUrl: require("../../static/img/poi_ycmt.png"),
						poiUrl: require("../../static/img/poi_ycmt.png"),
						checked: true,
						poiList: []
					},

				},
				rightItems: ["电动车", "游船", "打捞船", "安保", "保洁", "管理人员", "监控", "打捞路线", "巡检范围", "休息点", "游船码头"],
				imgLightOn: require("../../static/img/light_on.png"),
				imgLightOff: require("../../static/img/light_off.png"),
				map: null,
				searchStr: "",
				showUserFlag: false,
				popupDetailFlag: false,
				popupCalendarFlag: false,
				popupGuijiFlag: false,
				analysisResultFlag: false,
				popupWorkFlag: false, //工作分析弹窗
				guijiInfo: {},
				popX: 0,
				popY: 0,
				isAnalysis: false, //是否按下调度分析
				isAnalysisWork: false, //是否按下工作分析按钮
				rippleShowFlag: false,

				showLayersFlag: true,
				showRightFlag: true,
				checkLayer: {},
				checkLayerList: [],
				checkedDetailLayer: {},
				screenWidth: 0,
				screenHeight: 0,
				layers: {},
				guijiLayer: {},
				times: 0,
				markerInfo: {},
				isDrawLine: false,
				isDrawPolygon: false,
				geoServerUrl: "/gisServer/geoserver/ows",
				geoParams: {
					service: 'WFS',
					version: '1.1.0',
					request: 'GetFeature',
					typeName: 'oldtown:trf_security_car',
					outputFormat: 'application/json',
					srsName: 'EPSG:4326',
				},
				analysisType: "",
				ananlysisTipFlag: false,
				tipMsg: "",
				showAnalysisRightFlag: false,
				analysisItems: ["电动车", "安保", "游船"],
				analysisLayer: {},
				bigMarker: null, //点击放大的marker
				clickLayer: null,
				bigMarkerType: "",
				searchList: [],
				analysisBeforeType: "", //记录调度分析之前最新打开的图层
				alarmList: [],
				date: "2021/1/15", //系统日期
				time: '15:35:35', //系统时间
				onlineCount: 0,
				offlineCount: 0,
				fxInfo: "",
				lastFxInfo: "",
				isCalGuiji: false, //是否正在计算轨迹
				calGuijiList: [], //轨迹计算的点
				restMarkerList: [],
				hotlineTimer: null,
				checkedLength:0,
			}
		},
		created() {
			
			this.authorities = localStorage.getItem("authorities");
			console.log("authorities: " + JSON.stringify(this.authorities))
			if (this.authorities.includes("交通接驳安保车辆管理")) {
				//电动车
				this.layersInfo["电动车"].checked = true;
				this.selectedRightItem = "电动车"
			}else if (this.authorities.includes("交通接驳游船管理")) {
				//游船
				this.layersInfo["游船"].checked = true;
				this.selectedRightItem = "游船"
			}else if (this.authorities.includes("交通接驳打捞船管理")) {
				//打捞船
				this.layersInfo["打捞船"].checked = true;
				this.selectedRightItem = "打捞船"
			}else if (this.authorities.includes("交通接驳安保人员管理")) {
				//安保人员
				this.layersInfo["安保"].checked = true;
				this.selectedRightItem = "安保"
			}else if (this.authorities.includes("交通接驳管理人员管理")) {
				//管理人员
				this.layersInfo["管理人员"].checked = true;
				this.selectedRightItem = "管理人员"
			}else if (this.authorities.includes("交通接驳保洁人员管理")) {
				//保洁人员
				this.layersInfo["保洁"].checked = true;
				this.selectedRightItem = "保洁"
			}else if (this.authorities.includes("监控点监控视频查看")) {
				//监控
				this.layersInfo["监控"].checked = true;
				this.selectedRightItem = "监控"
			}else if (this.authorities.includes("交通接驳打捞船标准路线管理")) {
				//打捞路线
				this.layersInfo["打捞路线"].checked = true;
				this.selectedRightItem = "打捞路线"
			}else if (this.authorities.includes("交通接驳安保人员标准路线管理")) {
				//巡检范围
				this.layersInfo["巡检范围"].checked = true;
				this.selectedRightItem = "巡检范围"
			}
			this.checkedLength = 1;
			this.checkLayerList.unshift('休息点');
			this.checkLayerList.unshift(this.selectedRightItem);
			
			this.checkLayer = this.layersInfo[this.selectedRightItem];
		},
		mounted() {
			console.log("selectedRightItem: " + this.selectedRightItem);
			console.log(this.layersInfo[this.selectedRightItem].checked);
			this.getServeTime();
			this.screenWidth = document.body.clientWidth;
			this.screenHeight = document.body.clientHeight;
			console.log("screenWidth: " + this.screenWidth + " screenHeight: " + this.screenHeight)

			window.onresize = () => {
				return (() => {
					this.screenWidth = document.body.clientWidth;
					this.screenHeight = document.body.clientHeight;
					console.log("screenWidth: " + this.screenWidth + " screenHeight: " + this.screenHeight)
				})
			}
			var corner1 = L.latLng(30.9163, 120.353336) //设置左上角经纬度
			var corner2 = L.latLng(30.825491, 120.491524) //设置右下点经纬度
			var bounds = L.latLngBounds(corner1, corner2) //构建视图限制范围


			this.map = map2('map', {
				center: [30.878191, 120.426207],
				zoom: 14,
				maxZoom: 21,
				minZoom: 13,
				maxBounds: bounds,
				crs: L.CRS.EPSG4326, //设置坐标系4326
				zoomControl: false, //禁用 + - 按钮
				doubleClickZoom: false, // 禁用双击放大
				attributionControl: false // 移除右下角leaflet标识
			});

			var matrixIds = [];
			for (var i = 0; i < 22; ++i) {
				matrixIds[i] = {
					identifier: "" + i,
					topLeftCorner: new L.LatLng(90, -180)
				};
			}

			// esri.basemapLayer("Imagery").addTo(this.map);
			// L.tileLayer.chinaProvider('TianDiTu.Satellite.Map',{maxZoom:20,minZoom:5}).addTo(this.map);
			// esri.tiledMapLayer({
			// 	url: "http://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer", //地图切片服务的访问地址	
			// 	minZonm: 15,
			// 	maxZomm: 22,
			// }).addTo(this.map);


			var ign = new L.TileLayer.WMTS("/gisServer/geoserver/gwc/service/wmts", {
				layer: "nanxun_jbjbMaps", //图层名称
				tilematrixSet: "EPSG:4326", //GeoServer使用的网格名称
				width: 20,
				height: 20,
				format: 'image/png',
				maxZoom: 21,
				minZoom: 13,
				matrixIds: matrixIds,
			})
			this.map.addLayer(ign);

			this.map.on('click', (e) => {
				console.log("click: " + e.latlng)
				// this.map.setView(e.latlng)
				if (this.rippleShowFlag || this.popupGuijiFlag) {
					return;
				}
				if (this.isAnalysis && (!this.analysisResultFlag) && this.analysisType) {

					this.map.panTo(e.latlng);
					this.rippleShowFlag = true;
					this.analysisData(e.latlng);
				}
			});


			// const urlString = '/geoServer/geoserver/ows'
			// let params = {
			// 	service: 'WFS',
			// 	version: '1.1.0',
			// 	request: 'GetFeature',
			// 	typeName: 'nanxun:patrol_route_polygon',
			// 	outputFormat: 'application/json',
			// 	srsName: 'EPSG:4326',
			// 	cql_filter: "area=0"
			// }
			// const url = urlString + L.Util.getParamString(params, urlString)
			// console.log("getWFS url: " + url);
			// http.getWFS(url, params).then(res => {
			// 	console.log("getWFS: " + res);
			// 	var layer = L.geoJSON(res, {
			// 		style: function(feature) {
			// 			return {
			// 				stroke: true,
			// 				color: '#ffff00',
			// 				opacity: 1,
			// 				fillOpacity: 0.1,
			// 				fillColor: '#ffff7f',
			// 				weight: 2
			// 			}
			// 		},
			// 		// filter: function(feature, layer) {
			// 		// 	console.log("getWFS: " + feature);
			// 		// 	return feature.id=="patrol_route_polygon.6";
			// 		// }
			// 	})
			// 	// layer.options.filter = function(feature, layer) {
			// 	// 		console.log("getWFS: " + feature);
			// 	// 		return feature.id=="patrol_route_polygon.6";
			// 	// 	}
			// 	var layerList = layer.getLayers();
			// 	for (var i = 0; i < layerList.length; i++) {
			// 		layerList[0].addTo(this.map);
			// 		console.log("getWFS: " + layerList[i]);
			// 	}
			// })
			
			if (this.authorities.includes("交通接驳安保车辆管理")) {
				//电动车
				this.ddcSever();
			}
			if (this.authorities.includes("交通接驳游船管理")) {
				//游船
				this.yachtSever();
			}
			if (this.authorities.includes("交通接驳打捞船管理")) {
				//打捞船
				this.sweepSever()
			}
			if (this.authorities.includes("交通接驳安保人员管理")) {
				//安保人员
				this.securityStaffSever();
			}
			
			if (this.authorities.includes("交通接驳管理人员管理")) {
				//管理人员
				this.dockStaffSever();
			}
			
			if (this.authorities.includes("交通接驳保洁人员管理")) {
				//保洁人员
				this.cleanStaffSever();
			}
			
			setInterval(()=>{
				if(this.popupDetailFlag || this.isAnalysis || this.popupGuijiFlag || this.isAnalysisWork){
					return;
				}
				this.clickLayer = null;
				if (this.authorities.includes("交通接驳安保车辆管理")) {
					//电动车
					this.ddcSever();
				}
				if (this.authorities.includes("交通接驳游船管理")) {
					//游船
					this.yachtSever();
				}
				if (this.authorities.includes("交通接驳打捞船管理")) {
					//打捞船
					this.sweepSever()
				}
				if (this.authorities.includes("交通接驳安保人员管理")) {
					//安保人员
					this.securityStaffSever();
				}
				
				if (this.authorities.includes("交通接驳管理人员管理")) {
					//管理人员
					this.dockStaffSever();
				}
				
				if (this.authorities.includes("交通接驳保洁人员管理")) {
					//保洁人员
					this.cleanStaffSever();
				}
			},1000*10)
			
			if (this.authorities.includes("监控点监控视频查看")) {
				//监控
				this.monitorSever();
			}

			//码头 休息点
			this.pointSever();

			if (this.authorities.includes("交通接驳打捞船标准路线管理")) {
				//打捞路线
				this.sweepRoute();
				this.sweepPoint()
			}

			if (this.authorities.includes("交通接驳安保人员标准路线管理")) {
				//巡检范围
				this.polygonServe();
				//巡检路线
				this.securityRouteServe();
			}
			
			

			//测量图层
			this.drawGroupPolyline = L.featureGroup().addTo(this.map);
			this.drawGroupPolygon = L.featureGroup().addTo(this.map);
			//画图创建事件
			this.map.on(L.Draw.Event.CREATED, event => {
				const {
					layer,
					layerType
				} = event;
				if (layerType === "polygon") {
					//layer.getLatLngs()得到的多边形的经纬度集合，多边形得到的是一个二维数组，这里要取里面的数组，一定要注意
					let latlng = layer.getLatLngs()[0];
					//一个自定义的计算面积的函数
					let area = this.formatArea(latlng);
					this.addMeasureMarker(area, [latlng[latlng.length - 1].lat, latlng[latlng.length - 1].lng], this.drawGroupPolygon); //把画图之后计算的结果添加到地图上
					this.drawGroupPolygon.addLayer(layer);
				} else if (layerType === "polyline") {
					//polyline得到的是一个一维数组，直接使用 
					let latlng = layer.getLatLngs();
					//一个自定义的计算长度的函数
					let distance = this.formatLength(latlng);
					this.addMeasureMarker(distance, [latlng[latlng.length - 1].lat, latlng[latlng.length - 1].lng], this.drawGroupPolyline);
					this.drawGroupPolyline.addLayer(layer);
				}
				// drawGroup,groupLayer,把画图的图层添加到图层组方便管理
				// this.drawGroup.addLayer(layer);

			});
			// 结束绘制监听
			this.map.on(L.Draw.Event.DRAWSTOP, function() {
				console.log('结束绘制')

			});



			//添加画图的提示信息
			L.drawLocal.draw.handlers.polyline = {
				tooltip: {
					start: "点击地图开始画线",
					cont: "继续选择",
					end: "双击完成绘制"
				}
			};
			L.drawLocal.draw.handlers.polygon = {
				tooltip: {
					start: "点击地图开始绘制多边形",
					cont: "继续选择",
					end: "点击第一个顶点完成绘制"
				}
			};

		},
		methods: {
			ddcSever() {
				this.geoParams.typeName = 'oldtown:trf_security_car'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['电动车']) {
						this.map.removeLayer(this.layers['电动车'])
					}
					this.layers['电动车'] = L.geoJSON(res, {
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							console.log("pointToLayer 电动车: " + feature);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["电动车"].poiUrl,
								iconSize: [80, 110],
								iconAnchor: [40, 88],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								// offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["电动车"].checked){
						this.layers['电动车'].addTo(this.map)
					}
					this.layers['电动车'].eachLayer((layer) => {
						layer.on('click', (e) => {

							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["电动车"].poiUrl,
								iconSize: [96, 132],
								iconAnchor: [48, 106],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label'
								// offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "电动车"

							var params = {
								id: e.target.feature.properties.id
							}

							http.getCarById(params).then(res => {
								if (res.code == 200) {
									if (this.selectedRightItem != '电动车') {
										this.selectedRightItem = '电动车';
										this.checkLayerList.unshift(this.selectedRightItem);
									} else {

									}
									this.checkLayer = this.layersInfo['电动车'];
									if (this.showRightFlag) {
										this.$nextTick(() => {
											document.getElementById('电动车').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name;
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										})
									} else {
										if (this.isAnalysis) {
											this.showAnalysisRightFlag = true
										} else {
											this.showRightFlag = true;
										}
										setTimeout(() => {
											document.getElementById('电动车').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										}, 1000)
									}
									this.popX = e.originalEvent.clientX;
									this.popY = e.originalEvent.clientY;
									if (this.popX > this.screenWidth / 2) {
										this.popX = this.popX - 370 * this.screenWidth / 1920
									}
									if (this.popY > this.screenHeight / 2) {
										this.popY = this.popY - 440 * this.screenWidth / 1920
									}

									this.checkedDetailLayer = res.data;
									this.checkedDetailLayer.layerType = "电动车"
									// this.checkedDetailLayer.staffName = res.data.staffName;
									// this.checkedDetailLayer.staffTel = res.data.staffTel;
									console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)
									this.popupDetailFlag = true;
									this.$nextTick(() => {
										this.$refs.popDetail.detailInfo = this.checkedDetailLayer

									});
								}
							})

						});
					});
				})

				this.getCarList();
			},

			//接口获取所有电动车
			getCarList() {
				http.getAllCar().then(res => {
					if (res.code == 200) {
						this.layersInfo['电动车'].poiList = res.data;
					}

				})
			},

			//游船
			yachtSever() {
				this.geoParams.typeName = 'oldtown:trf_yacht'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['游船']) {
						this.map.removeLayer(this.layers['游船'])
					}
					this.layers['游船'] = L.geoJSON(res, {
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "游船";
							// this.layersInfo['游船'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船"].poiUrl,
								iconSize: [80, 110],
								iconAnchor: [40, 88],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								// offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["游船"].checked){
						this.layers['游船'].addTo(this.map)
					}
					this.layers['游船'].eachLayer((layer) => {
						layer.on('click', (e) => {

							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船"].poiUrl,
								iconSize: [96, 132],
								iconAnchor: [48, 106],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label'
								// offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "游船"

							var params = {
								id: e.target.feature.properties.id
							}

							http.getYachtById(params).then(res => {
								if (res.code == 200) {
									if (this.selectedRightItem != '游船') {
										this.selectedRightItem = '游船';
										this.checkLayerList.unshift(this.selectedRightItem);
									} else {

									}
									this.checkLayer = this.layersInfo['游船'];
									if (this.showRightFlag) {
										this.$nextTick(() => {
											document.getElementById('游船').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										})
									} else {
										if (this.isAnalysis) {
											this.showAnalysisRightFlag = true
										} else {
											this.showRightFlag = true;
										}
										setTimeout(() => {
											document.getElementById('游船').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										}, 1000)
									}
									this.popX = e.originalEvent.clientX;
									this.popY = e.originalEvent.clientY;
									if (this.popX > this.screenWidth / 2) {
										this.popX = this.popX - 370 * this.screenWidth / 1920
									}
									if (this.popY > this.screenHeight / 2) {
										this.popY = this.popY - 400 * this.screenWidth / 1920
									}

									// this.checkedDetailLayer = e.target.feature.properties;
									// this.checkedDetailLayer.staffName = res.data.staffName;
									// this.checkedDetailLayer.staffTel = res.data.staffTel;
									this.checkedDetailLayer = res.data;
									this.checkedDetailLayer.layerType = "游船"
									console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)
									this.popupDetailFlag = true;
									this.$nextTick(() => {
										this.$refs.popDetail.detailInfo = this.checkedDetailLayer

									});
								}
							})

						});
					});
				})
				this.getYachtList()
			},
			//接口获取所有游船
			getYachtList() {
				http.getAllYacht().then(res => {
					if (res.code == 200) {
						this.layersInfo['游船'].poiList = res.data;
					}

				})
			},

			//打捞船
			sweepSever() {
				this.geoParams.typeName = 'oldtown:trf_sweep'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['打捞船']) {
						this.map.removeLayer(this.layers['打捞船'])
					}
					this.layers['打捞船'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "打捞船";
							// this.layersInfo['打捞船'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["打捞船"].poiUrl,
								iconSize: [80, 110],
								iconAnchor: [40, 88],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								// offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["打捞船"].checked){
						this.layers['打捞船'].addTo(this.map)
					}
					this.layers['打捞船'].eachLayer((layer) => {
						layer.on('click', (e) => {

							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["打捞船"].poiUrl,
								iconSize: [96, 132],
								iconAnchor: [48, 106],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label'
								// offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "打捞船"

							var params = {
								id: e.target.feature.properties.id
							}
							http.getSweepById(params).then(res => {
								if (res.code == 200) {
									if (this.selectedRightItem != '打捞船') {
										this.selectedRightItem = '打捞船';
										this.checkLayerList.unshift(this.selectedRightItem);
									} else {

									}
									this.checkLayer = this.layersInfo['打捞船'];
									if (this.showRightFlag) {
										this.$nextTick(() => {
											document.getElementById('打捞船').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										})
									} else {
										this.showRightFlag = true;
										setTimeout(() => {
											document.getElementById('打捞船').scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
											let id = e.target.feature.properties.name
											document.getElementById(id).scrollIntoView({
												behavior: "smooth", // 默认 auto
												block: "center", // 默认 center
												inline: "nearest", // 默认 nearest
											});
										}, 1000)
									}
									this.popX = e.originalEvent.clientX;
									this.popY = e.originalEvent.clientY;
									if (this.popX > this.screenWidth / 2) {
										this.popX = this.popX - 370 * this.screenWidth / 1920
									}
									if (this.popY > this.screenHeight / 2) {
										this.popY = this.popY - 400 * this.screenWidth / 1920
									}

									// this.checkedDetailLayer = e.target.feature.properties;
									// this.checkedDetailLayer.staffName = res.data.staffName;
									// this.checkedDetailLayer.staffTel = res.data.staffTel;
									this.checkedDetailLayer = res.data;
									this.checkedDetailLayer.layerType = "打捞船"
									console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)
									this.popupDetailFlag = true;
									this.$nextTick(() => {
										this.$refs.popDetail.detailInfo = this.checkedDetailLayer
									});
								}
							})
						});
					});
				})
				this.getSweepList();
			},

			getSweepList() {
				http.getAllSweep().then(res => {
					if (res.code == 200) {
						this.layersInfo['打捞船'].poiList = res.data;
					}
				})
			},

			//安保人员
			securityStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_security_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['安保']) {
						this.map.removeLayer(this.layers['安保'])
					}
					this.layers['安保'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "安保";
							// this.layersInfo['安保'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["安保"].poiUrl,
								iconSize: [80, 110],
								iconAnchor: [40, 88],
							});
							
							// let laaa = gcj02towgs84(latlng.lng,latlng.lat)
							// console.log("#################")
							// console.log(laaa)
							// console.log(latlng)
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								// offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["安保"].checked){
						this.layers['安保'].addTo(this.map)
					}
					this.layers['安保'].eachLayer((layer) => {
						layer.on('click', (e) => {
							console.log("" + layer)
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["安保"].poiUrl,
								iconSize: [96, 132],
								iconAnchor: [48, 106],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label'
								// offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "安保"

							if (this.selectedRightItem != '安保') {
								this.selectedRightItem = '安保';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {

							}
							this.checkLayer = this.layersInfo['安保'];


							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('安保').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								if (this.isAnalysis) {
									this.showAnalysisRightFlag = true
								} else {
									this.showRightFlag = true;
								}
								setTimeout(() => {
									document.getElementById('安保').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 370 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 2) {
								this.popY = this.popY - 400 * this.screenWidth / 1920
							}

							// this.checkedDetailLayer = e.target.feature.properties;
							// console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)

							for (var i = 0; i < this.layersInfo['安保'].poiList.length; i++) {
								if (this.layersInfo['安保'].poiList[i].id == e.target.feature.properties.id) {
									this.checkedDetailLayer = this.layersInfo['安保'].poiList[i];
									this.checkedDetailLayer.layerType = "安保";
								}
							}
							this.popupDetailFlag = true;
							this.$nextTick(() => {
								this.$refs.popDetail.detailInfo = this.checkedDetailLayer
							});
						});
					});
				})
				this.getSecurityStaffList();
			},

			getSecurityStaffList() {
				http.getAllSecurityStaff().then(res => {
					if (res.code == 200) {
						this.layersInfo['安保'].poiList = res.data;
						this.onlineCount = 0;
						this.offlineCount = 0;
						for (var i = 0; i < this.layersInfo['安保'].poiList.length; i++) {
							let item = this.layersInfo['安保'].poiList[i];
							if (item.currentStatus=='静止'||item.currentStatus=='行驶'||item.currentStatus=='定位精度差') {
								this.onlineCount++;
							} else if(item.gpsCode) {
								this.offlineCount++;
							}

						}
					}
				})
			},

			//管理人员
			dockStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_dock_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['管理人员']) {
						this.map.removeLayer(this.layers['管理人员'])
					}
					this.layersInfo['管理人员'].poiList = [];
					this.layers['管理人员'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "管理人员";
							this.layersInfo['管理人员'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["管理人员"].poiUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
								// title: this.layersInfo[key].poiList[i].name
							});
							marker.bindTooltip(feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',

								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					})
					if(this.layersInfo["管理人员"].checked){
						this.layers['管理人员'].addTo(this.map)
					}
					this.layers['管理人员'].eachLayer((layer) => {
						layer.on('click', (e) => {

							console.log("" + layer)
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["管理人员"].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "管理人员"

							if (this.selectedRightItem != '管理人员') {
								this.selectedRightItem = '管理人员';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {

							}
							this.checkLayer = this.layersInfo['管理人员'];
							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('管理人员').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								this.showRightFlag = true;
								setTimeout(() => {
									document.getElementById('管理人员').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 370 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 2) {
								this.popY = this.popY - 300 * this.screenWidth / 1920
							}

							this.checkedDetailLayer = e.target.feature.properties;
							console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)
							this.popupDetailFlag = true;
							this.$nextTick(() => {
								this.$refs.popDetail.detailInfo = this.checkedDetailLayer
							});
						});
					});
				})
			},
			
			//保洁
			cleanStaffSever(){
				this.geoParams.typeName = 'oldtown:trf_cleaning_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if(this.layers['保洁']) {
						this.map.removeLayer(this.layers['保洁'])
					}
					this.layersInfo['保洁'].poiList = [];
					this.layers['保洁'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						filter:(feature, latlng) => {
							return feature.properties.gpsCode;
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "保洁";
							this.layersInfo['保洁'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["保洁"].poiUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
								// title: this.layersInfo[key].poiList[i].name
							});
							marker.bindTooltip(feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
				
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					})
					if(this.layersInfo["保洁"].checked){
						this.layers['保洁'].addTo(this.map)
					}
					this.layers['保洁'].eachLayer((layer) => {
						layer.on('click', (e) => {
				
							console.log("" + layer)
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}
				
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["保洁"].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "保洁"
				
							if (this.selectedRightItem != '保洁') {
								this.selectedRightItem = '保洁';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {
				
							}
							this.checkLayer = this.layersInfo['保洁'];
							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('保洁').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								this.showRightFlag = true;
								setTimeout(() => {
									document.getElementById('保洁').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.username
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 370 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 2) {
								this.popY = this.popY - 300 * this.screenWidth / 1920
							}
				
							this.checkedDetailLayer = e.target.feature.properties;
							console.log("checkedDetailLayer:  " + this.checkedDetailLayer.name)
							this.popupDetailFlag = true;
							this.$nextTick(() => {
								this.$refs.popDetail.detailInfo = this.checkedDetailLayer
							});
						});
					});
				})
			},

			//监控
			monitorSever() {
				this.geoParams.typeName = 'oldtown:com_poi'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['监控'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.type == "监控点";
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "监控";
							this.layersInfo['监控'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["监控"].poiUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
								// title: this.layersInfo[key].poiList[i].name
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["监控"].checked){
						this.layers['监控'].addTo(this.map)
					}
					this.layers['监控'].eachLayer((layer) => {
						layer.on('click', (e) => {


							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["监控"].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "监控"


							console.log("" + layer)
							if (this.$refs.popDetail && this.$refs.popDetail.videoPlayer) {
								this.$refs.popDetail.videoPlayer.pause();
								this.$refs.popDetail.videoPlayer.destroy();
								this.$refs.popDetail.videoPlayer = null;
							}

							if (this.selectedRightItem != '监控') {
								this.selectedRightItem = '监控';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {

							}
							this.checkLayer = this.layersInfo['监控'];
							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('监控').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.name
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								this.showRightFlag = true;
								setTimeout(() => {
									document.getElementById('监控').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
									let id = e.target.feature.properties.name
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 370 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 2) {
								this.popY = this.popY - 400 * this.screenWidth / 1920
							}

							this.checkedDetailLayer = e.target.feature.properties;
							console.log("监控checkedDetailLayer:  " + this.checkedDetailLayer.code)
							// if(this.$refs.popDetail){
							// 	console.log("监控checkedDetailLayer11111:  ")
							// 	this.$refs.popDetail.videoPlayer.dispose();
							// }
							setTimeout(() => {
								this.popupDetailFlag = true;
								this.$nextTick(() => {
									this.$refs.popDetail.detailInfo = this.checkedDetailLayer
									this.$refs.popDetail.getRtmp();
								});
							}, 500)

						});
					});
				})
			},


			//码头 休息点
			pointSever() {
				const urlString = '/gisServer/geoserver/ows'
				let params = {
					service: 'WFS',
					version: '1.1.0',
					request: 'GetFeature',
					typeName: 'oldtown:com_place_point',
					outputFormat: 'application/json',
					srsName: 'EPSG:4326',
					// cql_filter: "area=0"
				}
				const url = urlString + L.Util.getParamString(params, urlString)
				http.getWFS(url, params).then(res => {
					this.layers['游船码头'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.type == "码头";
						},
						pointToLayer: (feature, latlng) => {
							console.log("pointToLayer getWFS: " + feature);
							feature.properties.latlng = latlng;
							feature.properties.layerType = "游船码头";
							this.layersInfo['游船码头'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船码头"].poiUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
								// title: this.layersInfo[key].poiList[i].name
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					}).addTo(this.map);
					this.layers['游船码头'].eachLayer((layer) => {
						layer.on('click', (e) => {
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船码头"].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "游船码头"


							if (this.selectedRightItem != '游船码头') {
								this.selectedRightItem = '游船码头';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {

							}
							this.checkLayer = this.layersInfo['游船码头'];
							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('游船码头').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								if (this.isAnalysis) {
									this.showAnalysisRightFlag = true
								} else if(!this.popupWorkFlag) {
									this.showRightFlag = true;
								}
								setTimeout(() => {
									document.getElementById('游船码头').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 350 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 3) {
								this.popY = this.popY - 240 * this.screenWidth / 1920
							}
							this.checkedDetailLayer = e.target.feature.properties
							this.popupDetailFlag = true;
							this.$nextTick(() => {
								this.$refs.popDetail.detailInfo = this.checkedDetailLayer
							});
						});
					});
					this.layers['休息点'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.type == "安保人员休息点";
						},
						pointToLayer: (feature, latlng) => {
							console.log("pointToLayer getWFS: " + feature);
							feature.properties.latlng = latlng;
							feature.properties.layerType = "休息点";
							this.layersInfo['休息点'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["休息点"].poiUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
								// title: this.layersInfo[key].poiList[i].name
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();

							this.restMarkerList.push({
								id: feature.properties.id,
								marker: marker
							});
							return marker;
						}
					}).addTo(this.map);
					this.layers['休息点'].eachLayer((layer) => {
						layer.on('click', (e) => {

							this.popupDetailFlag = false;
							console.log("dMarker: " + this.popupGuijiFlag)
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["休息点"].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
								zIndexOffset: 100,
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 100
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 18]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "休息点"

							if (this.selectedRightItem != '休息点') {
								this.selectedRightItem = '休息点';
								this.checkLayerList.unshift(this.selectedRightItem);
							} else {

							}
							this.checkLayer = this.layersInfo['休息点'];
							if (this.showRightFlag) {
								this.$nextTick(() => {
									document.getElementById('休息点').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								})
							} else {
								if (this.isAnalysis) {
									this.showAnalysisRightFlag = true
								} else if(!this.popupWorkFlag){
									this.showRightFlag = true;
								}
								setTimeout(() => {
									document.getElementById('休息点').scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}, 1000)
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							if (this.popX > this.screenWidth / 2) {
								this.popX = this.popX - 340 * this.screenWidth / 1920
							}
							if (this.popY > this.screenHeight / 3) {
								this.popY = this.popY - 240 * this.screenWidth / 1920
							}
							this.checkedDetailLayer = e.target.feature.properties
							this.popupDetailFlag = true;
							this.$nextTick(() => {
								this.$refs.popDetail.detailInfo = this.checkedDetailLayer
							});
						});
					});
				})
			},

			//打捞路线
			sweepRoute() {
				this.geoParams.typeName = 'oldtown:trf_sweep_route'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['打捞路线'] = L.geoJSON(res, {
						style: (geoJsonFeature) => {
							// console.log("geoJsonFeature: " + JSON.stringify(geoJsonFeature))
							let color = geoJsonFeature.properties.color
							return {
								color: color
							}
						}
					});
					this.layers['打捞路线'].eachLayer((layer) => {
						console.log("打捞路线: " + layer)
						layer.feature.properties.bounds = layer._bounds
						this.layersInfo['打捞路线'].poiList.push(layer.feature.properties);
						layer.on('click', (e) => {
							console.log("" + layer)
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}

							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							this.plyInfo = {
								type: "打捞路线",
								info: e.target.feature.properties.name
							}
							this.popupPlyFlag = true;
						});
					});
					if(this.layersInfo["打捞路线"].checked){
						this.layers['打捞路线'].addTo(this.map)
					}
				})

			},

			//打捞站点
			sweepPoint() {
				this.geoParams.typeName = 'oldtown:trf_sweep_point'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['打捞站点'] = L.geoJSON(res, {
						pointToLayer: (feature, latlng) => {
							console.log("pointToLayer 打捞站点: " + feature);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["打捞站点"].iconUrl,
								iconSize: [48, 48],
								// iconAnchor: [24, 24],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 10]
							}).openTooltip();
							return marker;
						}
					});
					if(this.layersInfo["打捞路线"].checked){
						this.layers['打捞站点'].addTo(this.map)
					}
					this.layers['打捞站点'].eachLayer((layer) => {

					});
				})

			},

			
			//巡逻范围
			polygonServe() {
				this.geoParams.typeName = 'oldtown:trf_security_polygon'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['巡检范围'] = L.geoJSON(res, {
						style: (geoJsonFeature) => {
							// console.log("geoJsonFeature: " + JSON.stringify(geoJsonFeature))
							let color = geoJsonFeature.properties.color
							return {
								color: color,
								fillColor: color,
								weight: 3,
								fillOpacity: 0.5,
							}
						}
					});
					if(this.layersInfo["巡检范围"].checked){
						this.layers['巡检范围'].addTo(this.map)
					}
					this.layers['巡检范围'].eachLayer((layer) => {
						layer.feature.properties.bounds = layer._bounds
						this.layersInfo['巡检范围'].poiList.push(layer.feature.properties);
						layer.on('click', (e) => {
							console.log("" + layer)
							this.popupDetailFlag = false;
							if (this.rippleShowFlag || this.popupGuijiFlag) {
								return;
							}
							if (this.isAnalysis && !this.analysisResultFlag && this.analysisType) {
								this.map.panTo(e.latlng);
								this.rippleShowFlag = true;
								this.analysisData(e.latlng);
								return;
							}
							this.popX = e.originalEvent.clientX;
							this.popY = e.originalEvent.clientY;
							this.plyInfo = {
								type: "巡检范围",
								info: e.target.feature.properties.name
							}
							this.popupPlyFlag = true;
						});
					});


				})
			},

			//巡检路线
			securityRouteServe() {
				this.geoParams.typeName = 'oldtown:trf_security_route'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['巡检路线'] = L.geoJSON(res, {});
					if(this.layersInfo["巡检范围"].checked){
						this.layers['巡检路线'].addTo(this.map)
					}
				})

			},

			/* 判断不在线时间是否超过24小时 */
			formatStatus(item) {
				if (item.gpsTime) {
					if (!item.currentStatus) {
						var dates = new Date();
						var nowTime = dates.getTime() / 1000;
						if (nowTime - item.gpsTime > 48 * 60 * 60) {
							return true;
						}
					}
					return false;
				} else {
					return true;
				}
			},

			showUserBottom() {
				this.showUserFlag = !this.showUserFlag;
			},

			showDianci() {
				if (!this.dianciFlag) {
					this.getAlarmAll().then(() => {
						this.dianciFlag = !this.dianciFlag
					})
				} else {
					this.dianciFlag = !this.dianciFlag
				}
			},

			//选择右侧item
			selectDetail(item) {
				if (this.layersInfo[item].checked) {
					this.selectedRightItem = item;
					this.checkLayerList.unshift(this.selectedRightItem);
					this.checkLayer = this.layersInfo[this.selectedRightItem]
				}
			},



			//左侧图层开关
			toggleLayer(item) {
				console.log("toggleLayer： " + item)
				this.layersInfo[item].checked = !this.layersInfo[item].checked;
				if (this.layersInfo[item].checked) {
					this.checkedLength ++;
					if (item == "打捞路线") {
						this.layersInfo["打捞站点"].checked = true;
						this.map.addLayer(this.layers["打捞站点"])
					}
					if (this.layers[item]) {
						this.map.addLayer(this.layers[item])
					}
					if (this.bigMarker) {
						if (this.bigMarkerType == item) {
							this.map.removeLayer(this.bigMarker)
							// this.map.addLayer(this.clickLayer)
						} else {
							// this.map.removeLayer(this.bigMarker)
							// this.map.addLayer(this.clickLayer)
						}
					}

					this.selectedRightItem = item;
					this.showRight(this.layersInfo[item])
					setTimeout(() => {
						try {
							document.getElementById(item).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
						} catch (e) {
							//TODO handle the exception
						}
					}, 800)
					// this.$nextTick(() => {
					//      document.getElementById(item).scrollIntoView({
					// 	behavior: "smooth", // 默认 auto
					// 	block: "center", // 默认 center
					// 	inline: "nearest", // 默认 nearest
					// });
					// })
				} else {
					this.checkedLength --;
					if(this.checkedLength<1){
						this.showLayersFlag = false;
					}
					if (this.bigMarker) {
						if (this.bigMarkerType == item) {
							this.map.removeLayer(this.bigMarker)
							this.clickLayer = null;
						}
					}
					console.log("toggleLayer")
					this.popupDetailFlag = false;
					this.popupPlyFlag = false;
					this.checkedDetailLayer = {};
					if (this.layers[item]) {
						this.map.removeLayer(this.layers[item])
					}
					if (item == "打捞路线") {
						this.layersInfo["打捞站点"].checked = false;
						this.map.removeLayer(this.layers["打捞站点"])
					}
					// if (this.selectedRightItem == item) {
						this.checkLayerList.remove(item)
						console.log("checkLayerList: " + JSON.stringify(this.checkLayerList))
						
						// this.hideRight();
						this.selectedRightItem = this.checkLayerList[0];
						this.checkLayer = this.layersInfo[this.selectedRightItem];
						try {
							document.getElementById(this.selectedRightItem).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
						} catch (e) {
							//TODO handle the exception
						}
					// }
				}

			},
			
			
			showDalaoLuXian(routeId) {
				this.dalaoLuXian = true;
				if (routeId) {
					this.routeId = routeId
					this.layers["打捞路线"].eachLayer((layer) => {
						if (this.routeId == layer.feature.properties.id) {
							this.layers["工作分析"].clearLayers();
							this.layers["工作分析"].addLayer(layer);
							this.map.addLayer(this.layers["工作分析"]);
							this.map.flyToBounds(layer._bounds);
						}
					})

				} else {
					this.routeId = this.checkedDetailLayer.routeId;
					this.layers["打捞路线"].eachLayer((layer) => {
						if (this.routeId == layer.feature.properties.id) {
							this.map.addLayer(layer)
							this.map.flyToBounds(layer._bounds);
						}
					})
				}
			},
			showXunluo(routeId) {
				this.xunluoLuXian = true;
				if (routeId) {
					this.routeId = routeId
					this.layers["巡检路线"].eachLayer((layer) => {

						if (this.routeId == layer.feature.properties.id) {
							this.layers["工作分析"].clearLayers();
							this.layers["工作分析"].addLayer(layer);
							this.map.addLayer(this.layers["工作分析"]);
							this.map.flyToBounds(layer._bounds);
						}
					})
				} else {
					this.routeId = this.checkedDetailLayer.routeId;
					this.layers["巡检路线"].eachLayer((layer) => {

						if (this.routeId == layer.feature.properties.id) {
							this.map.addLayer(layer)
							this.map.flyToBounds(layer._bounds);
						}
					})
				}
			},

			// 退出登录
			loginOut() {
				localStorage.removeItem("token");
				window.location.href = "/login"
			},

			/**
			 * 绘制多边形
			 */
			drawPolygon() {
				let map = this.map;
				this.polygon = new L.Draw.Polygon(map, {
					shapeOptions: {
						weight: 1,
						color: "#ff0000",
						opacity: 0.8,
						fillColor: "#ff0000"
					}
				});
				this.polygon.enable();
			},
			/**
			 * 绘制线段
			 */
			drawPolyline() {
				let map = this.map;
				this.polyline = new L.Draw.Polyline(map, {
					shapeOptions: {
						weight: 1,
						color: "#ff0000",
						opacity: 0.8
					}
				});
				this.polyline.enable();
			},
			// 获取面积
			/**
			 * polygon [{lat:30,lng:104},{lat:30,lng:104},{lat:30,lng:104}]
			 */
			formatArea(polygon) {
				//L.GeometryUtil.geodesicArea(),返回number类型的数据，单位是平方米，这里做了一下转化
				var seeArea = L.GeometryUtil.geodesicArea(polygon);
				let area = seeArea.toFixed(0) + "㎡";
				if (seeArea > 10e5) {
					let area = (seeArea / 10e5).toFixed(2) + "k㎡";
				}
				return area;
			},
			// 获取长度
			/**
			 * line[{lat:30,lng:104},{lat:30,lng:104},{lat:30,lng:104}]
			 */
			formatLength(line) {
				let dis = 0;
				for (let i = 0; i < line.length - 1; i++) {
					let start = line[i];
					let end = line[i + 1];
					dis += L.latLng([start.lat, start.lng]).distanceTo([end.lat, end.lng]); //计算两个点之间的距离，并累加
				}
				//结果得到的也是number类型，单位是 米
				if (dis < 1000) {
					return dis.toFixed(2) + "m";
				}
				return (dis / 10e2).toFixed(2) + "km";
			},

			formatDis(dis) {
				if (dis < 1000) {
					return dis.toFixed(2) + "m";
				}
				return (dis / 10e2).toFixed(2) + "km";
			},
			
			blur(){
				this.searchList = [];
			},

			addMeasureMarker(text, latlng, groupLayer) {
				var myIcon = L.divIcon({
					html: text,
					className: 'my-div-icon',
					iconSize: [80, 26]
				});
				L.marker(latlng, {
					icon: myIcon
				}).addTo(groupLayer);

			},
			
			//打开工作分析按钮
			workOpen(){
				if(this.bigMarker){
					this.popupDetailFlag =false;
					this.map.removeLayer(this.bigMarker)
					this.map.addLayer(this.clickLayer)
				}
			},

			searchInput(event) {
				// console.log()
				if (!event.currentTarget.value) {
					this.searchList = [];
				} else {
					this.searchText();
				}
			},

			//搜索
			searchText() {
				// this.map.setView([30.874457, 120.42729], this.map.getZoom(), {
				// 	pan: {
				// 		animate: true,
				// 		duration: 0.5
				// 	},
				// 	zoom: {
				// 		animate: true
				// 	},
				// 	animate: true
				// });
				// setTimeout(() => {
				// 	this.popupDetailFlag = true;
				// 	this.popX = this.screenWidth / 2
				// 	this.popY = this.screenHeight / 2
				// }, 500)
				if (!this.searchStr) {
					return;
				}
				let params = {
					keyword: this.searchStr,
				}
				switch (this.selectedRightItem) {
					case "电动车":
						http.getAllCar(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "游船":
						http.getAllYacht(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "打捞船":
						http.getAllSweep(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "安保":
						http.getAllSecurityStaff(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "管理人员":
						http.getAllDockStaff(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "保洁":
						// http.getAllDockStaff(params).then(res => {
						// 	if (res.code == 200) {
						// 		this.searchList = res.data;
						// 	}
						// })
						break;	
					case "监控":
						params.type = "监控点";
						http.getComPoiAll(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "打捞路线":
						http.getAllSweepRoute(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "巡检范围":
						http.getAllSecurityRoute(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "休息点":
						params.type = "安保人员休息点";
						http.getAllPlace(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "游船码头":
						params.type = "码头";
						http.getAllPlace(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					default:
						break;
				}
				// if (this.selectedRightItem == "安保" || this.selectedRightItem == "管理人员") {
				// 	for (var i = 0; i < this.checkLayer.poiList.length; i++) {
				// 		if (this.checkLayer.poiList[i].username == this.searchStr) {
				// 			this.chooseDetailLayer(this.checkLayer.poiList[i]);
				// 			document.getElementById(this.checkLayer.poiList[i].username).scrollIntoView({
				// 				behavior: "smooth", // 默认 auto
				// 				block: "center", // 默认 center
				// 				inline: "nearest", // 默认 nearest
				// 			});
				// 		}
				// 	}
				// }
			},

			searchClick(item) {
				this.showRightFlag = true;
				this.checkLayer = this.layersInfo[this.selectedRightItem];
				for (var i = 0; i < this.checkLayer.poiList.length; i++) {
					if (this.checkLayer.poiList[i].id == item.id) {
						this.chooseDetailLayer(this.checkLayer.poiList[i]);
					}
				}
				// this.$nextTick(()=>{

				// })
				let id = item.name ? item.name : item.username;
				setTimeout(() => {
					document.getElementById(id).scrollIntoView({
						behavior: "smooth", // 默认 auto
						block: "center", // 默认 center
						inline: "nearest", // 默认 nearest
					});
				}, 500)
				this.searchList = [];

			},

			showZoomLevel() {
				console.log("zoom: " + this.map.getZoom());
			},
			selectDate(year, month, day, startTime, endTime) {
				console.log("选择日期: " + year + "年" + month + "月" + day + "日");
				// this.popupCalendarFlag = false;
				// this.popupDetailFlag = false;
				this.guijiInfo.desc = year + "年" + month + "月" + day + "日" + startTime + "-" + endTime + "轨迹"
				if (this.selectedRightItem == "安保") {
					this.guijiInfo.name = this.checkedDetailLayer.username + this.checkedDetailLayer.serial
				} else {
					this.guijiInfo.name = this.checkedDetailLayer.name
				}
				this.showLocus(year, month, day, startTime, endTime);
				let routeId = this.checkedDetailLayer.routeId;
				if (this.routeId) {
					routeId = this.routeId;
				}

				if (this.selectedRightItem == "打捞船") {
					this.dalaoLuXian = true;
					this.layers["打捞路线"].eachLayer((layer) => {
						if (routeId == layer.feature.properties.id) {
							this.routeId = routeId;
							this.map.addLayer(layer)
							this.map.flyToBounds(layer._bounds);
						}
					})

					this.layers["打捞站点"].eachLayer((layer) => {
						if (layer.feature.properties.routeId == routeId) {
							this.map.addLayer(layer)
						} else {
							this.map.removeLayer(layer)
						}
					})
				}
				if (this.selectedRightItem == "安保") {
					this.layers["巡检路线"].eachLayer((layer) => {
						if (routeId == layer.feature.properties.id) {
							this.routeId = routeId;
							this.xunluoLuXian = true;
							this.map.addLayer(layer)
							this.map.flyToBounds(layer._bounds);
						}
					})

				}

			},

			reset() {
				// this.drawGroup.clearLayers()
				// this.map.removeLayer(this.drawGroup)
				this.map.setView([30.878191, 120.426207], 14);
			},
			measureDistance() {
				this.isDrawLine = true;
				this.drawPolyline();
			},
			clearMeasureDistance() {
				this.isDrawLine = false;
				this.polyline.disable();
				this.drawGroupPolyline.clearLayers()
			},
			measureArea() {
				this.isDrawPolygon = true;
				this.drawPolygon();
			},
			clearMeasureArea() {
				this.isDrawPolygon = false;
				this.polygon.disable();
				this.drawGroupPolygon.clearLayers()
			},
			scale(sate) {
				console.log("zoom: " + this.map.getZoom())
				var zoom = this.map.getZoom();
				if (sate == 1) {
					zoom--;
					this.map.setZoom(zoom)
				} else {
					zoom++;
					this.map.setZoom(zoom)
				}

			},

			hideLeft() {
				this.showLayersFlag = false
			},
			showLeft() {
				if (this.isAnalysis || this.popupWorkFlag) {
					return;
				}
				this.showLayersFlag = !this.showLayersFlag
			},
			closePopupDetail() {
				this.popupDetailFlag = false;
				this.popupCalendarFlag = false;
				this.map.removeLayer(this.bigMarker)
				this.map.addLayer(this.clickLayer)

				try {
					if (this.dalaoLuXian) {

						this.layers["打捞路线"].eachLayer((layer) => {
							if (this.checkedDetailLayer.routeId == layer.feature.properties.id) {
								this.map.removeLayer(layer)
							}
						})
						this.layers["打捞站点"].eachLayer((layer) => {
							if (this.checkedDetailLayer.routeId == layer.feature.properties.routeId) {
								this.map.removeLayer(layer)
							}
						})
						this.dalaoLuXian = false;
					}
					if (this.xunluoLuXian) {
						this.layers["巡检路线"].eachLayer((layer) => {
							if (this.checkedDetailLayer.routeId == layer.feature.properties.id) {
								this.map.removeLayer(layer)
							}
						})
						this.xunluoLuXian = false;
					}

				} catch (e) {
					//TODO handle the exception
				}
			},
			closePopupCalendar() {
				this.popupCalendarFlag = false;
			},
			showCalendar(gpsCode, routeId, type) {
				this.popupCalendarX = this.popX + 380 * this.screenWidth / 1920
				this.popupCalendarFlag = true;
				this.gpsCode = gpsCode;
				this.checkType = type;
				this.routeId = routeId;
				console.log("showCalendar checkType： " + this.checkType)
			},
			analysisDd() {
				if (this.popupWorkFlag) {
					return;
				}
				if (this.isAnalysis) {
					this.selectedRightItem = this.analysisBeforeType;
					this.isAnalysis = false;
					this.rippleShowFlag = false;
					this.showLayersFlag = true;
					if (this.layers["调度分析"]) {
						this.map.removeLayer(this.layers["调度分析"])
					}
					if (this.analysisType) {
						this.map.removeLayer(this.layers[this.analysisType]);
						this.analysisType = "";
					}
					for (let key in this.layersInfo) {
						if (key == "休息点" || key == "游船码头") {
							continue;
						}
						if (this.layersInfo[key].checked) {
							this.map.addLayer(this.layers[key])
						}
					}
					this.analysisResultFlag = false;
				} else {
					for (let key in this.layersInfo) {
						if (key == "休息点" || key == "游船码头") {
							continue;
						}
						if (this.layersInfo[key].checked && this.layers[key]) {
							this.map.removeLayer(this.layers[key])
						}
						if (this.bigMarker) {
							this.map.removeLayer(this.bigMarker)
							this.clickLayer = null;
						}
					}
					this.analysisBeforeType = this.selectedRightItem;
					this.popupDetailFlag = false;
					this.isAnalysis = true;
					this.showLayersFlag = false;
					this.showRightFlag = false;
				}
			},
			showRight(item) {
				console.log("showRight: " + item)
				if (this.isAnalysis || this.popupWorkFlag) {
					this.showAnalysisRightFlag = true;
					return;
				}
				if (item.iconUrl) {
					if (this.showRightFlag) {
						// this.showRightFlag = false
						if (this.selectedRightItem != this.checkLayer.name) {
							// setTimeout(() => {
								this.showRightFlag = true
								this.checkLayer = item;
								this.checkLayerList.unshift(this.selectedRightItem);
							// }, 500)
						}
					} else {
						this.showRightFlag = true;
						this.checkLayer = item;
						this.checkLayerList.unshift(this.selectedRightItem);
					}
				} else {
					this.selectedRightItem = this.checkLayerList[0];
					this.checkLayer = this.layersInfo[this.selectedRightItem];
					this.showRightFlag = true;
				}

			},
			hideRight() {
				console.log("hideRight##############")
				if (this.isAnalysis) {
					this.showAnalysisRightFlag = false;
					return;
				}
				this.showRightFlag = false;

			},

			analysisData(latlng) {

				setTimeout(() => {
					if (this.rippleShowFlag) {
						this.analysisResultFlag = true;
						console.log("analysisData" + latlng)
						this.layers['调度分析'] = L.layerGroup();
						// var circleIn = new L.Circle(latlng, {
						// 	color: '#0C81FF', //颜色
						// 	fillColor: '#0C81FF',
						// 	fillOpacity: 1,
						// 	radius: 20
						// });
						// this.layers['调度分析'].addLayer(circleIn);
						var circleOut = new L.Circle(latlng, {
							color: '#67AAFF', //颜色
							fillColor: '#c2fff5',
							fillOpacity: 0.2,
							radius: 500
						}).addTo(this.map);
						this.layers['调度分析'].addLayer(circleOut);
						this.layers['调度分析'].addTo(this.map);
						// this.showRightFlag = true;
						let layerInfo = this.layersInfo[this.analysisType];
						this.analysisLayer = JSON.parse(JSON.stringify(layerInfo));
						let poiList = [];
						for (var i = 0; i < layerInfo.poiList.length; i++) {
							if (layerInfo.poiList[i].latitude && layerInfo.poiList[i].longitude) {
								let dis = L.latLng(latlng).distanceTo([layerInfo.poiList[i].latitude, layerInfo.poiList[i].longitude]);
								console.log("analysisData dis: " + dis)
								let poi = layerInfo.poiList[i];
								poi.dis = dis;
								if (poi.dis <= 500) {
									poiList.push(poi)
								}
							}
						}
						poiList.sort(function(obj1, obj2) {
							var val1 = obj1.dis;
							var val2 = obj2.dis;
							if (val1 < val2) {
								return -1
							} else if (val1 > val2) {
								return 1
							} else {
								return 0
							}
						})

						console.log("poiList: " + JSON.stringify(poiList))
						this.analysisLayer.poiList = poiList;
						console.log("poiList: " + JSON.stringify(this.analysisLayer.poiList))
						this.showAnalysisRightFlag = true;
					}
					this.rippleShowFlag = false;
					// this.isAnalysis = false;

				}, 2500)
			},

			showWorkLocus(data, type) {
				var params = {
					fromTime: data.date.substring(0, 10) + ' ' + '08:00:00',
					toTime: data.date.substring(0, 10) + ' ' + '18:00:00',
					// gpsCode: this.checkedDetailLayer.gpsCode,
					gpsCode: data.gpsCode
				}
				
				this.guijiInfo.desc = data.date.substring(0, 10) + "轨迹"
				if (data.name) {
					this.guijiInfo.name = data.name;
				}

				if (data.username) {
					this.guijiInfo.name = data.username;
				}
				
				this.checkType = type;
				if (type == "安保" || type == "管理人员" || type == "保洁") {
					http.getStaffListByGpsCode(params).then(res => {
						if (res.code == 200) {
							if (res.data && res.data.list.length < 2) {
								console.log("轨迹空1111")
								this.showGuijiEmpty();
							} else {
								this.showGuiji(res.data.list)
							}
						} else {
							console.log("轨迹空2222")
							this.showGuijiEmpty();
						}
					})
				} else {
					http.getListByGpsCode(params).then(res => {
						if (res.code == 200) {
							if (res.data.length < 2) {
								console.log("轨迹空3333")
								this.showGuijiEmpty();
							} else {
								console.log("轨迹不空")
								this.showGuiji(res.data)
							}
						} else {
							console.log("轨迹空4444")
							this.showGuijiEmpty();
						}
					})
				}
			},

			showLocus(year, month, day, startTime, endTime) {
				console.log('showLocus: ' + JSON.stringify(this.checkedDetailLayer))
				 this.loading = Loading.service({
				    lock: true,
				    text: '加载中……',
				    background: 'rgba(0, 0, 0, 0.7)'
				  });

				var params = {
					fromTime: year + '/' + month + '/' + day + ' ' + startTime + ':00',
					toTime: year + '/' + month + '/' + day + ' ' + endTime + ':00',
					// gpsCode: this.checkedDetailLayer.gpsCode,
					gpsCode: this.gpsCode ? this.gpsCode : this.checkedDetailLayer.gpsCode
				}
				let type;
				if (this.checkType) {
					type = this.checkType;
				} else {
					type = this.selectedRightItem;
				}

				console.log("checkType: " + this.checkType + this.gpsCode)

				if (type == "安保" || type == "管理人员" || type == "保洁") {
					http.getStaffListByGpsCode(params).then(res => {
						if (res.code == 200) {
							this.popupCalendarFlag = false;
							this.popupDetailFlag = false;
							this.loading.close();
							if (res.data && res.data.list.length < 2) {
								console.log("轨迹空5555")
								this.showGuijiEmpty();
							} else {
								this.showGuiji(res.data.list)
								if (type == "安保") {
									this.restList = res.data.map
									this.map.addLayer(this.layers['休息点'])
								}
							}
						} else {
							console.log("轨迹空6666")
							this.popupCalendarFlag = false;
							this.popupDetailFlag = false;
							this.loading.close();
							this.showGuijiEmpty();
						}
					})
				} else {
					http.getListByGpsCode(params).then(res => {
						if (res.code == 200) {
							this.popupCalendarFlag = false;
							this.popupDetailFlag = false;
							this.loading.close();
							if (res.data.length < 2) {
								this.showGuijiEmpty();
							} else {
								this.showGuiji(res.data)
								if (type == "游船") {
									this.map.addLayer(this.layers['游船码头'])
								}
							}
						} else {
							this.popupCalendarFlag = false;
							this.popupDetailFlag = false;
							this.loading.close();
							this.showGuijiEmpty();
						}
					})
				}
			},

			/* 无轨迹记录 */
			showGuijiEmpty() {
				for (let key in this.layersInfo) {
					if (this.layersInfo[key].checked) {
						this.map.removeLayer(this.layers[key])
					}
				}
				this.guijiInfo.name = "提示";
				this.guijiInfo.desc = '无轨迹相关信息'
				this.popupGuijiFlag = true;
			},

			//获取警报
			getAlarmAll() {
				return http.getAlarmAll().then(res => {
					if (res.code == 200) {
						this.alarmList = res.data;
					}
				})
			},
			
			formatAlarmCode(code) {
				switch (code) {
					case "REMOVE":
						return "断电"
					case "LOWVOT":
						return "低电"
					case "ERYA":
						return "二押"
					case "FENCEIN":
						return "进围栏"
					case "FENCEOUT":
						return "出围栏"
					case "SEP":
						return "分离"
					case "SOS":
						return "SOS报警"
					case "OVERSEED":
						return "超速报警"
					case "OVERSEED":
						return "超速报警"
					case "HOME":
						return "常住地异常（家）"
					case "COMPANY":
						return "常住地异常（公司）"
					case "ACCOFF":
						return "停车"
					case "ACCON":
						return "启动"
					default:
						break;
				}
			},

			showGuiji(list) {
				this.lastFxInfo = "";
				this.fxInfo = "";
				if (list.length > 0) {
					this.popupGuijiFlag = true;
					var pointList = [];
					for (var i = 0; i < list.length; i++) {
						if(list[i].latitude&&list[i].longitude){
							let point = [list[i].latitude, list[i].longitude]
							pointList.push(point)
						}
					}
					if (this.analysisType) {
						this.map.removeLayer(this.layers[this.analysisType]);
					} else {
						for (let key in this.layersInfo) {
							if (this.layersInfo[key].checked) {
								this.map.removeLayer(this.layers[key])
							}
						}
					}
					

					this.map.fitBounds(L.latLngBounds(pointList));
					setTimeout(() => {
						var currentZoom = this.map.getZoom();
						this.map.setMinZoom(currentZoom);
						this.map.setMaxZoom(currentZoom);
						this.guijiLayer = L.layerGroup();

						// new L.polyline.antPath(pointList, {
						// 	paused: false,
						// 	delay: 2000
						// }).addTo(this.map);



						var key = this.selectedRightItem;
						if (this.checkType) {
							key = this.checkType;
						}
						if (key == "休息点" || key == "电动车站点" || key == "游船码头" || key == "管理人员" || key == "监控" || key == "保洁") {
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo[key].poiUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
						} else {
							customIcon = new L.Icon({
								iconUrl: this.layersInfo[key].poiUrl,
								iconSize: [80, 110],
								iconAnchor: [40, 88],
							});

						}

						// moveLine.on('click', (e) => {
						// 	console.log("moveLine: " + e);
						// 	this.popupGuijiFxFlag = true
						// });

						var _latlngs = pointList.map(function(e, index) {
							return L.latLng(e);
						});


						// console.log("totalDistance： " + JSON.stringify(_latlngs))
						var totalDistance = 0;
						for (var i = 0; i < _latlngs.length - 1; i++) {
							// console.log("totalDistance： " + i + ": " + JSON.stringify(_latlngs[i + 1]))
							let currPoint = this.map.latLngToContainerPoint(
								_latlngs[i]);
							let nextPoint = this.map.latLngToContainerPoint(
								_latlngs[i + 1]);
							let distance = currPoint.distanceTo(nextPoint);
							totalDistance += distance;

							// console.log("totalDistance ： " + totalDistance)
						}

						// console.log("totalDistance ： " + totalDistance)

						let duration = totalDistance / 200 * 1000;
						var moveMarker = L.Marker.movingMarker(pointList,
							duration, {
								icon: customIcon
							}).addTo(this.guijiLayer);
						let name = this.checkedDetailLayer.name;
						if (key == '游船' || key == '打捞船') {
							name = this.checkedDetailLayer.name;
						}
						if (key == '安保' || key == '管理人员'|| key == '保洁') {
							name = this.checkedDetailLayer.username;
						}
						
						if (key == "休息点" || key == "电动车站点" || key == "游船码头" || key == "管理人员" || key == "监控" || key == "保洁") {
							moveMarker.bindTooltip(name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
						} else {
							moveMarker.bindTooltip(name, {
								permanent: 'true',
								direction: 'bottom',
								// offset: [0, 18]
							}).openTooltip();
						
						}
						
						this.guijiLayer.addTo(this.map);
						moveMarker.start();

						var moveLine = L.polyline(pointList, {
							snakingSpeed: 200,
							color: '#DDFF00'
						}).addTo(this.guijiLayer);

						this.guijiLayer.addTo(this.map);
						moveLine.snakeIn();

						console.log("##########" + moveLine);

						//添加hotline路线
						this.hotlineTimer = setTimeout(() => {

							this.map.setMinZoom(13);
							this.map.setMaxZoom(21);

							if (key == "安保") {
								for (var i = 0; i < this.restMarkerList.length; i++) {
									console.log("休息点id :" + this.restMarkerList[i].id)
									for (let restId in this.restList) {
										if (this.restMarkerList[i].id == restId) {
											console.log("休息点id111: " + restId)
											let marker = this.restMarkerList[i].marker
											marker.bindPopup("该点休息了" + this.restList[restId] + "分钟").openPopup();
										}
									}
								}
							}

							if (this.popupGuijiFlag) {
								this.map.removeLayer(this.guijiLayer)
								this.guijiLayer = L.layerGroup();
								this.calGuijilayer = L.layerGroup();

								var hotPointList = [];
								for (var i = 0; i < list.length; i++) {
									if(list[i].latitude&&list[i].longitude){
										let point = [list[i].latitude, list[i].longitude, list[i].speed]
										hotPointList.push(point);
									}
								}
								var hotlineLayer = L.hotline(hotPointList, {
									min: 0,
									max: 20,
									palette: {
										0.0: '#F69828',
										0.5: '#ffff00',
										1.0: '#008800'
									},
									weight: 2,
									outlineColor: '#000000',
									outlineWidth: 1
								}).addTo(this.guijiLayer);
								this.guijiLayer.addTo(this.map);

								for (var i = 0; i < pointList.length; i++) {
									var myIcon = L.divIcon({
										html: "",
										className: 'my-guiji-icon',
										iconSize: [10, 10]
									});
									let marker = L.marker(pointList[i], {
										icon: myIcon,
										attribute: list[i].gpsTime
									});
									marker.on('mouseover', (e) => {
										if (this.isCalGuiji) {
											return
										}
										this.popupGuijiFxFlag = false;

										this.lastFxInfo = "";
										this.fxInfo = e.target.options.attribute;
										this.popFxX = e.originalEvent.clientX;
										this.popFxY = e.originalEvent.clientY;
										console.log("moveLine: " + this.fxInfo);
										this.popupGuijiFxFlag = true
									});

									marker.on('click', (e) => {
										console.log("moveLine click: " + e);
										if (!this.isCalGuiji) {
											return
										}
										if (this.calGuijiList.length > 1) {
											return;
										}
										this.calGuijilayer.addTo(this.guijiLayer);
										this.calGuijiList.push(e.target.options.attribute);
										let calIcon = L.divIcon({
											html: "",
											className: 'cal-guiji-icon',
											iconSize: [10, 10]
										});
										let calMarker = L.marker(e.latlng, {
											icon: calIcon,
										});
										calMarker.addTo(this.calGuijilayer);
										if (this.calGuijiList.length == 1) {
											this.ananlysisTipFlag = true;
											this.tipMsg = "请选取第二个点"
											setTimeout(() => {
												this.ananlysisTipFlag = false;
											}, 1500)
										}
										if (this.calGuijiList.length > 1) {
											this.lastFxInfo = this.calGuijiList[0];
											this.fxInfo = this.calGuijiList[1];
											this.popFxX = e.originalEvent.clientX;
											this.popFxY = e.originalEvent.clientY;
											this.popupGuijiFxFlag = true;
										}
									});
									marker.addTo(this.guijiLayer);
								}
							}
						}, duration);





						// moveMarker.on('click', (e) => {
						// 	console.log(e);
						// 	this.popX = e.originalEvent.clientX;
						// 	this.popY = e.originalEvent.clientY;
						// 	if (this.popX > this.screenWidth / 2) {
						// 		this.popX = this.popX - 380 * this.screenWidth / 1920
						// 	}
						// 	if (this.popY > this.screenHeight / 3) {
						// 		this.popY = this.popY - 250 * this.screenWidth / 1920
						// 	}
						// 	this.popupDetailFlag = true;
						// })
					}, 800)
				} else {
					this.guijiInfo.name = "提示";
					this.guijiInfo.desc = '该时间段无轨迹记录'
					this.popupGuijiFlag = true;
				}

			},

			closePopupGuiji() {
				this.popupGuijiFlag = false;
				this.popupGuijiFxFlag = false;
				this.map.setMinZoom(13);
				this.map.setMaxZoom(21);
				clearTimeout(this.hotlineTimer);
				for (var i = 0; i < this.restMarkerList.length; i++) {
					let marker = this.restMarkerList[i].marker
					marker.closePopup();
				}

				if (this.guijiLayer) {
					this.map.removeLayer(this.guijiLayer)
				}
				if (this.bigMarker) {
					this.map.removeLayer(this.bigMarker)
				}
				if (this.clickLayer) {
					this.map.addLayer(this.clickLayer)
				}
				
				if (this.analysisType) {
					this.map.addLayer(this.layers[this.analysisType]);
				} else {
					for (let key in this.layersInfo) {
						if (this.layersInfo[key].checked) {
							this.map.addLayer(this.layers[key])
						}
					}
				}
				
				console.log("打捞路线 closePopupGuiji" + this.dalaoLuXian)
				console.log("巡检路线 closePopupGuiji" + this.xunluoLuXian)
				let routeId = this.checkedDetailLayer.routeId;
				console.log("routeId: " + routeId + "  " + this.routeId)
				if (this.routeId) {
					routeId = this.routeId
				}
				try {
					if (this.dalaoLuXian) {
						console.log("打捞路线 closePopupGuiji")
						this.layers["打捞路线"].eachLayer((layer) => {
							if (routeId == layer.feature.properties.id) {
								this.map.removeLayer(layer)
							}
						})
						this.layers["打捞站点"].eachLayer((layer) => {
							if (this.checkedDetailLayer.routeId == layer.feature.properties.routeId) {
								this.map.removeLayer(layer)
							}
						})
						this.dalaoLuXian = false;
					}
					if (this.xunluoLuXian) {
						this.layers["巡检路线"].eachLayer((layer) => {
							if (routeId == layer.feature.properties.id) {
								this.map.removeLayer(layer)
							}
						})
						this.xunluoLuXian = false;
					}

				} catch (e) {
					//TODO handle the exception
				}
			},
			closePopupGuijiFx() {
				this.popupGuijiFxFlag = false;
				if (this.calGuijiList.length > 0) {
					this.calGuijiList = [];
					this.calGuijilayer.removeFrom(this.guijiLayer);
					this.calGuijilayer = L.layerGroup();
				}
			},
			closePopupPly() {
				this.popupPlyFlag = false;
				if (this.selectedRightItem == "打捞船") {
					let routeId = this.checkedDetailLayer.routeId
					if (this.routeId) {
						routeId = this.routeId;
					}
					this.layers["打捞路线"].eachLayer((layer) => {
						if (routeId == layer.feature.properties.id) {
							this.map.removeLayer(layer)
						}
					})
				}
			},
			openPopupWork() {
				if (this.isAnalysis) {
					return;
				}
				if (this.popupWorkFlag) {
					this.closePopupWork()
				} else {
					this.popupWorkFlag = true;
					this.showLayersFlag = false;
					this.showRightFlag = false;
					this.popupDetailFlag = false;
					this.layers["工作分析"] = L.layerGroup();
					for (let key in this.layersInfo) {
						if (key == "休息点" || key == "游船码头") {
							continue;
						}
						if (this.layersInfo[key].checked && this.layers[key]) {
							this.map.removeLayer(this.layers[key])
						}
						if (this.bigMarker) {
							this.map.removeLayer(this.bigMarker)
							this.clickLayer = null;
						}
					}
				}

			},
			closePopupWork() {
				this.popupWorkFlag = false;
				this.showLayersFlag = true;

				if (this.layers["工作分析"]) {
					this.map.removeLayer(this.layers["工作分析"])
				}
				for (let key in this.layersInfo) {
					if (key == "休息点" || key == "游船码头") {
						continue;
					}
					if (this.layersInfo[key].checked) {
						this.map.addLayer(this.layers[key])
					}
				}
			},

			chooseDetailLayer(item) {

				if (this.$refs.popDetail && this.$refs.popDetail.videoPlayer) {
					this.$refs.popDetail.videoPlayer.pause();
					this.$refs.popDetail.videoPlayer.destroy();
					this.$refs.popDetail.videoPlayer = null;
				}
				this.popupDetailFlag = false;
				this.popupPlyFlag = false;
				this.checkedDetailLayer = JSON.parse(JSON.stringify(item));
				this.checkedDetailLayer.layerType = this.selectedRightItem;

				console.log("checkedDetailLayer: " + JSON.stringify(this.checkedDetailLayer))

				this.closePopupGuiji();

				if (this.selectedRightItem == "打捞路线" || this.selectedRightItem == "巡检范围") {
					this.layers[this.selectedRightItem].eachLayer((layer) => {
						if (layer.feature.properties.name == item.name) {
							this.map.addLayer(layer)
						} else {
							this.map.removeLayer(layer)
						}
					})

					if (this.selectedRightItem == "打捞路线") {
						this.layers["打捞站点"].eachLayer((layer) => {
							if (layer.feature.properties.routeId == item.id) {
								this.map.addLayer(layer)
							} else {
								this.map.removeLayer(layer)
							}
						})
					}

					this.map.flyToBounds(item.bounds)
					// this.popX = this.screenWidth / 2
					// this.popY = this.screenHeight / 2
					// this.plyInfo = {
					// 	type:this.selectedRightItem,
					// 	info:item.name
					// }
					// this.popupPlyFlag = true;
				} else {
					if (this.selectedRightItem == '打捞船') {
						var params1 = {
							id: item.id
						}
						http.getSweepById(params1).then(res => {
							if (res.code == 200) {
								this.checkedDetailLayer.staffName = res.data.staffName;
								this.checkedDetailLayer.staffTel = res.data.staffTel;
							}
						})
					} else if (this.selectedRightItem == '游船') {
						var params2 = {
							id: item.id
						}
						http.getYachtById(params2).then(res => {
							if (res.code == 200) {
								this.checkedDetailLayer.staffName = res.data.staffName;
								this.checkedDetailLayer.staffTel = res.data.staffTel;
							}
						})
					}
					this.showDetail(item);

				}


				// 

			},

			showDetail(item) {
				if (this.selectedRightItem == "电动车" || this.selectedRightItem == "游船" || this.selectedRightItem == "打捞船" ||
					this.selectedRightItem == "安保") {
					if (!item.latitude) {
						return;
					}
					this.map.setView([item.latitude, item.longitude], this.map.getZoom(), {
						pan: {
							animate: true,
							duration: 0.5
						},
						zoom: {
							animate: true
						},
						animate: true
					})
				} else {
					let latlng;
					if (item.latlng) {
						latlng = item.latlng
					} else {
						latlng = [item.latitude, item.longitude]
					}
					this.map.setView(latlng, this.map.getZoom(), {
						pan: {
							animate: true,
							duration: 0.5
						},
						zoom: {
							animate: true
						},
						animate: true
					})
				}

				setTimeout(() => {
					this.popupDetailFlag = true;
					if (this.selectedRightItem == "电动车" || this.selectedRightItem == "游船" || this.selectedRightItem == "打捞船" ||
						this
						.selectedRightItem == "安保") {
						this.popX = this.screenWidth / 2
						this.popY = this.screenHeight / 2 - 70
					} else if (this.selectedRightItem == "监控") {
						this.popX = this.screenWidth / 2 - this.screenWidth / 1920 * 410
						this.popY = this.screenHeight / 3
					} else {
						this.popX = this.screenWidth / 2
						this.popY = this.screenHeight / 2
					}

					if (this.selectedRightItem == "电动车" || this.selectedRightItem == "游船" || this.selectedRightItem == "打捞船" ||
						this.selectedRightItem == "安保") {
						if (this.bigMarker) {
							this.map.removeLayer(this.bigMarker)
						}
						if (this.clickLayer) {
							this.map.addLayer(this.clickLayer)
						}
						this.layers[this.selectedRightItem].eachLayer((layer) => {
							if (layer.feature.properties.id == item.id) {
								this.clickLayer = layer;
							}
						})

						var customIcon = new L.Icon({
							iconUrl: this.layersInfo[this.selectedRightItem].poiUrl,
							iconSize: [96, 132],
							iconAnchor: [48, 106],
							zIndexOffset: 100,
						});
						this.bigMarker = L.marker([item.latitude, item.longitude], {
							icon: customIcon,
							zIndexOffset: 100
						});
						let name = item.name;
						if (this.selectedRightItem == "游船" || this.selectedRightItem == "打捞船") {
							name = item.name;
						}
						if (this.selectedRightItem == "安保") {
							name = item.username;
						}
						this.bigMarker.bindTooltip(name, {
							permanent: 'true',
							direction: 'bottom',
							className: 'leaflet-label'
							// offset: [0, 18]
						}).openTooltip();
						this.bigMarker.addTo(this.map);
						this.map.removeLayer(this.clickLayer)
						this.bigMarkerType = this.selectedRightItem;
					}
					if (this.selectedRightItem == "管理人员" || this.selectedRightItem == "保洁" ||this.selectedRightItem == "监控" || this.selectedRightItem == "休息点" ||
						this.selectedRightItem == "游船码头") {
						if (this.bigMarker) {
							this.map.removeLayer(this.bigMarker)
						}
						if (this.clickLayer) {
							this.map.addLayer(this.clickLayer)
						}
						this.layers[this.selectedRightItem].eachLayer((layer) => {
							if (layer.feature.properties.id == item.id) {
								this.clickLayer = layer;
							}
						})

						var customIcon = new L.Icon({
							iconUrl: this.layersInfo[this.selectedRightItem].poiUrl,
							iconSize: [48, 48],
							iconAnchor: [24, 24],
							zIndexOffset: 100,
						});
						this.bigMarker = L.marker(item.latlng, {
							icon: customIcon,
							zIndexOffset: 100
						});
						let name = item.name;
						if (this.selectedRightItem == "管理人员" || this.selectedRightItem == "保洁") {
							name = item.username;
						}
						this.bigMarker.bindTooltip(name, {
							permanent: 'true',
							direction: 'bottom',
							className: 'leaflet-label',
							offset: [0, 18]
						}).openTooltip();
						this.bigMarker.addTo(this.map);
						this.map.removeLayer(this.clickLayer)
						this.bigMarkerType = this.selectedRightItem;
					}


					this.$nextTick(() => {
						this.$refs.popDetail.detailInfo = this.checkedDetailLayer
						if (this.selectedRightItem == "监控")
							this.$refs.popDetail.getRtmp();
					});
				}, 500)
			},

			toggleType(layer, checked, type) {
				console.log("toggleType: " + checked + type)
				this.analysisLayer.poiList = [];
				if (checked) {
					if (this.analysisType) {
						this.map.removeLayer(this.layers[this.analysisType]);
						if (this.bigMarker) {
							if (this.bigMarkerType == this.analysisType) {
								this.map.removeLayer(this.bigMarker)
								this.clickLayer = null;
								this.popupDetailFlag = false;
							}
						}

						if (this.layers["调度分析"]) {
							this.map.removeLayer(this.layers["调度分析"]);
							this.analysisResultFlag = false;
						}
						this.showAnalysisRightFlag = false;
					}
					this.analysisType = type;
					this.selectedRightItem = type;
					if (this.bigMarker) {
						if (this.bigMarkerType == this.analysisType) {
							this.map.removeLayer(this.bigMarker)
							this.clickLayer = null;
							this.popupDetailFlag = false;
						} else {
							// this.map.removeLayer(this.bigMarker)
							// this.map.addLayer(this.clickLayer)
						}
					}
					this.map.addLayer(this.layers[this.analysisType]);
					this.ananlysisTipFlag = true;
					this.tipMsg = "请选择调度区域"
					setTimeout(() => {
						this.ananlysisTipFlag = false;
					}, 1000)
				} else {
					if (this.bigMarker) {
						if (this.bigMarkerType == type) {
							this.map.removeLayer(this.bigMarker)
							this.clickLayer = null;
							this.popupDetailFlag = false;
						}
					}
					this.map.removeLayer(this.layers[type]);
					this.analysisResultFlag = false;
					this.analysisType = "";
					if (this.layers["调度分析"]) {
						this.map.removeLayer(this.layers["调度分析"]);
					}
				}

			},

			goManage() {
				let userName = localStorage.getItem('jq_username')
				let password = localStorage.getItem('jq_password')
				let params = {
					username: userName,
					password: password
				}
				console.log(window.location.href);
				window.open("http://zjtoprs.chinananxun.com/#/login?" + Base64.encode(JSON.stringify(params), "utf-8"), '_blank');
				// window.open("http://zjtoprs.f3322.net:9092/#/login?" + Base64.encode(JSON.stringify(params), "utf-8"), '_blank');
			},

			//点击开始计算轨迹
			calGuiji() {
				this.isCalGuiji = !this.isCalGuiji;
				if (this.isCalGuiji) {
					this.popupGuijiFxFlag = false;
					this.ananlysisTipFlag = true;
					this.tipMsg = "请在轨迹中选取第一个点"
					setTimeout(() => {
						this.ananlysisTipFlag = false;
					}, 1500)
				}
			},

			getServeTime() {
				// clearInterval(this.dataInterval)
				let serveTime = new Date().getTime();
				this.getNowTime(serveTime)
				this.dataInterval = setInterval(() => {
					serveTime = serveTime + 1000;
					this.getNowTime(serveTime)
				}, 1000);
			},

			getNowTime(serveTime) {
				//获取年月日
				var time = new Date(serveTime);
				var year = time.getFullYear();
				var month = time.getMonth() + 1;
				var date = time.getDate();

				//获取时分秒
				var h = time.getHours();
				var m = time.getMinutes();
				var s = time.getSeconds();

				var day = time.getDay();
				var weeks = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
				var week = weeks[day];

				//检查是否小于10
				h = this.check(h);
				m = this.check(m);
				s = this.check(s);
				this.time = h + ":" + m + ":" + s;
				this.date = year + "年" + month + "月" + date + "日  " + week;
				if (this.time == "23:59:59") {
					top.window.location.reload(true);
				}
			},
			
			
			formatTime(serveTime) {
				//获取年月日
				var time = new Date(serveTime);
				var year = time.getFullYear();
				var month = time.getMonth() + 1;
				var date = time.getDate();
			
				//获取时分秒
				var h = time.getHours();
				var m = time.getMinutes();
				var s = time.getSeconds();
			
				var day = time.getDay();
				var weeks = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
				var week = weeks[day];
			
				//检查是否小于10
				h = this.check(h);
				m = this.check(m);
				s = this.check(s);
				
				return year + "/" + month + "/" + date + " "+h + ":" + m + ":" + s;
			},
			
			getTotal(list){
				let num = 0;
				for (var i = 0; i < list.length; i++) {
					if(list[i].gpsCode || this.selectedRightItem=="监控"|| this.selectedRightItem=="打捞路线"|| this.selectedRightItem=="巡检范围"){
						num ++;
					}
				}
				return num;
			},
			
			//时间数字小于10，则在之前加个“0”补位。
			check(i) {
				var num;
				i < 10 ? num = "0" + i : num = i;
				return num;
			}

		},
	}
</script>
<style scoped src="./Home.css">
</style>
