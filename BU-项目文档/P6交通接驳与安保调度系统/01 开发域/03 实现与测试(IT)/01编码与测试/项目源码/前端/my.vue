<template>
	<view class="main">
		<view class="head">
			<image class="img_back" src="../../static/img/back.png" @click="back"></image>
			<text>我的</text>
		</view>
		<image class="img_head" :src="icon"></image>
		<view class="content">
			<text>姓名：{{name}}</text>
			<text>联系方式：{{tel}}</text>
			<text>角色：{{role}}</text>
		</view>
		<button class="login_btn" hover-class="login_btn_p"  @click="loginOut">
			<span>退出登录</span>
		</button>
	</view>
</template>

<script>
	export default{
		data() {
			return {
				icon:"../../static/img/back.png",
				name: "",
				tel: "",
				role:"",
			}
		},
		onLoad() {
			this.icon = uni.getStorageSync("icon");
			this.name = uni.getStorageSync("jt_username");
			this.tel = uni.getStorageSync("tel");
			this.role = uni.getStorageSync("role");
		},
		methods:{
			back(){
				uni.navigateBack();
			},
			loginOut(){
				uni.removeStorageSync("token")
				uni.redirectTo({
					url: '../login/login',
				});
			}
		}
	}
</script>

<style scoped>
	.main{
		width: 100%;
		height: 100vh;
		display: flex;
		flex-direction: column;
		align-items: center;
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
	.img_head {
		width: 232upx;
		height: 232upx;
		border-radius: 50%;
		margin-top: 106upx;
	}
	.content {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		margin-top: 66upx;
		margin-bottom: 300upx;
	}
	.content text {
		font-size: 32upx;
		font-weight: 500;
		color: #333333;
		line-height: 44upx;
		margin-bottom: 24upx;
	}
	.login_btn {
		width: 484upx;
		height: 72upx;
		background: #5476DF;
		border-radius: 38upx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.login_btn::after {
		outline: none;
		border: none;
	}
	
	.login_btn_p {
		background: #355ddf;
	}
	
	.login_btn span {
		font-size: 32upx;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 44upx;
	}
</style>
