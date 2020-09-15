package module.taiex
/**
當日融券賣出與借券賣出成交量值
*/
class ShortSalesVolumeAndValue{
	def sqlDirName = 'short_sales_volume_and_value'
	def dbName = 'findb'
	def tableName = 'short_sales_volume_and_value'
	def doSync(){
/////////////////////
		module.processor.ProcessorRunner.runDayByDay{
			startYear Calendar.getInstance().get(Calendar.YEAR)
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			process{yyyyMmDd->
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){

			    	print '>'
			    }else{
			    	sleep(2400)
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/exchangeReport/TWTASU?response=json&lang=en&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateShortSell true
					    }

						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.fields.collect(fieldNormalize)

					            def _sql = "REPLACE INTO `${dbName}`.`${tableName}` (`security_code`,`securities_lending_volume`,`securities_lending_value`,`borrow_volume`,`borrow_value`,`traded_day`) VALUES "
					            
					            for(int i=0;i<json.data.size();i++){
					               def _data = json.data[i].collect(valueNormalise).join("','");
					               if(i==0){
					                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
					               }else{
					                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
					               }
					            }
					            return _sql
				        	}
				    	}

					    if(resultSql && !resultSql.endsWith('VALUES ')){
							module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}.sql",'UTF-8',"${resultSql};"
           					}
           					print '*'
						}
			    }
			}
		}

		module.db.SqlExecuter.exec{
		    dir "${sqlDirName}"
		}
		module.io.Batch.exec{
			clean "./${sqlDirName}"
			delete "./${sqlDirName}"
			info 'import short_sales_volume_and_value done'
		}
/////////////////////
	}
	def static sync(@DelegatesTo(ShortSalesVolumeAndValue) Closure block){
	        ShortSalesVolumeAndValue m = new ShortSalesVolumeAndValue()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}