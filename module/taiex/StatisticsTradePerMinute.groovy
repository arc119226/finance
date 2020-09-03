 package module.taiex

class StatisticsTradePerMinute{
	def sqlDirName = 'statistics_trade_per_minute'
	def dbName = 'findb'
	def tableName = 'statistics_trade_per_minute'
	def doSync(){
////////////////////////////
		def sql = module.db.SqlExecuter.dbConnection{}

		def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")
		sql.close()
		stockCodes.each{
			def security_code=it.security_code
			def listing_day = it.listing_day
			if(Integer.valueOf(listing_day)<=20041015){
				listing_day='20041015'
			}
			module.processor.ProcessorRunner.runDayByDay{
				startYear Integer.valueOf(listing_day.toString()[0..3])
				startMonth Integer.valueOf(listing_day.toString()[4..5])
				startday Integer.valueOf(listing_day.toString()[6..7])
				endYear Calendar.getInstance().get(Calendar.YEAR)
				endMonth Calendar.getInstance().get(Calendar.MONTH)+1
				endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
				process{yyyyMmDd->
					if(!new File("./statistics_trade_per_minute/${yyyyMmDd}/${security_code}.sql").exists()){
						def _url = "https://www.twse.com.tw/exchangeReport/MI_5MINS?response=json&lang=en&date=${yyyyMmDd}&stockNo=${security_code}"
						//println _url
						def returnJson = module.web.Webget.download{
					         url _url
					         decode 'utf-8'
					    }
					    def resultSql=''
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
			            		for(int i=0;i<json.data.size;i++){
			            			def data = json.data[i];
			            			def time = "${yyyyMmDd}${data[0].replaceAll(':','')}" 
			            			def acc_bid_orders = data[1].replaceAll(',','')
			            			def acc_bid_volume = data[2].replaceAll(',','')
			            			def acc_ask_orders = data[3].replaceAll(',','')
			            			def acc_ask_volume = data[4].replaceAll(',','')
			            			def acc_transaction = data[5].replaceAll(',','')
			            			def acc_trade_volume = data[6].replaceAll(',','')
			            			def acc_trade_value_ntm = data[7].replaceAll(',','')

			            			if(resultSql.endsWith('VALUES ')){
					                  resultSql+="\r\n('${time}','${acc_bid_orders}','${acc_bid_volume}','${acc_ask_orders}','${acc_ask_volume}','${acc_transaction}','${acc_trade_volume}','${acc_trade_value_ntm}','${yyyyMmDd}','${security_code}')"
					               }else{
					                   resultSql+="\r\n,('${time}','${acc_bid_orders}','${acc_bid_volume}','${acc_ask_orders}','${acc_ask_volume}','${acc_transaction}','${acc_trade_volume}','${acc_trade_value_ntm}','${yyyyMmDd}','${security_code}')"
					               }
			            		}
							}
						}
						if(!resultSql.endsWith('VALUES ')){
						///////
							module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/${yyyyMmDd}"
                				write "./${sqlDirName}/${yyyyMmDd}/${security_code}.sql",'UTF-8',"${resultSql};"
           					}
						///////
						}
						print '*'
					}else{
						print '>'
					}
				}
			}
		}

		module.db.SqlExecuter.exec{
		    dir "./${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import statistics_trade_per_minute done'
		} 
////////////////////////////
	}

	def static sync(@DelegatesTo(StatisticsTradePerMinute) Closure block){
	        StatisticsTradePerMinute m = new StatisticsTradePerMinute()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}