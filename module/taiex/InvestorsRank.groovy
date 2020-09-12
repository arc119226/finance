package module.taiex
/**
三大法人連續買賣超統計
*/
class InvestorsRank{
 	Boolean isInit =false
 	def dbName='findb'
 	def tableName = 'investors'
 	def isInit(Boolean isInit){
 		this.isInit=isInit
 	}
	def doSync(){
		if(isInit){
			for(type in ['FD','STIC','D']){
				this.initRank(type)
			}
		}else{
			for(type in ['FD','STIC','D']){
				this.updateRank(type)
			}
			for(type in ['FD','STIC','D']){
				this.initRank(type)
			}
		}
	}
 	def initRank(String type){
		def sql = module.db.SqlExecuter.dbConnection{}
		def stockCodes = sql.rows("select distinct sd.security_code from ${tableName} sd where sd.updown_times is null and type=:type",type:type)

		println stockCodes.size()

		stockCodes.each{it->
			def security_code = it.security_code
			def datas = sql.rows("select * from ${tableName} where security_code = :security_code and type = :type order by traded_day",security_code:security_code,type:type)

			def lastRank = 0
			def currentRank = 0
			datas.each{dt->	
				if(dt.difference >0 ){
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

				}else if(dt.difference < 0){
					
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
				def updateResult = sql.executeUpdate("update ${tableName} set updown_times = :upDownTimes,last_updown_times = :lastUpDownTimes where id= :id",upDownTimes:currentRank,lastUpDownTimes:lastRank,id:dt.id)
				
			}
			print '#'
		}
		module.io.Batch.exec{
			info 'init rank done'
		}
		sql.close()
 	}

 	 def updateRank(String type){
 		def sql = module.db.SqlExecuter.dbConnection{}
		def stockCodes = sql.rows("select distinct security_code from ${tableName} where updown_times is null and type=:type",type:type)

		println stockCodes.size()
		stockCodes.each{it->
			def security_code = it.security_code

			def lastDatas = sql.rows("select * from ${tableName} where security_code = :security_code and updown_times is not null and type=:type order by traded_day desc limit 1",security_code:security_code,type:type)
			lastDatas.each{lastData->
				def last_updown_times = lastData.updown_times

				def datas = sql.rows("select * from ${tableName} where security_code = :security_code and updown_times is null and type=:type order by traded_day",security_code:security_code,type:type)
				def lastRank = last_updown_times
				def currentRank = 0
				datas.each{dt->
					if(dt.difference >0 ){
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

					}else if(dt.difference < 0){
						
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
					def updateResult = sql.executeUpdate("update ${tableName} set updown_times = :upDownTimes,last_updown_times = :lastUpDownTimes where id= :id",upDownTimes:currentRank,lastUpDownTimes:lastRank,id:dt.id)
					
				}
				print '#'
			}
		}
		sql.close()
		///////////////
		module.io.Batch.exec{
			info 'update rank done'
		}
 	}

	def static sync(@DelegatesTo(InvestorsRank) Closure block){
	        InvestorsRank m = new InvestorsRank()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}