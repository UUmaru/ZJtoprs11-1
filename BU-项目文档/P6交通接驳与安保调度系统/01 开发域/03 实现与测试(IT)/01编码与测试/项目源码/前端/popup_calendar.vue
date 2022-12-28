<template>
	<div class="calendar" v-drag>
		<span class="choose_text">选择时间</span>
		<div class="date-header">
			<img class="pre" src="../../static/img/arrow_right.png" @click="pre" />
			<div class="show-date">{{month}}月&#12288{{year}}</div>
			<img class="next" src="../../static/img/arrow_right.png" @click="next" />
		</div>
		<div class="date-content">
			<div class="week-header">
				<div v-for="item in ['一','二','三','四','五','六','日']" :key='item'>{{ item }}</div>
			</div>
			<div class="week-day">
				<div v-for="item in 42" :key="item">
					<div class="every-day-c" v-if="item - beginDay > 0 && item - beginDay <= curDays" @click="selectDay(item)">
						<div :class="(selectedDay == item - beginDay)&&selectedYear==year&&selectedMonth==month?'every-day-p':'every-day'">{{ item - beginDay }}</div>
					</div>
					<div class="other-day" v-else-if="item - beginDay <= 0" @click="pre">{{ item - beginDay + prevDays }}</div>
					<div class="other-day" v-else @click="next">{{ item - beginDay -curDays }}</div>
				</div>
			</div>
		</div>
		<div v-if="type==0" class="time">
			<span class="time_t">选择时间:</span>
			<div class="time_i">
				<span>{{startTime}}</span>
				<img src="../../static/img/icon_clock.png" @click="showStart" />
				<div ref="startTime" v-if="startFlag" class="time_list" @scroll="mouseScroll('startTime')">
					<span v-for="item in startTimeList" @click="chooseStartTime(item)">{{item}}</span>
				</div>
			</div>
			<span style="color: #FFF;margin: 0 0.05rem;font-size: 0.2rem;font-weight: 500;">-</span>
			<div class="time_i">
				<span>{{endTime}}</span>
				<img src="../../static/img/icon_clock.png" @click="showEnd" />
				<div ref="endTime" v-if="endFlag" class="time_list" @scroll="mouseScroll('endTime')">
					<span v-for="item in endTimeList" @click="chooseEndTime(item)">{{item}}</span>
				</div>
			</div>
		</div>
		<div class="bottom">
			<div class="confirm" @click="chooseData">确定</div>
			<div class="cancel" @click="close">取消</div>
		</div>
		<img class="close" src="../../static/img/close.png" @click="close" />
	</div>
</template>

