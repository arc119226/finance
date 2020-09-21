package module.taiex
/**
融資融券彙總 (全部)
*/
class MarginTransactionl{
	def sqlDirName = 'margin_transactionl'
	def dbName = 'findb'
	def tableName = 'margin_transactions_all'
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
			    if(new File("./${sqlDirName}/${yyyyMmDd}.sql").exists() && new File('./margin_transactionl/'+yyyyMmDd+'_all.sql').exists()){
			    	print '>'
			    }else{
						 def z = [2330,2340,2350]
						Random rnd = new Random()
						def w = z[rnd.nextInt(z.size())]
						println 'wait'+ w
						sleep(w)
					    def returnJson = module.web.Webget.download{
					         url "https://www.twse.com.tw/exchangeReport/MI_MARGN?response=json&lang=en&selectType=ALL&date=${yyyyMmDd}"
					         decode 'utf-8'
					         validateMarginTransactionl true
					    }

						def resultSql = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def _sql = "REPLACE INTO `${dbName}`.`${tableName}` (`security_code`,`margin_purchase`,`margin_sales`,`cash_redemption`,`balance_of_previous_day`,`balance_of_the_day`,`quota`,`short_covering`,`short_sale`,`stock_redemption`,`short_balance_of_previous_day`,`short_balance_of_the_day`,`short_quota`,`offsetting_of_margin_purchases_and_short_sales`,`note`,`traded_day`) VALUES "
					            
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
                				write "./${sqlDirName}/${yyyyMmDd}_all.sql",'UTF-8',"${resultSql};"
           					}
							print '#'
						}else{
							
						}

						def resultSqlSummary = module.parser.JsonConvert.convert{
				        	input returnJson
				        	parseRule {json->
					            if(json.stat != 'OK'){
					               return ''
					            }
					            def fields = json.creditFields.collect(fieldNormalize)
					            def _sql = "REPLACE INTO `${dbName}`.`${tableName}` (`${fields.join('`,`')}`,`traded_day`) VALUES "
					            
					            for(int i=0;i<json.creditList.size();i++){
					               def _data = json.creditList[i].collect(valueNormalise).join("','");
					               if(i==0){
					                  _sql+="\r\n('${_data}','${yyyyMmDd}')"
					               }else{
					                   _sql+="\r\n,('${_data}','${yyyyMmDd}')"
					               }
					            }
					            return _sql
				        	}
				    	}
					    if(resultSqlSummary && !resultSqlSummary.endsWith('VALUES ')){
				     		module.io.Batch.exec{
                				mkdirs "./${sqlDirName}/"
                				write "./${sqlDirName}/${yyyyMmDd}_summary.sql",'UTF-8',"${resultSql};"
           					}
							print '*'
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
			info 'import margin_transactionl done'
		} 
////////////////////
	}
	def static sync(@DelegatesTo(MarginTransactionl) Closure block){
	        MarginTransactionl m = new MarginTransactionl()
	        block.delegate = m
	        block()
	        m.doSync()
    }	
}