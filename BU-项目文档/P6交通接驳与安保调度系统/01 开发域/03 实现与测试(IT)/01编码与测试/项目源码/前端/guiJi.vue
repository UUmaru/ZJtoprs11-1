<template>
	<view class="main">
		<view class="head">
			<image class="img_back" src="../../static/img/back.png" @click="back"></image>
			<text>历史轨迹</text>
		</view>
		<view class="content">
			<div id="map1">
			</div>
			<movable-area style="position: absolute;top: 0;left: 0;width: 100%;height: 100%;pointer-events: none;">
				<movable-view class="time_picker" direction="all" @click="showTimePicker">
					<span>选择时间</span>
					<span>{{date}}</span>
					<span v-if="startTime" style="font-size: 24upx;">{{startTime}}-{{endTime}}</span>
				</movable-view>
			</movable-area>
			<!-- <div class="time_picker" @click="showTimePicker" v-drag>
				<span>选择时间</span>
				<span>{{date}}</span>
				<span v-if="startTime" style="font-size: 24upx;">{{startTime}}-{{endTime}}</span>
			</div> -->
		</view>
		<uni-popup ref="popup" type="bottom">
			<my-date-picker @closePicker="closePicker" @selectDate="selectDate"></my-date-picker>
		</uni-popup>
	</view>
</template>