<script>
	export default {
		props: {
			type: {
				default: 0,
			},
		},
		data() {
			return {
				month: "",
				year: "",
				beginDay: "",
				curDays: 0,
				prevDays: 0,
				selectedYear: 0,
				selectedMonth: 0,
				selectedDay: 0,
				startTime: "08:00",
				endTime: "22:00",
				startFlag: false,
				endFlag: false,
				showScrollTop:0,
				startTimeList: [
					"00:00",
					"01:00",
					"02:00",
					"03:00",
					"04:00",
					"05:00",
					"06:00",
					"07:00",
					"08:00",
					"09:00",
					"10:00",
					"11:00",
					"12:00",
					"13:00",
					"14:00",
					"15:00",
					"16:00",
					"17:00",
					"18:00",
					"19:00",
					"20:00",
					"21:00",
					"22:00",
					"23:00"
				],
				endTimeList: [
					"01:00",
					"02:00",
					"03:00",
					"04:00",
					"05:00",
					"06:00",
					"07:00",
					"08:00",
					"09:00",
					"10:00",
					"11:00",
					"12:00",
					"13:00",
					"14:00",
					"15:00",
					"16:00",
					"17:00",
					"18:00",
					"19:00",
					"20:00",
					"21:00",
					"22:00",
					"23:00",
					"23:59",
				]
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
		created() {
			this.getInitDate();
		},
		methods: {
			getInitDate() {
				const date = new Date();
				this.year = date.getFullYear();
				this.month = date.getUTCMonth() + 1;
				this.day = date.getDate();
				this.getBeginDay();
				this.selectedYear = this.year;
				this.selectedMonth = this.month;
				this.selectedDay = this.day;
			},
			getBeginDay() {
				this.curDays = new Date(this.year, this.month, 0).getDate();
				this.prevDays = new Date(this.year, this.month - 1, 0).getDate();
				this.beginDay = new Date(this.year, this.month - 1, 1).getDay() - 1;
				if (this.beginDay < 0) {
					this.beginDay = 6
				}

				console.log("curDays: " + this.curDays + " prevDays:" + this.prevDays + " beginDay:" + this.beginDay)
			},
			pre() {
				this.month--;
				if (this.month < 1) {
					this.month = 12;
					this.year--;
				}
				this.getBeginDay();
			},
			next() {
				this.month++;
				if (this.month > 12) {
					this.month = 1;
					this.year++;
				}
				this.getBeginDay();
			},
			selectDay(item) {
				this.selectedYear = this.year;
				this.selectedMonth = this.month;
				if (this.selectedMonth < 10) {
					this.selectedMonth = "0" + this.selectedMonth;
				}
				this.selectedDay = item - this.beginDay;
				if (this.selectedDay < 10) {
					this.selectedDay = "0" + this.selectedDay;
				}
			},
			showStart() {
				this.showScrollTop = 0
				this.startFlag = !this.startFlag
			},
			showEnd() {
				this.showScrollTop = 0
				this.endFlag = !this.endFlag
			},
			chooseStartTime(item) {
				if (item > this.endTime) {
					return;
				}
				this.startTime = item;
				this.startFlag = false;
			},
			chooseEndTime(item) {
				if (item < this.startTime) {
					return;
				}
				this.endTime = item;
				this.endFlag = false;
			},
			chooseData() {
				this.$parent.selectDate(this.selectedYear, this.selectedMonth, this.selectedDay, this.startTime, this.endTime)
			},
			close() {
				this.$emit("closePopupCalendar")
			},

			// 鼠标滚动事件
			mouseScroll(item) {
				const vm = this;
				console.log("mouseScroll：" + this.$refs[item].scrollTop + " " + vm.showScrollTop)
				if (this.$refs[item].scrollTop === vm.showScrollTop) {
					return false;
				} else if (this.$refs[item].scrollTop > vm.showScrollTop) {
					vm.showScrollTop = vm.showScrollTop + 15;
					this.$refs[item].scrollTop = vm.showScrollTop;
				} 
			}

		}
	}
</script>

<style scoped>
	.calendar {
		width: 4.30rem;
		background: url(../../static/img/popup_bg.png) no-repeat;
		background-size: 100% 100%;
		display: flex;
		flex-direction: column;
		padding: 0.36rem 0.5rem;
		box-sizing: border-box;
	}

	.choose_text {
		font-size: 0.2rem;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 0.28rem;
		align-self: flex-start;
	}

	.date-header {
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.pre {
		width: 0.24rem;
		height: 0.24rem;
		transform: rotate(180deg);
		cursor: pointer;
	}

	.next {
		width: 0.24rem;
		height: 0.24rem;
		cursor: pointer;
	}

	.show-date {
		width: 0.9rem;
		font-size: 0.18rem;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 0.24rem;
		margin: 0.12rem 0.24rem;
	}

	.date-content {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.week-header {
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.week-header div {
		height: 0.2rem;
		width: 0.45rem;
		font-size: 0.12rem;
		font-family: PingFangSC-Medium, PingFang SC;
		font-weight: 500;
		color: #FFFFFF;
		line-height: 0.2rem;
	}

	.week-day {
		display: flex;
		align-items: center;
		justify-content: space-between;
		flex-wrap: wrap;
	}

	.every-day-c {
		width: 0.45rem;
		height: 0.4rem;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.every-day {
		font-size: 0.14rem;
		color: #FFFFFF;
		line-height: 0.4rem;
		cursor: pointer;
	}

	.every-day-p {
		width: 0.4rem;
		height: 0.4rem;
		font-size: 0.14rem;
		color: #FFFFFF;
		line-height: 0.4rem;
		cursor: pointer;
		border-radius: 50%;
		border: 1px solid #5CDDED;
		box-sizing: border-box;
	}

	.other-day {
		width: 0.45rem;
		height: 0.4rem;
		font-size: 0.14rem;
		color: #9FA5B4;
		line-height: 0.4rem;
		/* cursor: pointer; */
	}

	.time {
		display: flex;
		align-items: center;
	}

	.time_t {
		color: #ffffff;
		margin-right: 0.12rem;
	}

	.time_i {
		position: relative;
		display: flex;
		align-items: center;
		border: 1px solid #5CDDED;
		width: 0.8rem;
		height: 0.3rem;
		border-radius: 0.05rem;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.time_i>span {
		color: #ffffff;
		font-size: 0.14rem;
		margin: 0 0.02rem;
	}

	.time_i img {
		width: 0.3rem;
		height: 0.3rem;
	}

	.time_list {
		position: absolute;
		top: 0.3rem;
		width: 0.8rem;
		height: 0.8rem;
		overflow-y: scroll;
		border: 1px solid #5CDDED;
		background: rgba(92, 221, 237, 0.3);
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	::-webkit-scrollbar {
		width: 0.14rem;
		height: 2px;
		background-color: transparent;
	}

	.time_list span {
		color: #FFF;
		font-size: 0.16rem;
	}

	.time_list span:hover {
		color: #ebd638;
	}

	.bottom {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 0.12rem;
	}

	.confirm {
		width: 0.85rem;
		height: 0.34rem;
		border-radius: 0.04rem;
		border: 1px solid #9FA5B4;
		font-size: 0.14rem;
		font-weight: 500;
		color: #9FA5B4;
		line-height: 0.34rem;
		margin-right: 0.24rem;
		cursor: pointer;
	}

	.confirm:hover {
		border: 1px solid #FFFFFF;
		color: #FFFFFF;
	}

	.cancel {
		width: 0.85rem;
		height: 0.34rem;
		border-radius: 0.04rem;
		border: 1px solid #9FA5B4;
		font-size: 0.14rem;
		font-weight: 500;
		color: #9FA5B4;
		line-height: 0.34rem;
		margin-left: 0.24rem;
		cursor: pointer;
	}

	.cancel:hover {
		border: 1px solid #FFFFFF;
		color: #FFFFFF;
	}

	.close {
		position: absolute;
		width: 0.18rem;
		height: 0.18rem;
		top: 0.28rem;
		right: 0.28rem;
		cursor: pointer;
	}
</style>
