<template>
	<div class="content">
		<!-- <div class="main"> -->
		<img class="logo" src="../../static/img/icon.png" />
		<div class="title_bg">
			<span class="title">南浔古镇交通接驳及安保调度管理系统</span>
		</div>
		<div class="main">
			<form v-on:submit.prevent="submit()">
				<div class="input_item">
					<!-- <span class="input_l">用户名:</span> -->
					<img class="left_img" src="../../static/img/lg_user.png" />
					<input autocomplete="off" id="user" type="text" placeholder="请输入用户名" name="userName" v-model="userName" />
				</div>
				<div class="input_item">
					<!-- <span class="input_l">密码:</span> -->
					<img class="left_img" src="../../static/img/lg_password.png" />
					<input autocomplete="off" id="passward" type="password" placeholder="请输入密码" name="password" v-model="password" />
				</div>
				<!-- <div class="remmber">
					<input type="checkbox" id="check" class="regular-checkbox" v-model="keepLoginFlag">
					<label for="check"></label>
					<span class="login_auto">自动登录</span>
				</div> -->
				<button class="login_btn" type="submit">
					<span>登录</span>
				</button>
			</form>
		</div>
		<!-- </div> -->
	</div>
</template>

<script>
	import http from '../../common/http.js';
	import md5 from 'js-md5';
	export default {
		data() {
			return {
				userName: "",
				password: "",
				keepLoginFlag: false,
			}
		},
		created() {
			this.userName = localStorage.getItem('jq_username')
			this.password = localStorage.getItem('jq_password')
		},
		methods: {
			submit() {
				let params = {
					username: this.userName,
					password: this.password
				}
				http.doLogin(params).then(res => {
					if (res.code == 200) {
						
						if(res.data.authorities.length<1){
							alert("该账户没有权限")
							return;
						}
						// if (this.keepLoginFlag) {
						localStorage.setItem("token", res.message)
						// }
						this.$store.dispatch('getNewToken', res.message);
						localStorage.setItem('jq_username', this.userName)
						localStorage.setItem('jq_password', this.password)
						let authorities = [];
						
						for (var i = 0; i < res.data.authorities.length; i++) {
							authorities.push(res.data.authorities[i].authority)
						}
						
						localStorage.setItem('authorities', authorities)
						
						let redirect = decodeURIComponent(this.$route.query.redirect || '/');
						console.log("redirect: " + redirect)
						this.$router.push({
							path: redirect
						})
					} else {
						alert(res.message)
					}

				})
			},
		}
	}
</script>

<style scoped>
	.content {
		width: 100%;
		height: 100vh;
		display: flex;
		flex-direction: column;
		align-items: center;
		background: url(../../static/img/login_bg.png) no-repeat;
		background-size: 100% 100%;
	}

	.main {
		width: 6.22rem;
		height: 3.61rem;
		background: url(../../static/img/login_main_bg.png) no-repeat;
		background-size: 100% 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		margin-top: 0.57rem;
	}

	.logo {
		width: 1.40rem;
		height: 1.06rem;
		margin-top: 2.1rem;
	}

	.title_bg {
		width: 7.89rem;
		height: 0.69rem;
		display: flex;
		align-items: center;
		justify-content: center;
		background: url(../../static/img/login_title_bg.png) no-repeat;
		background-size: 100% 100%;
		margin-top: 0.34rem;
	}

	.title {
		font-size: 0.40rem;
		font-family: FZLTZHK--GBK1-0, FZLTZHK--GBK1;
		font-weight: normal;
		color: #FFFFFF;
		line-height: 0.56rem;
		letter-spacing: 0.03rem;
		text-shadow: 0px 0.02rem 0.06rem rgba(18, 37, 46, 0.5);
	}

	.input_item {
		width: 3.56rem;
		height: 0.51rem;
		background: rgba(216, 216, 216, 0.22);
		box-shadow: 0px 0px 0.11rem 0px rgba(255, 255, 255, 0.5);
		border-radius: 0.06rem;
		border: 0.02rem solid rgba(255, 255, 255, 0.56);
		display: flex;
		align-items: center;
		/* padding: 0 0.37rem; */
		box-sizing: border-box;
		margin-bottom: 0.22rem;
	}

	.left_img {
		width: 0.32rem;
		height: 0.32rem;
		margin-right: 0.12rem;
		margin-left: 0.06rem;
	}

	.line_lit {
		width: 1px;
		height: 0.65rem;
		background: #D8D8D8;
		margin-right: 0.12rem;
	}

	.input_l {
		width: 0.8rem;
		height: 0.2rem;
		font-size: 0.2rem;
		font-family: PangMenZhengDao;
		color: #FFFFFF;
		line-height: 0.23rem;
		letter-spacing: 1px;
		flex-shrink: 0;
	}

	.input_item input {
		width: 2.6rem;
		outline-style: none;
		border: 0px;
		font-size: 0.24rem;
		height: 0.45rem;
		background: transparent;
		font-size: 0.20rem;
		color: #ffffff;
	}

	.remmber {
		display: flex;
		align-items: center;
		width: 5.4rem;
	}

	/*首先第一步就是隐藏原来的复选框*/
	.regular-checkbox {
		display: none;
	}

	/*第二部定义现在复选框样式*/
	.regular-checkbox+label {
		position: relative;
		display: inline-block;
		/*lable是内联元素所以需要加inline-block*/
		padding: 12px;
		/*设置复选框大小*/
		background-color: #fafafa;
		/*设置背景颜色*/
		border-radius: 3px;
		/*复选框border*/
		border: 1px solid #CACACA;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	}

	/*第三部分做一个active的效果*/
	.regular-checkbox+label:active,
	.regular-checkbox+label:checked+label:active {
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
	}

	/*选中之后的样式*/
	.regular-checkbox:checked+label {
		background-color: #E9ECEE;
		box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
	}

	/*选中后的效果*/
	.regular-checkbox:checked+label:after {
		content: '\2714';
		/*这是一个对勾*/
		position: absolute;
		font-size: 14px;
		/*设置对勾的大小*/
		top: 2px;
		left: 6px;
		/*设置位置的偏向*/
		color: #009900;
		/*设置颜色绿色*/
		font-weight: bold;
		/*设置对勾的粗细*/
	}

	.login_auto {
		font-size: 0.24rem;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #333333;
		line-height: 0.33rem;
		margin-left: 0.18rem;
	}

	.login_btn {
		width: 3.56rem;
		height: 0.6rem;
		background: linear-gradient(220deg, rgba(54, 132, 241, 0.61) 0%, rgba(25, 77, 223, 0.61) 100%);
		box-shadow: 0px 0px 0.07rem 0px rgba(229, 246, 244, 0.5);
		border-radius: 0.06rem;
		margin-top: 0.42rem;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		outline: none;
		border: none;
	}

	.login_btn:hover {
		background: linear-gradient(220deg, rgba(71, 162, 241, 0.6) 0%, rgba(37, 118, 223, 0.6) 100%);
	}

	.login_btn span {
		font-size: 0.30rem;
		font-family: PangMenZhengDao;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 0.6rem;
		letter-spacing: 0.2rem;
	}
</style>
