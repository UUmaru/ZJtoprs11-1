<template>
	<view class="main">
		<view class="head">
			<image class="img_back" src="../../static/img/back.png" @click="back"></image>
			<text>调度分析</text>
		</view>
		<view class="content">
			<button v-if="analysisResultFlag" type="primary" class="result_btn" @click="exit">退出</button>
			<div id="map1" ref="map"></div>
			<view v-if="rippleShowFlag" class="ripple_mid">
				<ripple></ripple>
			</view>
		</view>
		<touch-slide ref="touchSlide" style="background-color: #FFFFFF;" :top="mapHeight*0.15" :bottom="mapHeight-height2"
		 :titleHeight="40" :left="0">
			<view slot="title" style="width: 100%; display: flex;flex-direction: column;">
				<scroll-view scroll-x="true">
					<view class="tab">
						<text v-if="authorities.includes('交通接驳游船管理')" :class="selectedTab=='游船'?'item_p':'item_n'" @click="chooseTab('游船')">游船</text>
						<text v-if="authorities.includes('交通接驳安保车辆管理')" :class="selectedTab=='电动车'?'item_p':'item_n'" @click="chooseTab('电动车')">电动车</text>
						<text v-if="authorities.includes('交通接驳安保人员管理')" :class="selectedTab=='安保'?'item_p':'item_n'" @click="chooseTab('安保')">安保</text>
					</view>
				</scroll-view>
				<view style="width: 100%;height: 2upx; background:#DBDBDB;"></view>
			</view>

			<view style="padding-bottom: 80upx;">
				<view v-for="(item,index) in analysisLayer.poiList">
					<view :id="item.name" v-if="selectedTab=='游船'" :class="selectedSerial==item.serial?'item_d_p':'item'" hover-class="item_hover"
					 @click="chooseDetail(item)">
						<view class="item_top">
							<image v-if="selectedTab=='游船'" class="img_l" src="../../static/img/icon_yc.png"></image>
							<text class="item_name">{{item.name}}</text>
							<view class="item_state_bg">
								<text class="item_state">{{item.currentStatus?item.currentStatus:'不在线'}}</text>
							</view>
							<text class="item_text_n" style="color: #4CD964;">{{formatDis(item.dis)}}</text>
							<!-- <image class="img_energy" src="../../static/img/energy_4.png"></image> -->
						</view>
						<view class="item_mid">
							<text class="item_text_n">船工：{{item.staffName}}</text>
							<view class="item_guiji">
								<text class="item_text_n">载客量：</text>
								<text class="item_text_n">{{item.capacityNum}}人</text>
							</view>
						</view>
						<view class="item_bottom">
							<text class="item_text_n">联系方式：</text>
							<text class="item_text_blue" @click.stop="makePhoneCall(item.staffTel,item.serial)">{{item.staffTel}}</text>
						</view>
					</view>

					<view :id="item.name" v-else-if="selectedTab=='电动车'" :class="selectedSerial==item.serial?'item_d_p':'item'"
					 hover-class="item_hover" @click="chooseDetail(item)">
						<view class="item_top">
							<image class="img_l" src="../../static/img/icon_ddc.png"></image>
							<text class="item_name">{{item.name}}</text>
							<view class="item_state_bg">
								<text class="item_state">{{item.currentStatus?item.currentStatus:'不在线'}}</text>
							</view>
							<text class="item_text_n" style="color: #4CD964;">{{formatDis(item.dis)}}</text>
							<!-- <text class="energy_percent">78%</text>
							<image class="img_energy" src="../../static/img/energy_4.png"></image> -->
						</view>
						<view class="item_mid">
							<text class="item_text_n">负责人：{{item.staffName}}</text>
							<view class="item_guiji">
								<text class="item_text_n">载客量：</text>
								<text class="item_text_n">{{item.capacityNum}}人</text>
							</view>
						</view>
						<view class="item_bottom">
							<text class="item_text_n">联系方式：</text>
							<text class="item_text_blue" @click.stop="makePhoneCall(item.staffTel,item.serial)">{{item.staffTel}}</text>
						</view>
					</view>

					<view :id="item.username" v-else-if="selectedTab=='安保'" :class="selectedSerial==item.serial?'item_d_p':'item'"
					 hover-class="item_hover" @click="chooseDetail(item)">
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
								<text class="item_text_n">距离：</text>
								<!-- <text class="item_text_blue" @click.stop="showGuiji(item)">查看</text> -->
								<text class="item_text_n" style="color: #4CD964;">{{formatDis(item.dis)}}</text>
							</view>
						</view>
						<view class="item_bottom">
							<text class="item_text_n">联系方式：</text>
							<text class="item_text_blue" @click.stop="makePhoneCall(item.tel,item.serial)">{{item.tel}}</text>
						</view>
					</view>

				</view>
			</view>

		</touch-slide>
	</view>
