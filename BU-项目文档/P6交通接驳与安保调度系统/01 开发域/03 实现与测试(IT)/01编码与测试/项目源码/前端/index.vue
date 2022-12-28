<template>
	<view class="content">
		<!-- <image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view> -->

		<view class="head">
			<view v-if="isShow" class="head_i" @click="goMy">
				<image src="../../static/img/my.png"></image>
				<text>我的</text>
			</view>
			<view v-else class="head_i">
			</view>
			<text class="head_title">交通接驳管理系统</text>
			<view class="head_i" @click="goAnalysis">
				<image src="../../static/img/diaodu.png"></image>
				<text>调度分析</text>
			</view>
		</view>
		<view class="content_c">
			<div id="map"></div>
			<view class="search">
				<image src="../../static/img/search.png"></image>
				<form v-on:submit.prevent="search()" action="">
					<input autocomplete="off" id="search" type="search" placeholder="搜索人员或者工具编号"
						placeholder-class="placeholderInput" v-model="searchText" @input="searchInput"
						@keydown.enter="search" />
				</form>

				<div v-if="searchList.length>0" class="search_result">
					<span v-for="(item,index) in searchList" :key="index" class="search_item"
						@click="searchClick(item)">
						{{item.name?item.name:item.username}}
					</span>
				</div>

			</view>
			<touch-slide ref="touchSlide" style="background-color: #FFFFFF;" :top="mapHeight*0.15"
				:bottom="mapHeight-200" :titleHeight="40" :left="0">
				<!-- <text slot="title" style="background-color: #2C405A;
									border-top-left-radius: 4px;
									border-top-right-radius: 4px;
									color: #FFFFFF;">我是顶部固定部分</text> -->

				<view slot="title" style="width: 100%; display: flex;flex-direction: column;">
					<scroll-view scroll-x="true">
						<view class="tab">
							<text v-if="authorities.includes('交通接驳游船管理')" :class="selectedTab=='游船'?'item_p':'item_n'"
								@click="chooseTab('游船')">游船</text>
							<text v-if="authorities.includes('交通接驳安保车辆管理')"
								:class="selectedTab=='电动车'?'item_p':'item_n'" @click="chooseTab('电动车')">电动车</text>
							<text v-if="authorities.includes('交通接驳安保人员管理')" :class="selectedTab=='安保'?'item_p':'item_n'"
								@click="chooseTab('安保')">安保</text>
							<text v-if="authorities.includes('交通接驳保洁人员管理')" :class="selectedTab=='保洁'?'item_p':'item_n'"
								@click="chooseTab('保洁')">保洁</text>
							<text v-if="authorities.includes('交通接驳打捞船管理')" :class="selectedTab=='打捞船'?'item_p':'item_n'"
								@click="chooseTab('打捞船')">打捞船</text>
							<text v-if="authorities.includes('监控点监控视频查看')" :class="selectedTab=='监控'?'item_p':'item_n'"
								@click="chooseTab('监控')">监控 </text>
							<text v-if="authorities.includes('交通接驳管理人员管理')"
								:class="selectedTab=='管理人员'?'item_p':'item_n'" @click="chooseTab('管理人员')">综合管理</text>
						</view>
					</scroll-view>
					<view style="width: 100%;height: 2upx; background:#DBDBDB;"></view>
				</view>

				<view v-if="selectedTab" style="padding-bottom: 80upx;box-sizing: border-box;">
					<!-- <scroll-view scroll-y="true" :scroll-top="scrollTop" style="padding-bottom: 80upx;" @scroll="scroll"> -->
					<view v-for="(item,index) in layersInfo[selectedTab].poiList">
						<div :id="item.name" v-if="selectedTab=='监控'"
							:class="selectedJkid==item.id?'item_jk_p':'item_jk'" hover-class="item_jk_p"
							@click="chooseJkDetail(item)">
							<image class="img_l" src="../../static/img/icon_jk.png"></image>
							<text>{{item.name}}</text>
						</div>
						<div :id="item.name" v-else-if="selectedTab=='游船'||selectedTab=='打捞船'"
							:class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
							@click="chooseDetail(item)">
							<view class="item_top">
								<image v-if="selectedTab=='游船'" class="img_l" src="../../static/img/icon_yc.png">
								</image>
								<image v-else-if="selectedTab=='打捞船'" class="img_l" src="../../static/img/icon_dlc.png">
								</image>
								<text class="item_name">{{item.name}}</text>
								<view class="item_state_bg">
									<text class="item_state">{{item.currentStatus?item.currentStatus:'不在线'}}</text>
								</view>
								<!-- <text class="energy_percent">78%</text>
								<image class="img_energy" src="../../static/img/energy_4.png"></image> -->
							</view>
							<view class="item_mid">
								<text class="item_text_n">船工：{{item.staffName}}</text>
								<view class="item_guiji">
									<text class="item_text_n">历史轨迹：</text>
									<text class="item_text_blue" @click.stop="showGuiji(item)">查看</text>
								</view>
							</view>
							<view class="item_bottom">
								<text class="item_text_n">联系方式：</text>
								<text class="item_text_blue"
									@click.stop="makePhoneCall(item.staffTel,item.serial)">{{item.staffTel}}</text>
							</view>
						</div>
						<div :id="item.name" v-else-if="selectedTab=='电动车'"
							:class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
							@click="chooseDetail(item)">
							<view class="item_top">
								<image class="img_l" src="../../static/img/icon_ddc.png"></image>
								<text class="item_name">{{item.name}}</text>
								<view class="item_state_bg">
									<text class="item_state">{{item.currentStatus?item.currentStatus:'不在线'}}</text>
								</view>
								<!-- <text class="energy_percent">78%</text>
								<image class="img_energy" src="../../static/img/energy_4.png"></image> -->
							</view>
							<view class="item_mid">
								<text class="item_text_n">负责人：{{item.staffName}}</text>
								<view class="item_guiji">
									<text class="item_text_n">历史轨迹：</text>
									<text class="item_text_blue" @click.stop="showGuiji(item)">查看</text>
								</view>
							</view>
							<view class="item_bottom">
								<text class="item_text_n">联系方式：</text>
								<text class="item_text_blue"
									@click.stop="makePhoneCall(item.staffTel,item.serial)">{{item.staffTel}}</text>
							</view>
						</div>
						<div :id="item.username" v-else-if="selectedTab=='安保'"
							:class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
							@click="chooseDetail(item)">
							<view class="item_top">
								<image class="img_l" src="../../static/img/icon_ab.png"></image>
								<text class="item_name">{{item.username}}</text>
								<view class="ab_1">
									<text>{{item.type}}</text>
								</view>
								<view class="ab_2">
									<text>{{item.routeName?item.routeName:'网格长'}}</text>
								</view>
							</view>
							<view class="item_mid">
								<text class="item_text_n">编号：{{item.serial}}</text>
								<view class="item_guiji">
									<text class="item_text_n">历史轨迹：</text>
									<text class="item_text_blue" @click.stop="showGuiji(item)">查看</text>
								</view>
							</view>
							<view class="item_bottom">
								<text class="item_text_n">联系方式：</text>
								<text class="item_text_blue"
									@click.stop="makePhoneCall(item.tel,item.serial)">{{item.tel}}</text>
							</view>
						</div>
						<div :id="item.username" v-else-if="selectedTab=='管理人员'"
							:class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
							@click="chooseDetail(item)">
							<view class="item_top">
								<image class="img_l" src="../../static/img/icon_manage.png"></image>
								<text class="item_name">{{item.username}}</text>
								<text
									style="font-size: 24upx;font-weight: 500;color: #4BC437; margin-left: 12upx;">{{item.post}}</text>
							</view>
							<view class="item_mid">
								<text class="item_text_n">编号：{{item.serial}}</text>
								<view class="item_guiji">
									<text class="item_text_n">历史轨迹：</text>
									<text class="item_text_blue" @click.stop="showGuiji(item)">查看</text>
								</view>
							</view>
							<view class="item_bottom">
								<text class="item_text_n">联系方式：</text>
								<text class="item_text_blue"
									@click.stop="makePhoneCall(item.tel,item.serial)">{{item.tel}}</text>
							</view>
						</div>

						<div :id="item.username" v-else-if="selectedTab=='保洁'"
							:class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
							@click="chooseDetail(item)">
							<view class="item_top">
								<image class="img_l" src="../../static/img/icon_bj.png"></image>
								<text class="item_name">{{item.username}}</text>
								<text
									style="font-size: 24upx;font-weight: 500;color: #4BC437; margin-left: 12upx;">{{item.region}}</text>
							</view>
							<view class="item_mid">
								<text class="item_text_n">编号：{{item.serial}}</text>
								<view class="item_guiji">
									<text class="item_text_n">历史轨迹：</text>
									<text class="item_text_blue" @click.stop="showGuiji(item)">查看</text>
								</view>
							</view>
							<view class="item_bottom">
								<text class="item_text_n">联系方式：</text>
								<text class="item_text_blue"
									@click.stop="makePhoneCall(item.tel,item.serial)">{{item.tel}}</text>
							</view>
						</div>
					</view>
					<!-- </scroll-view> -->
				</view>

			</touch-slide>

		</view>
	</view>