<script>
	var Base64 = require('js-base64').Base64;
	import http from '../../common/http.js';
	import uniPopup from '@/components/uni-popup/uni-popup.vue'
	import MyDatePicker from '@/components/my-datepicker/my-datepicker.vue'
	export default {
		components: {
			MyDatePicker
		},
		data() {
			return {
				map: null,
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
				geoServerUrl: "/gisServer/geoserver/ows",
				geoParams: {
					service: 'WFS',
					version: '1.1.0',
					request: 'GetFeature',
					typeName: 'oldtown:trf_security_car',
					outputFormat: 'application/json',
					srsName: 'EPSG:4326',
				},

				layerType: "",
				// guijiInfo: {},
				checkedGuijiItem: {},
				date: "",
				startTime: "",
				endTime: "",
				guijiLayer: null,
			}
		},

		directives: {
			drag(el, bindings) {
				el.onmousedown = function(e) {
					var disx = e.pageX - el.offsetLeft;
					var disy = e.pageY - el.offsetTop;
					document.onmousemove = function(e) {
						el.style.left = e.pageX - disx + 'px';
						el.style.top = e.pageY - disy + 'px';
					}
					document.onmouseup = function() {
						document.onmousemove = document.onmouseup = null;
					}
				}
			}
		},

		onLoad: function(option) { //option为object类型，会序列化上个页面传递的参数
			console.log("onLoad data: " + Base64.decode(option.data));
			// this.guijiInfo = JSON.parse(Base64.decode(option.data));
			this.checkedGuijiItem = JSON.parse(Base64.decode(option.data));
			this.layerType = this.checkedGuijiItem.selectedTab;

		},
		// onBackPress(){
		// 	this.map.eachLayer((layer)=>{
		// 	    this.map.removeLayer(layer);
		// 	});
		// },
		mounted() {
			this.initMap()
		},
		destroyed() {
			this.map.eachLayer((layer) => {
				this.map.removeLayer(layer);
			});
		},
		methods: {
			back() {
				uni.navigateBack();
			},
			initMap() {
				this.map = L.map("map1", {
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
				this.pointSever();
				//打捞路线
				this.sweepRoute();
				//巡检路线
				this.securityRouteServe();


				// setTimeout(()=>{
				// 	this.showGuiji() 
				// },1000)

				this.$refs.popup.open();

			},

			showTimePicker() {
				this.$refs.popup.open();
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
						},
					})
					// }).addTo(this.map);

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
						},
					})
					// }).addTo(this.map);
				})
			},

			//打捞路线
			sweepRoute() {
				var that = this;
				this.geoParams.typeName = 'oldtown:trf_sweep_route'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['打捞路线'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.id == that.checkedGuijiItem.routeId;
						}
					}).addTo(this.map);
					this.layers["打捞路线"].eachLayer((layer) => {
						this.map.flyToBounds(layer._bounds);
					})
				})
			},
			//巡检路线
			securityRouteServe() {
				var that = this;
				this.geoParams.typeName = 'oldtown:trf_security_route'
				const url = this.geoServerUrl + L.Util.getParamString(this.geoParams, this.geoServerUrl)
				http.getWFS(url).then(res => {
					this.layers['巡检路线'] = L.geoJSON(res, {
						filter: function(feature, layer) {
							return feature.properties.id == that.checkedGuijiItem.routeId;
						}
					}).addTo(this.map);
					this.layers["巡检路线"].eachLayer((layer) => {
						// this.map.flyToBounds(layer._bounds);
					})
				})

			},


			closePicker() {
				this.$refs.popup.close();
			},

			selectDate(year, month, day, startTime, endTime) {
				this.date = year + '/' + month + '/' + day
				this.startTime = startTime;
				this.endTime = endTime;
				var params = {
					fromTime: this.date + ' ' + this.startTime + ':00',
					toTime: this.date + ' ' + this.endTime + ':00',
					gpsCode: this.checkedGuijiItem.gpsCode
				}
				if (this.layerType == "安保" || this.layerType == "管理人员" || this.layerType == "保洁") {
					http.getStaffListByGpsCode(params).then(res => {
						if (res.code == 200) {
							if (res.data && res.data.list.length > 0) {
								//展示轨迹
								this.showGuiji(res.data.list);
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

								//展示轨迹
								this.showGuiji(res.data)

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


			showGuiji(dataList) {
				var pointList = [];
				if (this.guijiLayer) {
					this.map.removeLayer(this.guijiLayer)
				}
				this.guijiLayer = L.layerGroup();
				this.map.addLayer(this.guijiLayer)
				for (var i = 0; i < dataList.length; i++) {
					let point = [dataList[i].latitude, dataList[i].longitude, dataList[i].speed]
					pointList.push(point);
				}

				var hotlineLayer = L.hotline(pointList, {
					min: 0,
					max: 10,
					palette: {
						0.0: '#ff0000',
						0.5: '#ffff00',
						1.0: '#008800'
					},
					weight: 2,
					outlineColor: '#000000',
					outlineWidth: 1
				});
				var bounds = hotlineLayer.getBounds();

				this.map.fitBounds(bounds);
				// hotlineLayer.bindPopup('Thanks for clicking.<br/>Play with me!').addTo(this.map);

				setTimeout(() => {


					var currentZoom = this.map.getZoom();
					this.map.setMinZoom(currentZoom);
					this.map.setMaxZoom(currentZoom);

					var moveLine = L.polyline(pointList, {
						snakingSpeed: 200,
						color: '#ff0000'
					}).addTo(this.guijiLayer);
					moveLine.snakeIn();
					
					
					

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
					var customIcon = new L.Icon({
						iconUrl: this.layersInfo[this.layerType].iconUrl,
						iconSize: [40, 40],
						iconAnchor: [20, 20],
					});
					var moveMarker = L.Marker.movingMarker(pointList,
						duration, {
							icon: customIcon
						}).addTo(this.guijiLayer);
					let name = this.checkedGuijiItem.name;
					if (this.layerType == '游船' || this.layerType == '打捞船') {
						name = this.checkedGuijiItem.type + this.checkedGuijiItem.serial;
					}
					if (this.layerType == '安保' || this.layerType == '管理人员'|| this.layerType == '保洁') {
						name = this.checkedGuijiItem.username;
					}
					moveMarker.bindTooltip(name, {
						permanent: 'true',
						direction: 'bottom',
						offset: [0, 18]
					}).openTooltip();
					moveMarker.start();

					setTimeout(() => {
						this.map.setMinZoom(12);
						this.map.setMaxZoom(21);
						this.map.removeLayer(moveLine);
						hotlineLayer.addTo(this.guijiLayer);
						for (var i = 0; i < dataList.length; i++) {
							var myIcon = L.divIcon({
								html: "",
								className: 'my-guiji-icon',
								iconSize: [10, 10]
							});
							let point = [dataList[i].latitude, dataList[i].longitude]
							let marker = L.marker(point, {
								icon: myIcon,
							});
							let time = this.timeConvert(dataList[i].gpsTime, 1);
							marker.bindPopup(time);
							marker.addTo(this.guijiLayer);
						}
						console.log("setTimeout" + duration)
					}, duration)

					

				}, 1000)

			},

			timeConvert(timestamp, num) { //num:0 YYYY-MM-DD  num:1  YYYY-MM-DD hh:mm:ss // timestamp:时间戳
				timestamp = timestamp + '';
				timestamp = timestamp.length == 10 ? timestamp * 1000 : timestamp;
				var date = new Date(timestamp);
				var y = date.getFullYear();
				var m = date.getMonth() + 1;
				m = m < 10 ? ('0' + m) : m;
				var d = date.getDate();
				d = d < 10 ? ('0' + d) : d;
				var h = date.getHours();
				h = h < 10 ? ('0' + h) : h;
				var minute = date.getMinutes();
				var second = date.getSeconds();
				minute = minute < 10 ? ('0' + minute) : minute;
				second = second < 10 ? ('0' + second) : second;
				if (num == 0) {
					return y + '-' + m + '-' + d;
				} else {
					return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
				}
			}
		}
	}
</script>

<style scoped>
	.main {
		width: 100%;
		height: 100vh;
		display: flex;
		flex-direction: column;
	}

	.head {
		width: 100%;
		height: 92upx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: #5C77D8;
		position: relative;
	}

	.head text {
		font-size: 32upx;
		font-weight: normal;
		color: #FFFFFF;
		line-height: 44upx;
	}

	.img_back {
		position: absolute;
		width: 32upx;
		height: 32upx;
		left: 34upx;
	}

	.content {
		position: relative;
		width: 100%;
		flex: 1;
		z-index: 0;
	}

	#map1 {
		width: 100%;
		height: 100%;
		z-index: 0;
	}

	.time_picker {
		width: 220upx;
		height: 140upx;
		background: rgba(255, 255, 255, 0.8);
		position: absolute;
		top: 50upx;
		left: 50upx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		border-radius: 20upx;
		z-index: 1;
		pointer-events: auto;
	}

	.time_picker span {
		font-size: 28upx;
		color: #000000;
	}
</style>
