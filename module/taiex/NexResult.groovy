package module.taiex
/**
次日漲跌計算
*/
class NexResult{
	def tableName = 'stock_day'
	def doSync(){
		def sql = module.db.SqlExecuter.dbConnection{}
		def stockCodes = sql.rows("select distinct security_code from stock_day")

		println stockCodes.size()
		stockCodes.each{it->
			def security_code = it.security_code
			def datas = sql.rows("select * from stock_day where security_code = :security_code order by traded_day desc",security_code:security_code)
			def lastResult=null
			print '#'
			datas.each{dt->	
				if(lastResult!=null){
					if(dt.next_result==null){
						def updateResult = sql.executeUpdate("update stock_day set next_result = :next_result where id= :id",next_result:lastResult,id:dt.id)
						lastResult=null
					}
				}
				if(dt.change>0){
					lastResult=1
				}else if(dt.change<0){
					lastResult=-1
				}else{
					lastResult=0
				}
			}
		}
		sql.close()
	}

	def static sync(@DelegatesTo(NexResult) Closure block){
	        NexResult m = new NexResult()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}