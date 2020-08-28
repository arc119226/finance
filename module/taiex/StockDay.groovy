 package module.taiex

 class StockDay{

 	def defaultLlistingDay = 20100101
 	def sqlDirName = 'stock_day'
 	def sqlConditon = 'select stock.security_code,stock.listing_day from stock where stock_type=\'上市\' order by stock.listing_day'

 	def doSync(){
		def stockCodes = module.db.SqlExecuter.query{queryString sqlConditon}
		stockCodes.each{it->
			def security_code = it.security_code
			def listing_day = it.listing_day
			if(listing_day<defaultLlistingDay){
				listing_day=defaultLlistingDay
			}
			def resultSql =''
			if(!new File("./${sqlDirName}/${security_code}.sql").exists()){
				module.processor.ProcessorRunner.runMonthByMonth{
					startYear Calendar.getInstance().get(Calendar.YEAR)//Integer.valueOf(listing_day.toString()[0..3])//
					startMonth Calendar.getInstance().get(Calendar.MONTH)+1//Integer.valueOf(listing_day.toString()[4..5])
					startday 1//Integer.valueOf(listing_day.toString()[6..7])
					endYear Calendar.getInstance().get(Calendar.YEAR)
					endMonth Calendar.getInstance().get(Calendar.MONTH)+1
					endDay 1
					process{yyyyMmDd->
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
					            	resultSql = "REPLACE INTO `stock_tw`.`stock_day` (`${fields.join('`,`')}`,`traded_day`,`security_code`) VALUES "
					            }
					            for(int i=0;i<json.data.size;i++){
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
                		write "./${sqlDirName}/${security_code}.tmp",'UTF-8',"${resultSql};"
                		rename "./${sqlDirName}/${security_code}.tmp","./${sqlDirName}/${security_code}.sql"
           			}
				}
				print '*'
			}else{
				print ">"
			}
		}//each stock

		module.db.SqlExecuter.execute{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import stock_day done'
		}


		///////////////
		def sql = module.db.SqlExecuter.dbConnection{}


		stockCodes = sql.rows("select distinct security_code from stock_day where updown_times is null")

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
		sql.close()
		///////////////
 	}

    def static sync(@DelegatesTo(StockDay) Closure block){
	        StockDay m = new StockDay()
	        block.delegate = m
	        block()
	        m.doSync()
    }
 }