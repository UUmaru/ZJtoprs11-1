<template>
	<div class="about">
		<!-- <iframe ref="iframe" src="http://zjtoprs.f3322.net:9092/" style="width: 100%; height: 100%;" frameborder="0"></iframe> -->
		<iframe ref="iframe" src="http://61.153.180.66:9092/" style="width: 100%; height: 100%;" frameborder="0"></iframe>
	</div>
</template>

<script>
	import store from '../../store/index.js';
	import router from '../../router/index.js';
	export default {
		data() {
			return {
				isChildReady: false
			}
		},
		onLoad() {

		},
		beforeMount() {
			addEventListener('message', evt => {
				console.log("token addEventListener: " + evt.data.type)
				if (evt.data.type == 'childStatus') {
					this.isChildReady = evt.data.data
					console.log("token isChildReady: " + this.isChildReady)
				}
				if (evt.data.type == 'goBack') {
					console.log("token isChildReady: " + evt.data.type)
					this.$router.push("/");
				}
				if (evt.data.type == 'loginout') {
					localStorage.removeItem("Access-Token");
					this.$router.push("/login");
				}

			})
		},
		mounted() {
			// 在外部vue的window上添加postMessage的监听，并且绑定处理函数handleMessage
			// window.addEventListener('message', this.handleMessage)
			// this.iframeWin = this.$refs.iframe.contentWindow
			// setTimeout(() => {
			// 	this.sendMessage()
			// }, 2000);
			let token = store.getters.getToken
			// if (!token) {
			// 	token = loadToken()
			// }

			if (!token) {
				router.replace({
					path: 'login',
					query: {
						redirect: router.currentRoute.path
					},
				})
			}

		},

		watch: {
			isChildReady(isReady) {
				console.log("token: " + isReady)
				if (!isReady) {
					console.log("token1111: " + isReady)
					return
				}
				console.log("token22222: " + isReady)
				this.deliverToken()
			}
		},

		methods: {
			deliverToken() {
				console.log("token: deliverToken")
				let token = store.getters.getToken
				if (!token) {
					token = loadToken()
				}
				let userName = localStorage.getItem("ms_username")
				let role = localStorage.getItem("role")
				let townid = localStorage.getItem("townid")
				console.log("token deliver" + token)
				this.$refs.iframe.contentWindow.postMessage({
					type: 'token',
					data: {
						token: token,
						userName: userName,
						role: role,
						townid: townid
					}
				}, '*')
			},
		}
	}
</script>
<style scoped>
	.about {
		width: 100%;
		height: 100vh;
	}
</style>
