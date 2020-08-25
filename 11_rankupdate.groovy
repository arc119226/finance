/**
跑完stock_day跑排行
*/
@Grab(group='commons-io', module='commons-io', version='2.5')
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql

def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
				'root','Esorn@ldorn110','com.mysql.jdbc.Driver')


def stockCodes = sql.rows("select distinct security_code from stock_day where updown_times is null")
println stockCodes.size()
stockCodes.each{it->
	def security_code = it.security_code

	def lastDatas = sql.rows("select * from stock_day where security_code = :security_code and updown_times is not null order by traded_day desc limit 1",security_code:security_code)
	lastDatas.each{lastData->
		def last_updown_times = lastData.updown_times

		def datas = sql.rows("select * from stock_day where security_code = :security_code and updown_times is null order by traded_day",security_code:security_code)
		def lastRank = last_updown_times
		def currentRank = 0
		datas.each{dt->
			if(dt.change >0 ){
				if(currentRank == 0){
					lastRank=currentRank
					currentRank=currentRank+1
				}else if(currentRank < 0){
					lastRank=currentRank
					currentRank = 0
					currentRank = 1
				}else if(currentRank>0){
					lastRank = currentRank
					currentRank=currentRank+1
				}

			}else if(dt.change < 0){
				
				if(currentRank==0){
					lastRank=currentRank
					currentRank=currentRank-1
				}else if(currentRank<0){
					lastRank=currentRank
					currentRank=currentRank-1
				}else if(currentRank>0){
					lastRank=currentRank
					currentRank=0
					currentRank=-1
				}

			}else{
				lastRank=currentRank
				currentRank = 0
			}
			def updateResult = sql.executeUpdate("update stock_day set updown_times = :upDownTimes,last_updown_times = :lastUpDownTimes where id= :id",upDownTimes:currentRank,lastUpDownTimes:lastRank,id:dt.id)
			print '*'
		}
	}
}

println '.'

sql.close()