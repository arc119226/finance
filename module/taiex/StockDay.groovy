 package module.taiex
/**
盤後 日交易量
*/
 class StockDay{
 	Boolean isInit =false
 	def dbName = 'findb'
 	def tableName = 'stock_day'
 	def isInit(Boolean isInit){
 		this.isInit=isInit
 	}
 	def defaultLlistingDay = 20100101
 	def sqlDirName = 'stock_day'
 	def sqlConditon = 'select * from stock where stock_type=\'上市\' order by stock.listing_day'
 	def sqlConditon2 = "select distinct security_code from stock_day where updown_times is null"
 	def z = [2330,2340,2350]
	Random rnd = new Random()
 	def doSync(){
		def stockCodes = module.db.SqlExecuter.query{queryString sqlConditon}
		def fils = []
		if(new File("./${sqlDirName}").list()){
			fils=new File("./${sqlDirName}").list()
		}
		int skip=0
		int processed=0
		stockCodes.each{it->
			def security_code = it.security_code
			def listing_day = it.listing_day
			def stock_name = it.stock_name
			def product_type = it.product_type
			if(listing_day<defaultLlistingDay){
				listing_day=defaultLlistingDay
			}
			
			def resultSql =''
			if(!fils.contains("${security_code}.sql")){

//////////////////
				println 'total:'+stockCodes.size()+' skip:'+skip+' processed:'+processed
				if(product_type.contains('權證')){
					if(stock_name.length()>5){

						def y = stock_name.replaceAll(/^.+(\d)([0-9A-c]).+(\d)(\d)$/,'$1');
						def m = stock_name.replaceAll(/^.+(\d)([0-9A-c]).+(\d)(\d)$/,'$2');

						if(Integer.parseInt(y) in [9]){
							if(m in ['9','A','B','C']){
								println stock_name+' 未到期'
								
							}else{
								println stock_name+' 到期'
								skip++
								return 
							}
						}else if(Integer.parseInt(y) in [0]){
							println stock_name+' 未到期'

						}else{
							println stock_name+' 到期'
							skip++
							return
						}
					}
				}	
//////////////////

				module.processor.ProcessorRunner.runMonthByMonth{
					startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])//
					startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
					startday 1//Integer.valueOf(listing_day.toString()[6..7])
					endYear Calendar.getInstance().get(Calendar.YEAR)
					endMonth Calendar.getInstance().get(Calendar.MONTH)+1
					endDay 1
					process{yyyyMmDd->
						def w = z[rnd.nextInt(z.size())]
						println 'wait'+ w
						sleep(w)

					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&lang=en&stockNo=${security_code}&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateStockDay true
					    }
						module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return null
					            }
					            def fields = json.fields.collect(fieldNormalize)
					            if(resultSql==''){
					            	resultSql = "REPLACE INTO `${dbName}`.`${tableName}` (`${fields.join('`,`')}`,`traded_day`,`security_code`) VALUES "
					            }
					            for(int i=0;i<json.data.size();i++){
					            	def dt = json.data[i]
					            	def dayArr = dt[0].split('/')
									def year = Integer.parseInt(dayArr[0].trim())
									def month = String.format("%02d", Integer.parseInt(dayArr[1].trim()))
									def day = String.format("%02d", Integer.parseInt(dayArr[2].trim()))
									def price_day = "${year}${month}${day}" 
					                def _data = json.data[i].collect(valueNormalise).join("','");
					                if(resultSql.endsWith('VALUES ')){
					                  resultSql+="\r\n('${_data}','${price_day}','${security_code}')"
					               }else{
					                   resultSql+="\r\n,('${_data}','${price_day}','${security_code}')"
					               }
					            }
				        	}
				    	}//convert
					}//process
				}//run
				if(resultSql && !resultSql.endsWith('VALUES ')){
			    	module.io.Batch.exec{
                		mkdirs "./${sqlDirName}"
                		write "./${sqlDirName}/${security_code}.sql",'UTF-8',"${resultSql};"
           			}
				}else{
					module.io.Batch.exec{
                		mkdirs "./${sqlDirName}"
                		write "./${sqlDirName}/${security_code}.sql",'UTF-8',";"
           			}
				}
				processed++
				println "${new Date()}"
			}else{
				// print ">"
				skip++
			}
		}//each stock

		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import stock_day done'
			info 'start update rank'
		}
		///////////////
		if(isInit){
			this.initRank()
		}else{
			this.updateRank()
			this.initRank()
		}

 	}

 	def initRank(){
		def sql = module.db.SqlExecuter.dbConnection{}
		def stockCodes = sql.rows("select distinct sd.security_code from stock_day sd where sd.updown_times is null")

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
				
			}
			print '#'
		}
		module.io.Batch.exec{
			info 'update rank done'
		}
		sql.close()
 	}
 	def updateRank(){
 		def sql = module.db.SqlExecuter.dbConnection{}
		def stockCodes = sql.rows(sqlConditon2)

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

    def static sync(@DelegatesTo(StockDay) Closure block){
	        StockDay m = new StockDay()
	        block.delegate = m
	        block()
	        m.doSync()
    }
 }