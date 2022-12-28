<template>
	<div id="map"></div>
</template>

<script>
	var esri = require("esri-leaflet");
	export default {
		mounted() {
			var corner1 = L.latLng(30.916557, 120.353508) //设置左上角经纬度
			var corner2 = L.latLng(30.82592, 120.491524) //设置右下点经纬度
			var bounds = L.latLngBounds(corner1, corner2) //构建视图限制范围
			this.map = L.map('map', {
				center: [30.874017, 120.425509],
				zoom: 14,
				maxZoom: 21,
				minZoom: 1,
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
				minZoom: 1,
				matrixIds: matrixIds,
			})
			this.map.addLayer(ign);
			// L.tileLayer(
			// 	'https://api.mapbox.com/styles/v1/mapbox/streets-v11/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoieXFvIiwiYSI6ImNraGxlY2FkbTExbnEyc3M0bzI5cGcyemcifQ.judmD0QY87RNfHva7TRzcg', {
			// 		tileSize: 512,
			// 		zoomOffset: -1,
			// 		matrixIds: matrixIds,
			// 		attribution: '© <a href="https://apps.mapbox.com/feedback/">Mapbox</a> © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
			// 	}).addTo(this.map);
		
		}
	}
</script>

<style scoped>
	#map {
		width: 100%;
		height: 100%;
		cursor: default;
		z-index: 0;
	}
</style>
