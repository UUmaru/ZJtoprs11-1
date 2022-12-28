import axios from 'axios'
import qs from 'qs'
import store from '../store/index.js';
import router from '../router/index.js';

// axios 配置
axios.defaults.timeout = 5000;
axios.defaults.withCredentials = true;
axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
// axios.defaults.baseURL = 'http://192.168.0.143:3452';
// axios.defaults.baseURL = '/nanxun3.0';
// axios.defaults.baseURL = "";
// axios.defaults.baseURL = 'http://saas.h5.ylmob.com';
// axios.defaults.baseURL = process.env.NODE_ENV === 'http://api.wx.ylmob.com/api';
//POST传参序列化
axios.interceptors.request.use((config) => {
	// console.log("axios config:  " + JSON.stringify(config));
	let token = store.getters.getToken
	if (!token) {
		// token = loadToken()
		token = localStorage.getItem("token")
	}
	if (!token) {
		console.log("router path: " + router.currentRoute.path)
		if (router.currentRoute.path.indexOf('login') == -1) {
			console.log("router path11: " + router.currentRoute.path.indexOf('login'))
			router.replace({
				path: 'login',
				query: {
					redirect: router.currentRoute.path
				},
			})
			return Promise.reject('no token');
		}
	}
	if(config.url.indexOf('sysAdmin/login') == -1){
		config.headers.Authorization = 'Bearer ' + token;
	}
	
	if (config.method === 'post') {
		// config.data.time = new Date().getTime();
		// config.data.temporaryCode = loadTemporaryCode();
		// console.log("axios data:  " + JSON.stringify(config.data));
		// config.headers.api_version = "1.0.0";
		// if (loadSchoolUuid()) { // 判断是否存在schoolUuid，如果存在的话，则每个http header都加上schoolUuid
		// 	config.headers.schoolUuid = loadSchoolUuid();
		// }
		// let secret = loadAesKey();
		// if (secret === '' && config.url.indexOf(default_aes_url_prefix) > -1) {
		// 	secret = loadDefaultAesKey();
		// }
		// config.data = code.encode(JSON.stringify(config.data), secret);
		config.data = qs.stringify(config.data)
	}
	return config;
}, (error) => {
	// Indicator.close();
	// _.toast("错误的传参", 'fail');
	console.log("错误的传参")
	return Promise.reject(error);
});

//返回状态判断
axios.interceptors.response.use((res) => {
	// Indicator.close();
	// if (res.data.code != "I001000") {
	// 	return Promise.reject(res);
	// }
	// console.log("response: " + JSON.stringify(res))
	if (res.data.code && res.data.code != 200) {
		if (res.data.message.indexOf("token") != -1) {
			router.replace({
				path: 'login',
				query: {
					redirect: router.currentRoute.path
				},
			})
		}
	}

	// if (res.config.method === 'get') {
	// 	return res;
	// }
	// if(loadAesKey) {
	// 	// 无需解密的情况
	// 	if (res.data.data.indexOf(':') > - 1) {
	// 		return res;
	// 	}
	// 	// 默认解密方式
	// 	let secret = loadAesKey();
	// 	if (secret === '') {
	// 		secret = loadDefaultAesKey();
	// 	}
	// 	res.data.data = code.decode(res.data.data, secret);
	// }
	return res;
}, (error) => {
	console.log("网络异常");
	// Indicator.close();
	return Promise.reject(error);
});

export function fetchPost(url, params) {
	return new Promise((resolve, reject) => {
		axios.post(url, params).then(response => {
				resolve(response.data);
			}, err => {
				reject(err);
			})
			.catch((error) => {
				reject(error)
			})
	});
}

export function fetchGet(url, param) {
	return new Promise((resolve, reject) => {
		axios.get(url, {
				params: param
			})
			.then(response => {
				resolve(response.data)
			}, err => {
				reject(err)
			})
			.catch((error) => {
				reject(error)
			})
	})
}

export default {
	get(url) {
		if (url === '' || url == null) return;
		return fetchGet(url);
	},
	post(url, params) {
		if (url === '' || url == null) return;
		return fetchPost(url, params);
	},
	
	getWFS(url){
		return fetchPost(url)
	},
	
	doLogin(params){
		return fetchPost('/api/sys/sysAdmin/login', params)
	},
	
	getSelfDetails(params){
		return fetchGet('/api/sys/sysAdmin/getSelfDetails', params)
	},
	
	getAllCar(params) {
		return fetchGet('/api/trf/trfSecurityCar/getAll', params)
	},
	
	getCarById(params){
		return fetchGet('/api/trf/trfSecurityCar/getById', params)
	},
	getAllYacht(params){
		return fetchGet('/api/trf/trfYacht/getAll', params)
	},
	getYachtById(params){
		return fetchGet('/api/trf/trfYacht/getById', params)
	},
	getAllSweep(params){
		return fetchGet('/api/trf/trfSweep/getAll', params)
	},
	getSweepById(params){
		return fetchGet('/api/trf/trfSweep/getById', params)
	},
	getAllSecurityStaff(params){
		return fetchGet('/api/trf/trfSecurityStaff/getAll', params)
	},
	getAllDockStaff(params){
		return fetchGet('/api/trf/trfDockStaff/getAll', params)
	},
	getComPoiAll(params){
		return fetchGet('/api/com/comPoi/getAll', params)
		
	},
	getAllSweepRoute(params){
		return fetchGet('/api/trf/trfSweepRoute/getAll', params)
	},
	getAllSecurityRoute(params){
		return fetchGet('/api/trf/trfSecurityRoute/getAll', params)
	},
	getAllPlace(params){
		return fetchGet('/api/com/comPlace/getAll', params)
	},
	getStaffListByGpsCode(params){
		return fetchGet('/api/trf/trfStaffGps/getListByGpsCode', params)
	},
	getListByGpsCode(params){
		return fetchGet('/api/trf/trfGps/getListByGpsCode', params)
	},
	getAlarmAll(params){
		return fetchGet('/api/trf/trfAlarm/getAll', params)
	},
	getRtmp(params){
		return fetchPost('/api/com/comPoi/rtmpByCameraCode', params)
	},
	
}