</template>

<script>
	import touchSlide from '@/components/touch-slide/touch-slide.vue'
	import http from '../../common/http.js';
	import ripple from '../../components/ripple/ripple.vue'
	export default {
		components: {
			touchSlide,
			ripple
		},
		data() {
			return {
				checkedGuijiItem: {},
				selectedSerial: '',
				selectedJkid: '',
				title: 'Hello',
				mapHeight: 650,
				height2:100,
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
					"安保": {
						num: 3,
						iconUrl: require("../../static/img/icon_ab.png"),
						checked: false,
						poiList: []
					},

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
				rippleShowFlag: false,
				analysisResultFlag: false,
				selectedSerial: "",
				analysisLayer: {},
				authorities: [],
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

		},
		mounted() {
			const _this = this;
			console.log("mounted")
			uni.getSystemInfo({
				success: (res) => {
					_this.mapHeight = res.screenHeight - res.statusBarHeight
					// #ifdef APP-NVUE || MP-WEIXIN
					_this.mapHeight = _this.mapHeight - 45
					// #endif
				}
			})
			this.initMap();
		},
		methods: {
			back() {
				uni.navigateBack();
			},
			formatDis(dis) {
				if (dis < 1000) {
					return dis.toFixed(2) + "m";
				}
				return (dis / 10e2).toFixed(2) + "km";
			},
			initMap() {
				this.map = L.map("map1", {
					center: [30.878191, 120.426207],
					zoom: 13,
					maxZoom: 21,
					minZoom: 12,
					// maxBounds: bounds,
					crs: L.CRS.EPSG4326, //设置坐标系4326
					zoomControl: false, //禁用 + - 按钮
					doubleClickZoom: false, // 禁用双击放大
					attributionControl: false // 移除右下角leaflet标识
				});
				// this.map.eachLayer((layer)=>{
				//     this.map.removeLayer(layer);
				// });
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

				//码头 休息点
				// this.pointSever();
				//电动车
				this.ddcSever();
				//游船
				this.yachtSever();
				//安保人员
				this.securityStaffSever();

				this.markerIcon = L.icon({
					iconUrl: require("../../static/img/location.png"), //"../static/img/clickIcon.png",
					iconSize: [24, 24], // 图标尺寸
					iconAnchor: [12, 12], // 图标偏移量
					popupAnchor: [0, 0] // 弹出框偏移量
				});
				
				var that = this;
				
				this.map.on("popupopen", function(e){
					console.log("popupopen: " + e)
					setTimeout(()=>{
						document.getElementById("btnConfirm").onclick = function(){
							console.log("btnConfirm: ")
							that.spot.closePopup();
							that.map.panTo(e.popup._latlng);
							that.rippleShowFlag = true;
							that.analysisData(e.popup._latlng);
						}
						
						document.getElementById("btnCancel").onclick = function(){
							console.log("btnCancel: ")
							that.spot.remove();
						}
					},500)
				});

				this.str = "<div class='myPopup'>" +
					"<span class='myPopup_text'>" + "确认选择该点调度" + "</span>" +
					"<div class='myPopup_b'>" +
					"<span class='myPopup_b_text' id='btnConfirm'>确定</span>" +
					"<div class='myPopup_line'></div>" +
					"<span class='myPopup_b_text' id='btnCancel'>取消</span>" +
					"</div>" +
					"</div>"

				this.map.on('click', (e) => {
					console.log("click: " + e.latlng)
					// this.map.setView(e.latlng)
					if (this.rippleShowFlag || this.analysisResultFlag) {
						return;
					}

					if (this.selectedTab) {
						// uni.showModal({
						// 	title: '提示',
						// 	content: '确认选择该点调度',
						// 	success: (res) => {
						// 		if (res.confirm) {
						// 			this.map.panTo(e.latlng);
						// 			this.rippleShowFlag = true;
						// 			this.analysisData(e.latlng);
						// 		} else if (res.cancel) {
						// 			console.log('用户点击取消');
						// 		}
						// 	}
						// })
						if(this.spot){
							this.spot.remove();
						}

						this.spot = L.marker(e.latlng, {
							icon: this.markerIcon
						}).bindPopup(this.str).addTo(this.map);
						this.spot.openPopup()
						
					} else {
						uni.showModal({
							title: '提示',
							content: '请先选择调度类型',
							showCancel: false
						})
					}
				});
			},
			
			pointSever() {
				const urlString = 'gisServer/geoserver/ows'
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

			//游船
			yachtSever() {
				this.geoParams.typeName = 'oldtown:trf_yacht'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
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
					this.getYachtAll();

					this.layers['游船'].eachLayer((layer) => {
						layer.on('click', (e) => {
							// if (!this.analysisResultFlag) {
							// 	return;
							// }
							if (this.rippleShowFlag) {
								return;
							}
							if (this.analysisResultFlag) {
								this.height2 = 200
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

								let id = e.target.feature.properties.name
								if (document.getElementById(id)) {
									document.getElementById(id).scrollIntoView({
										behavior: "smooth", // 默认 auto
										block: "center", // 默认 center
										inline: "nearest", // 默认 nearest
									});
								}
								this.selectedSerial = e.target.feature.properties.serial;
							} else {
								if (this.selectedTab) {
									if(this.spot){
										this.spot.remove();
									}
									
									this.spot = L.marker(e.latlng, {
										icon: this.markerIcon
									}).bindPopup(this.str).addTo(this.map);
									this.spot.openPopup()
								} else {
									uni.showModal({
										title: '提示',
										content: '请先选择调度类型',
										showCancel: false
									})
								}
							}
						});
					});
				})
			},

			//获取游船列表
			getYachtAll() {
				http.getYachtAll().then(res => {
					this.layersInfo['游船'].poiList = res.data;
				})
			},

			//安保
			securityStaffSever() {
				this.geoParams.typeName = 'oldtown:trf_security_staff'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['安保'] = L.geoJSON(res, {
						// filter: function(feature, layer) {
						// 	return feature.properties.type=="打捞船";
						// },
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
					this.getAllSecurityStaff();

					this.layers['安保'].eachLayer((layer) => {
						layer.on('click', (e) => {
							if (this.bigMarker) {
								this.map.removeLayer(this.bigMarker)
							}
							if (this.clickLayer) {
								this.map.addLayer(this.clickLayer)
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

							let id = e.target.feature.properties.username
							if (document.getElementById(id)) {
								document.getElementById(id).scrollIntoView({
									behavior: "smooth", // 默认 auto
									block: "center", // 默认 center
									inline: "nearest", // 默认 nearest
								});
							}
							this.selectedSerial = e.target.feature.properties.serial;
						});
					});
				})
			},

			getAllSecurityStaff() {
				http.getAllSecurityStaff().then(res => {
					this.layersInfo['安保'].poiList = res.data;
				})
			},


			//电动车服务
			ddcSever() {
				// this.geoParams.typeName = 'oldtown:trf_security_car'

				this.geoParams.typeName = 'oldtown:trf_security_car'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['电动车'] = L.geoJSON(res, {
						pointToLayer: (feature, latlng) => {
							// console.log("pointToLayer 电动车: " + latlng);
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
					this.getCarAll();

					this.layers['电动车'].eachLayer((layer) => {
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

							let id = e.target.feature.properties.name
							if (document.getElementById(id)) {
								document.getElementById(id).scrollIntoView({
									behavior: "smooth", // 默认 auto
									block: "center", // 默认 center
									inline: "nearest", // 默认 nearest
								});
							}
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


			chooseTab(item) {
				if (this.selectedTab == item) {
					return;
				}
				if (this.selectedTab) {
					if (this.analysisResultFlag) {
						uni.showModal({
							content: "请先退出当前调度",
							showCancel: false
						})
					} else {
						console.log("selectedTab:" + this.selectedTab + "  item:" + item)
						this.map.removeLayer(this.layers[this.selectedTab])
						this.analysisLayer.poiList = [];
						this.selectedTab = item;
						console.log("layers:" + this.layers[this.selectedTab])
						this.map.addLayer(this.layers[this.selectedTab])
					}
				} else {
					console.log("item:" + item)
					this.selectedTab = item;
					this.map.addLayer(this.layers[this.selectedTab])
				}

			},

			//点击列表中某个点
			chooseDetail(item) {
				this.selectedSerial = item.serial;
				let latlng;
				if (item.latlng) {
					latlng = item.latlng;
				} else {
					if(item.latitude&&item.longitude){
						latlng = [item.latitude, item.longitude]
					}
				}
				let name = item.name;
				if (item.username) {
					name = item.username;
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
					if(layer.feature.properties.username){
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
				
				setTimeout(()=>{
					document.getElementById(name).scrollIntoView({
						behavior: "smooth", // 默认 auto
						block: "center", // 默认 center
						inline: "nearest", // 默认 nearest
					});
				},500)
				
			},

			exit() {
				this.height2 = 100;
				this.analysisLayer.poiList = [];
				this.analysisResultFlag = false;
				this.map.removeLayer(this.layers['调度分析'])
				if (this.bigMarker) {
					this.map.removeLayer(this.bigMarker)
				}
				if (this.clickLayer) {
					this.map.addLayer(this.clickLayer)
				}
				if(this.spot){
					this.spot.remove();
				}
			},
			
			//拨打电话
			makePhoneCall(tel, serial) {
				this.selectedSerial = serial;
				console.log("makePhoneCall")
				window.android.makePhoneCall(tel);
			},

			analysisData(latlng) {
				setTimeout(() => {
					if (this.rippleShowFlag) {
						this.analysisResultFlag = true;
						if(this.spot){
							this.spot.remove();
						}
						this.height2 = 200;
						console.log("analysisData" + latlng)
						this.layers['调度分析'] = L.layerGroup();
						// var circleIn = new L.Circle(latlng, {
						// 	color: '#0C81FF', //颜色
						// 	fillColor: '#0C81FF',
						// 	fillOpacity: 1,
						// 	radius: 20
						// });
						// this.layers['调度分析'].addLayer(circleIn);
						
						let marker = L.marker(latlng, {
							icon: this.markerIcon
						})
						this.layers['调度分析'].addLayer(marker);
						var circleOut = new L.Circle(latlng, {
							color: '#67AAFF', //颜色
							fillColor: '#c2fff5',
							fillOpacity: 0.2,
							radius: 500
						}).addTo(this.map);
						this.layers['调度分析'].addLayer(circleOut);
						this.layers['调度分析'].addTo(this.map);
						// this.showRightFlag = true;
						let layerInfo = this.layersInfo[this.selectedTab];
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
						this.$refs.touchSlide.btnClick('toTop');
					}
					this.rippleShowFlag = false;
					// this.isAnalysis = false;

				}, 2500)
			},
		}
	}
</script>

<style scoped src="./analysis.css">

</style>