</template>

<script>
	import touchSlide from '@/components/touch-slide/touch-slide.vue'
	import http from '../../common/http.js';
	var Base64 = require('js-base64').Base64;
	export default {
		components: {
			touchSlide,
		},
		data() {
			return {
				checkedGuijiItem: {},
				selectedSerial: '',
				selectedJkid: '',
				title: 'Hello',
				mapHeight: 650,
				map: null,
				selectedTab: "",
				searchText: "",
				geoServerUrl: "/gisServer/geoserver/ows",
				geoParams: {
					service: 'WFS',
					version: '1.1.0',
					request: 'GetFeature',
					typeName: 'oldtown:trf_security_car',
					outputFormat: 'application/json',
					srsName: 'EPSG:4326',
				},
				layers: {},
				layersInfo: {
					"电动车": {
						num: 3,
						iconUrl: require("../../static/img/icon_ddc.png"),
						checked: true,
						poiList: []
					},
					"游船": {
						num: 5,
						iconUrl: require("../../static/img/icon_yc.png"),
						checked: false,
						poiList: []
					},
					"打捞船": {
						num: 2,
						iconUrl: require("../../static/img/icon_dlc.png"),
						checked: false,
						poiList: []
					},
					"安保": {
						num: 3,
						iconUrl: require("../../static/img/icon_ab.png"),
						checked: false,
						poiList: []
					},
					"保洁": {
						num: 3,
						iconUrl: require("../../static/img/icon_bj.png"),
						checked: false,
						poiList: []
					},
					"管理人员": {
						num: 4,
						iconUrl: require("../../static/img/icon_manage.png"),
						checked: false,
						poiList: []
					},
					"监控": {
						num: 2,
						iconUrl: require("../../static/img/icon_jk.png"),
						checked: false,
						poiList: []
					},
					// "打捞路线": {
					// 	num: 1,
					// 	iconUrl: require("../../static/img/dlclx.png"),
					// 	checked: false,
					// 	poiList: []
					// },
					// "巡检范围": {
					// 	num: 1,
					// 	iconUrl: require("../../static/img/abxj.png"),
					// 	checked: false,
					// 	poiList: []
					// },
					"休息点": {
						num: 1,
						iconUrl: require("../../static/img/icon_xxd.png"),
						checked: true,
						poiList: []
					},
					"游船码头": {
						num: 1,
						iconUrl: require("../../static/img/icon_ycmt.png"),
						checked: true,
						poiList: []
					},

				},
				authorities: [],
				searchList: [],
				locationMark: null,
				isShow: true,
				bigMarkerkey: "",
			}
		},
		onLoad() {
			//uni获取本地数据API
			uni.getStorage({
				key: 'authorities', //数据key值，也就是你存储数据时的名称
				success: (res) => {
					//数据成功获取，用户已登录
					this.authorities = res.data;
				},
				fail: (res) => {
					//数据未获取成功，用户没有登录，这里我们直接跳转到登录页面
					uni.redirectTo({
						url: '../login/login',
					});
				}
			});
			window.getLocation = this.getLocation;
			// var u = navigator.userAgent;
			// var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
			// var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);//ios终端
			// if(isiOS){
			// 	this.isShow = false
			// } else {
			// 	this.isShow = true
			// }

		},
		mounted() {
			const _this = this;
			console.log("mounted")
			this.initMap()

			uni.getSystemInfo({
				success: (res) => {
					_this.mapHeight = res.screenHeight - res.statusBarHeight
					// #ifdef APP-NVUE || MP-WEIXIN
					_this.mapHeight = _this.mapHeight - 45
					// #endif
				}
			})
		},
		onShow() {
			console.log("onShow")
			// uni.showModal({
			// 	content:"定位"
			// })
		},
		methods: {
			getLocation(lat, lng) {
				getApp().globalData.lat = lat;
				getApp().globalData.lng = lng;
				if (this.map) {
					if (!this.locationMark) {
						var customIcon = new L.Icon({
							iconUrl: require('../../static/img/location.png'),
							iconSize: [24, 24],
							iconAnchor: [12, 24],
						});
						this.locationMark = L.marker([lat, lng], {
							icon: customIcon
						}).addTo(this.map);
					} else {
						this.locationMark.removeFrom(this.map);
						var customIcon = new L.Icon({
							iconUrl: require('../../static/img/location.png'),
							iconSize: [24, 24],
							iconAnchor: [12, 24],
						});
						this.locationMark = L.marker([lat, lng], {
							icon: customIcon
						}).addTo(this.map);
					}

				} else {

				}

			},
			initMap() {
				this.map = L.map("map", {
					center: [30.878191, 120.426207],
					zoom: 14,
					maxZoom: 21,
					minZoom: 12,
					// maxBounds: bounds,
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

				var ign = new L.TileLayer.WMTS("/gisServer/geoserver/gwc/service/wmts", {
					layer: "nanxun_jbjbMaps", //图层名称
					tilematrixSet: "EPSG:4326", //GeoServer使用的网格名称
					width: 20,
					height: 20,
					format: 'image/png',
					maxZoom: 21,
					minZoom: 12,
					matrixIds: matrixIds,
				})

				this.map.addLayer(ign);

				var lat = getApp().globalData.lat;
				var lng = getApp().globalData.lng
				// uni.showModal({
				// 	title:"initMap",
				// 	content:lat+"," + lng
				// })
				if (lat && lng) {
					if (!this.locationMark) {
						var customIcon = new L.Icon({
							iconUrl: require('../../static/img/location.png'),
							iconSize: [24, 24],
							iconAnchor: [12, 24],
						});
						this.locationMark = L.marker([lat, lng], {
							icon: customIcon
						}).addTo(this.map);
					} else {
						this.locationMark.removeFrom(this.map);
						var customIcon = new L.Icon({
							iconUrl: require('../../static/img/location.png'),
							iconSize: [24, 24],
							iconAnchor: [12, 24],
						});
						this.locationMark = L.marker([lat, lng], {
							icon: customIcon
						}).addTo(this.map);
					}
				}

				//码头 休息点
				this.pointSever();

				if (this.authorities.includes("交通接驳游船管理")) {
					this.selectedTab = "游船"
					//游船
					this.yachtSever();
				}
				if (this.authorities.includes("交通接驳安保车辆管理")) {
					if (!this.selectedTab) {
						this.selectedTab = "电动车"
					}
					//电动车
					this.ddcSever();
				}

				if (this.authorities.includes("交通接驳安保人员管理")) {
					if (!this.selectedTab) {
						this.selectedTab = "安保"
					}
					//安保人员
					this.securityStaffSever();
				}

				if (this.authorities.includes("交通接驳保洁人员管理")) {
					if (!this.selectedTab) {
						this.selectedTab = "保洁"
					}
					//安保人员
					this.cleanStaffSever();
				}

				if (this.authorities.includes("交通接驳打捞船管理")) {
					if (!this.selectedTab) {
						this.selectedTab = "打捞船"
					}
					//打捞船
					this.sweepSever()
				}

				if (this.authorities.includes("监控点监控视频查看")) {
					if (!this.selectedTab) {
						this.selectedTab = "监控"
					}
					//监控
					this.monitorSever();
				}

				if (this.authorities.includes("交通接驳管理人员管理")) {
					if (!this.selectedTab) {
						this.selectedTab = "管理人员"
					}
					//管理人员
					this.dockStaffSever();
				}

				setInterval(() => {
					console.log("setInterval")

					if (this.authorities.includes("交通接驳安保车辆管理") && this.selectedTab == "电动车") {
						//电动车
						this.ddcSever();
					}
					if (this.authorities.includes("交通接驳游船管理") && this.selectedTab == "游船") {
						//游船
						this.yachtSever();
					}
					if (this.authorities.includes("交通接驳打捞船管理") && this.selectedTab == "打捞船") {
						//打捞船
						this.sweepSever()
					}
					if (this.authorities.includes("交通接驳安保人员管理") && this.selectedTab == "安保") {
						//安保人员
						this.securityStaffSever();
					}

					if (this.authorities.includes("交通接驳保洁人员管理") && this.selectedTab == "安保") {
						//保洁人员
						this.cleanStaffSever();
					}

					if (this.authorities.includes("交通接驳管理人员管理") && this.selectedTab == "管理人员") {
						//管理人员
						this.dockStaffSever();
					}

				}, 1000 * 10)

			},
			chooseTab(item) {
				if (this.selectedTab == item) {
					return;
				}
				this.selectedSerial = "";
				this.selectedJkid = "";
				this.map.removeLayer(this.layers[this.selectedTab])
				if (this.bigMarker) {
					this.map.removeLayer(this.bigMarker)
					this.bigMarker = null;
					this.clickLayer = null;
				}
				this.selectedTab = item;
				this.map.addLayer(this.layers[this.selectedTab])
				this.$refs.touchSlide.scrollTop = 0;

				this.$nextTick(() => {
					let info = this.layersInfo[this.selectedTab].poiList[0]
					let id = info.name;
					if (info.username) {
						id = info.username
					}
					document.getElementById(id).scrollIntoView({
						behavior: "smooth", // 默认 auto
						block: "center", // 默认 center
						inline: "nearest", // 默认 nearest
					});
				})
			},
			search() {
				// uni.showToast({
				// 	title: this.searchText,
				// 	icon: "none",
				// })

				if (!this.searchText) {
					return;
				}
				let params = {
					keyword: this.searchText,
					orderByCurrentStatus: 1,
				}
				switch (this.selectedTab) {
					case "电动车":
						http.getCarAll(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "游船":
						http.getYachtAll(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					case "打捞船":
						http.getSweepAll(params).then(res => {
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
					case "监控":
						params.type = "监控点";
						http.getComPoiAll(params).then(res => {
							if (res.code == 200) {
								this.searchList = res.data;
							}
						})
						break;
					default:
						break;
				}

			},

			searchInput(event) {
				console.log("searchInput" + event.detail.value)
				if (!event.detail.value) {
					this.searchList = [];
				} else {
					this.search();
				}
			},

			searchClick(item) {
				let info = this.layersInfo[this.selectedTab];
				for (var i = 0; i < info.poiList.length; i++) {
					if (info.poiList[i].id == item.id) {
						this.chooseDetail(info.poiList[i]);
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

			//游船
			yachtSever() {
				this.geoParams.typeName = 'oldtown:trf_yacht'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['游船']) {
						this.map.removeLayer(this.layers['游船'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layers['游船'] = L.geoJSON(res, {
						pointToLayer: (feature, latlng) => {
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "游船";
							// this.layersInfo['游船'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船"].iconUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if (this.selectedTab == "游船") {
						this.layers['游船'].addTo(this.map)
					}
					this.getYachtAll();
					this.layers['游船'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.name) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["游船"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.name, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["游船"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "游船";
							this.bigMarkerkey = layer.feature.properties.name;

							let id = e.target.feature.properties.name
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});

				})
			},

			//获取游船列表
			getYachtAll() {
				var params = {
					orderByCurrentStatus: 1
				}
				http.getYachtAll(params).then(res => {
					this.layersInfo['游船'].poiList = res.data;
				})
			},

			//打捞船
			sweepSever() {
				this.geoParams.typeName = 'oldtown:trf_sweep'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['打捞船']) {
						this.map.removeLayer(this.layers['打捞船'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layers['打捞船'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						pointToLayer: (feature, latlng) => {
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "打捞船";
							// this.layersInfo['打捞船'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["打捞船"].iconUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon,
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});
					if (this.selectedTab == "打捞船") {
						this.layers['打捞船'].addTo(this.map)
					}
					this.getSweepAll();

					this.layers['打捞船'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.name) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["打捞船"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.name, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["打捞船"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "打捞船";
							this.bigMarkerkey = layer.feature.properties.name;
							let id = e.target.feature.properties.name
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});
				})
			},
			//获取打捞船列表
			getSweepAll() {
				var params = {
					orderByCurrentStatus: 1
				}
				http.getSweepAll(params).then(res => {
					this.layersInfo['打捞船'].poiList = res.data;
				})
			},
			//安保
			securityStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_security_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['安保']) {
						this.map.removeLayer(this.layers['安保'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layers['安保'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
						filter: function(feature, layer) {
							return feature.properties.currentStatus == "行驶" || feature.properties.currentStatus == "静止" || feature.properties.currentStatus == "定位精度差";
						},
						pointToLayer: (feature, latlng) => {
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "安保";
							// this.layersInfo['安保'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["安保"].iconUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						}
					});

					if (this.selectedTab == "安保") {
						this.layers['安保'].addTo(this.map)
					}
					this.getAllSecurityStaff();

					this.layers['安保'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.username) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["安保"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.username, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							console.log("click: " + e.latlng)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
								this.addLayer = this.clickLayer;
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["安保"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "安保";
							this.bigMarkerkey = layer.feature.properties.username;
							let id = e.target.feature.properties.username
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});
				})
			},

			getAllSecurityStaff() {
				var params = {
					orderByCurrentStatus: 1
				}
				http.getAllSecurityStaff(params).then(res => {
					this.layersInfo['安保'].poiList = res.data.filter((item) => {
						return item.currentStatus == "行驶" || item.currentStatus =="静止" || item.currentStatus =="定位精度差"
					});
				})
			},

			//保洁
			cleanStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_cleaning_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['保洁']) {
						this.map.removeLayer(this.layers['保洁'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layersInfo['保洁'].poiList = [];
					this.layers['保洁'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.currentStatus == "行驶" || feature.properties.currentStatus == "静止" || feature.properties.currentStatus == "定位精度差";
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "保洁";
							this.layersInfo['保洁'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["保洁"].iconUrl,
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

					if (this.selectedTab == "保洁") {
						this.layers['保洁'].addTo(this.map)
					}
					this.layers['保洁'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.username) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["保洁"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.username, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["保洁"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "保洁";
							this.bigMarkerkey = layer.feature.properties.username;
							let id = e.target.feature.properties.username
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});
				})
			},


			//管理人员
			dockStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_dock_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['管理人员']) {
						this.map.removeLayer(this.layers['管理人员'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layersInfo['管理人员'].poiList = [];
					this.layers['管理人员'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.currentStatus == "行驶" || feature.properties.currentStatus == "静止" || feature.properties.currentStatus == "定位精度差";
						},
						pointToLayer: (feature, latlng) => {
							feature.properties.latlng = latlng;
							feature.properties.layerType = "管理人员";
							this.layersInfo['管理人员'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["管理人员"].iconUrl,
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

					if (this.selectedTab == "管理人员") {
						this.layers['管理人员'].addTo(this.map)
					}
					this.layers['管理人员'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.username) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["管理人员"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.username, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["管理人员"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.username, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "管理人员";
							this.bigMarkerkey = layer.feature.properties.username;
							let id = e.target.feature.properties.username
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
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
								iconUrl: this.layersInfo["监控"].iconUrl,
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

					this.layers['监控'].eachLayer((layer) => {
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["监控"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "监控";
							this.bigMarkerkey = layer.feature.properties.name;
							let id = e.target.feature.properties.name
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedJkid = e.target.feature.properties.id;

							this.playVideo(e.target.feature.properties)
						});
					});

				})
			},

			//电动车服务
			ddcSever() {
				this.geoParams.typeName = 'oldtown:trf_security_car'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					if (this.layers['电动车']) {
						this.map.removeLayer(this.layers['电动车'])
					}
					if (this.addLayer) {
						this.map.removeLayer(this.addLayer)
						this.addLayer = null;
					}
					this.layers['电动车'] = L.geoJSON(res, {
						pointToLayer: (feature, latlng) => {
							// console.log("pointToLayer 电动车: " + feature);
							// feature.properties.latlng = latlng;
							// feature.properties.layerType = "电动车";
							// this.layersInfo['电动车'].poiList.push(feature.properties);
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["电动车"].iconUrl,
								iconSize: [40, 40],
								iconAnchor: [20, 20],
							});
							var marker = L.marker(latlng, {
								icon: customIcon
							});
							marker.bindTooltip(feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								offset: [0, 18]
							}).openTooltip();
							return marker;
						},

					});
					if (this.selectedTab == "电动车") {
						this.layers['电动车'].addTo(this.map)
					}
					this.getCarAll();
					this.layers['电动车'].eachLayer((layer) => {
						if (this.bigMarkerkey == layer.feature.properties.name) {
							console.log("bigMarkerkey: " + this.bigMarkerkey)
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
								this.map.removeLayer(layer)

								var customIcon = new L.Icon({
									iconUrl: this.layersInfo["电动车"].iconUrl,
									iconSize: [48, 48],
									iconAnchor: [24, 24],
								});
								let latlng = [layer.feature.geometry.coordinates[1], layer.feature.geometry
									.coordinates[0]
								]
								this.bigMarker = L.marker(latlng, {
									icon: customIcon,
									zIndexOffset: 1000,
								});
								this.bigMarker.bindTooltip(layer.feature.properties.name, {
									permanent: 'true',
									direction: 'bottom',
									className: 'leaflet-label',
									offset: [0, 22]
								}).openTooltip();
								this.bigMarker.addTo(this.map);
							}
						}
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
							}
							this.clickLayer = layer;
							var customIcon = new L.Icon({
								iconUrl: this.layersInfo["电动车"].iconUrl,
								iconSize: [48, 48],
								iconAnchor: [24, 24],
							});
							this.bigMarker = L.marker(e.latlng, {
								icon: customIcon,
								zIndexOffset: 1000,
							});
							this.bigMarker.bindTooltip(layer.feature.properties.name, {
								permanent: 'true',
								direction: 'bottom',
								className: 'leaflet-label',
								offset: [0, 22]
							}).openTooltip();
							this.bigMarker.addTo(this.map);
							this.map.removeLayer(this.clickLayer)
							this.bigMarkerType = "电动车";
							this.bigMarkerkey = layer.feature.properties.name;
							let id = e.target.feature.properties.name
							document.getElementById(id).scrollIntoView({
								behavior: "smooth", // 默认 auto
								block: "center", // 默认 center
								inline: "nearest", // 默认 nearest
							});
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});
				})
			},

			//获取电动车列表
			getCarAll() {
				var params = {
					orderByCurrentStatus: 1
				}
				http.getCarAll(params).then(res => {
					this.layersInfo['电动车'].poiList = res.data;
				})
			},

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
								iconUrl: this.layersInfo["游船码头"].iconUrl,
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
								iconUrl: this.layersInfo["休息点"].iconUrl,
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
				})
			},

			//点击列表中某个点
			chooseDetail(item) {
				this.selectedSerial = item.serial;
				let latlng;
				if (item.latlng) {
					latlng = item.latlng;
				} else {
					if (item.latitude && item.longitude) {
						latlng = [item.latitude, item.longitude]
					}
				}
				let name = item.name;
				if (item.username) {
					name = item.username;
				}

				if (!latlng) {
					uni.showToast({
						title: "无位置信息",
						icon: "none"
					})
					return;
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
				this.$refs.touchSlide.btnClick('toBottom');

				if (this.bigMarker) {
					this.map.removeLayer(this.bigMarker)
				}
				if (this.clickLayer) {
					this.map.addLayer(this.clickLayer)
				}

				this.layers[this.selectedTab].eachLayer((layer) => {
					let key = layer.feature.properties.name;
					if (layer.feature.properties.username) {
						key = layer.feature.properties.username
					}
					if (key == name) {
						this.clickLayer = layer
					}
				})
				var customIcon = new L.Icon({
					iconUrl: this.layersInfo[this.selectedTab].iconUrl,
					iconSize: [48, 48],
					iconAnchor: [24, 24],
					zIndexOffset: 100,
				});
				this.bigMarker = L.marker(latlng, {
					icon: customIcon,
					zIndexOffset: 1000,
				});
				this.bigMarker.bindTooltip(name, {
					permanent: 'true',
					direction: 'bottom',
					className: 'leaflet-label',
					offset: [0, 22]
				}).openTooltip();
				this.bigMarker.addTo(this.map);
				this.map.removeLayer(this.clickLayer)
				this.bigMarkerType = this.selectedTab;
				this.bigMarkerkey = name;
				setTimeout(() => {
					document.getElementById(name).scrollIntoView({
						behavior: "smooth", // 默认 auto
						block: "center", // 默认 center
						inline: "nearest", // 默认 nearest
					});
				}, 500)

			},
			chooseJkDetail(item) {
				this.selectedJkid = item.id;
				this.map.setView(item.latlng, this.map.getZoom(), {
					pan: {
						animate: true,
						duration: 0.5
					},
					zoom: {
						animate: true
					},
					animate: true
				})

				let name = item.name;
				if (item.username) {
					name = item.username;
				}

				if (this.bigMarker) {
					this.map.removeLayer(this.bigMarker)
				}
				if (this.clickLayer) {
					this.map.addLayer(this.clickLayer)
				}

				this.layers[this.selectedTab].eachLayer((layer) => {
					let key = layer.feature.properties.name;
					if (layer.feature.properties.username) {
						key = layer.feature.properties.username
					}
					if (key == name) {
						this.clickLayer = layer
					}
				})
				var customIcon = new L.Icon({
					iconUrl: this.layersInfo[this.selectedTab].iconUrl,
					iconSize: [48, 48],
					iconAnchor: [24, 24],
					zIndexOffset: 100,
				});
				this.bigMarker = L.marker(item.latlng, {
					icon: customIcon
				});
				this.bigMarker.bindTooltip(name, {
					permanent: 'true',
					direction: 'bottom',
					className: 'leaflet-label',
					offset: [0, 22]
				}).openTooltip();
				this.bigMarker.addTo(this.map);
				this.map.removeLayer(this.clickLayer)
				this.bigMarkerType = this.selectedTab;
				this.bigMarkerkey = name;
				document.getElementById(name).scrollIntoView({
					behavior: "smooth", // 默认 auto
					block: "center", // 默认 center
					inline: "nearest", // 默认 nearest
				});

				this.playVideo(item)
			},

			playVideo(item) {
				var browser = {
					versions: function() {
						var u = navigator.userAgent,
							app = navigator.appVersion;
						return { //移动终端浏览器版本信息 
							ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
							android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
							iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
							iPad: u.indexOf('iPad') > -1, //是否iPad
						};
					}(),
					language: (navigator.browserLanguage || navigator.language).toLowerCase()
				}

				var params = {
					code: item.code
				}
				http.getRtmp(params).then(res => {
					if (res.code == 200) {
						if (browser.versions.ios || browser.versions.iPhone || browser.versions.iPad) {
							// 调用IOS的方法
							// window.webkit.messageHandlers.palyVideo.postMessage();
							window.webkit.messageHandlers.palyVideo.postMessage({
								name: item.name,
								code: item.code,
								state: "在用",
								url: res.data,
								remarks: item.remarks
							});
						} else if (browser.versions.android) {
							//调用Android的方法
							window.android.palyVideo(item.name, item.code, "在用", res.data, item.remarks);
						}
						// window.android.palyVideo(item.name, item.code, "在用", res.data, item.remarks);
					}
				}).catch(error => {
					if (error.data && error.data.message) {
						uni.showToast({
							title: error.data.message,
							duration: 2000,
							icon: "none",
						})
					}
				});
			},



			showGuiji(item) {
				item.selectedTab = this.selectedTab;
				uni.navigateTo({
					url: "../guiJi/guiJi?data=" + Base64.encode(JSON.stringify(item), "utf-8")
				})
				// this.checkedGuijiItem = item;
				// this.$refs.popup.open();
			},

			closePicker() {
				this.$refs.popup.close();
			},

			selectDate(year, month, day, startTime, endTime) {
				var params = {
					fromTime: year + '/' + month + '/' + day + ' ' + startTime + ':00',
					toTime: year + '/' + month + '/' + day + ' ' + endTime + ':00',
					gpsCode: this.checkedGuijiItem.gpsCode
				}
				if (this.selectedTab == "安保" || this.selectedTab == "管理人员") {
					http.getStaffListByGpsCode(params).then(res => {
						if (res.code == 200) {
							if (res.data.length > 0) {
								let guiJidata = this.checkedGuijiItem;
								guiJidata.selectedTab = this.selectedTab;
								guiJidata.pointList = res.data;
								uni.navigateTo({
									url: "../guiJi/guiJi?data=" + Base64.encode(JSON.stringify(guiJidata),
										"utf-8")
								})

								this.$refs.popup.close();
							} else {
								uni.showToast({
									title: "该时间段无轨迹记录",
									duration: 2000,
									icon: "none",
								})
							}
						} else {
							uni.showToast({
								title: res.message,
								duration: 2000,
								icon: "none",
							})
						}
						this.$refs.popup.close();
					}).catch(error => {
						if (error.data && error.data.message) {
							uni.showToast({
								title: error.data.message,
								duration: 2000,
								icon: "none",
							})
						}
						this.$refs.popup.close();
					});
				} else {
					http.getListByGpsCode(params).then(res => {
						if (res.code == 200) {
							if (res.data.length > 0) {
								let guiJidata = this.checkedGuijiItem;
								guiJidata.selectedTab = this.selectedTab;
								guiJidata.pointList = res.data;
								uni.navigateTo({
									url: "../guiJi/guiJi?data=" + Base64.encode(JSON.stringify(guiJidata),
										"utf-8")
								})
								this.$refs.popup.close();
							} else {
								uni.showToast({
									title: "该时间段无轨迹记录",
									duration: 2000,
									icon: "none",
								})
							}
						} else {
							uni.showToast({
								title: res.message,
								duration: 2000,
								icon: "none",
							})
						}
					}).catch(error => {
						if (error.data && error.data.message) {
							uni.showToast({
								title: error.data.message,
								duration: 2000,
								icon: "none",
							})
						}
						this.$refs.popup.close();
					});
				}
			},

			//拨打电话
			makePhoneCall(tel, serial) {
				this.selectedSerial = serial;
				console.log("makePhoneCall")
				window.android.makePhoneCall(tel);
			},
			goMy() {
				uni.navigateTo({
					url: "../my/my"
				})
			},
			goAnalysis() {
				uni.navigateTo({
					url: "../analysis/analysis"
				})
			},
		}
	}
</script>

<style scoped src="./index.css">


</style>
