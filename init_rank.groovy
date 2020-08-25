/**
初次下載stock_day後跑這個 之後更新跑rankupdate
*/
@Grab(group='commons-io', module='commons-io', version='2.5')
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql
def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')
def stockCodes = sql.rows("select stock.security_code from stock")
println stockCodes.size()

stockCodes.each{it->
	def security_code = it.security_code
	def datas = sql.rows("select * from stock_day where security_code = :security_code order by traded_day",security_code:security_code)

	def lastRank = 0
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
		//println dt.traded_day+' '+dt.stock_code+' '+currentRank+' '+lastRank
		def updateResult = sql.executeUpdate("update stock_day set updown_times = :upDownTimes,last_updown_times = :lastUpDownTimes where id= :id",upDownTimes:currentRank,lastUpDownTimes:lastRank,id:dt.id)
		println updateResult
	}

}
println 'update rank done'
sql.close()