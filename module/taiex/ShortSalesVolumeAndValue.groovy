package module.taiex
class ShortSalesVolumeAndValue{
	def sqlDirName = 'short_sales_volume_and_value'
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

					            def _sql = "REPLACE INTO `stock_tw`.`short_sales_volume_and_value` (`security_code`,`securities_lending_volume`,`securities_lending_value`,`borrow_volume`,`borrow_value`,`traded_day`) VALUES "
					            
					            for(int i=0;i<json.data.size;i++){
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
                				write "./${sqlDirName}/${yyyyMmDd}.tmp",'UTF-8',"${resultSql};"
                				rename "./${sqlDirName}/${yyyyMmDd}.tmp","./${sqlDirName}/${yyyyMmDd}.sql"
           					}
           					print '*'
						}
			    }
			}
		}

		module.db.SqlExecuter.execute{
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