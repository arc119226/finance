package module.taiex
/**
 每日外資買賣 產業類股統計
*/
class DailyForeignShareholdingByIndustrial{
	def sqlDirName = 'daily_foreign_shareholding_by_industrial'
	def dbName = 'findb'
	def tableName_daily_foreign_shareholding_by_industrial = 'daily_foreign_shareholding_by_industrial'
	def doSync(){
/////////////////////
		module.processor.ProcessorRunner.runDayByDay{
			startYear Calendar.getInstance().get(Calendar.YEAR)
			startMonth Calendar.getInstance().get(Calendar.MONTH)+1
			startday 1//Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			endYear Calendar.getInstance().get(Calendar.YEAR)
			endMonth Calendar.getInstance().get(Calendar.MONTH)+1
			endDay Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			process{yyyyMmDd->
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists()){
			    	print '>'
			    }else{
			    	sleep(2400)
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/fund/MI_QFIIS_cat?response=json&lang=en&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateIndustrial true
					    }

						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.fields.collect(fieldNormalize)

					            def _sql = "REPLACE INTO `${dbName}`.`${tableName_daily_foreign_shareholding_by_industrial}` (`category_of_Industry`,`numbers`,`number_of_shares_issued`,`currently_foreign_and_mainland_area_shares_held`,`percentage_of_foreign_and_mainland_area_shares_held`,`traded_day`) VALUES "
					            
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
           					println '*'
						}else{
					    	module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}.sql",'UTF-8',";"
           					}					
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
			info 'import daily_foreign_shareholding_by_industrial done'
		} 
//////////////////////
	}
	def static sync(@DelegatesTo(DailyForeignShareholdingByIndustrial) Closure block){
	        DailyForeignShareholdingByIndustrial m = new DailyForeignShareholdingByIndustrial()
	        block.delegate = m
	        block()
	        m.doSync()
    }
}